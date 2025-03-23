package com.example.rabotamb.data.models.resumes;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResumesResponse {
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
        private ResumesData data;
    }

    public static class ResumesData {
        @SerializedName("meta")
        private Meta meta;

        @SerializedName("result")
        private List<Resume> result;
    }

    public static class Meta {
        @SerializedName("total")
        private int total;

        public int getTotal() {
            return total;
        }
    }

    public Meta getMeta() {
        return data != null && data.data != null ? data.data.meta : null;
    }

    public List<Resume> getResumes() {
        return data != null && data.data != null ? data.data.result : new ArrayList<>();
    }
}