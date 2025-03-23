package com.example.rabotamb.ui.job;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.api.ApiService;
import com.example.rabotamb.data.models.job.Job;
import com.example.rabotamb.data.models.job.JobDetailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobDetailsViewModel extends ViewModel {
    private static final String TAG = "JobDetailsViewModel";

    private final MutableLiveData<Job> jobDetails = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final ApiService apiService;

    public JobDetailsViewModel() {
        apiService = ApiClient.getInstance();
    }

    public LiveData<Job> getJobDetails() {
        return jobDetails;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadJobDetails(String jobId) {
        isLoading.setValue(true);
        error.setValue(null);

        try {
            apiService.getJobDetails(jobId).enqueue(new Callback<JobDetailResponse>() {
                @Override
                public void onResponse(Call<JobDetailResponse> call, Response<JobDetailResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        JobDetailResponse jobResponse = response.body();
                        if (jobResponse.getData() != null) {
                            jobDetails.setValue(jobResponse.getData());
                        } else {
                            error.setValue("Không tìm thấy thông tin công việc");
                        }
                    } else {
                        error.setValue("Lỗi tải dữ liệu");
                    }
                    isLoading.setValue(false);
                }

                @Override
                public void onFailure(Call<JobDetailResponse> call, Throwable t) {
                    Log.e(TAG, "Error loading job details", t);
                    error.setValue("Lỗi kết nối: " + t.getMessage());
                    isLoading.setValue(false);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error executing job details request", e);
            error.setValue("Lỗi thực thi");
            isLoading.setValue(false);
        }
    }
}