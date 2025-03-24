package com.example.rabotamb.data.models.auth;

import com.google.gson.annotations.SerializedName;

public class OtpVerificationResponse {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private String error;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
}