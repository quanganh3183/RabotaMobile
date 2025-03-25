package com.example.rabotamb.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "https://api.rabotaworks.com/";
    private static ApiService instance;

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static ApiService getInstance() {
        if (instance == null) {
            // Configure Gson
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // Create logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> {
                Log.d(TAG, "API Log: " + message);
            });
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Create OkHttpClient
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(chain -> {
                        Request original = chain.request();

                        // Log request details
                        Log.d(TAG, "Request URL: " + original.url());
                        Log.d(TAG, "Request Method: " + original.method());
                        Log.d(TAG, "Request Headers: " + original.headers());

                        if (original.body() != null) {
                            Log.d(TAG, "Request Body: " + original.body());
                        }

                        Request request = original.newBuilder()
                                .header("Content-Type", "application/json; charset=UTF-8")
                                .header("Accept", "application/json")
                                .header("Accept-Charset", "UTF-8")
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    })
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();

            try {
                // Create Retrofit instance
                Log.d(TAG, "Creating Retrofit instance with base URL: " + BASE_URL);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();

                // Create API service
                instance = retrofit.create(ApiService.class);
                Log.d(TAG, "API Service created successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error creating API service", e);
            }
        }
        return instance;
    }
}