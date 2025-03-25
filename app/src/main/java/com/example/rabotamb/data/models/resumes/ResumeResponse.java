package com.example.rabotamb.data.models.resumes;

import java.util.List;

public class ResumeResponse {
    private int statusCode;
    private String message;
    private ResumeDataWrapper data;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public ResumeDataWrapper getData() {
        return data;
    }

    public static class ResumeDataWrapper {
        private int statusCode;
        private String message;
        private ResumeData data;

        public int getStatusCode() {
            return statusCode;
        }

        public String getMessage() {
            return message;
        }

        public ResumeData getData() {
            return data;
        }
    }

    public static class ResumeData {
        private Meta meta;
        private List<Resume> result;

        public Meta getMeta() {
            return meta;
        }

        public List<Resume> getResult() {
            return result;
        }
    }

    public static class Meta {
        private int current;
        private int pageSize;
        private int pages;
        private int total;

        public int getCurrent() {
            return current;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getPages() {
            return pages;
        }

        public int getTotal() {
            return total;
        }
    }

    public static class Resume {
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

        public String getId() {
            return _id;
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

        public List<History> getHistory() {
            return history;
        }

        public String getDescription() {
            return description;
        }

        public CreatedBy getCreatedBy() {
            return createdBy;
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

        public String getJobName() {
            return jobName;
        }
    }

    public static class History {
        private String status;
        private String updatedAt;
        private UpdatedBy updatedBy;

        public String getStatus() {
            return status;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public UpdatedBy getUpdatedBy() {
            return updatedBy;
        }
    }

    public static class UpdatedBy {
        private String _id;
        private String email;

        public String getId() {
            return _id;
        }

        public String getEmail() {
            return email;
        }
    }

    public static class CreatedBy {
        private String _id;
        private String email;

        public String getId() {
            return _id;
        }

        public String getEmail() {
            return email;
        }
    }
}