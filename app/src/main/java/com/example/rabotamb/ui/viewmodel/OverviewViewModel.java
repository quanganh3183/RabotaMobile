package com.example.rabotamb.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.data.models.job.JobsHrResponse;
import com.example.rabotamb.data.models.resumes.ResumeResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OverviewViewModel extends ViewModel {
    private static final String TAG = "OverviewViewModel";

    private final MutableLiveData<Integer> jobCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> resumeCount = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public void loadOverviewData(String companyId) {
        isLoading.setValue(true);
        error.setValue(null);

        // Load Jobs Count
        ApiClient.getInstance().getJobsByCompany(companyId, 1, 1000)
                .enqueue(new Callback<JobsHrResponse>() {
                    @Override
                    public void onResponse(Call<JobsHrResponse> call, Response<JobsHrResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JobsHrResponse.Meta meta = response.body().getMeta();
                            jobCount.setValue(meta != null ? meta.getTotal() : 0);
                            Log.d(TAG, "Jobs count: " + (meta != null ? meta.getTotal() : 0));
                        } else {
                            error.setValue("Không thể tải số lượng việc làm");
                            Log.e(TAG, "Error loading jobs: " + response.message());
                        }
                        checkLoadingComplete();
                    }

                    @Override
                    public void onFailure(Call<JobsHrResponse> call, Throwable t) {
                        Log.e(TAG, "Failed to load jobs", t);
                        error.setValue("Lỗi kết nối: " + t.getMessage());
                        checkLoadingComplete();
                    }
                });

        // Load Resumes Count
        ApiClient.getInstance().getResumesByCompany(companyId, 1, 1000)
                .enqueue(new Callback<ResumeResponse>() {
                    @Override
                    public void onResponse(Call<ResumeResponse> call, Response<ResumeResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ResumeResponse.ResumeDataWrapper dataWrapper = response.body().getData();
                            if (dataWrapper != null && dataWrapper.getData() != null) {
                                ResumeResponse.Meta meta = dataWrapper.getData().getMeta();
                                resumeCount.setValue(meta != null ? meta.getTotal() : 0);
                                Log.d(TAG, "Resumes count: " + (meta != null ? meta.getTotal() : 0));
                            } else {
                                resumeCount.setValue(0);
                            }
                        } else {
                            error.setValue("Không thể tải số lượng hồ sơ");
                            Log.e(TAG, "Error loading resumes: " + response.message());
                        }
                        checkLoadingComplete();
                    }

                    @Override
                    public void onFailure(Call<ResumeResponse> call, Throwable t) {
                        Log.e(TAG, "Failed to load resumes", t);
                        error.setValue("Lỗi kết nối: " + t.getMessage());
                        checkLoadingComplete();
                    }
                });
    }

    private void checkLoadingComplete() {
        if (jobCount.getValue() != null && resumeCount.getValue() != null) {
            isLoading.setValue(false);
        }
    }

    public LiveData<Integer> getJobCount() {
        return jobCount;
    }

    public LiveData<Integer> getResumeCount() {
        return resumeCount;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }
}