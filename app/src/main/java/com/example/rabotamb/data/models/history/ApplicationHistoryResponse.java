package com.example.rabotamb.data.models.history;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApplicationHistoryResponse {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<ApplicationItem> data;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public List<ApplicationItem> getData() {
        return data;
    }
}