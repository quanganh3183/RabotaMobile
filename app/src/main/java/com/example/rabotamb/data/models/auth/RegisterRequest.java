package com.example.rabotamb.data.models.auth;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("age")
    private int age;

    @SerializedName("gender")
    private String gender;

    @SerializedName("address")
    private String address;

    public RegisterRequest(String name, String email, String password, int age, String gender, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender = gender;
        this.address = address;
    }
}