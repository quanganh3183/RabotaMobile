package com.example.rabotamb.ui.hr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.rabotamb.R;
import com.example.rabotamb.data.models.job.JobHr;
import com.example.rabotamb.ui.adapter.JobsHrAdapter;
import com.example.rabotamb.ui.auth.LoginActivity;
import com.example.rabotamb.ui.viewmodel.JobHrViewModel;
import com.example.rabotamb.ui.viewmodel.JobsHrViewModelFactory;
import com.example.rabotamb.utils.UserPreferences;
import com.google.gson.Gson;

import java.util.List;

public class JobsHrListFragment extends Fragment implements JobsHrAdapter.OnJobHrClickListener {
    private static final String TAG = "JobsHrListFragment";

    private JobHrViewModel viewModel;
    private JobsHrAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private FloatingActionButton fabCreate;
    private UserPreferences userPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_jobs_hr_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userPreferences = new UserPreferences(requireContext());
        initViews(view);
        setupViewModel();
        loadData();
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        RecyclerView recyclerView = view.findViewById(R.id.jobsHrRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new JobsHrAdapter(this);
        recyclerView.setAdapter(adapter);

        fabCreate = view.findViewById(R.id.fabCreate);
        fabCreate.setOnClickListener(v -> navigateToCreateJob());
    }

    private void setupViewModel() {
        JobsHrViewModelFactory factory = new JobsHrViewModelFactory(requireContext());
        viewModel = new ViewModelProvider(this, factory).get(JobHrViewModel.class);

        viewModel.getJobs().observe(getViewLifecycleOwner(), jobs -> {
            if (jobs != null) {
                Log.d(TAG, "Received jobs: " + jobs.size());
                adapter.setJobs(jobs);
                updateEmptyView(jobs);
            } else {
                Log.d(TAG, "Received null jobs");
                updateEmptyView(null);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Log.e(TAG, "Error: " + errorMessage);
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();

                if (errorMessage.contains("Phiên đăng nhập đã hết hạn")) {
                    handleExpiredSession();
                }
            }
        });
    }

    private void handleExpiredSession() {
        userPreferences.clearAll();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void loadData() {
        UserPreferences.UserInfo userInfo = userPreferences.getUserInfo();

        // Log user info for debugging
        Log.d(TAG, "UserInfo: " + new Gson().toJson(userInfo));

        if (userInfo != null && userInfo.getCompany() != null) {
            String companyId = userInfo.getCompany().get_id();
            Log.d(TAG, "Loading jobs for company ID: " + companyId);

            String token = userPreferences.getToken();
            if (token == null || token.isEmpty()) {
                Log.e(TAG, "Token is null or empty");
                Toast.makeText(requireContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                handleExpiredSession();
                return;
            }

            viewModel.loadJobs(companyId);
        } else {
            Log.e(TAG, "Company info not found");
            Toast.makeText(requireContext(), "Không tìm thấy thông tin công ty", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmptyView(List<JobHr> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            Log.d(TAG, "Showing empty view");
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("Chưa có việc làm nào được tạo");
        } else {
            Log.d(TAG, "Hiding empty view");
            emptyView.setVisibility(View.GONE);
        }
    }

    private void navigateToCreateJob() {
        UserPreferences.UserInfo userInfo = userPreferences.getUserInfo();
        if (userInfo == null || userInfo.getCompany() == null) {
            Toast.makeText(requireContext(), "Bạn cần thuộc một công ty để tạo việc làm", Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment createFragment = new JobHrCreateFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, createFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onJobClick(JobHr job) {
        if (job != null) {
            Bundle args = new Bundle();
            args.putString("jobId", job.getId());

            Fragment detailFragment = new JobHrDetailFragment();
            detailFragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}