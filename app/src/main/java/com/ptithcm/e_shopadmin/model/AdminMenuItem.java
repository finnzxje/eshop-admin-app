package com.ptithcm.e_shopadmin.model;

public class AdminMenuItem {
    private String title;
    private String description;
    private Class<?> activityClass;
    private boolean adminOnly;

    public AdminMenuItem(String title, String description, Class<?> activityClass, boolean adminOnly) {
        this.title = title;
        this.description = description;
        this.activityClass = activityClass;
        this.adminOnly = adminOnly;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Class<?> getActivityClass() {
        return activityClass;
    }

    public void setActivityClass(Class<?> activityClass) {
        this.activityClass = activityClass;
    }

    public boolean isAdminOnly() {
        return adminOnly;
    }

    public void setAdminOnly(boolean adminOnly) {
        this.adminOnly = adminOnly;
    }
}
