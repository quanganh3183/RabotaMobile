package com.example.rabotamb.data.models.resumes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Resume {
    @SerializedName("_id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("userId")
    private String userId;

    @SerializedName("url")
    private String url;

    @SerializedName("status")
    private String status;

    @SerializedName("companyId")
    private String companyId;

    @SerializedName("jobId")
    private String jobId;

    @SerializedName("description")
    private String description;

    @SerializedName("history")
    private List<History> history;

    @SerializedName("createdBy")
    private User createdBy;

    @SerializedName("updatedBy")
    private User updatedBy;

    @SerializedName("isDeleted")
    private boolean isDeleted;

    @SerializedName("deletedAt")
    private String deletedAt;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("userName")
    private String userName;

    public static class History {
        @SerializedName("status")
        private String status;

        @SerializedName("updatedAt")
        private String updatedAt;

        @SerializedName("updatedBy")
        private User updatedBy;

        public String getStatus() {
            return status;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public User getUpdatedBy() {
            return updatedBy;
        }
    }

    public static class User {
        @SerializedName("_id")
        private String id;

        @SerializedName("email")
        private String email;

        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getJobId() {
        return jobId;
    }

    public String getDescription() {
        return description;
    }

    public List<History> getHistory() {
        return history;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getUserName() {
        return userName;
    }
}