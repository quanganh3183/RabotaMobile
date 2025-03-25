package com.example.rabotamb.ui.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.data.models.job.JobHr;
import com.example.rabotamb.data.models.job.JobHrDetailResponse;
import com.example.rabotamb.data.models.job.JobsHrResponse;
import com.example.rabotamb.data.models.skill.Skill;
import com.example.rabotamb.utils.UserPreferences;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobHrViewModel extends ViewModel {
    private static final String TAG = "JobHrViewModel";
    private Context context;
    private final MutableLiveData<List<JobHr>> jobs = new MutableLiveData<>();
    private final MutableLiveData<JobHr> selectedJob = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDeleted = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isUpdated = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCreated = new MutableLiveData<>();

    public JobHrViewModel(Context context) {
        this.context = context;
    }

    public void clearError() {
        error.setValue(null);
    }

    public void resetUpdateStatus() {
        isUpdated.setValue(false);
    }

    public void createJob(JobHr newJob) {
        isLoading.setValue(true);

        UserPreferences userPreferences = new UserPreferences(context);
        String token = userPreferences.getToken();
        UserPreferences.UserInfo userInfo = userPreferences.getUserInfo();

        Log.d(TAG, "UserInfo: " + new Gson().toJson(userInfo));
        if (userInfo != null && userInfo.getCompany() != null) {
            Log.d(TAG, "Company info: " + new Gson().toJson(userInfo.getCompany()));
        }

        if (token == null || token.isEmpty()) {
            error.setValue("Không tìm thấy thông tin đăng nhập");
            isLoading.setValue(false);
            return;
        }

        if (userInfo == null || userInfo.getCompany() == null) {
            error.setValue("Không tìm thấy thông tin công ty");
            isLoading.setValue(false);
            return;
        }

        Map<String, Object> jobData = new HashMap<>();
        jobData.put("name", newJob.getName());
        // Chuyển đổi List<Skill> thành List<String>
        List<String> skillIds = newJob.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
        jobData.put("skills", skillIds);
        jobData.put("location", newJob.getLocation());
        jobData.put("quantity", newJob.getQuantity());
        jobData.put("salary", newJob.getSalary());
        jobData.put("level", newJob.getLevel());
        jobData.put("startDate", newJob.getStartDate());
        jobData.put("endDate", newJob.getEndDate());
        jobData.put("description", newJob.getDescription());
        jobData.put("isActive", true);
        if (newJob.getCompany() != null) {
            jobData.put("company", newJob.getCompany().get_id());
        }

        Log.d(TAG, "Request data: " + new Gson().toJson(jobData));

        ApiClient.getInstance().createJob(jobData, token)
                .enqueue(new Callback<JobHrDetailResponse>() {
                    @Override
                    public void onResponse(Call<JobHrDetailResponse> call, Response<JobHrDetailResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Success response: " + new Gson().toJson(response.body()));
                            isCreated.setValue(true);
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ?
                                        response.errorBody().string() : "Unknown error";
                                Log.e(TAG, "Error response: " + errorBody);
                                error.setValue("Không thể tạo việc làm: " + errorBody);
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body", e);
                                error.setValue("Có lỗi xảy ra: " + e.getMessage());
                            }
                        }
                        isLoading.setValue(false);
                    }

                    @Override
                    public void onFailure(Call<JobHrDetailResponse> call, Throwable t) {
                        Log.e(TAG, "Network error", t);
                        error.setValue("Lỗi kết nối: " + t.getMessage());
                        isLoading.setValue(false);
                    }
                });
    }

    public void loadJobs(String companyId) {
        isLoading.setValue(true);

        UserPreferences userPreferences = new UserPreferences(context);
        String token = userPreferences.getToken();

        if (token == null || token.isEmpty()) {
            error.setValue("Không tìm thấy thông tin đăng nhập");
            isLoading.setValue(false);
            return;
        }

        Log.d(TAG, "Loading jobs for company: " + companyId);
        Log.d(TAG, "Token: " + token);

        ApiClient.getInstance().getJobsByCompany(companyId, 1, 1000)
                .enqueue(new Callback<JobsHrResponse>() {
                    @Override
                    public void onResponse(Call<JobsHrResponse> call, Response<JobsHrResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JobsHrResponse jobsResponse = response.body();
                            Log.d(TAG, "Response body: " + new Gson().toJson(jobsResponse));

                            List<JobHr> jobsList = jobsResponse.getJobs();
                            if (jobsList != null) {
                                Log.d(TAG, "Loaded jobs: " + jobsList.size());
                                jobs.setValue(jobsList);
                            } else {
                                Log.d(TAG, "Jobs list is null");
                                jobs.setValue(new ArrayList<>());
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ?
                                        response.errorBody().string() : "Unknown error";
                                Log.e(TAG, "Error response: " + errorBody);
                                if (response.code() == 401) {
                                    error.setValue("Phiên đăng nhập đã hết hạn");
                                    userPreferences.clearAll();
                                } else {
                                    error.setValue("Không thể tải danh sách việc làm: " + errorBody);
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "Error reading error body", e);
                                error.setValue("Có lỗi xảy ra: " + e.getMessage());
                            }
                        }
                        isLoading.setValue(false);
                    }

                    @Override
                    public void onFailure(Call<JobsHrResponse> call, Throwable t) {
                        Log.e(TAG, "Network error", t);
                        error.setValue("Lỗi kết nối: " + t.getMessage());
                        isLoading.setValue(false);
                    }
                });
    }

    public void loadJobDetail(String jobId) {
        if (selectedJob.getValue() == null) {
            isLoading.setValue(true);

            UserPreferences userPreferences = new UserPreferences(context);
            String token = userPreferences.getToken();

            if (token == null || token.isEmpty()) {
                error.setValue("Không tìm thấy thông tin đăng nhập");
                isLoading.setValue(false);
                return;
            }

            ApiClient.getInstance().getJobHrDetail(jobId)
                    .enqueue(new Callback<JobHrDetailResponse>() {
                        @Override
                        public void onResponse(Call<JobHrDetailResponse> call, Response<JobHrDetailResponse> response) {
                            if (response.isSuccessful() && response.body() != null
                                    && response.body().getData() != null) {
                                selectedJob.setValue(response.body().getData());
                            } else {
                                try {
                                    String errorBody = response.errorBody() != null ?
                                            response.errorBody().string() : "Unknown error";
                                    error.setValue("Không thể tải thông tin việc làm: " + errorBody);
                                } catch (IOException e) {
                                    error.setValue("Có lỗi xảy ra: " + e.getMessage());
                                }
                            }
                            isLoading.setValue(false);
                        }

                        @Override
                        public void onFailure(Call<JobHrDetailResponse> call, Throwable t) {
                            error.setValue("Lỗi kết nối: " + t.getMessage());
                            isLoading.setValue(false);
                        }
                    });
        }
    }

    public void updateJob(String jobId, JobHr updatedJob) {
        if (isLoading.getValue() != null && isLoading.getValue()) {
            return;
        }
        isLoading.setValue(true);

        UserPreferences userPreferences = new UserPreferences(context);
        String token = userPreferences.getToken();

        if (token == null || token.isEmpty()) {
            error.setValue("Không tìm thấy thông tin đăng nhập");
            isLoading.setValue(false);
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", updatedJob.getName());
        // Chuyển đổi List<Skill> thành List<String>
        List<String> skillIds = updatedJob.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
        updates.put("skills", skillIds);
        updates.put("location", updatedJob.getLocation());
        updates.put("quantity", updatedJob.getQuantity());
        updates.put("salary", updatedJob.getSalary());
        updates.put("level", updatedJob.getLevel());
        updates.put("startDate", updatedJob.getStartDate());
        updates.put("endDate", updatedJob.getEndDate());
        updates.put("description", updatedJob.getDescription());

        ApiClient.getInstance().updateJob(jobId, updates, token)
                .enqueue(new Callback<JobHrDetailResponse>() {
                    @Override
                    public void onResponse(Call<JobHrDetailResponse> call, Response<JobHrDetailResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            isUpdated.setValue(true);
                            selectedJob.setValue(response.body().getData());
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ?
                                        response.errorBody().string() : "Unknown error";
                                if (response.code() == 401) {
                                    error.setValue("Phiên đăng nhập đã hết hạn");
                                    userPreferences.clearAll();
                                } else {
                                    error.setValue("Không thể cập nhật thông tin việc làm: " + errorBody);
                                }
                            } catch (Exception e) {
                                error.setValue("Có lỗi xảy ra: " + e.getMessage());
                            }
                        }
                        isLoading.setValue(false);
                    }

                    @Override
                    public void onFailure(Call<JobHrDetailResponse> call, Throwable t) {
                        error.setValue("Lỗi kết nối: " + t.getMessage());
                        isLoading.setValue(false);
                    }
                });
    }

    public void deleteJob(String jobId) {
        isLoading.setValue(true);

        UserPreferences userPreferences = new UserPreferences(context);
        String token = userPreferences.getToken();

        if (token == null || token.isEmpty()) {
            error.setValue("Không tìm thấy thông tin đăng nhập");
            isLoading.setValue(false);
            return;
        }

        ApiClient.getInstance().deleteJob(jobId, token)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Job deleted successfully");
                            isDeleted.setValue(true);
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ?
                                        response.errorBody().string() : "Unknown error";
                                if (response.code() == 401) {
                                    error.setValue("Phiên đăng nhập đã hết hạn");
                                    userPreferences.clearAll();
                                } else {
                                    error.setValue("Không thể xóa việc làm: " + errorBody);
                                }
                            } catch (IOException e) {
                                error.setValue("Có lỗi xảy ra: " + e.getMessage());
                            }
                        }
                        isLoading.setValue(false);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "Delete job failed", t);
                        error.setValue("Lỗi kết nối: " + t.getMessage());
                        isLoading.setValue(false);
                    }
                });
    }

    public LiveData<Boolean> getIsCreated() {
        return isCreated;
    }

    public LiveData<List<JobHr>> getJobs() {
        return jobs;
    }

    public LiveData<JobHr> getSelectedJob() {
        return selectedJob;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsDeleted() {
        return isDeleted;
    }

    public LiveData<Boolean> getIsUpdated() {
        return isUpdated;
    }
}