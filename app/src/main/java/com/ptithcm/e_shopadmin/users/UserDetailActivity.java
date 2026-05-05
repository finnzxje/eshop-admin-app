package com.ptithcm.e_shopadmin.users;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.common.AdminBaseActivity;
import com.ptithcm.e_shopadmin.common.ApiClient;
import com.ptithcm.e_shopadmin.common.ApiResponse;
import com.ptithcm.e_shopadmin.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

public class UserDetailActivity extends AdminBaseActivity {
    public static final String EXTRA_USER_ID = "userId";

    private TextView tvUserDetailTitle;
    private TextView tvUserDetailStatus;
    private TextView tvUserDetailEmail;
    private TextView tvUserDetailName;
    private TextView tvUserDetailEnabled;
    private TextView tvUserDetailRoles;
    private TextView tvUserDetailCreatedAt;
    private TextView tvUserDetailUpdatedAt;
    private TextView tvUserDetailEmailVerified;
    private TextView tvUserAddresses;
    private Button btnBackToUsers;
    private Button btnToggleUserStatus;
    private Button btnSaveUserRole;
    private Spinner spUserDetailRole;

    private String userId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireAdminSession()) {
            return;
        }

        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Only ADMIN can open User Details.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userId = getIntent().getStringExtra(EXTRA_USER_ID);
        if (userId == null || userId.trim().isEmpty()) {
            Toast.makeText(this, "Missing user ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_user_detail);

        initViews();
        setupRoleSpinner();
        initListeners();
        loadUserDetail();
    }

    private void initViews() {
        tvUserDetailTitle = findViewById(R.id.tvUserDetailTitle);
        tvUserDetailStatus = findViewById(R.id.tvUserDetailStatus);
        tvUserDetailEmail = findViewById(R.id.tvUserDetailEmail);
        tvUserDetailName = findViewById(R.id.tvUserDetailName);
        tvUserDetailEnabled = findViewById(R.id.tvUserDetailEnabled);
        tvUserDetailRoles = findViewById(R.id.tvUserDetailRoles);
        tvUserDetailCreatedAt = findViewById(R.id.tvUserDetailCreatedAt);
        tvUserDetailUpdatedAt = findViewById(R.id.tvUserDetailUpdatedAt);
        tvUserDetailEmailVerified = findViewById(R.id.tvUserDetailEmailVerified);
        tvUserAddresses = findViewById(R.id.tvUserAddresses);
        btnBackToUsers = findViewById(R.id.btnBackToUsers);
        btnToggleUserStatus = findViewById(R.id.btnToggleUserStatus);
        btnSaveUserRole = findViewById(R.id.btnSaveUserRole);
        spUserDetailRole = findViewById(R.id.spUserDetailRole);
    }

    private void setupRoleSpinner() {
        String[] roleLabels = {"ADMIN", "STAFF", "CUSTOMER"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roleLabels);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUserDetailRole.setAdapter(roleAdapter);
    }

    private void initListeners() {
        btnBackToUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnToggleUserStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserStatus();
            }
        });

        btnSaveUserRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserRole();
            }
        });
    }

    private void loadUserDetail() {
        setLoading(true, "Loading user detail...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiResponse response = ApiClient.get("/api/admin/users/" + userId, sessionManager.getAccessToken());
                    if (response.isSuccessful()) {
                        final JSONObject object = new JSONObject(response.getBody());
                        currentUser = User.fromJson(object);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fillUserDetail(object);
                                setLoading(false, "");
                            }
                        });
                    } else {
                        handleRequestFailure(response, "Could not load user detail.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Could not load user detail. Please try again.");
                }
            }
        });
        thread.start();
    }

    private void fillUserDetail(JSONObject object) {
        tvUserDetailTitle.setText("User Detail");
        tvUserDetailEmail.setText(currentUser.getEmail());
        tvUserDetailName.setText(currentUser.getFullName());
        tvUserDetailEnabled.setText(currentUser.isEnabled() ? "Status: Active" : "Status: Disabled");
        tvUserDetailRoles.setText("Roles: " + currentUser.getRolesText());
        tvUserDetailCreatedAt.setText("Created: " + valueOrUnknown(currentUser.getCreatedAt()));
        tvUserDetailUpdatedAt.setText("Updated: " + valueOrUnknown(currentUser.getUpdatedAt()));
        tvUserDetailEmailVerified.setText("Email verified: " + valueOrNotVerified(currentUser.getEmailVerifiedAt()));
        btnToggleUserStatus.setText(currentUser.isEnabled() ? "Disable User" : "Enable User");
        selectCurrentRole();
        tvUserAddresses.setText(parseAddresses(object.optJSONArray("addresses")));
    }

    private void selectCurrentRole() {
        String role = "";
        if (currentUser.getRoles() != null && !currentUser.getRoles().isEmpty()) {
            role = currentUser.getRoles().get(0).toUpperCase(Locale.US);
        }

        if ("STAFF".equals(role)) {
            spUserDetailRole.setSelection(1);
        } else if ("CUSTOMER".equals(role)) {
            spUserDetailRole.setSelection(2);
        } else {
            spUserDetailRole.setSelection(0);
        }
    }

    private String parseAddresses(JSONArray addresses) {
        if (addresses == null || addresses.length() == 0) {
            return "No saved addresses.";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < addresses.length(); i++) {
            JSONObject address = addresses.optJSONObject(i);
            if (address == null) {
                continue;
            }

            if (builder.length() > 0) {
                builder.append("\n\n");
            }
            builder.append(address.optString("fullName", "Unknown"));
            if (address.optBoolean("isDefault", false)) {
                builder.append(" (Default)");
            }
            builder.append("\nPhone: ").append(address.optString("phoneNumber", "Unknown"));
            builder.append("\n").append(address.optString("addressLine1", ""));

            String addressLine2 = address.optString("addressLine2", "");
            if (!addressLine2.trim().isEmpty()) {
                builder.append("\n").append(addressLine2);
            }

            builder.append("\n")
                    .append(address.optString("city", ""))
                    .append(", ")
                    .append(address.optString("state", ""))
                    .append(" ")
                    .append(address.optString("zipCode", ""));
        }

        if (builder.length() == 0) {
            return "No saved addresses.";
        }
        return builder.toString();
    }

    private void updateUserStatus() {
        if (currentUser == null) {
            return;
        }

        setLoading(true, currentUser.isEnabled() ? "Disabling user..." : "Enabling user...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject body = new JSONObject();
                    body.put("enabled", !currentUser.isEnabled());
                    ApiResponse response = ApiClient.patchJson("/api/admin/users/" + userId + "/status", body, sessionManager.getAccessToken());

                    if (response.isSuccessful()) {
                        final JSONObject object = new JSONObject(response.getBody());
                        currentUser = User.fromJson(object);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fillUserDetail(object);
                                setLoading(false, "");
                                Toast.makeText(UserDetailActivity.this, "User status updated.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        handleRequestFailure(response, "Could not update user status.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Could not update user status.");
                }
            }
        });
        thread.start();
    }

    private void updateUserRole() {
        if (currentUser == null) {
            return;
        }

        final String selectedRole = spUserDetailRole.getSelectedItem().toString();
        setLoading(true, "Saving user role...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray roles = new JSONArray();
                    roles.put(selectedRole);

                    JSONObject body = new JSONObject();
                    body.put("roles", roles);

                    ApiResponse response = ApiClient.putJson("/api/admin/users/" + userId + "/roles", body, sessionManager.getAccessToken());
                    if (response.isSuccessful()) {
                        final JSONObject object = new JSONObject(response.getBody());
                        currentUser = User.fromJson(object);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fillUserDetail(object);
                                setLoading(false, "");
                                Toast.makeText(UserDetailActivity.this, "User role updated.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        handleRequestFailure(response, "Could not update user role.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Could not update user role.");
                }
            }
        });
        thread.start();
    }

    private void handleRequestFailure(ApiResponse response, String fallbackMessage) {
        if (response.getStatusCode() == 401) {
            sessionManager.clearSession();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UserDetailActivity.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                    openLogin();
                }
            });
        } else {
            String message = response.getErrorMessage();
            if (message == null || message.trim().isEmpty()) {
                message = fallbackMessage;
            }
            showError(message);
        }
    }

    private void showError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoading(false, "");
                tvUserDetailStatus.setText(message);
                tvUserDetailStatus.setVisibility(View.VISIBLE);
                Toast.makeText(UserDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading, String message) {
        btnBackToUsers.setEnabled(!loading);
        btnToggleUserStatus.setEnabled(!loading && currentUser != null);
        btnSaveUserRole.setEnabled(!loading && currentUser != null);

        if (loading) {
            tvUserDetailStatus.setText(message);
            tvUserDetailStatus.setVisibility(View.VISIBLE);
        } else if (message == null || message.trim().isEmpty()) {
            tvUserDetailStatus.setText("");
            tvUserDetailStatus.setVisibility(View.GONE);
        }
    }

    private String valueOrUnknown(String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value)) {
            return "Unknown";
        }
        return value;
    }

    private String valueOrNotVerified(String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value)) {
            return "Not verified";
        }
        return value;
    }
}
