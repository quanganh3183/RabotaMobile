package com.example.rabotamb.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rabotamb.R;
import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.data.models.auth.OtpResponse;
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

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";
    private static final int STEP_SEND_OTP = 1;
    private static final int STEP_VERIFY_OTP = 2;
    private static final int STEP_RESET_PASSWORD = 3;

    private TextInputEditText emailInput;
    private TextInputEditText otpInput;
    private TextInputEditText newPasswordInput;
    private TextInputEditText confirmPasswordInput;
    private MaterialButton actionButton;
    private ProgressBar progressBar;
    private View otpLayout;
    private View passwordLayout;
    private ImageButton backButton;

    private int currentStep = STEP_SEND_OTP;
    private String verifiedEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
        setupActionButton();

        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            emailInput.setText(email);
        }
    }

    private void initViews() {
        emailInput = findViewById(R.id.emailInput);
        otpInput = findViewById(R.id.otpInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        actionButton = findViewById(R.id.actionButton);
        progressBar = findViewById(R.id.progressBar);
        otpLayout = findViewById(R.id.otpLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        backButton.setOnClickListener(v -> onBackPressed());

    }

    @Override
    public void onBackPressed() {
        if (currentStep == STEP_VERIFY_OTP) {
            // If in OTP verification step, go back to email input
            updateUIForStep(STEP_SEND_OTP);
        } else if (currentStep == STEP_RESET_PASSWORD) {
            // If in reset password step, go back to OTP verification
            updateUIForStep(STEP_VERIFY_OTP);
        } else {
            // Otherwise, exit activity
            super.onBackPressed();
        }
    }

    private void setupActionButton() {
        actionButton.setOnClickListener(v -> {
            switch (currentStep) {
                case STEP_SEND_OTP:
                    sendOTP();
                    break;
                case STEP_VERIFY_OTP:
                    verifyOTP();
                    break;
                case STEP_RESET_PASSWORD:
                    resetPassword();
                    break;
            }
        });
    }

    private void updateUIForStep(int step) {
        currentStep = step;
        switch (step) {
            case STEP_SEND_OTP:
                emailInput.setEnabled(true);
                otpLayout.setVisibility(View.GONE);
                passwordLayout.setVisibility(View.GONE);
                actionButton.setText("Gửi mã OTP");
                break;
            case STEP_VERIFY_OTP:
                emailInput.setEnabled(false);
                otpLayout.setVisibility(View.VISIBLE);
                passwordLayout.setVisibility(View.GONE);
                actionButton.setText("Xác nhận OTP");
                break;
            case STEP_RESET_PASSWORD:
                otpInput.setEnabled(false);
                passwordLayout.setVisibility(View.VISIBLE);
                actionButton.setText("Đặt lại mật khẩu");
                break;
        }
    }

    private void sendOTP() {
        String email = emailInput.getText().toString().trim();

        if (validateEmail(email)) {
            showLoading();
            Map<String, String> emailMap = new HashMap<>();
            emailMap.put("email", email);

            ApiClient.getInstance().sendOTP(emailMap).enqueue(new Callback<OtpResponse>() {
                @Override
                public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null) {
                        OtpResponse otpResponse = response.body();
                        if (otpResponse.getStatusCode() == 200 || otpResponse.getStatusCode() == 201) {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Mã OTP đã được gửi đến email của bạn", Toast.LENGTH_LONG).show();
                            verifiedEmail = email;
                            updateUIForStep(STEP_VERIFY_OTP);
                        } else {
                            handleError(otpResponse.getMessage());
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                OtpResponse errorResponse = new Gson().fromJson(
                                        response.errorBody().string(), OtpResponse.class);
                                handleError(errorResponse.getMessage());
                            } else {
                                handleError("Không thể gửi mã OTP. Vui lòng thử lại!");
                            }
                        } catch (IOException e) {
                            handleError("Có lỗi xảy ra, vui lòng thử lại");
                        }
                    }
                }

                @Override
                public void onFailure(Call<OtpResponse> call, Throwable t) {
                    hideLoading();
                    handleNetworkError(t);
                }
            });
        }
    }

    private void verifyOTP() {
        String otp = otpInput.getText().toString().trim();

        if (validateOtp(otp)) {
            showLoading();
            Map<String, String> data = new HashMap<>();
            data.put("email", verifiedEmail);
            data.put("otp", otp);

            ApiClient.getInstance().checkOTP(data).enqueue(new Callback<OtpResponse>() {
                @Override
                public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null) {
                        OtpResponse otpResponse = response.body();
                        if (otpResponse.getStatusCode() == 201) {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Xác thực OTP thành công", Toast.LENGTH_SHORT).show();
                            updateUIForStep(STEP_RESET_PASSWORD);
                        } else {
                            handleError(otpResponse.getMessage());
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                OtpResponse errorResponse = new Gson().fromJson(
                                        response.errorBody().string(), OtpResponse.class);
                                handleError(errorResponse.getMessage());
                            } else {
                                handleError("Mã OTP không hợp lệ");
                            }
                        } catch (IOException e) {
                            handleError("Có lỗi xảy ra, vui lòng thử lại");
                        }
                    }
                }

                @Override
                public void onFailure(Call<OtpResponse> call, Throwable t) {
                    hideLoading();
                    handleNetworkError(t);
                }
            });
        }
    }

    private void resetPassword() {
        String newPassword = newPasswordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        String otp = otpInput.getText().toString().trim();

        if (validatePasswords(newPassword, confirmPassword)) {
            showLoading();
            Map<String, String> data = new HashMap<>();
            data.put("email", verifiedEmail);
            data.put("newPassword", newPassword);
            data.put("otp", otp);

            ApiClient.getInstance().forgetPassword(data).enqueue(new Callback<OtpResponse>() {
                @Override
                public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null) {
                        OtpResponse resetResponse = response.body();
                        if (resetResponse.getStatusCode() == 200 || resetResponse.getStatusCode() == 201) {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Đặt lại mật khẩu thành công", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            handleError(resetResponse.getMessage());
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                OtpResponse errorResponse = new Gson().fromJson(
                                        response.errorBody().string(), OtpResponse.class);
                                handleError(errorResponse.getMessage());
                            } else {
                                handleError("Không thể đặt lại mật khẩu. Vui lòng thử lại!");
                            }
                        } catch (IOException e) {
                            handleError("Có lỗi xảy ra, vui lòng thử lại");
                        }
                    }
                }

                @Override
                public void onFailure(Call<OtpResponse> call, Throwable t) {
                    hideLoading();
                    handleNetworkError(t);
                }
            });
        }
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            emailInput.setError("Vui lòng nhập email");
            return false;
        }

        // Kiểm tra định dạng email
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!email.matches(emailPattern)) {
            emailInput.setError("Email không hợp lệ");
            return false;
        }

        return true;
    }

    private boolean validateOtp(String otp) {
        if (otp.isEmpty()) {
            otpInput.setError("Vui lòng nhập mã OTP");
            return false;
        }
        if (otp.length() != 6) {
            otpInput.setError("Mã OTP phải có 6 chữ số");
            return false;
        }
        return true;
    }

    private boolean validatePasswords(String password, String confirmPassword) {
        if (password.isEmpty()) {
            newPasswordInput.setError("Vui lòng nhập mật khẩu mới");
            return false;
        }
        String errorMessage = getPasswordErrorMessage(password);
        if (!errorMessage.isEmpty()) {
            newPasswordInput.setError(errorMessage);
            return false;
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.setError("Vui lòng xác nhận mật khẩu");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Mật khẩu không khớp");
            return false;
        }

        return true;
    }

    private String getPasswordErrorMessage(String password) {
        StringBuilder errorMessage = new StringBuilder();

        if (password.length() < 6) {
            errorMessage.append("- Mật khẩu phải có ít nhất 6 ký tự\n");
        }

        if (!containsUpperCase(password)) {
            errorMessage.append("- Phải có ít nhất 1 chữ viết hoa\n");
        }

        if (!containsNumber(password)) {
            errorMessage.append("- Phải có ít nhất 1 chữ số\n");
        }

        if (!containsSpecialChar(password)) {
            errorMessage.append("- Phải có ít nhất 1 ký tự đặc biệt (!@#$%^&*(),.?\":{}|<>)");
        }

        return errorMessage.toString();
    }

    private boolean containsUpperCase(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsNumber(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSpecialChar(String password) {
        String specialCharacters = "!@#$%^&*(),.?\":{}|<>";
        for (char c : password.toCharArray()) {
            if (specialCharacters.contains(String.valueOf(c))) {
                return true;
            }
        }
        return false;
    }

    private void handleError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void handleNetworkError(Throwable t) {
        String errorMessage;
        if (!isNetworkAvailable()) {
            errorMessage = "Không có kết nối internet";
        } else if (t instanceof UnknownHostException) {
            errorMessage = "Không thể kết nối đến máy chủ";
        } else {
            errorMessage = "Lỗi kết nối: " + t.getMessage();
        }
        handleError(errorMessage);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        actionButton.setEnabled(false);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        actionButton.setEnabled(true);
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
}