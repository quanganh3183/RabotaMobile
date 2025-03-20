package com.example.rabotamb.data.models.job;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class JobsHrResponse {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data;

    public static class Data {
        @SerializedName("statusCode")
        private int statusCode;

        @SerializedName("message")
        private String message;

        @SerializedName("data")
        private JobsData data;
    }

    public static class JobsData {
        @SerializedName("meta")
        private Meta meta;

        @SerializedName("result")
        private List<JobHr> result;
    }

    public static class Meta {
        @SerializedName("current")
        private int current;

        @SerializedName("pageSize")
        private int pageSize;

        @SerializedName("pages")
        private int pages;

        @SerializedName("total")
        private int total;

        public int getCurrent() { return current; }
        public int getPageSize() { return pageSize; }
        public int getPages() { return pages; }
        public int getTotal() { return total; }
    }

    public Meta getMeta() {
        return data != null && data.data != null ? data.data.meta : null;
    }

    public List<JobHr> getJobs() {
        return data != null && data.data != null ? data.data.result : new ArrayList<>();
    }

    public int getStatusCode() { return statusCode; }
    public String getMessage() { return message; }
}