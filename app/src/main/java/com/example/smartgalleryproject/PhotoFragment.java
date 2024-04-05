package com.example.smartgalleryproject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartgalleryproject.ImageAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PhotoFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo, container, false);

       // recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Initially load and sort images
        loadAndSortImages();

        return root;
    }

    private void loadAndSortImages() {
        // Replace "YourAlbumName" with the actual album name
        List<String> imagePaths = getImagesFromAlbum("YourAlbumName");

        // Sort the images based on their names
        Collections.sort(imagePaths, new Comparator<String>() {
            @Override
            public int compare(String path1, String path2) {
                return path1.compareToIgnoreCase(path2);
            }
        });

        adapter = new ImageAdapter(imagePaths);
        recyclerView.setAdapter(adapter);
    }

    private List<String> getImagesFromAlbum(String albumName) {
        List<String> imagePaths = new ArrayList<>();

        String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ?";
        String[] selectionArgs = new String[]{albumName};

        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = requireActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            while (cursor.moveToNext()) {
                String imagePath = cursor.getString(columnIndex);
                imagePaths.add(imagePath);
            }

            cursor.close();
        }

        return imagePaths;
    }

    // Call this method when the album button is clicked
    public void onAlbumClicked() {
        // Retrieve sorted image paths
        List<String> sortedImagePaths = getImagesFromAlbum("YourAlbumName");

        // Sort the images based on their names
        Collections.sort(sortedImagePaths, new Comparator<String>() {
            @Override
            public int compare(String path1, String path2) {
                return path1.compareToIgnoreCase(path2);
            }
        });

        // Start SortedAlbumActivity and pass the sorted image paths
        Intent intent = new Intent(getActivity(), SortedAlbumActivity.class);
        intent.putStringArrayListExtra("sortedImagePaths", new ArrayList<>(sortedImagePaths));
        startActivity(intent);
    }
}
