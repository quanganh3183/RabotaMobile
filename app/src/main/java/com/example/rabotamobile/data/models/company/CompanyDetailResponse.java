package com.example.rabotamb.data.models.company;

public class CompanyDetailResponse {
    private int statusCode;
    private String message;
    private Company data;

    public int getStatusCode() { return statusCode; }
    public String getMessage() { return message; }
    public Company getData() { return data; }
}