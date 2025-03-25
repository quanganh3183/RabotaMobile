package com.example.rabotamb.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.api.ApiService;
import com.example.rabotamb.utils.UserPreferences;

import retrofit2.Call;

public class JobRepository {
    private static final String TAG = "JobRepository";
    private final ApiService apiService;
    private final UserPreferences userPreferences;

    public JobRepository(Context context) {
        this.apiService = ApiClient.getInstance();
        this.userPreferences = new UserPreferences(context);
    }

    public Call<Void> deleteJob(String jobId) {
        String token = userPreferences.getToken();
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token không tồn tại");
            throw new IllegalStateException("Token không tồn tại");
        }
        Log.d(TAG, "Deleting job with ID: " + jobId);
        return apiService.deleteJob(jobId, token);
    }
}