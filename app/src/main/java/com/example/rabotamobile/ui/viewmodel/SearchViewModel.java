package com.example.rabotamb.ui.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.api.ApiService;
import com.example.rabotamb.data.models.job.Job;
import com.example.rabotamb.data.models.job.JobListResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {
    private static final String TAG = "SearchViewModel";
    private final MutableLiveData<List<Job>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final ApiService apiService;
    private Call<JobListResponse> currentCall;
    private List<Job> currentJobs = new ArrayList<>();

    private String searchName = "";
    private String searchLocation = "";

    public SearchViewModel() {
        apiService = ApiClient.getInstance();
    }

    public LiveData<List<Job>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadJobs() {
        currentJobs.clear();
        searchName = "";
        searchLocation = "";
        executeSearch();
    }

    public void search(String name, String location) {
        currentJobs.clear();
        searchName = name;
        searchLocation = location;
        executeSearch();
    }

    private void executeSearch() {
        if (currentCall != null) {
            currentCall.cancel();
        }

        isLoading.setValue(true);
        error.setValue(null);

        try {
            currentCall = apiService.getJobs(
                    1,
                    100,
                    searchName.isEmpty() ? null : searchName,
                    searchLocation.isEmpty() ? null : searchLocation,
                    null,
                    null,
                    null
            );

            currentCall.enqueue(new Callback<JobListResponse>() {
                @Override
                public void onResponse(Call<JobListResponse> call, Response<JobListResponse> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            JobListResponse jobListResponse = response.body();
                            if (jobListResponse.getData() != null &&
                                    jobListResponse.getData().getResult() != null) {
                                List<Job> newJobs = jobListResponse.getData().getResult();

                                List<Job> activeJobs = newJobs.stream()
                                        .filter(job -> job != null && job.isActive())
                                        .collect(Collectors.toList());

                                currentJobs.clear();
                                if (activeJobs.isEmpty()) {
                                    searchResults.setValue(currentJobs);
                                    error.setValue("Không tìm thấy việc làm phù hợp");
                                } else {
                                    currentJobs.addAll(activeJobs);
                                    searchResults.setValue(currentJobs);
                                }
                            } else {
                                currentJobs.clear();
                                searchResults.setValue(currentJobs);
                                error.setValue("Không tìm thấy việc làm phù hợp");
                            }
                        } else {
                            currentJobs.clear();
                            searchResults.setValue(currentJobs);
                            error.setValue("Lỗi tải dữ liệu");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing response", e);
                        currentJobs.clear();
                        searchResults.setValue(currentJobs);
                        error.setValue("Lỗi xử lý dữ liệu");
                    } finally {
                        isLoading.setValue(false);
                    }
                }

                @Override
                public void onFailure(Call<JobListResponse> call, Throwable t) {
                    if (!call.isCanceled()) {
                        Log.e(TAG, "Network error", t);
                        currentJobs.clear();
                        searchResults.setValue(currentJobs);
                        error.setValue("Lỗi kết nối: " + t.getMessage());
                    }
                    isLoading.setValue(false);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error executing search", e);
            isLoading.setValue(false);
            currentJobs.clear();
            searchResults.setValue(currentJobs);
            error.setValue("Lỗi thực thi tìm kiếm");
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (currentCall != null) {
            currentCall.cancel();
        }
    }
}