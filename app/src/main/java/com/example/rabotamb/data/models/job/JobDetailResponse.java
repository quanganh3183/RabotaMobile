package com.example.rabotamb.data.models.job;

public class JobDetailResponse {
    private int statusCode;
    private String message;
    private Job data;

    public int getStatusCode() { return statusCode; }
    public String getMessage() { return message; }
    public Job getData() { return data; }
}