package com.example.smartgalleryproject;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIService {
    private static final OkHttpClient client = ApiClient.getInstance();

    private static final MediaType MEDIA_TYPE_IMAGE_JPEG = MediaType.parse("image/jpeg");

    private static UploadListener uploadListener;

    public static void setUploadListener(UploadListener uploadListener) {
        APIService.uploadListener = uploadListener;
    }

    public static void upload(String key, File file, String url) {
        var requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(key, file.getName(), RequestBody.create(file, MEDIA_TYPE_IMAGE_JPEG))
                .build();

        var request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                uploadListener.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful())
                    uploadListener.onSuccess(response);
            }
        });
    }

    public static void getData(final String url) {
        var request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("MyAPiService", e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() & response.body() != null)
                    Log.d("MyAPiService", response.body().string());
            }
        });
    }
}
