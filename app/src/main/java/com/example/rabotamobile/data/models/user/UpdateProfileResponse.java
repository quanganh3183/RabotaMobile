package com.example.rabotamb.data.models.user;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileResponse {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private UserData data;

    public boolean isSuccess() {
        return statusCode == 200;
    }

    public String getMessage() {
        return message;
    }

    public UserData getData() {
        return data;
    }

    public static class UserData {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("email")
        private String email;

        @SerializedName("age")
        private int age;

        @SerializedName("gender")
        private String gender;

        @SerializedName("address")
        private String address;

        @SerializedName("isActived")
        private boolean isActived;

        @SerializedName("isDeleted")
        private boolean isDeleted;

        @SerializedName("role")
        private String role;

        @SerializedName("premium")
        private int premium;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("updatedAt")
        private String updatedAt;

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public int getAge() { return age; }
        public String getGender() { return gender; }
        public String getAddress() { return address; }
        public boolean isActived() { return isActived; }
        public boolean isDeleted() { return isDeleted; }
        public String getRole() { return role; }
        public int getPremium() { return premium; }
    }
}