package com.example.rabotamb.ui.hr.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rabotamb.R;
import com.example.rabotamb.data.models.job.JobHr;
import com.example.rabotamb.data.models.skill.Skill;
import com.example.rabotamb.ui.viewmodel.JobHrViewModel;
import com.example.rabotamb.ui.viewmodel.JobsHrViewModelFactory;
import com.example.rabotamb.utils.DateUtils;

import java.util.stream.Collectors;

public class JobHrDetailFragment extends Fragment {
    private JobHrViewModel viewModel;
    private String jobId;

    // UI components
    private TextView nameText;
    private TextView skillsText;
    private TextView locationText;
    private TextView quantityText;
    private TextView salaryText;
    private TextView levelText;
    private TextView datesText;
    private TextView statusText;
    private TextView descriptionText;
    private Button updateButton;
    private Button deleteButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jobId = getArguments().getString("jobId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_hr_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewModel();
        setupListeners();
    }

    private void initViews(View view) {
        nameText = view.findViewById(R.id.jobDetailName);
        skillsText = view.findViewById(R.id.jobDetailSkills);
        locationText = view.findViewById(R.id.jobDetailLocation);
        quantityText = view.findViewById(R.id.jobDetailQuantity);
        salaryText = view.findViewById(R.id.jobDetailSalary);
        levelText = view.findViewById(R.id.jobDetailLevel);
        datesText = view.findViewById(R.id.jobDetailDates);
        statusText = view.findViewById(R.id.jobDetailStatus);
        descriptionText = view.findViewById(R.id.jobDetailDescription);
        updateButton = view.findViewById(R.id.updateButton);
        deleteButton = view.findViewById(R.id.deleteButton);
    }

    private void setupViewModel() {
        JobsHrViewModelFactory factory = new JobsHrViewModelFactory(requireContext());
        viewModel = new ViewModelProvider(this, factory).get(JobHrViewModel.class);

        viewModel.getSelectedJob().observe(getViewLifecycleOwner(), this::updateUI);

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsDeleted().observe(getViewLifecycleOwner(), isDeleted -> {
            if (isDeleted) {
                Toast.makeText(requireContext(), "Đã xóa việc làm thành công", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Load job details
        if (jobId != null) {
            viewModel.loadJobDetail(jobId);
        }
    }

    private void setupListeners() {
        updateButton.setOnClickListener(v -> {
            if (jobId != null) {
                // Tạo instance của JobHrUpdateFragment
                JobHrUpdateFragment updateFragment = new JobHrUpdateFragment();

                // Truyền jobId qua Bundle
                Bundle args = new Bundle();
                args.putString("jobId", jobId);
                updateFragment.setArguments(args);

                // Chuyển đến fragment update
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                        )
                        .replace(R.id.fragment_container, updateFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (jobId != null) {
                // Hiển thị dialog xác nhận trước khi xóa
                new AlertDialog.Builder(requireContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa việc làm này?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            viewModel.deleteJob(jobId);
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
    }

    private void updateUI(JobHr job) {
        if (job == null) return;

        nameText.setText(job.getName());

        // Xử lý hiển thị skills
        String skillNames = job.getSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.joining(", "));
        skillsText.setText("Kỹ năng: " + skillNames);

        locationText.setText("Địa điểm: " + job.getLocation());
        quantityText.setText("Số lượng: " + job.getQuantity());

        // Định dạng lương với dấu phẩy ngăn cách hàng nghìn
        String formattedSalary = String.format("%,d", job.getSalary());
        salaryText.setText("Lương: " + formattedSalary + " VNĐ");

        levelText.setText("Cấp độ: " + job.getLevel());

        String startDate = DateUtils.formatDateForDisplay(job.getStartDate());
        String endDate = DateUtils.formatDateForDisplay(job.getEndDate());
        datesText.setText("Thời gian: " + startDate + " - " + endDate);

        statusText.setText("Trạng thái: " + (job.isActive() ? "Đang hoạt động" : "Không hoạt động"));
        descriptionText.setText(Html.fromHtml(job.getDescription(), Html.FROM_HTML_MODE_COMPACT));
    }
}