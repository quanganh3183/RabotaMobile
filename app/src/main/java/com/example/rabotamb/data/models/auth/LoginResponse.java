package com.example.rabotamb.data.models.auth;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LoginResponse {
    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("level")
    private String level;

    @SerializedName("data")
    private Data data;

    public static class Data {
        @SerializedName("access_token")
        private String accessToken;

        @SerializedName("user")
        private User user;

        public String getAccessToken() {
            return accessToken;
        }

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

        @SerializedName("company")
        private Company company;

        @SerializedName("age")
        private int age;

        @SerializedName("gender")
        private String gender;

        @SerializedName("address")
        private String address;

        @SerializedName("permissions")
        private List<Permission> permissions;

        @SerializedName("isDeleted")
        private boolean isDeleted;

        @SerializedName("isActived")
        private boolean isActived;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public Role getRole() { return role; }
        public Company getCompany() { return company; }
        public int getAge() { return age; }
        public String getGender() { return gender; }
        public String getAddress() { return address; }
        public List<Permission> getPermissions() { return permissions; }
        public boolean isDeleted() { return isDeleted; }
        public boolean isActived() { return isActived; }
    }

    public static class Role {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        public String getId() { return id; }
        public String getName() { return name; }
    }

    public static class Company {
        @SerializedName("_id")
        private String _id;

        @SerializedName("name")
        private String name;

        @SerializedName("logo")
        private String logo;

        public String get_id() { return _id; } // Sửa từ getId() thành get_id()
        public String getName() { return name; }
        public String getLogo() { return logo; }

        public void set_id(String id) { this._id = id; } // Sửa từ setId() thành set_id()
        public void setName(String name) { this.name = name; }
        public void setLogo(String logo) { this.logo = logo; }
    }

    public static class Permission {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("apiPath")
        private String apiPath;

        @SerializedName("method")
        private String method;

        @SerializedName("module")
        private String module;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getApiPath() { return apiPath; }
        public String getMethod() { return method; }
        public String getModule() { return module; }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getLevel() {
        return level;
    }

    public Data getData() {
        return data;
    }

    public String getToken() {
        return data != null ? data.getAccessToken() : null;
    }
}