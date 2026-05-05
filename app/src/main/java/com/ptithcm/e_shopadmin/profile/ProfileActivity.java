package com.ptithcm.e_shopadmin.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.common.ApiClient;
import com.ptithcm.e_shopadmin.common.ApiResponse;
import com.ptithcm.e_shopadmin.common.AdminBaseActivity;
import com.ptithcm.e_shopadmin.model.ChangePasswordRequest;
import com.ptithcm.e_shopadmin.model.UpdateProfileRequest;
import com.ptithcm.e_shopadmin.model.UserProfile;

import org.json.JSONObject;

public class ProfileActivity extends AdminBaseActivity {
    private TextView tvProfileState;
    private TextView tvProfileId;
    private TextView tvProfileEmail;
    private TextView tvProfileFullName;
    private TextView tvProfileRoles;
    private TextView tvProfileEnabled;
    private TextView tvProfileEmailVerifiedAt;
    private TextView tvProfileCreatedAt;
    private TextView tvProfileUpdatedAt;
    private EditText edtProfileFirstName;
    private EditText edtProfileLastName;
    private EditText edtProfilePhone;
    private EditText edtCurrentPassword;
    private EditText edtNewPassword;
    private EditText edtConfirmPassword;
    private Button btnProfileBack;
    private Button btnProfileRefresh;
    private Button btnSaveProfile;
    private Button btnChangePassword;
    private Button btnProfileLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireAdminSession()) {
            return;
        }

        setContentView(R.layout.activity_profile);

        initViews();
        initListeners();
        loadProfile();
    }

    private void initViews() {
        tvProfileState = findViewById(R.id.tvProfileState);
        tvProfileId = findViewById(R.id.tvProfileId);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileFullName = findViewById(R.id.tvProfileFullName);
        tvProfileRoles = findViewById(R.id.tvProfileRoles);
        tvProfileEnabled = findViewById(R.id.tvProfileEnabled);
        tvProfileEmailVerifiedAt = findViewById(R.id.tvProfileEmailVerifiedAt);
        tvProfileCreatedAt = findViewById(R.id.tvProfileCreatedAt);
        tvProfileUpdatedAt = findViewById(R.id.tvProfileUpdatedAt);
        edtProfileFirstName = findViewById(R.id.edtProfileFirstName);
        edtProfileLastName = findViewById(R.id.edtProfileLastName);
        edtProfilePhone = findViewById(R.id.edtProfilePhone);
        edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnProfileBack = findViewById(R.id.btnProfileBack);
        btnProfileRefresh = findViewById(R.id.btnProfileRefresh);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnProfileLogout = findViewById(R.id.btnProfileLogout);
    }

    private void initListeners() {
        btnProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnProfileRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadProfile();
            }
        });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        btnProfileLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.clearSession();
                openLogin();
            }
        });
    }

    private void loadProfile() {
        setProfileLoading(true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiResponse response = ApiClient.getCurrentUserProfile(sessionManager.getAccessToken());
                    if (response.getStatusCode() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    if (!response.isSuccessful()) {
                        showError(response.getErrorMessage());
                        return;
                    }

                    final UserProfile profile = UserProfile.fromJson(new JSONObject(response.getBody()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProfile(profile);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Cannot load profile. Please try again.");
                }
            }
        });
        thread.start();
    }

    private void updateProfile() {
        final String firstName = edtProfileFirstName.getText().toString().trim();
        final String lastName = edtProfileLastName.getText().toString().trim();
        final String phone = edtProfilePhone.getText().toString().trim();

        if (!validateProfile(firstName, lastName, phone)) {
            return;
        }

        setSavingProfile(true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UpdateProfileRequest request = new UpdateProfileRequest(firstName, lastName, phone);
                    ApiResponse response = ApiClient.updateCurrentUserProfile(sessionManager.getAccessToken(), request.toJson());
                    if (response.getStatusCode() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    if (!response.isSuccessful()) {
                        showError(response.getErrorMessage());
                        setSavingProfile(false);
                        return;
                    }

                    final UserProfile profile = UserProfile.fromJson(new JSONObject(response.getBody()));
                    sessionManager.updateProfileInfo(profile.getEmail(), profile.getFirstName(), profile.getLastName());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProfile(profile);
                            setSavingProfile(false);
                            showSuccess("Profile updated successfully.");
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Cannot update profile. Please try again.");
                    setSavingProfile(false);
                }
            }
        });
        thread.start();
    }

    private void changePassword() {
        final String currentPassword = edtCurrentPassword.getText().toString();
        final String newPassword = edtNewPassword.getText().toString();
        final String confirmPassword = edtConfirmPassword.getText().toString();

        if (!validatePassword(currentPassword, newPassword, confirmPassword)) {
            return;
        }

        setChangingPassword(true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword, confirmPassword);
                    ApiResponse response = ApiClient.changePassword(sessionManager.getAccessToken(), request.toJson());
                    if (response.getStatusCode() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    if (!response.isSuccessful()) {
                        showError(response.getErrorMessage());
                        setChangingPassword(false);
                        return;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            edtCurrentPassword.setText("");
                            edtNewPassword.setText("");
                            edtConfirmPassword.setText("");
                            setChangingPassword(false);
                            showSuccess("Password changed successfully.");
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Cannot change password. Please try again.");
                    setChangingPassword(false);
                }
            }
        });
        thread.start();
    }

    private boolean validateProfile(String firstName, String lastName, String phone) {
        if (firstName.isEmpty()) {
            edtProfileFirstName.setError("First name is required");
            edtProfileFirstName.requestFocus();
            return false;
        }
        if (firstName.length() > 80) {
            edtProfileFirstName.setError("First name must be 80 characters or fewer");
            edtProfileFirstName.requestFocus();
            return false;
        }
        if (lastName.isEmpty()) {
            edtProfileLastName.setError("Last name is required");
            edtProfileLastName.requestFocus();
            return false;
        }
        if (lastName.length() > 80) {
            edtProfileLastName.setError("Last name must be 80 characters or fewer");
            edtProfileLastName.requestFocus();
            return false;
        }
        if (phone.length() > 30) {
            edtProfilePhone.setError("Phone number must be 30 characters or fewer");
            edtProfilePhone.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validatePassword(String currentPassword, String newPassword, String confirmPassword) {
        if (currentPassword.trim().isEmpty()) {
            edtCurrentPassword.setError("Current password is required");
            edtCurrentPassword.requestFocus();
            return false;
        }
        if (newPassword.trim().isEmpty()) {
            edtNewPassword.setError("New password is required");
            edtNewPassword.requestFocus();
            return false;
        }
        if (newPassword.length() < 6) {
            edtNewPassword.setError("New password must be at least 6 characters");
            edtNewPassword.requestFocus();
            return false;
        }
        if (newPassword.equals(currentPassword)) {
            edtNewPassword.setError("New password must be different from current password");
            edtNewPassword.requestFocus();
            return false;
        }
        if (confirmPassword.trim().isEmpty()) {
            edtConfirmPassword.setError("Password confirmation is required");
            edtConfirmPassword.requestFocus();
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            edtConfirmPassword.setError("Password confirmation must match");
            edtConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void showProfile(UserProfile profile) {
        setProfileLoading(false);
        tvProfileState.setVisibility(View.GONE);

        tvProfileId.setText("User ID: " + emptyToDash(profile.getId()));
        tvProfileEmail.setText("Email: " + emptyToDash(profile.getEmail()));
        tvProfileFullName.setText("Name: " + emptyToDash(profile.getFullName()));
        tvProfileRoles.setText("Roles: " + emptyToDash(sessionManager.getRolesText()));
        tvProfileEnabled.setText("Enabled: " + (profile.isEnabled() ? "true" : "false"));
        tvProfileEmailVerifiedAt.setText("Email Verified At: " + emptyToDash(profile.getEmailVerifiedAt()));
        tvProfileCreatedAt.setText("Created At: " + emptyToDash(profile.getCreatedAt()));
        tvProfileUpdatedAt.setText("Updated At: " + emptyToDash(profile.getUpdatedAt()));

        edtProfileFirstName.setText(profile.getFirstName());
        edtProfileLastName.setText(profile.getLastName());
        edtProfilePhone.setText(profile.getPhone());
    }

    private void setProfileLoading(final boolean loading) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnProfileRefresh.setEnabled(!loading);
                btnSaveProfile.setEnabled(!loading);
                if (loading) {
                    tvProfileState.setText("Loading profile...");
                    tvProfileState.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setSavingProfile(final boolean saving) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnSaveProfile.setEnabled(!saving);
                btnProfileRefresh.setEnabled(!saving);
            }
        });
    }

    private void setChangingPassword(final boolean changing) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnChangePassword.setEnabled(!changing);
                edtCurrentPassword.setEnabled(!changing);
                edtNewPassword.setEnabled(!changing);
                edtConfirmPassword.setEnabled(!changing);
            }
        });
    }

    private void showSuccess(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvProfileState.setText(message);
                tvProfileState.setVisibility(View.VISIBLE);
                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setProfileLoading(false);
                btnProfileRefresh.setEnabled(true);
                btnSaveProfile.setEnabled(true);
                btnChangePassword.setEnabled(true);
                edtCurrentPassword.setEnabled(true);
                edtNewPassword.setEnabled(true);
                edtConfirmPassword.setEnabled(true);
                tvProfileState.setText(message);
                tvProfileState.setVisibility(View.VISIBLE);
                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUnauthorized() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ProfileActivity.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                sessionManager.clearSession();
                openLogin();
            }
        });
    }

    private String emptyToDash(String value) {
        if (value == null || value.trim().isEmpty() || "null".equals(value)) {
            return "-";
        }
        return value;
    }
}
