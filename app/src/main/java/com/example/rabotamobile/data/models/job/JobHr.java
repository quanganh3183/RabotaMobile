package com.example.rabotamb.data.models.job;

import com.example.rabotamb.data.models.skill.Skill;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.ArrayList;

public class JobHr {
    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("skills")
    private List<Skill> skills;

    @SerializedName("location")
    private String location;

    @SerializedName("salary")
    private int salary;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("level")
    private String level;

    @SerializedName("description")
    private String description;

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("company")
    private Company company;  // Thay đổi từ String thành Company object

    // Inner class for Company
    public static class Company {
        @SerializedName("_id")
        private String _id;

        @SerializedName("name")
        private String name;

        @SerializedName("logo")
        private String logo;

        public String get_id() { return _id; }
        public String getName() { return name; }
        public String getLogo() { return logo; }

        public void set_id(String id) { this._id = id; }
        public void setName(String name) { this.name = name; }
        public void setLogo(String logo) { this.logo = logo; }
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public List<Skill> getSkills() { return skills != null ? skills : new ArrayList<>(); }
    public String getLocation() { return location; }
    public int getSalary() { return salary; }
    public int getQuantity() { return quantity; }
    public String getLevel() { return level; }
    public String getDescription() { return description; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public boolean isActive() { return isActive; }
    public Company getCompany() { return company; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }
    public void setLocation(String location) { this.location = location; }
    public void setSalary(int salary) { this.salary = salary; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setLevel(String level) { this.level = level; }
    public void setDescription(String description) { this.description = description; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setActive(boolean active) { isActive = active; }
    public void setCompany(Company company) { this.company = company; }
}