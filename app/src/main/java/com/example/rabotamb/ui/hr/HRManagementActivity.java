package com.example.rabotamb.ui.hr;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.rabotamb.R;
import com.example.rabotamb.ui.hr.fragments.CandidateListFragment;
import com.example.rabotamb.ui.hr.fragments.JobsHrListFragment; // Sửa tên import
import com.example.rabotamb.ui.hr.fragments.OverviewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HRManagementActivity extends AppCompatActivity {
    private static final String TAG = "HRManagement";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr_management);

        initViews();
        setupNavigation();

        if (savedInstanceState == null) {
            loadFragment(new OverviewFragment());
            updateToolbarTitle("Tổng quan");
        }
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Tổng quan");

    }

    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                finish();
                return true;
            }
            else if (itemId == R.id.navigation_overview) {
                loadFragment(new OverviewFragment());
                updateToolbarTitle("Tổng quan");
                return true;
            }
            else if (itemId == R.id.navigation_jobs) {
                loadFragment(new JobsHrListFragment()); // Sửa tên Fragment
                updateToolbarTitle("Danh sách công việc");
                return true;
            }
            else if (itemId == R.id.navigation_candidates) {
                loadFragment(new CandidateListFragment());
                updateToolbarTitle("Danh sách ứng viên");
                return true;
            }

            return false;
        });

        navView.setSelectedItemId(R.id.navigation_overview);
    }

    private void updateToolbarTitle(String title) {
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(title);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}