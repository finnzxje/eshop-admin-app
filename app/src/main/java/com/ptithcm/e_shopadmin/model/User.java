package com.ptithcm.e_shopadmin.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private String createdAt;
    private String updatedAt;
    private String emailVerifiedAt;
    private String token;
    private String refreshToken;
    private List<String> roles;

    public User() {
        roles = new ArrayList<>();
    }

    public static User fromJson(JSONObject object) {
        User user = new User();
        user.setId(object.optString("id", ""));
        user.setEmail(object.optString("email", ""));
        user.setFirstName(object.optString("firstName", ""));
        user.setLastName(object.optString("lastName", ""));
        user.setEnabled(object.optBoolean("enabled", false));
        user.setCreatedAt(object.optString("createdAt", ""));
        user.setUpdatedAt(object.optString("updatedAt", ""));
        user.setEmailVerifiedAt(object.optString("emailVerifiedAt", ""));
        user.setToken(object.optString("token", ""));
        user.setRefreshToken(object.optString("refreshToken", ""));

        JSONArray rolesArray = object.optJSONArray("roles");
        if (rolesArray != null) {
            for (int i = 0; i < rolesArray.length(); i++) {
                String role = rolesArray.optString(i, "");
                if (!role.trim().isEmpty()) {
                    user.getRoles().add(role);
                }
            }
        }

        return user;
    }

    public boolean hasAdminAccess() {
        for (int i = 0; i < roles.size(); i++) {
            String role = normalizeRole(roles.get(i));
            if ("ADMIN".equals(role) || "STAFF".equals(role)) {
                return true;
            }
        }
        return false;
    }

    private String normalizeRole(String role) {
        if (role == null) {
            return "";
        }

        String normalized = role.trim();
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring(5);
        }
        return normalized;
    }

    public String getFullName() {
        String fullName = (firstName + " " + lastName).trim();
        if (fullName.isEmpty()) {
            return email;
        }
        return fullName;
    }

    public String getRolesText() {
        if (roles == null || roles.isEmpty()) {
            return "No roles";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < roles.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(normalizeRole(roles.get(i)));
        }
        return builder.toString();
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public String getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(String emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
