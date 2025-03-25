package com.example.rabotamb.data.models.job;

public enum Location {
    HANOI("Hà Nội"),
    HOCHIMINH("Hồ Chí Minh"),
    DANANG("Đà Nẵng"),
    OTHER("Khác");

    private final String displayName;

    Location(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}