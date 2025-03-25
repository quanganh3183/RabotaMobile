package com.example.rabotamb.data.models.job;

public enum Level {
    FULLTIME("Full Time"),
    PARTTIME("Part Time"),
    OTHER("Khác");

    private final String displayName;

    Level(String displayName) {
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