package com.example.rabotamobile.ui.profile;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.api.ApiService;
import com.example.rabotamb.data.models.auth.ChangePasswordRequest;
import com.example.rabotamb.utils.UserPreferences;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordViewModel extends AndroidViewModel {
    private static final String TAG = "ChangePasswordViewModel";
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    private final ApiService apiService;
    private final UserPreferences userPreferences;

    public ChangePasswordViewModel(Application application) {
        super(application);
        apiService = ApiClient.getInstance();
        userPreferences = new UserPreferences(application);
    }

    public void changePassword(String oldPassword, String newPassword) {
        isLoading.setValue(true);

        String token = "Bearer " + userPreferences.getToken();
        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword);

        Log.d(TAG, "Attempting to change password");

        apiService.changePassword(token, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                isLoading.setValue(false);

                if (response.isSuccessful()) {
                    Log.d(TAG, "Password changed successfully");
                    success.setValue(true);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error response: " + errorBody);
                            JsonObject errorJson = new Gson().fromJson(errorBody, JsonObject.class);
                            String message = errorJson.has("message") ?
                                    errorJson.get("message").getAsString() :
                                    "Có lỗi xảy ra";
                            error.setValue(message);
                        } else {
                            Log.e(TAG, "Error response body is null");
                            error.setValue("Có lỗi xảy ra");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                        error.setValue("Có lỗi xảy ra");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Network call failed", t);
                isLoading.setValue(false);
                error.setValue("Không thể kết nối đến server");
            }
        });
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }
}