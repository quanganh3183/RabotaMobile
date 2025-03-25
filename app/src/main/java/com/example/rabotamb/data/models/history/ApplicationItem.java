package com.example.rabotamb.data.models.history;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApplicationItem {
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
    private CompanyInfo companyId;

    @SerializedName("jobId")
    private JobInfo jobId;

    @SerializedName("description")
    private String description;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("history")
    private List<HistoryItem> history;

    // Nested classes
    public static class CompanyInfo {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        public String getId() { return id; }
        public String getName() { return name; }
    }

    public static class JobInfo {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        public String getId() { return id; }
        public String getName() { return name; }
    }

    public static class HistoryItem {
        @SerializedName("status")
        private String status;

        @SerializedName("updatedAt")
        private String updatedAt;

        @SerializedName("updatedBy")
        private UserInfo updatedBy;

        public String getStatus() { return status; }
        public String getUpdatedAt() { return updatedAt; }
        public UserInfo getUpdatedBy() { return updatedBy; }
    }

    public static class UserInfo {
        @SerializedName("_id")
        private String id;

        @SerializedName("email")
        private String email;

        public String getId() { return id; }
        public String getEmail() { return email; }
    }

    // Getters
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getUserId() { return userId; }
    public String getUrl() { return url; }
    public String getStatus() { return status; }
    public CompanyInfo getCompanyId() { return companyId; }
    public JobInfo getJobId() { return jobId; }
    public String getDescription() { return description; }
    public String getCreatedAt() { return createdAt; }
    public List<HistoryItem> getHistory() { return history; }
}