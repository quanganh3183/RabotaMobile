package com.example.rabotamb.ui.hr.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rabotamb.R;
import com.example.rabotamb.data.models.resumes.ResumeResponse;
import com.example.rabotamb.data.models.resumes.Resume;
import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.ui.adapter.CandidateAdapter;
import com.example.rabotamb.ui.auth.LoginActivity;
import com.example.rabotamb.utils.UserPreferences;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CandidateListFragment extends Fragment {
    private static final String TAG = "CandidateListFragment";

    private RecyclerView recyclerView;
    private CandidateAdapter adapter;
    private View progressBar;
    private BroadcastReceiver statusUpdateReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo broadcast receiver
        statusUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Broadcast received: " + intent.getAction());
                loadCandidates(); // Reload data when broadcast received
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_candidate_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        loadCandidates();

        // Register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("RESUME_STATUS_UPDATED");
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(statusUpdateReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        loadCandidates(); // Reload data when fragment resumes
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView called");
        // Unregister broadcast receiver
        if (statusUpdateReceiver != null) {
            try {
                LocalBroadcastManager.getInstance(requireContext())
                        .unregisterReceiver(statusUpdateReceiver);
            } catch (Exception e) {
                Log.e(TAG, "Error unregistering receiver", e);
            }
        }
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewCandidates);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        adapter = new CandidateAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Set click listener for resume items
        adapter.setOnItemClickListener(resume -> {
            Intent intent = new Intent(getContext(), ResumeDetailActivity.class);
            intent.putExtra("resume", resume);
            startActivity(intent);
        });
    }

    private void loadCandidates() {
        Log.d(TAG, "Loading candidates...");
        progressBar.setVisibility(View.VISIBLE);

        UserPreferences userPreferences = new UserPreferences(requireContext());
        UserPreferences.UserInfo userInfo = userPreferences.getUserInfo();

        if (userInfo != null && userInfo.getCompany() != null) {
            String companyId = userInfo.getCompany().get_id();
            Log.d(TAG, "Loading for company: " + companyId);

            Call<ResumeResponse> call = ApiClient.getInstance().getResumesByCompany(companyId, 1, 10);
            call.enqueue(new Callback<ResumeResponse>() {
                @Override
                public void onResponse(Call<ResumeResponse> call, Response<ResumeResponse> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        ResumeResponse.ResumeData resumeData = response.body().getData().getData();
                        if (resumeData != null && resumeData.getResult() != null) {
                            List<Resume> resumes = new ArrayList<>();
                            for (ResumeResponse.Resume responseResume : resumeData.getResult()) {
                                resumes.add(Resume.fromResponseResume(responseResume));
                            }
                            Log.d(TAG, "Loaded " + resumes.size() + " candidates");
                            adapter.setData(resumes);
                        } else {
                            Log.d(TAG, "Resume data is null or empty");
                        }
                    } else {
                        Log.e(TAG, "Response not successful. Code: " + response.code());
                        if (response.code() == 401) {
                            userPreferences.clearAll();
                            navigateToLogin();
                        } else {
                            showError("Không thể tải danh sách ứng viên");
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResumeResponse> call, Throwable t) {
                    Log.e(TAG, "Load failed", t);
                    progressBar.setVisibility(View.GONE);
                    showError("Đã xảy ra lỗi: " + t.getMessage());
                }
            });
        } else {
            Log.e(TAG, "User info or company info is null");
            progressBar.setVisibility(View.GONE);
            showError("Không tìm thấy thông tin công ty");
        }
    }

    private void navigateToLogin() {
        Log.d(TAG, "Navigating to login screen");
        Intent loginIntent = new Intent(requireContext(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void showError(String message) {
        Log.e(TAG, "Error: " + message);
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        // Clean up any resources
        if (statusUpdateReceiver != null) {
            try {
                LocalBroadcastManager.getInstance(requireContext())
                        .unregisterReceiver(statusUpdateReceiver);
            } catch (Exception e) {
                Log.e(TAG, "Error unregistering receiver in onDestroy", e);
            }
        }
    }
}