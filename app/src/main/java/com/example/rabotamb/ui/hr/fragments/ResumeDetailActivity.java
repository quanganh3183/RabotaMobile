package com.example.rabotamb.ui.hr.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import android.graphics.drawable.GradientDrawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.rabotamb.R;
import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.data.models.resumes.Resume;
import com.example.rabotamb.ui.auth.LoginActivity;
import com.example.rabotamb.utils.UserPreferences;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResumeDetailActivity extends AppCompatActivity {

    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_detail);

        progressBar = findViewById(R.id.progressBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết ứng viên");
        }

        Resume resume = getIntent().getParcelableExtra("resume");
        if (resume == null) {
            Toast.makeText(this, "Không thể tải thông tin ứng viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupViews(resume);
    }

    private void setupViews(Resume resume) {
        TextView tvName = findViewById(R.id.tvName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvJobName = findViewById(R.id.tvJobName);
        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvDescription = findViewById(R.id.tvDescription);
        MaterialButton btnViewCV = findViewById(R.id.btnViewCV);
        MaterialButton btnPassCV = findViewById(R.id.btnPassCV);
        MaterialButton btnApprove = findViewById(R.id.btnApprove);
        MaterialButton btnReject = findViewById(R.id.btnReject);

        tvName.setText(resume.getUserName());
        tvEmail.setText(resume.getEmail());
        tvJobName.setText(resume.getJobName());
        tvDescription.setText(resume.getDescription());

        String status = resume.getStatus();
        tvStatus.setText(getStatusText(status));
        GradientDrawable background = (GradientDrawable) tvStatus.getBackground();
        background.setColor(getStatusColor(status));

        btnViewCV.setOnClickListener(v -> {
            String cvUrl = resume.getUrl();
            if (cvUrl != null && !cvUrl.isEmpty()) {
                String fullUrl = "https://api.rabotaworks.com//images/resume/" + cvUrl;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Không tìm thấy CV", Toast.LENGTH_SHORT).show();
            }
        });

        btnPassCV.setOnClickListener(v -> updateResumeStatus(resume, "PASSCV"));
        btnApprove.setOnClickListener(v -> updateResumeStatus(resume, "APPROVED"));
        btnReject.setOnClickListener(v -> updateResumeStatus(resume, "REJECTED"));

        updateButtonStates(status, btnPassCV, btnApprove, btnReject);
    }

    private void updateResumeStatus(Resume resume, String newStatus) {
        progressBar.setVisibility(View.VISIBLE);

        Map<String, String> statusBody = new HashMap<>();
        statusBody.put("status", newStatus);

        UserPreferences userPreferences = new UserPreferences(this);
        String token = userPreferences.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin đăng nhập", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        Log.d("ResumeDetail", "Token when updating: " + token);

        ApiClient.getInstance()
                .updateResumeStatus(resume.getId(), statusBody, token)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful()) {
                            resume.setStatus(newStatus);
                            TextView tvStatus = findViewById(R.id.tvStatus);
                            tvStatus.setText(getStatusText(newStatus));
                            GradientDrawable background = (GradientDrawable) tvStatus.getBackground();
                            background.setColor(getStatusColor(newStatus));

                            MaterialButton btnPassCV = findViewById(R.id.btnPassCV);
                            MaterialButton btnApprove = findViewById(R.id.btnApprove);
                            MaterialButton btnReject = findViewById(R.id.btnReject);
                            updateButtonStates(newStatus, btnPassCV, btnApprove, btnReject);

                            Toast.makeText(ResumeDetailActivity.this,
                                    "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent("RESUME_STATUS_UPDATED");
                            intent.putExtra("resume_id", resume.getId());
                            intent.putExtra("new_status", newStatus);
                            LocalBroadcastManager.getInstance(ResumeDetailActivity.this)
                                    .sendBroadcast(intent);

                            // Đặt result để parent activity biết cần refresh
                            setResult(RESULT_OK);
                            finish(); // Quay về màn list
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ?
                                        response.errorBody().string() : "Unknown error";
                                if (response.code() == 401) {
                                    Toast.makeText(ResumeDetailActivity.this,
                                            "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
                                    userPreferences.clearAll();
                                    Intent loginIntent = new Intent(ResumeDetailActivity.this, LoginActivity.class);
                                    // Xóa tất cả activity khác trong stack
                                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(loginIntent);
                                    finish(); // Đóng activity hiện tại
                                } else {
                                    Toast.makeText(ResumeDetailActivity.this,
                                            "Không thể cập nhật trạng thái: " + errorBody,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                Toast.makeText(ResumeDetailActivity.this,
                                        "Có lỗi xảy ra: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ResumeDetailActivity.this,
                                "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateButtonStates(String currentStatus, MaterialButton btnPassCV,
                                    MaterialButton btnApprove, MaterialButton btnReject) {
        switch (currentStatus.toUpperCase()) {
            case "PENDING":
                btnPassCV.setEnabled(true);
                btnApprove.setEnabled(false);
                btnReject.setEnabled(true);
                break;
            case "PASSCV":
                btnPassCV.setEnabled(false);
                btnApprove.setEnabled(true);
                btnReject.setEnabled(true);
                break;
            case "APPROVED":
            case "REJECTED":
                btnPassCV.setEnabled(false);
                btnApprove.setEnabled(false);
                btnReject.setEnabled(false);
                break;
        }
    }

    private int getStatusColor(String status) {
        switch (status.toUpperCase()) {
            case "PENDING":
                return getColor(R.color.status_pending);
            case "PASSCV":
                return getColor(R.color.status_passcv);
            case "APPROVED":
                return getColor(R.color.status_approved);
            case "REJECTED":
                return getColor(R.color.status_rejected);
            default:
                return getColor(android.R.color.darker_gray);
        }
    }

    private String getStatusText(String status) {
        switch (status.toUpperCase()) {
            case "PENDING":
                return getString(R.string.status_pending);
            case "PASSCV":
                return getString(R.string.status_passcv);
            case "APPROVED":
                return getString(R.string.status_approved);
            case "REJECTED":
                return getString(R.string.status_rejected);
            default:
                return status;
        }
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