package com.example.smartgalleryproject;

import okhttp3.Response;

public interface UploadListener {
    public abstract void onSuccess(Response response);
    public abstract void onFailure(Exception exception);
}
