package com.example.rabotamb.data.models.auth;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data;

    public static class Data {
        @SerializedName("user")
        private User user;

        public User getUser() {
            return user;
        }
    }

    public static class User {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("email")
        private String email;

        @SerializedName("role")
        private Role role;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public Role getRole() { return role; }
    }

    public static class Role {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        public String getId() { return id; }
        public String getName() { return name; }
    }

    // Getters for main class
    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }
}