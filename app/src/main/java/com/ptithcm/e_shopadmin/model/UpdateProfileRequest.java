package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String phone;

    public UpdateProfileRequest(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public JSONObject toJson() throws Exception {
        JSONObject object = new JSONObject();
        object.put("firstName", firstName);
        object.put("lastName", lastName);
        object.put("phone", phone);
        return object;
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
}
