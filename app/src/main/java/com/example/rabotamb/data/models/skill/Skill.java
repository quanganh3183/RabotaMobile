package com.example.rabotamb.data.models.skill;

public class Skill {
    private String _id;
    private String name;
    private boolean isDeleted;
    private String deletedAt;
    private String createdAt;
    private String updatedAt;

    // Constructor
    public Skill() {}

    // Constructor với tham số
    public Skill(String id, String name) {
        this._id = id;
        this.name = name;
    }

    // Getters
    public String getId() { return _id; }
    public String getName() { return name; }
    public boolean isDeleted() { return isDeleted; }
    public String getDeletedAt() { return deletedAt; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(String id) { this._id = id; }
    public void setName(String name) { this.name = name; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public void setDeletedAt(String deletedAt) { this.deletedAt = deletedAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return name;
    }
}