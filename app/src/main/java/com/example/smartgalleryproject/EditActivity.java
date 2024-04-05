package com.example.smartgalleryproject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditActivity extends AppCompatActivity {

    private static final int PIC_CROP = 1;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        imageView = findViewById(R.id.editedImageView);

        // Retrieve the selected image path from the intent
        String selectedPhotoPath = getIntent().getStringExtra("selectedPhotoPath");

        // Load the image into the ImageView
        if (selectedPhotoPath != null) {
            // Use an image loading library or your own logic to load the image
            // For demonstration purposes, assuming the path is a local file
            Bitmap myBitmap = BitmapFactory.decodeFile(selectedPhotoPath);
            imageView.setImageBitmap(myBitmap);
        }

        // Set up bottom navigation view
        BottomNavigationView editBottomNavigationView = findViewById(R.id.editBottomNavigationView);
        editBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.cropButton) {
                    // Handle cropping logic here
                    // You may want to launch the image picker or initiate cropping
                    openImagePicker();
                    return true;
                } else if (item.getItemId() == R.id.filterButton) {
                    // Implement applying filters functionality
                    // You might launch a new activity or show a dialog for selecting filters
                    return true;
                } else if (item.getItemId() == R.id.textButton) {
                    // Implement adding text functionality
                    // You might launch a new activity or show a dialog for adding text
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PIC_CROP);
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PIC_CROP) {
            if (data != null) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap selectedBitmap = extras.getParcelable("data");

                // Set the cropped bitmap to the ImageView
                imageView.setImageBitmap(selectedBitmap);
            }
        }
    }
}
