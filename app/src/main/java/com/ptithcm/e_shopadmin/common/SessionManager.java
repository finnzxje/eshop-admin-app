package com.ptithcm.e_shopadmin.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.ptithcm.e_shopadmin.model.User;

import java.util.List;

public class SessionManager {
    private static final String PREF_NAME = "admin_session";
    private static final String KEY_USER_JSON = "user_json";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_ROLES = "roles";

    private SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(User user, String rawJson) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_JSON, rawJson);
        editor.putString(KEY_ACCESS_TOKEN, user.getToken());
        editor.putString(KEY_REFRESH_TOKEN, user.getRefreshToken());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_FIRST_NAME, user.getFirstName());
        editor.putString(KEY_LAST_NAME, user.getLastName());
        editor.putBoolean(KEY_ENABLED, user.isEnabled());
        editor.putString(KEY_ROLES, joinRoles(user.getRoles()));
        editor.apply();
    }

    public boolean hasValidAdminSession() {
        String token = getAccessToken();
        return token != null && !token.trim().isEmpty() && hasAdminRole();
    }

    public boolean hasAdminRole() {
        String roles = getRolesText();
        return roles.contains("ADMIN") || roles.contains("STAFF");
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, "");
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    public String getFullName() {
        String firstName = sharedPreferences.getString(KEY_FIRST_NAME, "");
        String lastName = sharedPreferences.getString(KEY_LAST_NAME, "");
        String fullName = (firstName + " " + lastName).trim();
        if (fullName.isEmpty()) {
            return getEmail();
        }
        return fullName;
    }

    public String getRolesText() {
        return sharedPreferences.getString(KEY_ROLES, "");
    }

    public void clearSession() {
        sharedPreferences.edit().clear().apply();
    }

    private String joinRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < roles.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(roles.get(i));
        }
        return builder.toString();
    }
}
