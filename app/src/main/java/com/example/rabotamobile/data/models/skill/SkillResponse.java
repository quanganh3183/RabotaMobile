package com.example.rabotamb.data.models.skill;

import java.util.List;

public class SkillResponse {
    private int statusCode;
    private String message;
    private SkillData data;

    public int getStatusCode() { return statusCode; }
    public String getMessage() { return message; }
    public SkillData getData() { return data; }

    public static class SkillData {
        private SkillMeta meta;
        private List<Skill> result;

        public SkillMeta getMeta() { return meta; }
        public List<Skill> getResult() { return result; }
    }

    public static class SkillMeta {
        private int current;
        private int pageSize;
        private int pages;
        private int total;

        public int getCurrent() { return current; }
        public int getPageSize() { return pageSize; }
        public int getPages() { return pages; }
        public int getTotal() { return total; }
    }
}