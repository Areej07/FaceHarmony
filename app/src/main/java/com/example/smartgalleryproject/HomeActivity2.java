
package com.example.smartgalleryproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class HomeActivity2 extends AppCompatActivity implements UploadListener {

    private static final String SERVER_URL = "http://192.168.10.8:5000/recognize";

    private List<String> photoList; // List of image file paths
    private List<String> selectedPhotos; // List to keep track of selected photos
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 1;
    private GridView photoGridView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        selectedPhotos = new ArrayList<>(); // Initialize the selectedPhotos list

        if (areImagesPermissionGranted()) {
            loadImagesFromSdCard();
            initializeGridView();
        } else
            requestImagesPermission();

//        APIService.getData(SERVER_URL);

        // Set item selected listener for the BottomNavigationView
        bottomNavigationView = findViewById(R.id.botom);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.photo) {
                // You can add your logic here for the 'Photo' item if needed
                return true;
            } else if (item.getItemId() == R.id.album) {
                // Handle the 'Album' item click
                moveSelectedPhotosToAlbum();
                return true;
            } else {
                return false;
            }
        });
    }

    private void requestImagesPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_READ_EXTERNAL_STORAGE_PERMISSION
            );
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE_PERMISSION
            );
        }
    }

    private void moveSelectedPhotosToAlbum() {
        // Clear the list of selected photos after moving
        selectedPhotos.clear();

        // Optionally, you can update the UI or show a message indicating that the photos have been moved
        Toast.makeText(this, "Selected photos moved to album.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HomeActivity2.this, SortedAlbumActivity.class);
        startActivity(intent);
    }

    private boolean areImagesPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return  (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED);
        }

        return  (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED);
    }

    private void loadImagesFromSdCard() {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        int columnIndex = cursor != null ? cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) : -1;
        List<String> filePaths = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(columnIndex);
                filePaths.add(filePath);
            }
            cursor.close();
        }

        photoList = filePaths;
    }

    private void initializeGridView() {
        // Initialize GridView
        photoGridView = findViewById(R.id.photoGridView);

        APIService.setUploadListener(this);

        // Create and set the adapter
        PhotoAdapter adapter = new PhotoAdapter(photoList);
        photoGridView.setAdapter(adapter);

        // Set item click listener
        photoGridView.setOnItemClickListener((parent, view, position, id) -> {
            // Handle item click, for example, start a new activity
            String selectedPhotoPath = photoList.get(position);
            Log.d("my_path", selectedPhotoPath);

            APIService.upload("images", new File(selectedPhotoPath), SERVER_URL);

            selectedPhotos.add(selectedPhotoPath); // Add the selected photo to the list
            Intent intent = new Intent(HomeActivity2.this, ScreenDisplay.class); // Replace with your desired activity
            intent.putExtra("selectedPhotoPath", selectedPhotoPath);
            startActivity(intent);
        });
    }

    @Override
    public void onSuccess(Response response) {
        runOnUiThread(() -> Toast.makeText(this, "Upload was success", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onFailure(Exception exception) {
        runOnUiThread(() -> Toast.makeText(this, "Upload failed: " + exception.getMessage(), Toast.LENGTH_LONG).show());
    }

    static class PhotoAdapter extends BaseAdapter {
        private final List<String> photoList; // List of image file paths

        // Constructor to initialize the photoList
        public PhotoAdapter(List<String> photoList) {
            this.photoList = photoList;
        }

        @Override
        public int getCount() {
            return photoList.size();
        }

        @Override
        public Object getItem(int position) {
            return photoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(parent.getContext());
                imageView.setLayoutParams(new GridView.LayoutParams(300, 300)); // Adjust the size as needed
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            String photoPath = photoList.get(position);
            // Load the image using a library like Glide or Picasso for better performance
            // Example using Glide: Glide.with(parent.getContext()).load(photoPath).into(imageView)
            // For simplicity, we use setImageURI here
            imageView.setImageURI(Uri.parse(photoPath));

            return imageView;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImagesFromSdCard();
                initializeGridView();
            } else {
                // Handle permission denied
                Toast.makeText(this, "Permission denied. Cannot load images.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
