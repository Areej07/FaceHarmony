package com.example.smartgalleryproject;

import okhttp3.OkHttpClient;

public class ApiClient {
    private static OkHttpClient instance;

    private ApiClient() {}

    public static synchronized OkHttpClient getInstance() {
        if (instance == null) {
            instance = new OkHttpClient();
        }
        return instance;
    }
}
