package com.example.rabotamb.data.models.job;

import java.util.List;

public class JobListResponse {
    private int statusCode;
    private String message;
    private DataWrapper data;

    public static class DataWrapper {
        private Meta meta;
        private List<Job> result;

        public Meta getMeta() { return meta; }
        public List<Job> getResult() { return result; }
    }

    public static class Meta {
        private int current;
        private int pageSize;
        private int pages;
        private int total;

        public int getCurrent() { return current; }
        public int getPageSize() { return pageSize; }
        public int getPages() { return pages; }
        public int getTotal() { return total; }
    }

    public int getStatusCode() { return statusCode; }
    public String getMessage() { return message; }
    public DataWrapper getData() { return data; }
}