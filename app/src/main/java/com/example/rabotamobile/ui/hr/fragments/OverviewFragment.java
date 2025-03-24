package com.example.rabotamb.ui.hr.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rabotamb.R;
import com.example.rabotamb.utils.UserPreferences;
import com.example.rabotamb.ui.viewmodel.OverviewViewModel;
import com.google.android.material.card.MaterialCardView;

public class OverviewFragment extends Fragment {
    private static final String TAG = "OverviewFragment";

    private OverviewViewModel viewModel;
    private TextView jobCountText;
    private TextView resumeCountText;
    private MaterialCardView cardView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewModel();
        loadData();
    }

    private void initViews(View view) {
        jobCountText = view.findViewById(R.id.jobCountText);
        resumeCountText = view.findViewById(R.id.resumeCountText);
        cardView = view.findViewById(R.id.overviewCard);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(OverviewViewModel.class);

        viewModel.getJobCount().observe(getViewLifecycleOwner(), count -> {
            jobCountText.setText(String.valueOf(count));
        });

        viewModel.getResumeCount().observe(getViewLifecycleOwner(), count -> {
            resumeCountText.setText(String.valueOf(count));
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            cardView.setAlpha(isLoading ? 0.5f : 1.0f);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        UserPreferences userPreferences = new UserPreferences(requireContext());
        UserPreferences.UserInfo userInfo = userPreferences.getUserInfo();

        if (userInfo != null && userInfo.getCompany() != null) {
            String companyId = userInfo.getCompany().get_id();
            Log.d(TAG, "Loading overview data for company: " + companyId);
            viewModel.loadOverviewData(companyId);
        } else {
            Log.e(TAG, "No company information found");
            Toast.makeText(requireContext(), "Không tìm thấy thông tin công ty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // Reload data when fragment resumes
    }
}