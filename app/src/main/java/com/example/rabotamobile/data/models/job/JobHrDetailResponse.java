package com.example.rabotamb.data.models.job;

import com.google.gson.annotations.SerializedName;

public class JobHrDetailResponse {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private JobHr data;

    public JobHr getData() {
        return data;
    }
}