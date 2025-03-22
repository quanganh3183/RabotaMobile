package com.example.rabotamb.data.models.job;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class JobHr {
    @SerializedName("_id")
    private String id;




    @SerializedName("name")
    private String name;

    @SerializedName("skills")
    private List<String> skills;

    @SerializedName("location")
    private String location;

    @SerializedName("salary")
    private int  salary;

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
    private Company company;

    // Nested Company class
    public static class Company {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        public Company(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // Existing getters
    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getSkills() { return skills; }
    public String getLocation() { return location; }
    public int getSalary() { return salary; }
    public int getQuantity() { return quantity; }
    public String getLevel() { return level; }
    public String getDescription() { return description; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public boolean isActive() { return isActive; }
    public Company getCompany() { return company; }

    // Existing setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSkills(List<String> skills) { this.skills = skills; }
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