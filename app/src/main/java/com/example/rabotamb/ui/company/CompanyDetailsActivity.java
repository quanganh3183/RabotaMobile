package com.example.rabotamb.ui.company;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.rabotamb.R;
import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.data.models.company.Company;
import com.example.rabotamb.utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;  // Nếu bạn dùng Picasso để load ảnh

public class CompanyDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_COMPANY_ID = "extra_company_id";
    private static final String TAG = "CompanyDetailsActivity";

    private CompanyDetailsViewModel viewModel;
    private ProgressBar progressBar;
    private ImageView companyLogoImageView;
    private TextView companyNameTextView;
    private TextView companyDescriptionTextView;
    private TextView companyAddressTextView;
    private CoordinatorLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);

        initViews();
        setupToolbar();
        setupViewModel();

        String companyId = getIntent().getStringExtra(EXTRA_COMPANY_ID);
        if (companyId != null) {
            viewModel.loadCompanyDetails(companyId);
        } else {
            finish();
        }
    }

    private void initViews() {
        rootLayout = findViewById(R.id.rootCoordinatorLayout);
        progressBar = findViewById(R.id.progressBar);
        companyLogoImageView = findViewById(R.id.companyLogoImageView);
        companyNameTextView = findViewById(R.id.companyNameTextView);
        companyDescriptionTextView = findViewById(R.id.companyDescriptionTextView);
        companyAddressTextView = findViewById(R.id.companyAddressTextView);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chi tiết công ty");
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CompanyDetailsViewModel.class);

        viewModel.getCompanyDetails().observe(this, this::updateUI);
        viewModel.getIsLoading().observe(this, isLoading ->
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));
        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
            }
        });
    }

    private void updateUI(Company company) {
        if (company != null) {
            companyNameTextView.setText(company.getName());
            companyAddressTextView.setText(company.getAddress());

            // Xử lý HTML trong description
            if (company.getDescription() != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    companyDescriptionTextView.setText(Html.fromHtml(
                            company.getDescription(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    companyDescriptionTextView.setText(Html.fromHtml(company.getDescription()));
                }
            }

            // Load logo với đường dẫn đúng
            if (company.getLogo() != null && !company.getLogo().isEmpty()) {
                String logoUrl = Constants.IMAGE_URL + company.getLogo();
                Picasso.get()
                        .load(logoUrl)
                        .placeholder(R.drawable.logo)  // Sử dụng logo.png làm placeholder
                        .error(R.drawable.logo)        // Sử dụng logo.png khi load thất bại
                        .into(companyLogoImageView);
            } else {
                companyLogoImageView.setImageResource(R.drawable.logo);  // Sử dụng logo.png khi không có logo
            }
        }
    }

    private void showError(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .setAction("Thử lại", v -> {
                    String companyId = getIntent().getStringExtra(EXTRA_COMPANY_ID);
                    if (companyId != null) {
                        viewModel.loadCompanyDetails(companyId);
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