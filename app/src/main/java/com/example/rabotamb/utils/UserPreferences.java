package com.example.rabotamb.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.rabotamb.data.models.auth.LoginResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class UserPreferences {
    private static final String TAG = "UserPreferences";
    private static final String PREF_NAME = "AppPrefs";
    private final SharedPreferences prefs;
    private final Gson gson;

    public UserPreferences(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    // Company class với đúng cấu trúc
    public static class Company {
        private String _id;
        private String name;
        private String logo;

        public String get_id() { return _id; }
        public void set_id(String id) { this._id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLogo() { return logo; }
        public void setLogo(String logo) { this.logo = logo; }
    }

    public static class UserInfo {
        private String id;
        private String name;
        private String email;
        private int age;
        private String gender;
        private String address;
        private boolean isActived;
        private boolean isDeleted;
        private LoginResponse.Role role;
        private List<LoginResponse.Permission> permissions;
        private Company company;

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public int getAge() { return age; }
        public String getGender() { return gender; }
        public String getAddress() { return address; }
        public boolean isActived() { return isActived; }
        public boolean isDeleted() { return isDeleted; }
        public LoginResponse.Role getRole() { return role; }
        public List<LoginResponse.Permission> getPermissions() { return permissions; }
        public Company getCompany() { return company; }

        // Setters
        public void setId(String id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setAge(int age) { this.age = age; }
        public void setGender(String gender) { this.gender = gender; }
        public void setAddress(String address) { this.address = address; }
        public void setActived(boolean actived) { isActived = actived; }
        public void setDeleted(boolean deleted) { isDeleted = deleted; }
        public void setRole(LoginResponse.Role role) { this.role = role; }
        public void setPermissions(List<LoginResponse.Permission> permissions) { this.permissions = permissions; }

        // Special setter for Company that converts from LoginResponse.Company
        public void setCompany(LoginResponse.Company responseCompany) {
            if (responseCompany != null) {
                Company company = new Company();
                company.set_id(responseCompany.get_id());
                company.setName(responseCompany.getName());
                company.setLogo(responseCompany.getLogo());
                this.company = company;

                // Log để debug
                Log.d(TAG, "Setting company - ID: " + company.get_id());
                Log.d(TAG, "Setting company - Name: " + company.getName());
            } else {
                this.company = null;
                Log.d(TAG, "Setting company to null - response company was null");
            }
        }
    }

    public UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo();

        userInfo.setId(prefs.getString("userId", ""));
        userInfo.setName(prefs.getString("userName", ""));
        userInfo.setEmail(prefs.getString("userEmail", ""));
        userInfo.setAge(prefs.getInt("userAge", 0));
        userInfo.setGender(prefs.getString("userGender", ""));
        userInfo.setAddress(prefs.getString("userAddress", ""));
        userInfo.setActived(prefs.getBoolean("isActived", false));
        userInfo.setDeleted(prefs.getBoolean("isDeleted", false));

        // Đọc Role
        String roleJson = prefs.getString("role", "");
        if (!roleJson.isEmpty()) {
            try {
                LoginResponse.Role role = gson.fromJson(roleJson, LoginResponse.Role.class);
                userInfo.setRole(role);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing role JSON: " + e.getMessage());
            }
        }

        // Đọc Permissions
        String permissionsJson = prefs.getString("permissions", "");
        if (!permissionsJson.isEmpty()) {
            try {
                Type type = new TypeToken<List<LoginResponse.Permission>>(){}.getType();
                List<LoginResponse.Permission> permissions = gson.fromJson(permissionsJson, type);
                userInfo.setPermissions(permissions);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing permissions JSON: " + e.getMessage());
            }
        }

        // Đọc Company
        String companyJson = prefs.getString("company", "");
        if (!companyJson.isEmpty()) {
            try {
                Company company = gson.fromJson(companyJson, Company.class);
                if (company != null) {
                    Log.d(TAG, "Retrieved company - ID: " + company.get_id());
                    Log.d(TAG, "Retrieved company - Name: " + company.getName());
                    userInfo.company = company;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing company JSON: " + e.getMessage());
            }
        }

        return userInfo;
    }

    public void saveUserInfo(UserInfo userInfo) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("userId", userInfo.getId());
        editor.putString("userName", userInfo.getName());
        editor.putString("userEmail", userInfo.getEmail());
        editor.putInt("userAge", userInfo.getAge());
        editor.putString("userGender", userInfo.getGender());
        editor.putString("userAddress", userInfo.getAddress());
        editor.putBoolean("isActived", userInfo.isActived());
        editor.putBoolean("isDeleted", userInfo.isDeleted());

        // Lưu Role
        if (userInfo.getRole() != null) {
            String roleJson = gson.toJson(userInfo.getRole());
            editor.putString("role", roleJson);
        }

        // Lưu Permissions
        if (userInfo.getPermissions() != null) {
            String permissionsJson = gson.toJson(userInfo.getPermissions());
            editor.putString("permissions", permissionsJson);
        }

        // Lưu Company
        if (userInfo.getCompany() != null) {
            String companyJson = gson.toJson(userInfo.getCompany());
            Log.d(TAG, "Saving company JSON: " + companyJson);
            editor.putString("company", companyJson);
        } else {
            Log.d(TAG, "Company is null, removing from preferences");
            editor.remove("company");
        }

        editor.apply();
    }

    public String getToken() {
        String token = prefs.getString("token", "");
        if (token.isEmpty()) {
            return null;
        }
        return token.startsWith("Bearer ") ? token : "Bearer " + token;
    }

    private String getRawToken() {
        return prefs.getString("token", "");
    }

    public boolean isLoggedIn() {
        return !getRawToken().isEmpty();
    }

    public boolean isHRWithCompany() {
        UserInfo userInfo = getUserInfo();
        return userInfo != null &&
                userInfo.getRole() != null &&
                "HR_ROLE".equals(userInfo.getRole().getName()) &&
                userInfo.getCompany() != null;
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }
}