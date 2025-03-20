package com.example.rabotamobile.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.rabotamb.R;
import com.example.rabotamb.utils.UserPreferences;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {
    private UserPreferences userPreferences;
    private TextView nameText, emailText, ageText, genderText, addressText;
    private MaterialButton updateProfileButton, changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userPreferences = new UserPreferences(this);
        initViews();
        setupToolbar();
        displayUserInfo();
        setupButtons();
    }

    private void initViews() {
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        ageText = findViewById(R.id.ageText);
        genderText = findViewById(R.id.genderText);
        addressText = findViewById(R.id.addressText);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Thông tin cá nhân");
        }
    }

    private void displayUserInfo() {
        UserPreferences.UserInfo userInfo = userPreferences.getUserInfo();

        if (userInfo != null) {
            nameText.setText(userInfo.getName());
            emailText.setText(userInfo.getEmail());
            ageText.setText(String.valueOf(userInfo.getAge()));
            genderText.setText(userInfo.getGender());
            addressText.setText(userInfo.getAddress());
        }
    }

    private void setupButtons() {
        updateProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdateProfileActivity.class);
            startActivity(intent);
        });

        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại thông tin khi quay lại từ màn UpdateProfile
        displayUserInfo();
    }
}