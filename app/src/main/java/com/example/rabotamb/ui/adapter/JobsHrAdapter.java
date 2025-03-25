package com.example.rabotamb.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rabotamb.R;
import com.example.rabotamb.data.models.job.JobHr;
import com.example.rabotamb.data.models.skill.Skill;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class JobsHrAdapter extends RecyclerView.Adapter<JobsHrAdapter.JobHrViewHolder> {
    private List<JobHr> jobs = new ArrayList<>();
    private OnJobHrClickListener listener;

    public interface OnJobHrClickListener {
        void onJobClick(JobHr job);
    }

    public JobsHrAdapter(OnJobHrClickListener listener) {
        this.listener = listener;
    }

    public void setJobs(List<JobHr> jobs) {
        this.jobs = jobs != null ? jobs : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobHrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job_hr, parent, false);
        return new JobHrViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobHrViewHolder holder, int position) {
        JobHr job = jobs.get(position);
        holder.bind(job);
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    class JobHrViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView skillsText;
        private TextView locationText;
        private TextView quantityText;
        private TextView salaryText;

        public JobHrViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.jobHrName);
            skillsText = itemView.findViewById(R.id.jobHrSkills);
            locationText = itemView.findViewById(R.id.jobHrLocation);
            quantityText = itemView.findViewById(R.id.jobHrQuantity);
            salaryText = itemView.findViewById(R.id.jobHrSalary);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onJobClick(jobs.get(position));
                }
            });
        }

        public void bind(JobHr job) {
            nameText.setText(job.getName());

            // Xử lý skills
            StringBuilder skillsBuilder = new StringBuilder("Skills: ");
            List<Skill> skills = job.getSkills();
            if (skills != null && !skills.isEmpty()) {
                boolean isFirst = true;
                for (Skill skill : skills) {
                    if (!isFirst) {
                        skillsBuilder.append(", ");
                    }
                    skillsBuilder.append(skill.getName());
                    isFirst = false;
                }
            } else {
                skillsBuilder.append("None");
            }
            skillsText.setText(skillsBuilder.toString());

            locationText.setText("Location: " + job.getLocation());
            quantityText.setText(String.format(Locale.getDefault(), "Quantity: %d", job.getQuantity()));
            salaryText.setText(String.format(Locale.getDefault(), "Salary: $%d", job.getSalary()));
        }
    }
}