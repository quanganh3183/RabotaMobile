package com.example.rabotamb.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rabotamb.R;
import com.example.rabotamb.data.models.resumes.Resume;

import java.util.ArrayList;
import java.util.List;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder> {
    private List<Resume> candidates = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Resume resume);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<Resume> newCandidates) {
        this.candidates = newCandidates;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_candidate, parent, false);
        return new CandidateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateViewHolder holder, int position) {
        holder.bind(candidates.get(position));
    }

    @Override
    public int getItemCount() {
        return candidates.size();
    }

    class CandidateViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvEmail;
        private final TextView tvJobName;
        private final TextView tvStatus;

        public CandidateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvJobName = itemView.findViewById(R.id.tvJobName);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(candidates.get(position));
                }
            });
        }

        public void bind(Resume resume) {
            tvName.setText(resume.getUserName());
            tvEmail.setText(resume.getEmail());
            tvJobName.setText(resume.getJobName());

            String status = resume.getStatus();
            Context context = itemView.getContext();

            tvStatus.setText(getStatusText(context, status));

            GradientDrawable background = (GradientDrawable) tvStatus.getBackground();
            background.setColor(getStatusColor(context, status));
            tvStatus.setTextColor(Color.WHITE);
        }

        private int getStatusColor(Context context, String status) {
            switch (status.toUpperCase()) {
                case "PENDING":
                    return context.getColor(R.color.status_pending);
                case "PASSCV":
                    return context.getColor(R.color.status_passcv);
                case "APPROVED":
                    return context.getColor(R.color.status_approved);
                case "REJECTED":
                    return context.getColor(R.color.status_rejected);
                default:
                    return Color.GRAY;
            }
        }

        private String getStatusText(Context context, String status) {
            switch (status.toUpperCase()) {
                case "PENDING":
                    return context.getString(R.string.status_pending);
                case "PASSCV":
                    return context.getString(R.string.status_passcv);
                case "APPROVED":
                    return context.getString(R.string.status_approved);
                case "REJECTED":
                    return context.getString(R.string.status_rejected);
                default:
                    return status;
            }
        }
    }
}