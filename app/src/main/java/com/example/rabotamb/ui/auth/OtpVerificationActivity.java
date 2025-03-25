package com.example.rabotamb.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rabotamb.R;
import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.data.models.auth.OtpVerificationRequest;
import com.example.rabotamb.data.models.auth.OtpVerificationResponse;
import com.example.rabotamb.data.models.auth.RegisterResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationActivity extends AppCompatActivity {
    private TextInputEditText otpInput;
    private MaterialButton verifyButton;
    private TextView resendButton;
    private TextView countdownText;
    private ProgressBar progressBar;
    private String email;
    private static final String TAG = "OtpVerification";
    private static final int COUNTDOWN_TIME = 60; // seconds
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        email = getIntent().getStringExtra("email");
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupVerifyButton();
        setupResendButton();
        startCountdownTimer();
    }

    private void initViews() {
        otpInput = findViewById(R.id.otpInput);
        verifyButton = findViewById(R.id.verifyButton);
        resendButton = findViewById(R.id.resendButton);
        countdownText = findViewById(R.id.countdownText);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupVerifyButton() {
        verifyButton.setOnClickListener(v -> performOtpVerification());
    }

    private void setupResendButton() {
        resendButton.setOnClickListener(v -> {
            if (!isTimerRunning) {
                resendOtp();
            }
        });
    }

    private void startCountdownTimer() {
        isTimerRunning = true;
        resendButton.setEnabled(false);
        resendButton.setTextColor(getResources().getColor(android.R.color.darker_gray));

        countDownTimer = new CountDownTimer(COUNTDOWN_TIME * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                countdownText.setText(String.format("(%ds)", seconds));
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                resendButton.setEnabled(true);
                resendButton.setTextColor(getResources().getColor(R.color.primary_color));
                countdownText.setText("");
            }
        }.start();
    }

    private void resendOtp() {
        showLoading();
        Map<String, String> emailMap = new HashMap<>();
        emailMap.put("email", email);

        ApiClient.getInstance().resendOtp(emailMap).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse resendResponse = response.body();
                    if (resendResponse.getStatusCode() == 200 || resendResponse.getStatusCode() == 201) {
                        Toast.makeText(OtpVerificationActivity.this,
                                "Đã gửi lại mã OTP. Vui lòng kiểm tra email.",
                                Toast.LENGTH_SHORT).show();
                        startCountdownTimer();
                    } else {
                        handleError(resendResponse.getMessage());
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            RegisterResponse errorResponse =
                                    new Gson().fromJson(errorBody, RegisterResponse.class);
                            handleError(errorResponse.getMessage());
                        } else {
                            handleError("Không thể gửi lại mã OTP");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                        handleError("Đã xảy ra lỗi khi gửi lại OTP");
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "Network Error", t);
                handleError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void performOtpVerification() {
        String otp = otpInput.getText().toString().trim();

        if (validateInput(otp)) {
            showLoading();

            Log.d(TAG, "Sending OTP verification request:");
            Log.d(TAG, "Email: " + email);
            Log.d(TAG, "OTP: " + otp);

            OtpVerificationRequest request = new OtpVerificationRequest(email, otp);

            ApiClient.getInstance().verifyOtp(request).enqueue(new Callback<OtpVerificationResponse>() {
                @Override
                public void onResponse(Call<OtpVerificationResponse> call,
                                       Response<OtpVerificationResponse> response) {
                    hideLoading();
                    Log.d(TAG, "Response code: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        OtpVerificationResponse verificationResponse = response.body();
                        Log.d(TAG, "Success response - Status code: " + verificationResponse.getStatusCode());
                        Log.d(TAG, "Success response - Message: " + verificationResponse.getMessage());

                        if (verificationResponse.getStatusCode() == 200 ||
                                verificationResponse.getStatusCode() == 201) {
                            Toast.makeText(OtpVerificationActivity.this,
                                    "Xác thực thành công!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(OtpVerificationActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            handleError(verificationResponse.getMessage());
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                Log.d(TAG, "Raw error body: " + errorBody);

                                try {
                                    OtpVerificationResponse errorResponse =
                                            new Gson().fromJson(errorBody, OtpVerificationResponse.class);
                                    handleError(errorResponse.getMessage());
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing direct JSON", e);
                                    byte[] bytes = errorBody.getBytes(StandardCharsets.ISO_8859_1);
                                    String utf8String = new String(bytes, StandardCharsets.UTF_8);
                                    OtpVerificationResponse errorResponse =
                                            new Gson().fromJson(utf8String, OtpVerificationResponse.class);
                                    handleError(errorResponse.getMessage());
                                }
                            } else {
                                handleError("Xác thực thất bại");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error handling response", e);
                            handleError("Đã xảy ra lỗi khi xác thực OTP");
                        }
                    }
                }

                @Override
                public void onFailure(Call<OtpVerificationResponse> call, Throwable t) {
                    hideLoading();
                    Log.e(TAG, "Network Error", t);
                    handleError("Lỗi kết nối: " + t.getMessage());
                }
            });
        }
    }

    private void handleError(String message) {
        if (message != null && !message.isEmpty()) {
            Toast.makeText(OtpVerificationActivity.this, message, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(OtpVerificationActivity.this,
                    "Đã xảy ra lỗi khi xác thực OTP", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateInput(String otp) {
        if (otp.isEmpty()) {
            otpInput.setError("Vui lòng nhập mã OTP");
            return false;
        }
        if (otp.length() != 6) {
            otpInput.setError("Mã OTP phải có 6 số");
            return false;
        }
        return true;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        verifyButton.setEnabled(false);
        resendButton.setEnabled(false);
        otpInput.setEnabled(false);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        verifyButton.setEnabled(true);
        otpInput.setEnabled(true);
        if (!isTimerRunning) {
            resendButton.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}