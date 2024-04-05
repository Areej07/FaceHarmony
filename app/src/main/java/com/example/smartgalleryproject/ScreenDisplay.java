package com.example.smartgalleryproject;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.File;

public class ScreenDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screendisplay);

        // Retrieve the selected image path from the intent
        String selectedPhotoPath = getIntent().getStringExtra("selectedPhotoPath");

        // Set up the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set item selected listener for the BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.edit) {
                    // Handle Edit button click
                    openEditScreen(selectedPhotoPath);
                    return true;
                } else if (item.getItemId() == R.id.delete) {
                    // Handle Delete button click
                    deleteImage(selectedPhotoPath);
                    return true;
                } else {
                    return false;
                }
            }
        });

        // Load the image into the ImageView
        ImageView selectedImageView = findViewById(R.id.imageView);
        if (selectedPhotoPath != null) {
            // Use an image loading library or your own logic to load the image
            // For demonstration purposes, assuming the path is a local file
            File imgFile = new File(selectedPhotoPath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                selectedImageView.setImageBitmap(myBitmap);
            }
        }
    }

    private void deleteImage(String selectedPhotoPath) {
        // Create an AlertDialog to confirm the deletion
        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User confirmed the deletion
                        performDeleteAction(selectedPhotoPath);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    // Separate method to perform the actual deletion
    private void performDeleteAction(String selectedPhotoPath) {
        File file = new File(selectedPhotoPath);
        if (file.exists()) {
            if (file.delete()) {
                // Deletion successful
                Log.d("DeleteImage", "File deleted successfully");
                finish();
            } else {
                // Deletion failed
                Log.e("DeleteImage", "Failed to delete file");
            }
        } else {
            // File does not exist
            Log.e("DeleteImage", "File does not exist");
        }
    }

    private void openEditScreen(String selectedPhotoPath) {
        // Start a new activity to display the selected picture for editing
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("selectedPhotoPath", selectedPhotoPath);
        startActivity(intent);
    }
}
