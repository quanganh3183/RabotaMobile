package com.example.rabotamobile.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.rabotamobile.R;
import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.api.ApiService;
import com.example.rabotamb.data.models.user.UpdateProfileRequest;
import com.example.rabotamb.data.models.user.UpdateProfileResponse;
import com.example.rabotamb.ui.auth.LoginActivity;
import com.example.rabotamb.utils.UserPreferences;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {
    private UserPreferences userPreferences;
    private TextInputEditText nameInput, emailInput, ageInput, addressInput;
    private AutoCompleteTextView genderInput;
    private MaterialButton saveButton;
    private CircularProgressIndicator progressIndicator;
    private UserPreferences.UserInfo currentUserInfo;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        userPreferences = new UserPreferences(this);
        apiService = ApiClient.getInstance();
        currentUserInfo = userPreferences.getUserInfo();

        initViews();
        setupToolbar();
        loadCurrentUserInfo();
        setupSaveButton();
    }

    private void initViews() {
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        ageInput = findViewById(R.id.ageInput);
        genderInput = findViewById(R.id.genderInput);
        addressInput = findViewById(R.id.addressInput);
        saveButton = findViewById(R.id.saveButton);
        progressIndicator = findViewById(R.id.progressIndicator);

        emailInput.setEnabled(false);
        emailInput.setFocusable(false);
        emailInput.setFocusableInTouchMode(false);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cập nhật thông tin");
        }
    }

    private void loadCurrentUserInfo() {
        if (currentUserInfo != null) {
            nameInput.setText(currentUserInfo.getName());
            emailInput.setText(currentUserInfo.getEmail());
            ageInput.setText(String.valueOf(currentUserInfo.getAge()));
            genderInput.setText(currentUserInfo.getGender());
            addressInput.setText(currentUserInfo.getAddress());
        }
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> updateProfile());
    }

    private void updateProfile() {
        String name = nameInput.getText().toString().trim();
        String ageStr = ageInput.getText().toString().trim();
        String gender = genderInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();

        if (validateInput(name, ageStr, gender, address)) {
            int age = Integer.parseInt(ageStr);

            setLoading(true);

            UpdateProfileRequest request = new UpdateProfileRequest(name, age, gender, address);

            String userId = currentUserInfo.getId();
            String token = userPreferences.getToken();

            // Log để debug
            Log.d("UpdateProfile", "userId: " + userId);
            Log.d("UpdateProfile", "token: " + token);
            Log.d("UpdateProfile", "request: " + new Gson().toJson(request));

            apiService.updateProfile(userId, token, request).enqueue(new Callback<UpdateProfileResponse>() {
                @Override
                public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                    setLoading(false);

                    if (response.isSuccessful() && response.body() != null) {
                        UpdateProfileResponse updateResponse = response.body();
                        if (updateResponse.isSuccess()) {
                            UpdateProfileResponse.UserData userData = updateResponse.getData();

                            // Chỉ cập nhật các trường có thể thay đổi
                            currentUserInfo.setName(userData.getName());
                            currentUserInfo.setAge(userData.getAge());
                            currentUserInfo.setGender(userData.getGender());
                            currentUserInfo.setAddress(userData.getAddress());

                            // Các trường khác giữ nguyên giá trị cũ
                            userPreferences.saveUserInfo(currentUserInfo);

                            Toast.makeText(UpdateProfileActivity.this,
                                    "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();

                            // Gửi broadcast để thông báo profile đã được cập nhật
                            Intent broadcastIntent = new Intent("PROFILE_UPDATED");
                            sendBroadcast(broadcastIntent);

                            // Quay về màn hình profile
                            finish();
                        } else {
                            Toast.makeText(UpdateProfileActivity.this,
                                    updateResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        if (response.code() == 401) {
                            Toast.makeText(UpdateProfileActivity.this,
                                    "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                            userPreferences.clearAll();
                            Intent intent = new Intent(UpdateProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            return;
                        }

                        if (response.errorBody() != null) {
                            try {
                                String errorBody = response.errorBody().string();
                                Log.e("UpdateProfile", "Error Body: " + errorBody);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        Toast.makeText(UpdateProfileActivity.this,
                                "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                    setLoading(false);
                    Log.e("UpdateProfile", "Error: " + t.getMessage());
                    Toast.makeText(UpdateProfileActivity.this,
                            "Lỗi kết nối, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setLoading(boolean isLoading) {
        progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        saveButton.setEnabled(!isLoading);
    }

    private boolean validateInput(String name, String age, String gender, String address) {
        if (name.isEmpty()) {
            nameInput.setError("Vui lòng nhập họ tên");
            return false;
        }

        if (age.isEmpty()) {
            ageInput.setError("Vui lòng nhập tuổi");
            return false;
        }

        try {
            int ageValue = Integer.parseInt(age);
            if (ageValue < 0 || ageValue > 120) {
                ageInput.setError("Tuổi không hợp lệ");
                return false;
            }
        } catch (NumberFormatException e) {
            ageInput.setError("Tuổi phải là số");
            return false;
        }

        if (gender.isEmpty()) {
            genderInput.setError("Vui lòng nhập giới tính");
            return false;
        }

        if (address.isEmpty()) {
            addressInput.setError("Vui lòng nhập địa chỉ");
            return false;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}