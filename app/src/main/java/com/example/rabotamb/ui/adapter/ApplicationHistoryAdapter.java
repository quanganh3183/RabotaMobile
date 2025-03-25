package com.example.rabotamb.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rabotamb.R;
import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.data.models.history.ApplicationItem;
import com.example.rabotamb.ui.job.JobDetailsActivity;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ApplicationHistoryAdapter extends RecyclerView.Adapter<ApplicationHistoryAdapter.ViewHolder> {
    private List<ApplicationItem> applications = new ArrayList<>();
    private Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_application_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApplicationItem item = applications.get(position);

        holder.jobTitleText.setText(item.getJobId().getName());
        holder.companyNameText.setText(item.getCompanyId().getName());
        holder.descriptionText.setText(item.getDescription());

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(item.getCreatedAt());
            holder.applicationDateText.setText("Ngày ứng tuyển: " + outputFormat.format(date));
        } catch (ParseException e) {
            holder.applicationDateText.setText("Ngày ứng tuyển: " + item.getCreatedAt());
        }

        // Set status with background color
        String status = item.getStatus();
        holder.statusText.setText(getStatusText(status));
        holder.statusText.setBackgroundResource(R.drawable.status_background);
        holder.statusText.setBackgroundTintList(ColorStateList.valueOf(getStatusColor(status)));

        holder.viewCvButton.setOnClickListener(v -> {
            if (item.getUrl() != null && !item.getUrl().isEmpty()) {
                String url = ApiClient.getBaseUrl() + "images/resume/" + item.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });

        holder.viewJobButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, JobDetailsActivity.class);
            intent.putExtra(JobDetailsActivity.EXTRA_JOB_ID, item.getJobId().getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    public void setApplications(List<ApplicationItem> applications) {
        this.applications = applications;
        notifyDataSetChanged();
    }

    private String getStatusText(String status) {
        switch (status) {
            case "PENDING":
                return "Đang chờ";
            case "PASSCV":
                return "Đã duyệt CV";
            case "APPROVED":
                return "Phù hợp";
            case "REJECTED":
                return "Chưa phù hợp";
            default:
                return "Không xác định";
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "PENDING":
                return Color.parseColor("#FF9800"); // Orange
            case "PASSCV":
                return Color.parseColor("#2196F3"); // Blue
            case "APPROVED":
                return Color.parseColor("#4CAF50"); // Green
            case "REJECTED":
                return Color.parseColor("#F44336"); // Red
            default:
                return Color.parseColor("#757575"); // Grey
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitleText;
        TextView companyNameText;
        TextView applicationDateText;
        TextView statusText;
        TextView descriptionText;
        MaterialButton viewCvButton;
        MaterialButton viewJobButton;

        ViewHolder(View view) {
            super(view);
            jobTitleText = view.findViewById(R.id.jobTitleText);
            companyNameText = view.findViewById(R.id.companyNameText);
            applicationDateText = view.findViewById(R.id.applicationDateText);
            statusText = view.findViewById(R.id.statusText);
            descriptionText = view.findViewById(R.id.descriptionText);
            viewCvButton = view.findViewById(R.id.viewCvButton);
            viewJobButton = view.findViewById(R.id.viewJobButton);
        }
    }
}