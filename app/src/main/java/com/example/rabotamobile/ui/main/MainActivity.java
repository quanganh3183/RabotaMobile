package com.example.rabotamb.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rabotamb.R;
import com.example.rabotamb.ui.adapter.JobAdapter;
import com.example.rabotamb.ui.hr.HRManagementActivity;
import com.example.rabotamb.ui.job.JobDetailsActivity;
import com.example.rabotamb.ui.viewmodel.SearchViewModel;
import com.example.rabotamb.ui.auth.LoginActivity;
import com.example.rabotamb.ui.profile.ProfileActivity;
import com.example.rabotamb.utils.UserPreferences;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final long SEARCH_DELAY = 300; // milliseconds

    private TextInputEditText searchEditText;
    private RecyclerView jobsRecyclerView;
    private JobAdapter jobAdapter;
    private SearchViewModel viewModel;
    private BottomNavigationView navView;
    private UserPreferences userPreferences;
    private ProgressBar progressBar;
    private Handler searchHandler;
    private Runnable searchRunnable;
    private NestedScrollView nestedScrollView;
    private MaterialButton viewMoreButton;
    private MaterialAutoCompleteTextView locationDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userPreferences = new UserPreferences(this);
        searchHandler = new Handler(Looper.getMainLooper());
        jobAdapter = new JobAdapter();

        initViews();
        setupViews();
        setupViewModel();
        setupListeners();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        searchEditText = findViewById(R.id.searchEditText);
        locationDropdown = findViewById(R.id.locationDropdown);
        jobsRecyclerView = findViewById(R.id.jobsRecyclerView);
        navView = findViewById(R.id.nav_view);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        progressBar = findViewById(R.id.progressBar);
        viewMoreButton = findViewById(R.id.viewMoreButton);
    }

    private void setupViews() {
        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        jobsRecyclerView.setLayoutManager(layoutManager);
        jobsRecyclerView.setAdapter(jobAdapter);
        jobsRecyclerView.setNestedScrollingEnabled(false);

        // Setup job item click
        jobAdapter.setOnItemClickListener(job -> {
            Log.d("DEBUG", "Job clicked: " + job.getId());
            Intent intent = new Intent(this, JobDetailsActivity.class);
            intent.putExtra(JobDetailsActivity.EXTRA_JOB_ID, job.getId());
            startActivity(intent);
        });

        // Setup location dropdown
        setupLocationDropdown();

        // Setup navigation
        updateNavigationMenu();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        // Observe search results
        viewModel.getSearchResults().observe(this, jobs -> {
            if (jobs != null) {
                Log.d(TAG, "Received " + jobs.size() + " jobs");
                jobAdapter.updateJobs(jobs);

                // Show/Hide view more button
                viewMoreButton.setVisibility(jobAdapter.canShowMore() ? View.VISIBLE : View.GONE);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe errors
        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Snackbar.make(nestedScrollView, error, Snackbar.LENGTH_SHORT).show();
            }
        });

        // Load initial data
        viewModel.loadJobs();
    }

    private void setupLocationDropdown() {
        // Map để ánh xạ giữa text hiển thị và giá trị search
        Map<String, String> locationMap = new HashMap<>();
        locationMap.put("Tất cả", "");
        locationMap.put("Hà Nội", "HANOI");
        locationMap.put("Hồ Chí Minh", "HOCHIMINH");
        locationMap.put("Đà Nẵng", "DANANG");
        locationMap.put("Khác", "OTHER");

        // Array để hiển thị
        String[] displayLocations = new String[]{"Tất cả", "Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Khác"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_menu_item,
                displayLocations
        );

        locationDropdown.setAdapter(adapter);

        // Lấy TextInputLayout parent của locationDropdown
        TextInputLayout textInputLayout = (TextInputLayout) locationDropdown.getParent().getParent();

        // Set end icon click listener
        textInputLayout.setEndIconOnClickListener(view -> {
            locationDropdown.setText("Tất cả", false);  // Đổi thành "Tất cả" thay vì null
            String searchQuery = searchEditText.getText().toString().trim();
            performSearch(searchQuery, "");
        });

        locationDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String displayLocation = parent.getItemAtPosition(position).toString();
            String searchQuery = searchEditText.getText().toString().trim();

            if ("Tất cả".equals(displayLocation)) {
                locationDropdown.setText("Tất cả", false);  // Đổi thành "Tất cả" thay vì null
                performSearch(searchQuery, "");
            } else {
                String searchValue = locationMap.get(displayLocation);
                if (searchValue != null) {
                    locationDropdown.setText(displayLocation, false);
                    performSearch(searchQuery, searchValue);
                }
            }
        });

        locationDropdown.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                locationDropdown.showDropDown();
            }
            return false;
        });

        // Set giá trị mặc định là "Tất cả"
        locationDropdown.setText("Tất cả", false);
    }

    private void setupListeners() {
        // Search text change listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    String query = s.toString().trim();
                    // Lấy giá trị search từ locationMap dựa trên text hiển thị
                    String displayLocation = locationDropdown.getText().toString().trim();
                    String locationValue = "";

                    // Kiểm tra và map giá trị location
                    if ("Hà Nội".equals(displayLocation)) {
                        locationValue = "HANOI";
                    } else if ("Hồ Chí Minh".equals(displayLocation)) {
                        locationValue = "HOCHIMINH";
                    } else if ("Đà Nẵng".equals(displayLocation)) {
                        locationValue = "DANANG";
                    } else if ("Khác".equals(displayLocation)) {
                        locationValue = "OTHER";
                    }
                    // Nếu là "Tất cả" hoặc giá trị khác, locationValue sẽ là chuỗi rỗng

                    performSearch(query, locationValue);
                };

                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }
        });

        // View More button click listener
        viewMoreButton.setOnClickListener(v -> {
            jobAdapter.showAllItems();
            viewMoreButton.setVisibility(View.GONE);
        });
    }

    private void performSearch(String query, String location) {
        viewModel.search(query, location);
    }

    private void updateNavigationMenu() {
        UserPreferences.UserInfo userInfo = userPreferences.getUserInfo();

        // Log để debug
        Log.d(TAG, "Checking user info:");
        Log.d(TAG, "Is logged in: " + userPreferences.isLoggedIn());
        if (userInfo != null && userInfo.getRole() != null) {
            Log.d(TAG, "Role name: " + userInfo.getRole().getName());
            if (userInfo.getCompany() != null) {
                Log.d(TAG, "Company: " + userInfo.getCompany().getName());
            }
        }

        navView.getMenu().clear();

        if (userPreferences.isLoggedIn()) {
            if (userInfo != null && userInfo.getRole() != null && "HR_ROLE".equals(userInfo.getRole().getName())) {
                // Menu cho HR role
                navView.inflateMenu(R.menu.bottom_nav_menu_hr);
                navView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
                Log.d(TAG, "Loading HR menu");
            } else {
                // Menu cho user thường
                navView.inflateMenu(R.menu.bottom_nav_menu_logged_in);
                navView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
                Log.d(TAG, "Loading regular user menu");
            }
        } else {
            // Menu khi chưa đăng nhập
            navView.inflateMenu(R.menu.bottom_nav_menu_logged_out);
            navView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
            Log.d(TAG, "Loading logged out menu");
        }

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            boolean isLoggedIn = userPreferences.isLoggedIn();

            if (itemId == R.id.navigation_home) {
                resetSearchAndReload();
                return true;
            }
            else if (itemId == R.id.navigation_login && !isLoggedIn) {
                // Sửa lại phần này
                Intent intent = new Intent(this, LoginActivity.class);
                // Không kết thúc MainActivity
                startActivity(intent);
                // Giữ lại item được chọn
                return false; // Thay đổi thành false để giữ nguyên trạng thái navigation
            }
            else if (itemId == R.id.navigation_profile && isLoggedIn) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            else if (itemId == R.id.navigation_hr_management && isLoggedIn) {
                Intent intent = new Intent(this, HRManagementActivity.class);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.navigation_logout && isLoggedIn) {
                handleLogout();
                return true;
            }

            return false;
        });
    }

    private void resetSearchAndReload() {
        searchEditText.setText("");
        locationDropdown.setText("Tất cả", false);  // Set về "Tất cả" thay vì null
        viewModel.loadJobs();
    }

    private void handleLogout() {
        userPreferences.clearAll();
        updateNavigationMenu();
        resetSearchAndReload();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNavigationMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}