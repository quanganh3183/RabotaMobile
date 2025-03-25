package com.example.rabotamb.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rabotamb.R;
import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.data.models.auth.RegisterRequest;
import com.example.rabotamb.data.models.auth.RegisterResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private MaterialButton registerButton;
    private TextView loginLink;
    private ProgressBar progressBar;
    private TextInputEditText ageInput;
    private TextInputEditText addressInput;
    private RadioGroup genderGroup;
    private static final String GENDER_MALE = "Male";
    private static final String GENDER_FEMALE = "Female";
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupInputValidations();
        setupRegisterButton();
        setupLoginLink();
    }

    private void initViews() {
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        ageInput = findViewById(R.id.ageInput);
        addressInput = findViewById(R.id.addressInput);
        genderGroup = findViewById(R.id.genderGroup);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupInputValidations() {
        // Validation cho tuổi khi đang nhập
        ageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    try {
                        int age = Integer.parseInt(s.toString());
                        if (age <= 0) {
                            ageInput.setError("Tuổi phải là số dương");
                        }
                    } catch (NumberFormatException e) {
                        ageInput.setError("Tuổi không hợp lệ");
                    }
                }
            }
        });

        // Validation cho email khi đang nhập
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() && !isValidEmail(s.toString())) {
                    emailInput.setError("Email không hợp lệ");
                }
            }
        });

        // Validation cho mật khẩu khi đang nhập
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    validatePassword(s.toString());
                }
            }
        });
    }

    private void setupRegisterButton() {
        registerButton.setOnClickListener(v -> performRegister());
    }

    private void setupLoginLink() {
        loginLink.setOnClickListener(v -> finish());
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePassword(String password) {
        boolean isValid = true;
        if (password.length() < 6) {
            passwordInput.setError("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        }
        if (!password.matches(".*[A-Z].*")) {
            passwordInput.setError("Mật khẩu phải có ít nhất 1 chữ in hoa");
            isValid = false;
        }
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            passwordInput.setError("Mật khẩu phải có ít nhất 1 ký tự đặc biệt");
            isValid = false;
        }
        return isValid;
    }

    private boolean validateInput(String name, String email, String password,
                                  String confirmPassword, String age, String address) {
        boolean isValid = true;

        // Validate name
        if (name.isEmpty()) {
            nameInput.setError("Vui lòng nhập họ tên");
            isValid = false;
        }

        // Validate email
        if (email.isEmpty()) {
            emailInput.setError("Vui lòng nhập email");
            isValid = false;
        } else if (!isValidEmail(email)) {
            emailInput.setError("Email không hợp lệ");
            isValid = false;
        }

        // Validate password
        if (password.isEmpty()) {
            passwordInput.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        } else if (!validatePassword(password)) {
            isValid = false;
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.setError("Vui lòng xác nhận mật khẩu");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Mật khẩu không khớp");
            isValid = false;
        }

        // Validate age
        if (age.isEmpty()) {
            ageInput.setError("Vui lòng nhập tuổi");
            isValid = false;
        } else {
            try {
                int ageValue = Integer.parseInt(age);
                if (ageValue <= 0) {
                    ageInput.setError("Tuổi phải là số dương");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                ageInput.setError("Tuổi không hợp lệ");
                isValid = false;
            }
        }

        // Validate address
        if (address.isEmpty()) {
            addressInput.setError("Vui lòng nhập địa chỉ");
            isValid = false;
        }

        return isValid;
    }

    private void performRegister() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String ageStr = ageInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String gender = genderGroup.getCheckedRadioButtonId() == R.id.maleRadio ?
                GENDER_MALE : GENDER_FEMALE;

        if (validateInput(name, email, password, confirmPassword, ageStr, address)) {
            showLoading();

            int age = Integer.parseInt(ageStr);
            RegisterRequest request = new RegisterRequest(name, email, password, age, gender, address);

            ApiClient.getInstance().register(request).enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null) {
                        RegisterResponse registerResponse = response.body();

                        Log.d(TAG, "Response Code: " + response.code());
                        Log.d(TAG, "Status Code: " + registerResponse.getStatusCode());
                        Log.d(TAG, "Message: " + registerResponse.getMessage());

                        if (registerResponse.getStatusCode() == 201) {
                            Toast.makeText(RegisterActivity.this,
                                    "Đăng ký thành công! Vui lòng kiểm tra email để xác thực",
                                    Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(RegisterActivity.this, OtpVerificationActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ?
                                    response.errorBody().string() : "Unknown error";
                            Log.e(TAG, "Error Body: " + errorBody);
                            Toast.makeText(RegisterActivity.this,
                                    "Đăng ký thất bại: " + errorBody, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Log.e(TAG, "Error reading error body", e);
                            Toast.makeText(RegisterActivity.this,
                                    "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    hideLoading();
                    Log.e(TAG, "Network Error", t);
                    Toast.makeText(RegisterActivity.this,
                            "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        registerButton.setEnabled(false);
        nameInput.setEnabled(false);
        emailInput.setEnabled(false);
        passwordInput.setEnabled(false);
        confirmPasswordInput.setEnabled(false);
        ageInput.setEnabled(false);
        addressInput.setEnabled(false);
        genderGroup.setEnabled(false);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        registerButton.setEnabled(true);
        nameInput.setEnabled(true);
        emailInput.setEnabled(true);
        passwordInput.setEnabled(true);
        confirmPasswordInput.setEnabled(true);
        ageInput.setEnabled(true);
        addressInput.setEnabled(true);
        genderGroup.setEnabled(true);
    }
}