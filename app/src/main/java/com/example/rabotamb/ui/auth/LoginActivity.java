package com.example.rabotamb.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rabotamb.ui.main.MainActivity;
import com.example.rabotamb.R;
import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.data.models.auth.LoginRequest;
import com.example.rabotamb.data.models.auth.LoginResponse;
import com.example.rabotamb.data.models.auth.RegisterResponse;
import com.example.rabotamb.utils.UserPreferences;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private ProgressBar progressBar;
    private TextView registerLink;
    private TextView forgotPasswordLink;
    private ImageButton backButton;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userPreferences = new UserPreferences(this);
        initViews();
        setupLoginButton();
        setupRegisterLink();
        setupForgotPasswordLink();
    }

    private void initViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
        progressBar = findViewById(R.id.progressBar);
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink);
    }

    private void setupRegisterLink() {
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void setupForgotPasswordLink() {
        forgotPasswordLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            String email = emailInput.getText().toString().trim();
            if (!email.isEmpty()) {
                intent.putExtra("email", email);
            }
            startActivity(intent);
        });
    }

    private void setupLoginButton() {
        loginButton.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (validateInput(email, password)) {
            showLoading();
            LoginRequest request = new LoginRequest(email, password);

            ApiClient.getInstance().login(request).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null) {
                        handleLoginResponse(response.body(), email);
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                Log.d(TAG, "Error body: " + errorBody);
                                LoginResponse errorResponse = new Gson().fromJson(errorBody, LoginResponse.class);
                                handleLoginResponse(errorResponse, email);
                            } else {
                                handleLoginError("Đăng nhập thất bại. Vui lòng thử lại!");
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error reading error body", e);
                            handleLoginError("Đăng nhập thất bại. Vui lòng thử lại!");
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    hideLoading();
                    Log.e(TAG, "Network error", t);
                    String errorMessage;
                    if (!isNetworkAvailable()) {
                        errorMessage = "Không có kết nối internet";
                    } else if (t instanceof UnknownHostException) {
                        errorMessage = "Không thể kết nối đến máy chủ";
                    } else {
                        errorMessage = "Lỗi kết nối: " + t.getMessage();
                    }
                    handleLoginError(errorMessage);
                }
            });
        }
    }

    private void handleLoginResponse(LoginResponse response, String email) {
        // Kiểm tra level VERIFY_REQUIRED trước
        if ("VERIFY_REQUIRED".equals(response.getLevel())) {
            // Gửi OTP trước khi chuyển trang
            sendOTP(email, new OTPCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(LoginActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, OtpVerificationActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }

                @Override
                public void onError(String errorMessage) {
                    handleLoginError(errorMessage);
                }
            });
            return;
        }

        // Xử lý các trường hợp khác
        if (response.getStatusCode() == 201) {
            if (response.getData() != null && response.getData().getAccessToken() != null) {
                String accessToken = response.getData().getAccessToken();
                saveToken(accessToken);

                // Lưu thông tin user nếu cần
                if (response.getData().getUser() != null) {
                    saveUserInfo(response.getData().getUser());
                }

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                handleLoginError("Token không hợp lệ");
            }
        } else {
            handleLoginError(response.getMessage());
        }
    }

    private interface OTPCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }

    private void sendOTP(String email, OTPCallback callback) {
        Map<String, String> emailMap = new HashMap<>();
        emailMap.put("email", email);

        showLoading();
        ApiClient.getInstance().resendOtp(emailMap).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse otpResponse = response.body();
                    Log.d(TAG, "OTP Response: " + otpResponse.getMessage());

                    if (otpResponse.getStatusCode() == 200 || otpResponse.getStatusCode() == 201) {
                        callback.onSuccess(otpResponse.getMessage());
                    } else {
                        callback.onError(otpResponse.getMessage());
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Send OTP error body: " + errorBody);
                            RegisterResponse errorResponse =
                                    new Gson().fromJson(errorBody, RegisterResponse.class);
                            callback.onError(errorResponse.getMessage());
                        } else {
                            callback.onError("Không thể gửi mã OTP");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing OTP error response", e);
                        callback.onError("Đã xảy ra lỗi khi gửi mã OTP");
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "Send OTP network error", t);
                String errorMessage;
                if (!isNetworkAvailable()) {
                    errorMessage = "Không có kết nối internet";
                } else if (t instanceof UnknownHostException) {
                    errorMessage = "Không thể kết nối đến máy chủ";
                } else {
                    errorMessage = "Lỗi kết nối: " + t.getMessage();
                }
                callback.onError(errorMessage);
            }
        });
    }

    private void saveUserInfo(LoginResponse.User user) {
        UserPreferences.UserInfo userInfo = new UserPreferences.UserInfo();

        userInfo.setId(user.getId());
        userInfo.setName(user.getName());
        userInfo.setEmail(user.getEmail());
        userInfo.setAge(user.getAge());
        userInfo.setGender(user.getGender());
        userInfo.setAddress(user.getAddress());
        userInfo.setActived(user.isActived());
        userInfo.setDeleted(user.isDeleted());
        userInfo.setRole(user.getRole());
        userInfo.setPermissions(user.getPermissions());

        // Log thông tin company trước khi lưu
        if (user.getCompany() != null) {
            Log.d(TAG, "Company before saving - ID: " + user.getCompany().get_id());
            Log.d(TAG, "Company before saving - Name: " + user.getCompany().getName());
            Log.d(TAG, "Full Company object: " + new Gson().toJson(user.getCompany()));
        }

        // Set company
        userInfo.setCompany(user.getCompany());

        // Log toàn bộ userInfo trước khi lưu
        Log.d(TAG, "Full UserInfo before saving: " + new Gson().toJson(userInfo));

        userPreferences.saveUserInfo(userInfo);

        // Kiểm tra lại thông tin đã lưu
        UserPreferences.UserInfo savedInfo = userPreferences.getUserInfo();
        Log.d(TAG, "Saved UserInfo: " + new Gson().toJson(savedInfo));
        if (savedInfo.getCompany() != null) {
            Log.d(TAG, "Saved Company ID: " + savedInfo.getCompany().get_id());
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            emailInput.setError("Vui lòng nhập email");
            return false;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Vui lòng nhập mật khẩu");
            return false;
        }
        return true;
    }

    private void handleLoginError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        emailInput.setEnabled(false);
        passwordInput.setEnabled(false);
        registerLink.setEnabled(false);
        forgotPasswordLink.setEnabled(false);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);
        emailInput.setEnabled(true);
        passwordInput.setEnabled(true);
        registerLink.setEnabled(true);
        forgotPasswordLink.setEnabled(true);
    }

    private void saveToken(String token) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        prefs.edit().putString("token", token).apply();
    }
}