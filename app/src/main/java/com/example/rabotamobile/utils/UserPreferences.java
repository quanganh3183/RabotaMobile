package com.example.rabotamb.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.rabotamb.data.models.auth.LoginResponse.Role;
import com.example.rabotamb.data.models.auth.LoginResponse.Permission;
import com.example.rabotamb.data.models.auth.LoginResponse.Company;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class UserPreferences {
    private static final String PREF_NAME = "AppPrefs";
    private final SharedPreferences prefs;
    private final Gson gson;

    public UserPreferences(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
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
        private Role role;
        private List<Permission> permissions;
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
        public Role getRole() { return role; }
        public List<Permission> getPermissions() { return permissions; }
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
        public void setRole(Role role) { this.role = role; }
        public void setPermissions(List<Permission> permissions) { this.permissions = permissions; }
        public void setCompany(Company company) { this.company = company; }
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
            Role role = gson.fromJson(roleJson, Role.class);
            userInfo.setRole(role);
        }

        // Đọc Permissions
        String permissionsJson = prefs.getString("permissions", "");
        if (!permissionsJson.isEmpty()) {
            Type type = new TypeToken<List<Permission>>(){}.getType();
            List<Permission> permissions = gson.fromJson(permissionsJson, type);
            userInfo.setPermissions(permissions);
        }

        // Đọc Company
        String companyJson = prefs.getString("company", "");
        if (!companyJson.isEmpty()) {
            Company company = gson.fromJson(companyJson, Company.class);
            userInfo.setCompany(company);
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
            editor.putString("company", companyJson);
        }

        editor.apply();
    }

    private String getRawToken() {
        return prefs.getString("token", "");
    }

    public String getToken() {
        String token = getRawToken();
        if (token.isEmpty()) {
            return null;
        }
        if (token.startsWith("Bearer ")) {
            return token;
        }
        return "Bearer " + token;
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