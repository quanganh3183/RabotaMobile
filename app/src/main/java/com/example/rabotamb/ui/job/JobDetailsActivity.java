package com.example.rabotamb.ui.job;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.rabotamb.R;
import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.data.models.job.Job;
import com.example.rabotamb.ui.auth.LoginActivity;
import com.example.rabotamb.ui.company.CompanyDetailsActivity;
import com.example.rabotamb.ui.main.MainActivity;
import com.example.rabotamb.utils.UserPreferences;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_JOB_ID = "extra_job_id";
    private static final String TAG = "JobDetailsActivity";
    private static final long MAX_FILE_SIZE = 1 * 1024 * 1024; // 5MB

    private JobDetailsViewModel viewModel;
    private ProgressBar progressBar;
    private TextView jobTitleTextView;
    private TextView companyNameTextView;
    private TextView locationTextView;
    private TextView salaryTextView;
    private TextView descriptionTextView;
    private CoordinatorLayout rootLayout;
    private MaterialButton applyButton;
    private UserPreferences userPreferences;
    private Job currentJob;
    private AlertDialog currentDialog;
    private Uri selectedFileUri;

    private ActivityResultLauncher<String> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        userPreferences = new UserPreferences(this);
        initViews();
        setupToolbar();
        setupViewModel();
        setupFilePicker();

        String jobId = getIntent().getStringExtra(EXTRA_JOB_ID);
        if (jobId != null) {
            viewModel.loadJobDetails(jobId);
        } else {
            finish();
        }
    }

    private void setupFilePicker() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        handleSelectedFile(uri);
                    }
                }
        );
    }

    private void initViews() {
        rootLayout = findViewById(R.id.rootCoordinatorLayout);
        progressBar = findViewById(R.id.progressBar);
        if (progressBar == null) {
            Log.e("JobDetailsActivity", "ProgressBar is null. Check layout XML.");
        }

        jobTitleTextView = findViewById(R.id.jobTitleTextView);
        companyNameTextView = findViewById(R.id.companyNameTextView);
        locationTextView = findViewById(R.id.locationTextView);
        salaryTextView = findViewById(R.id.salaryTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        applyButton = findViewById(R.id.applyButton);

        applyButton.setOnClickListener(v -> handleApplyClick());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chi tiết công việc");
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(JobDetailsViewModel.class);

        viewModel.getJobDetails().observe(this, job -> {
            currentJob = job;
            updateUI(job);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            } else {
                Log.e("JobDetailsActivity", "Cannot set ProgressBar visibility - progressBar is null");
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
            }
        });
    }

    private void handleApplyClick() {
        if (!userPreferences.isLoggedIn()) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.putExtra("redirect", "job_detail");
            loginIntent.putExtra("jobId", currentJob.getId());
            startActivity(loginIntent);
            return;
        }
        showApplyDialog();
    }

    private void showApplyDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_apply_job, null);

        EditText coverLetterEdit = dialogView.findViewById(R.id.coverLetterEdit);
        TextView fileNameText = dialogView.findViewById(R.id.fileNameText);
        Button uploadCvButton = dialogView.findViewById(R.id.uploadCvButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button submitButton = dialogView.findViewById(R.id.submitButton);

        currentDialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .create();

        uploadCvButton.setOnClickListener(v -> {
            String[] mimeTypes = {
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            };

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

            filePickerLauncher.launch("*/*");
            uploadCvButton.setText("Đang tải...");
            uploadCvButton.setEnabled(false);
        });

        cancelButton.setOnClickListener(v -> currentDialog.dismiss());

        submitButton.setOnClickListener(v -> {
            String coverLetter = coverLetterEdit.getText().toString();
            if (selectedFileUri == null) {
                Toast.makeText(this, "Vui lòng upload CV!", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadFileAndCreateResume(selectedFileUri, coverLetter);
            currentDialog.dismiss();
        });

        currentDialog.show();
    }

    private void handleSelectedFile(Uri uri) {
        try {
            String mimeType = getContentResolver().getType(uri);
            if (mimeType == null) {
                showError("Không thể xác định định dạng file");
                resetUploadButton();
                return;
            }

            if (!isValidFileType(mimeType)) {
                showError("Vui lòng chọn file PDF hoặc Word (DOC, DOCX)");
                resetUploadButton();
                return;
            }

            if (!isValidFileSize(uri)) {
                showError("File không được vượt quá 1MB");
                resetUploadButton();
                return;
            }

            selectedFileUri = uri;
            String fileName = getFileName(uri);

            if (currentDialog != null && currentDialog.isShowing()) {
                View dialogView = currentDialog.findViewById(android.R.id.content);
                if (dialogView != null) {
                    TextView fileNameText = dialogView.findViewById(R.id.fileNameText);
                    Button uploadCvButton = dialogView.findViewById(R.id.uploadCvButton);

                    if (fileNameText != null && uploadCvButton != null) {
                        fileNameText.setText("File đã chọn: " + fileName);
                        fileNameText.setVisibility(View.VISIBLE);
                        uploadCvButton.setText("Upload CV");
                        uploadCvButton.setEnabled(true);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling file", e);
            showError("Lỗi xử lý file");
            resetUploadButton();
        }
    }

    private void resetUploadButton() {
        if (currentDialog != null) {
            View dialogView = currentDialog.findViewById(android.R.id.content);
            if (dialogView != null) {
                Button uploadCvButton = dialogView.findViewById(R.id.uploadCvButton);
                if (uploadCvButton != null) {
                    uploadCvButton.setText("Upload CV");
                    uploadCvButton.setEnabled(true);
                }
            }
        }
    }

    private boolean isValidFileType(String mimeType) {
        return mimeType.equals("application/pdf") ||
                mimeType.equals("application/msword") ||
                mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    private boolean isValidFileSize(Uri uri) {
        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (sizeIndex != -1) {
                    long size = cursor.getLong(sizeIndex);
                    cursor.close();
                    return size <= MAX_FILE_SIZE;
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking file size", e);
        }
        return false;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void uploadFileAndCreateResume(Uri fileUri, String coverLetter) {
        try {
            progressBar.setVisibility(View.VISIBLE);

            // Tạo RequestBody từ Uri
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);
            inputStream.close();

            // Tạo RequestBody từ file
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse(getContentResolver().getType(fileUri)),
                    fileBytes
            );

            // Tạo MultipartBody.Part với key "fileUpload"
            MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                    "fileUpload",
                    getFileName(fileUri),  // Sử dụng tên file gốc
                    requestFile
            );

            // Gọi API upload file
            ApiClient.getInstance().uploadFile(
                    userPreferences.getToken(),
                    "resume",
                    filePart
            ).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            // Parse response để lấy URL file
                            String responseData = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseData);
                            String fileUrl = jsonObject.getJSONObject("data").getString("url");

                            // Gọi API tạo resume với URL file đã upload
                            createResume(fileUrl, coverLetter);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing upload response", e);
                            showError("Lỗi xử lý file");
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Upload error: " + errorBody);
                        } catch (IOException e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                        showError("Lỗi upload file");
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "Upload failed", t);
                    showError("Lỗi kết nối");
                    progressBar.setVisibility(View.GONE);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error handling file", e);
            showError("Lỗi xử lý file");
            progressBar.setVisibility(View.GONE);
        }
    }

    private void createResume(String fileUrl, String coverLetter) {
        Map<String, Object> body = new HashMap<>();
        body.put("url", fileUrl);
        body.put("companyId", currentJob.getCompany().getId());
        body.put("jobId", currentJob.getId());
        body.put("description", coverLetter);

        ApiClient.getInstance().createResume(
                userPreferences.getToken(),
                body
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    // Hiển thị dialog thông báo thành công
                    new MaterialAlertDialogBuilder(JobDetailsActivity.this)
                            .setTitle("Thành công")
                            .setMessage("Bạn đã ứng tuyển thành công! Nhà tuyển dụng sẽ liên hệ với bạn sớm nhất.")
                            .setPositiveButton("OK", (dialog, which) -> {
                                // Chuyển về trang chủ
                                Intent intent = new Intent(JobDetailsActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            })
                            .setCancelable(false) // Không cho phép dismiss bằng back button
                            .show();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "Create resume error: " + errorBody);
                        showError("Lỗi tạo hồ sơ ứng tuyển");
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Create resume failed", t);
                showError("Lỗi kết nối");
            }
        });
    }

    private void updateUI(Job job) {
        if (job != null) {
            jobTitleTextView.setText(job.getName());

            if (job.getCompany() != null) {
                companyNameTextView.setText(job.getCompany().getName());
                companyNameTextView.setOnClickListener(v -> {
                    Intent intent = new Intent(this, CompanyDetailsActivity.class);
                    intent.putExtra(CompanyDetailsActivity.EXTRA_COMPANY_ID, job.getCompany().getId());
                    startActivity(intent);
                });
                companyNameTextView.setClickable(true);
                companyNameTextView.setTextColor(getResources().getColor(R.color.linkColor));
            }

            locationTextView.setText(job.getLocation());

            if (job.getSalary() > 0) {
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                String formattedSalary = formatter.format(job.getSalary());
                salaryTextView.setText(formattedSalary);
            } else {
                salaryTextView.setText("Thỏa thuận");
            }

            if (job.getDescription() != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    descriptionTextView.setText(Html.fromHtml(job.getDescription(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    @SuppressWarnings("deprecation")
                    CharSequence description = Html.fromHtml(job.getDescription());
                    descriptionTextView.setText(description);
                }
            } else {
                descriptionTextView.setText("");
            }
        }
    }

    private void showError(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .setAction("Thử lại", v -> {
                    String jobId = getIntent().getStringExtra(EXTRA_JOB_ID);
                    if (jobId != null) {
                        viewModel.loadJobDetails(jobId);
                    }
                })
                .show();
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