package com.example.rabotamb.ui.job;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.rabotamb.R;
import com.example.rabotamb.data.models.job.Job;
import com.example.rabotamb.ui.company.CompanyDetailsActivity;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.Locale;

public class JobDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_JOB_ID = "extra_job_id";
    private static final String TAG = "JobDetailsActivity";

    private JobDetailsViewModel viewModel;
    private ProgressBar progressBar;
    private TextView jobTitleTextView;
    private TextView companyNameTextView;
    private TextView locationTextView;
    private TextView salaryTextView;
    private TextView descriptionTextView;
    private CoordinatorLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        initViews();
        setupToolbar();
        setupViewModel();

        String jobId = getIntent().getStringExtra(EXTRA_JOB_ID);
        if (jobId != null) {
            viewModel.loadJobDetails(jobId);
        } else {
            finish();
        }
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
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(JobDetailsViewModel.class);

        viewModel.getJobDetails().observe(this, this::updateUI);

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

    private void updateUI(Job job) {
        if (job != null) {
            jobTitleTextView.setText(job.getName());

            if (job.getCompany() != null) {
                companyNameTextView.setText(job.getCompany().getName());

                // Thêm click listener cho company name
                companyNameTextView.setOnClickListener(v -> {
                    Intent intent = new Intent(this, CompanyDetailsActivity.class);
                    intent.putExtra(CompanyDetailsActivity.EXTRA_COMPANY_ID, job.getCompany().getId());
                    startActivity(intent);
                });

                // Thêm visual feedback
                companyNameTextView.setClickable(true);
                companyNameTextView.setTextColor(getResources().getColor(R.color.linkColor)); // Thêm màu link
            }

            locationTextView.setText(job.getLocation());

            if (job.getSalary() > 0) {
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                String formattedSalary = formatter.format(job.getSalary());
                salaryTextView.setText(formattedSalary);
            } else {
                salaryTextView.setText("Thỏa thuận");
            }

            // Xử lý HTML trong description
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
