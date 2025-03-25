package com.example.rabotamb.data.models.resumes;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Resume implements Parcelable {
    private String _id;
    private String email;
    private String userId;
    private String url;
    private String status;
    private String companyId;
    private String jobId;
    private List<History> history;
    private String description;
    private CreatedBy createdBy;
    private boolean isDeleted;
    private String deletedAt;
    private String createdAt;
    private String updatedAt;
    private String userName;
    private String jobName;

    // Constructor mặc định
    public Resume() {}

    // Getters
    public String get_id() { return _id; }
    public String getId() { return _id; }
    public String getEmail() { return email; }
    public String getUserId() { return userId; }
    public String getUrl() { return url; }
    public String getStatus() { return status; }
    public String getCompanyId() { return companyId; }
    public String getJobId() { return jobId; }
    public List<History> getHistory() { return history; }
    public String getDescription() { return description; }
    public CreatedBy getCreatedBy() { return createdBy; }
    public boolean isDeleted() { return isDeleted; }
    public String getDeletedAt() { return deletedAt; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getUserName() { return userName; }
    public String getJobName() { return jobName; }

    // Setters
    public void set_id(String _id) { this._id = _id; }
    public void setEmail(String email) { this.email = email; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUrl(String url) { this.url = url; }
    public void setStatus(String status) { this.status = status; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public void setHistory(List<History> history) { this.history = history; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedBy(CreatedBy createdBy) { this.createdBy = createdBy; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public void setDeletedAt(String deletedAt) { this.deletedAt = deletedAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setJobName(String jobName) { this.jobName = jobName; }

    // Parcelable implementation
    protected Resume(Parcel in) {
        _id = in.readString();
        email = in.readString();
        userId = in.readString();
        url = in.readString();
        status = in.readString();
        companyId = in.readString();
        jobId = in.readString();
        history = new ArrayList<>();
        in.readList(history, History.class.getClassLoader());
        description = in.readString();
        createdBy = in.readParcelable(CreatedBy.class.getClassLoader());
        isDeleted = in.readByte() != 0;
        deletedAt = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        userName = in.readString();
        jobName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(email);
        dest.writeString(userId);
        dest.writeString(url);
        dest.writeString(status);
        dest.writeString(companyId);
        dest.writeString(jobId);
        dest.writeList(history);
        dest.writeString(description);
        dest.writeParcelable(createdBy, flags);
        dest.writeByte((byte) (isDeleted ? 1 : 0));
        dest.writeString(deletedAt);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(userName);
        dest.writeString(jobName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Resume> CREATOR = new Creator<Resume>() {
        @Override
        public Resume createFromParcel(Parcel in) {
            return new Resume(in);
        }

        @Override
        public Resume[] newArray(int size) {
            return new Resume[size];
        }
    };

    // Conversion method
    public static Resume fromResponseResume(ResumeResponse.Resume responseResume) {
        Resume resume = new Resume();
        resume.set_id(responseResume.getId());
        resume.setEmail(responseResume.getEmail());
        resume.setUserId(responseResume.getUserId());
        resume.setUrl(responseResume.getUrl());
        resume.setStatus(responseResume.getStatus());
        resume.setCompanyId(responseResume.getCompanyId());
        resume.setJobId(responseResume.getJobId());
        resume.setDescription(responseResume.getDescription());
        resume.setUserName(responseResume.getUserName());
        resume.setJobName(responseResume.getJobName());
        resume.setDeleted(responseResume.isDeleted());
        resume.setDeletedAt(responseResume.getDeletedAt());
        resume.setCreatedAt(responseResume.getCreatedAt());
        resume.setUpdatedAt(responseResume.getUpdatedAt());

        // Convert history
        if (responseResume.getHistory() != null) {
            List<History> historyList = new ArrayList<>();
            for (ResumeResponse.History responseHistory : responseResume.getHistory()) {
                History history = new History();
                history.setStatus(responseHistory.getStatus());
                history.setUpdatedAt(responseHistory.getUpdatedAt());
                if (responseHistory.getUpdatedBy() != null) {
                    UpdatedBy updatedBy = new UpdatedBy();
                    updatedBy.setId(responseHistory.getUpdatedBy().getId());
                    updatedBy.setEmail(responseHistory.getUpdatedBy().getEmail());
                    history.setUpdatedBy(updatedBy);
                }
                historyList.add(history);
            }
            resume.setHistory(historyList);
        }

        // Convert CreatedBy
        if (responseResume.getCreatedBy() != null) {
            CreatedBy createdBy = new CreatedBy();
            createdBy.setId(responseResume.getCreatedBy().getId());
            createdBy.setEmail(responseResume.getCreatedBy().getEmail());
            resume.setCreatedBy(createdBy);
        }

        return resume;
    }

    // History nested class
    public static class History implements Parcelable {
        private String status;
        private String updatedAt;
        private UpdatedBy updatedBy;

        public History() {}

        public String getStatus() { return status; }
        public String getUpdatedAt() { return updatedAt; }
        public UpdatedBy getUpdatedBy() { return updatedBy; }

        public void setStatus(String status) { this.status = status; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public void setUpdatedBy(UpdatedBy updatedBy) { this.updatedBy = updatedBy; }

        protected History(Parcel in) {
            status = in.readString();
            updatedAt = in.readString();
            updatedBy = in.readParcelable(UpdatedBy.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(status);
            dest.writeString(updatedAt);
            dest.writeParcelable(updatedBy, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<History> CREATOR = new Creator<History>() {
            @Override
            public History createFromParcel(Parcel in) {
                return new History(in);
            }

            @Override
            public History[] newArray(int size) {
                return new History[size];
            }
        };
    }

    // UpdatedBy nested class
    public static class UpdatedBy implements Parcelable {
        private String _id;
        private String email;

        public UpdatedBy() {}

        public String get_id() { return _id; }
        public String getId() { return _id; }
        public String getEmail() { return email; }

        public void setId(String id) { this._id = id; }
        public void setEmail(String email) { this.email = email; }

        protected UpdatedBy(Parcel in) {
            _id = in.readString();
            email = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(_id);
            dest.writeString(email);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<UpdatedBy> CREATOR = new Creator<UpdatedBy>() {
            @Override
            public UpdatedBy createFromParcel(Parcel in) {
                return new UpdatedBy(in);
            }

            @Override
            public UpdatedBy[] newArray(int size) {
                return new UpdatedBy[size];
            }
        };
    }

    // CreatedBy nested class
    public static class CreatedBy implements Parcelable {
        private String _id;
        private String email;

        public CreatedBy() {}

        public String get_id() { return _id; }
        public String getId() { return _id; }
        public String getEmail() { return email; }

        public void setId(String id) { this._id = id; }
        public void setEmail(String email) { this.email = email; }

        protected CreatedBy(Parcel in) {
            _id = in.readString();
            email = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(_id);
            dest.writeString(email);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<CreatedBy> CREATOR = new Creator<CreatedBy>() {
            @Override
            public CreatedBy createFromParcel(Parcel in) {
                return new CreatedBy(in);
            }

            @Override
            public CreatedBy[] newArray(int size) {
                return new CreatedBy[size];
            }
        };
    }
}