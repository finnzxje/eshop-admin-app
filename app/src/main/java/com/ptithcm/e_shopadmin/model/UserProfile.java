package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class UserProfile {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private boolean enabled;
    private String emailVerifiedAt;
    private String createdAt;
    private String updatedAt;

    public static UserProfile fromJson(JSONObject object) {
        UserProfile profile = new UserProfile();
        profile.setId(optText(object, "id"));
        profile.setEmail(optText(object, "email"));
        profile.setFirstName(optText(object, "firstName"));
        profile.setLastName(optText(object, "lastName"));
        profile.setPhone(optText(object, "phone"));
        profile.setEnabled(object.optBoolean("enabled", false));
        profile.setEmailVerifiedAt(optText(object, "emailVerifiedAt"));
        profile.setCreatedAt(optText(object, "createdAt"));
        profile.setUpdatedAt(optText(object, "updatedAt"));
        return profile;
    }

    private static String optText(JSONObject object, String key) {
        if (!object.has(key) || object.isNull(key)) {
            return "";
        }
        return object.optString(key, "");
    }

    public String getFullName() {
        return (firstName + " " + lastName).trim();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(String emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
