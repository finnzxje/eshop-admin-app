package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class PaymentCustomer {
    private String id;
    private String email;
    private String firstName;
    private String lastName;

    public static PaymentCustomer fromJson(JSONObject object) {
        PaymentCustomer customer = new PaymentCustomer();
        if (object == null) {
            return customer;
        }

        customer.setId(object.optString("id", ""));
        customer.setEmail(object.optString("email", ""));
        customer.setFirstName(object.optString("firstName", ""));
        customer.setLastName(object.optString("lastName", ""));
        return customer;
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
