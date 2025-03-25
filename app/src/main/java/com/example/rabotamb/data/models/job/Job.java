package com.example.rabotamb.data.models.job;

import java.util.List;

public class Job {
    private String _id;
    private String name;
    private List<Skill> skills;
    private Company company;
    private String location;
    private int salary;
    private int quantity;
    private String level;
    private String description;
    private String startDate;
    private String endDate;
    private boolean isActive;
    private User createdBy;
    private boolean isDeleted;
    private String deletedAt;
    private String createdAt;
    private String updatedAt;
    private String requirements;

    // Định nghĩa một lần cho cả hai method
    public String getId() { return _id; }
    public String get_id() { return _id; }

    // Skill nested class
    public static class Skill {
        private String _id;
        private String name;

        public String get_id() { return _id; }
        public String getName() { return name; }
    }

    // Company nested class
    public static class Company {
        private String _id;
        private String name;
        private String logo;

        public String get_id() { return _id; }
        public String getId() { return _id; }
        public String getName() { return name; }
        public String getLogo() { return logo; }
    }

    // User nested class
    public static class User {
        private String _id;
        private String email;

        public String get_id() { return _id; }
        public String getEmail() { return email; }
    }

    // Getters (bỏ get_id() ở đây vì đã định nghĩa ở trên)
    public String getName() { return name; }
    public List<Skill> getSkills() { return skills; }
    public Company getCompany() { return company; }
    public String getLocation() { return location; }
    public int getSalary() { return salary; }
    public int getQuantity() { return quantity; }
    public String getLevel() { return level; }
    public String getDescription() { return description; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public boolean isActive() { return isActive; }
    public User getCreatedBy() { return createdBy; }
    public boolean isDeleted() { return isDeleted; }
    public String getDeletedAt() { return deletedAt; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
}