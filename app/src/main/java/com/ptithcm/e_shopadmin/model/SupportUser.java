package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class SupportUser {
    private String id;
    private String email;
    private String firstName;
    private String lastName;

    public static SupportUser fromJson(JSONObject object) {
        SupportUser user = new SupportUser();
        if (object == null) {
            return user;
        }

        user.setId(object.optString("id", ""));
        user.setEmail(object.optString("email", ""));
        user.setFirstName(object.optString("firstName", ""));
        user.setLastName(object.optString("lastName", ""));
        return user;
    }

    public String getDisplayName() {
        String fullName = (firstName + " " + lastName).trim();
        if (fullName.isEmpty()) {
            return email;
        }
        return fullName;
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
}
