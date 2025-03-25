package com.example.rabotamb.data.models.auth;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Thêm getters nếu cần
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}