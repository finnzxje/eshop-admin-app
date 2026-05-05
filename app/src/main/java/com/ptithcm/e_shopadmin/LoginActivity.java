package com.ptithcm.e_shopadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.e_shopadmin.common.ApiClient;
import com.ptithcm.e_shopadmin.common.ApiResponse;
import com.ptithcm.e_shopadmin.common.SessionManager;
import com.ptithcm.e_shopadmin.model.User;

import org.json.JSONObject;

import java.net.UnknownHostException;

public class LoginActivity extends AppCompatActivity {
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView tvPasswordHelp;
    private TextView tvLoginStatus;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        if (sessionManager.hasValidAdminSession()) {
            openAdminHome();
            return;
        }

        initViews();
        initListeners();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvPasswordHelp = findViewById(R.id.tvPasswordHelp);
        tvLoginStatus = findViewById(R.id.tvLoginStatus);
    }

    private void initListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogin();
            }
        });

        tvPasswordHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "Password reset will be added later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Please enter your email");
            edtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Please enter your password");
            edtPassword.requestFocus();
            return;
        }

        login(email, password);
    }

    private void login(final String email, final String password) {
        setLoading(true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("email", email);
                    requestBody.put("password", password);

                    ApiResponse response = ApiClient.postJson("/api/auth/login", requestBody);
                    if (response.isSuccessful()) {
                        JSONObject responseObject = new JSONObject(response.getBody());
                        final User user = User.fromJson(responseObject);

                        if (!user.hasAdminAccess()) {
                            showLoginError("Only admin and staff accounts can use this app.");
                            return;
                        }

                        sessionManager.saveUser(user, response.getBody());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                openAdminHome();
                            }
                        });
                    } else {
                        showLoginError(response.getErrorMessage());
                    }
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                    showLoginError("Cannot resolve API host. Check emulator internet or DNS.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showLoginError("Cannot connect to the server. Please try again.");
                }
            }
        });
        thread.start();
    }

    private void showLoginError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoading(false);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        if (loading) {
            tvLoginStatus.setText("Logging in...");
            tvLoginStatus.setVisibility(View.VISIBLE);
        } else {
            tvLoginStatus.setText("");
            tvLoginStatus.setVisibility(View.GONE);
        }
    }

    private void openAdminHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
