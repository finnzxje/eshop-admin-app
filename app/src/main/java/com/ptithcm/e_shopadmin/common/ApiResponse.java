package com.ptithcm.e_shopadmin.common;

import org.json.JSONObject;

public class ApiResponse {
    private int statusCode;
    private String body;

    public ApiResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

    public String getErrorMessage() {
        if (body == null || body.trim().isEmpty()) {
            return "Request failed. Please try again.";
        }

        try {
            JSONObject object = new JSONObject(body);
            String message = object.optString("message", "");
            if (!message.trim().isEmpty()) {
                return message;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (statusCode == 401) {
            return "Incorrect email or password.";
        }
        if (statusCode == 403) {
            return "Access denied.";
        }
        return "Request failed. Please try again.";
    }
}
