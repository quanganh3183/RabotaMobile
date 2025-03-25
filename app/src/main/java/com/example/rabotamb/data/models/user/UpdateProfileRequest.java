package com.example.rabotamb.data.models.user;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("age")
    private Integer age;  // Đổi thành Integer để có thể null

    @SerializedName("gender")
    private String gender;

    @SerializedName("address")
    private String address;

    public UpdateProfileRequest(String name, Integer age, String gender, String address) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.address = address;
    }
}