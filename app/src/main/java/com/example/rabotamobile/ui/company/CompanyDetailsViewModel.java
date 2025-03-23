package com.example.rabotamb.ui.company;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.api.ApiService;
import com.example.rabotamb.data.models.company.Company;
import com.example.rabotamb.data.models.company.CompanyDetailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompanyDetailsViewModel extends ViewModel {
    private static final String TAG = "CompanyDetailsViewModel";

    private final MutableLiveData<Company> companyDetails = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final ApiService apiService;

    public CompanyDetailsViewModel() {
        apiService = ApiClient.getInstance();
    }

    public LiveData<Company> getCompanyDetails() { return companyDetails; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getError() { return error; }

    public void loadCompanyDetails(String companyId) {
        isLoading.setValue(true);
        error.setValue(null);

        try {
            apiService.getCompanyDetails(companyId).enqueue(new Callback<CompanyDetailResponse>() {
                @Override
                public void onResponse(Call<CompanyDetailResponse> call,
                                       Response<CompanyDetailResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CompanyDetailResponse companyResponse = response.body();
                        if (companyResponse.getData() != null) {
                            companyDetails.setValue(companyResponse.getData());
                        } else {
                            error.setValue("Không tìm thấy thông tin công ty");
                        }
                    } else {
                        error.setValue("Lỗi tải dữ liệu");
                    }
                    isLoading.setValue(false);
                }

                @Override
                public void onFailure(Call<CompanyDetailResponse> call, Throwable t) {
                    Log.e(TAG, "Error loading company details", t);
                    error.setValue("Lỗi kết nối: " + t.getMessage());
                    isLoading.setValue(false);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error executing company details request", e);
            error.setValue("Lỗi thực thi");
            isLoading.setValue(false);
        }
    }
}