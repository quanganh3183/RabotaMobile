package com.example.rabotamobile.ui.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.rabotamobile.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputLayout oldPasswordLayout, newPasswordLayout, confirmPasswordLayout;
    private TextInputEditText oldPasswordInput, newPasswordInput, confirmPasswordInput;
    private MaterialButton changePasswordButton;
    private ProgressBar progressBar;
    private ChangePasswordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();
        setupToolbar();
        setupViewModel();
        setupButton();
        setupTextWatchers();
    }

    private void initViews() {
        oldPasswordLayout = findViewById(R.id.oldPasswordLayout);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);

        oldPasswordInput = findViewById(R.id.oldPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);

        changePasswordButton = findViewById(R.id.changePasswordButton);
        progressBar = findViewById(R.id.progressBar);

        // Set initial helper text
        newPasswordLayout.setHelperText("Mật khẩu phải có ít nhất 6 ký tự, 1 chữ hoa, 1 chữ số và 1 ký tự đặc biệt");
        newPasswordLayout.setHelperTextEnabled(true);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Đổi mật khẩu");
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(ChangePasswordViewModel.class);

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            changePasswordButton.setEnabled(!isLoading);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
            }
        });

        viewModel.getSuccess().observe(this, success -> {
            if (success) {
                Snackbar.make(changePasswordButton, "Đổi mật khẩu thành công", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupButton() {
        changePasswordButton.setEnabled(false); // Disable button initially

        changePasswordButton.setOnClickListener(v -> {
            if (!validateInputs()) {
                return; // Stop if validation fails
            }

            String oldPassword = oldPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();

            if (oldPassword.equals(newPassword)) {
                newPasswordLayout.setError("Mật khẩu mới không được trùng với mật khẩu cũ");
                return;
            }

            viewModel.changePassword(oldPassword, newPassword);
        });
    }

    private void setupTextWatchers() {
        oldPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateOldPassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        newPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateNewPassword(s.toString());
                // Validate confirm password again when new password changes
                validateConfirmPassword(confirmPasswordInput.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        confirmPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateConfirmPassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void validateOldPassword(String password) {
        if (password.isEmpty()) {
            oldPasswordLayout.setError("Vui lòng nhập mật khẩu cũ");
            changePasswordButton.setEnabled(false);
        } else {
            oldPasswordLayout.setError(null);
            validateAllFields();
        }
    }

    private void validateNewPassword(String password) {
        if (password.isEmpty()) {
            newPasswordLayout.setError("Vui lòng nhập mật khẩu mới");
            changePasswordButton.setEnabled(false);
            return;
        }

        if (password.length() < 6) {
            newPasswordLayout.setError("Mật khẩu phải có ít nhất 6 ký tự");
            changePasswordButton.setEnabled(false);
            return;
        }

        if (!password.matches(".*[A-Z].*")) {
            newPasswordLayout.setError("Mật khẩu phải có ít nhất 1 chữ viết hoa");
            changePasswordButton.setEnabled(false);
            return;
        }

        if (!password.matches(".*[0-9].*")) {
            newPasswordLayout.setError("Mật khẩu phải có ít nhất 1 chữ số");
            changePasswordButton.setEnabled(false);
            return;
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            newPasswordLayout.setError("Mật khẩu phải có ít nhất 1 ký tự đặc biệt");
            changePasswordButton.setEnabled(false);
            return;
        }

        newPasswordLayout.setError(null);
        newPasswordLayout.setHelperText("Mật khẩu hợp lệ");
        validateAllFields();
    }

    private void validateConfirmPassword(String confirmPassword) {
        String newPassword = newPasswordInput.getText().toString();
        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.setError("Vui lòng xác nhận mật khẩu");
            changePasswordButton.setEnabled(false);
        } else if (!confirmPassword.equals(newPassword)) {
            confirmPasswordLayout.setError("Mật khẩu xác nhận không khớp");
            changePasswordButton.setEnabled(false);
        } else {
            confirmPasswordLayout.setError(null);
            validateAllFields();
        }
    }

    private void validateAllFields() {
        String oldPassword = oldPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        boolean isValid = !oldPassword.isEmpty() &&
                isValidPassword(newPassword) &&
                !confirmPassword.isEmpty() &&
                confirmPassword.equals(newPassword);

        changePasswordButton.setEnabled(isValid);
    }

    private boolean validateInputs() {
        String oldPassword = oldPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        boolean isValid = true;

        // Validate old password
        if (oldPassword.isEmpty()) {
            oldPasswordLayout.setError("Vui lòng nhập mật khẩu cũ");
            isValid = false;
        }

        // Validate new password
        if (newPassword.isEmpty()) {
            newPasswordLayout.setError("Vui lòng nhập mật khẩu mới");
            isValid = false;
        } else if (newPassword.length() < 6) {
            newPasswordLayout.setError("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        } else if (!newPassword.matches(".*[A-Z].*")) {
            newPasswordLayout.setError("Mật khẩu phải có ít nhất 1 chữ viết hoa");
            isValid = false;
        } else if (!newPassword.matches(".*[0-9].*")) {
            newPasswordLayout.setError("Mật khẩu phải có ít nhất 1 chữ số");
            isValid = false;
        } else if (!newPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            newPasswordLayout.setError("Mật khẩu phải có ít nhất 1 ký tự đặc biệt");
            isValid = false;
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.setError("Vui lòng xác nhận mật khẩu");
            isValid = false;
        } else if (!confirmPassword.equals(newPassword)) {
            confirmPasswordLayout.setError("Mật khẩu xác nhận không khớp");
            isValid = false;
        }

        // Only proceed if all validations pass
        if (!isValid) {
            return false;
        }

        // Additional validation for new password
        if (!isValidPassword(newPassword)) {
            newPasswordLayout.setError("Mật khẩu không đáp ứng yêu cầu");
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 &&
                password.matches(".*[A-Z].*") &&  // có ít nhất 1 chữ hoa
                password.matches(".*[0-9].*") &&  // có ít nhất 1 chữ số
                password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*"); // có ít nhất 1 ký tự đặc biệt
    }

    private void showError(String message) {
        Snackbar.make(changePasswordButton, message, Snackbar.LENGTH_SHORT).show();
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