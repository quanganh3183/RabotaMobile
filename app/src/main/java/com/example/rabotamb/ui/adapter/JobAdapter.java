package com.example.rabotamb.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rabotamb.R;
import com.example.rabotamb.data.models.job.Job;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    private List<Job> jobs = new ArrayList<>();
    private boolean showAllItems = false;
    private static final int INITIAL_ITEMS = 5;
    private OnItemClickListener onItemClickListener;


    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobs.get(position);
        if (job != null) {
            holder.bind(job);
        }
    }

    @Override
    public int getItemCount() {
        if (showAllItems) {
            return jobs.size();
        }
        return Math.min(jobs.size(), INITIAL_ITEMS);
    }

    public void showAllItems() {
        showAllItems = true;
        notifyDataSetChanged();
    }

    public boolean canShowMore() {
        return jobs.size() > INITIAL_ITEMS && !showAllItems;
    }

    public void updateJobs(List<Job> newJobs) {
        if (newJobs != null) {
            this.jobs = newJobs.stream()
                    .filter(job -> job != null && job.isActive())
                    .collect(Collectors.toList());
            showAllItems = false;
            notifyDataSetChanged();
        } else {
            this.jobs.clear();
            notifyDataSetChanged();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Job job);
    }

    // Setter for click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    class JobViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView companyTextView;
        private final TextView locationTextView;
        private final TextView salaryTextView;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            companyTextView = itemView.findViewById(R.id.companyTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            salaryTextView = itemView.findViewById(R.id.salaryTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener.onItemClick(JobAdapter.this.jobs.get(position));
                }
            });
        }

        public void bind(Job job) {
            try {
                if (job.getName() != null) {
                    titleTextView.setText(job.getName());
                } else {
                    titleTextView.setText("");
                }

                if (job.getCompany() != null && job.getCompany().getName() != null) {
                    companyTextView.setText(job.getCompany().getName());
                } else {
                    companyTextView.setText("");
                }

                if (job.getLocation() != null) {
                    locationTextView.setText(job.getLocation());
                } else {
                    locationTextView.setText("");
                }

                // Format salary to VND currency
                if (job.getSalary() > 0) {
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    String formattedSalary = formatter.format(job.getSalary());
                    salaryTextView.setText(formattedSalary);
                } else {
                    salaryTextView.setText("Thỏa thuận");
                }
            } catch (Exception e) {
                // Fallback values in case of errors
                titleTextView.setText(job.getName() != null ? job.getName() : "");
                companyTextView.setText("");
                locationTextView.setText("");
                salaryTextView.setText("Thỏa thuận");
            }
        }
    }

    // Helper method to clear all data
    public void clear() {
        int size = jobs.size();
        jobs.clear();
        notifyItemRangeRemoved(0, size);
    }

    // Helper method to get current jobs
    public List<Job> getCurrentJobs() {
        return new ArrayList<>(jobs);
    }
}