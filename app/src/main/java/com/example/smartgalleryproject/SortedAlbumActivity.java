package com.example.smartgalleryproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class SortedAlbumActivity extends AppCompatActivity {

    private RecyclerView folderRecycler;
    private TextView empty;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorted_album);

        empty = findViewById(R.id.empty);
        folderRecycler = findViewById(R.id.folderRecycler);

        // Request permission
        if (ContextCompat.checkSelfPermission(SortedAlbumActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SortedAlbumActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            setupRecyclerView();
        }

        changeStatusBarColor();
    }

    private void setupRecyclerView() {
        folderRecycler.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<imageFolder> folders = getPicturePaths();

        if (folders.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
        } else {
            PictureFolderAdapter folderAdapter = new PictureFolderAdapter(folders, this);
            folderRecycler.setAdapter(folderAdapter);
        }
    }

    private ArrayList<imageFolder> getPicturePaths() {
        ArrayList<imageFolder> picFolders = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();
        Uri allImagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID};
        Cursor cursor = getContentResolver().query(allImagesUri, projection, null, null, null);

        try {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            do {
                imageFolder folds = new imageFolder();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String dataPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                String folderPaths = dataPath.substring(0, dataPath.lastIndexOf(folder + "/"));
                folderPaths = folderPaths + folder + "/";
                if (!picPaths.contains(folderPaths)) {
                    picPaths.add(folderPaths);

                    folds.setPath(folderPaths);
                    folds.setFolderName(folder);
                    folds.setFirstPic(dataPath);
                    folds.addpics();
                    picFolders.add(folds);
                } else {
                    for (int i = 0; i < picFolders.size(); i++) {
                        if (picFolders.get(i).getPath().equals(folderPaths)) {
                            picFolders.get(i).setFirstPic(dataPath);
                            picFolders.get(i).addpics();
                        }
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return picFolders;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeStatusBarColor() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
    }

    public class PictureFolderAdapter extends RecyclerView.Adapter<PictureFolderAdapter.PicHolder> {

        private ArrayList<imageFolder> folders;
        private Context context;

        public PictureFolderAdapter(ArrayList<imageFolder> folders, Context context) {
            this.folders = folders;
            this.context = context;
        }

        @NonNull
        @Override
        public PicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_sorted_album, parent, false);
            return new PicHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PicHolder holder, int position) {
            final imageFolder folder = folders.get(position);

            try {
                Glide.with(context).load(new File(folder.getFirstPic())).into(holder.imageView);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            holder.itemView.setOnClickListener(v -> onPicClicked(holder, position, folder.getPics()));
        }

        @Override
        public int getItemCount() {
            return folders.size();
        }

        public class PicHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public PicHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView); // Replace with the actual ID of your ImageView
            }
        }
    }

    public void onPicClicked(PictureFolderAdapter.PicHolder holder, int position, ArrayList<imageFolder.pictureFacer> pics) {
        // Handle the click event for
    }
}
