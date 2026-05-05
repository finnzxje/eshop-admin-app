package com.ptithcm.e_shopadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.e_shopadmin.common.SessionManager;

public class MainActivity extends AppCompatActivity {
    private TextView tvAdminWelcome;
    private TextView tvAdminRole;
    private Button btnLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (!sessionManager.hasValidAdminSession()) {
            openLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        initViews();
        showAdminInfo();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.clearSession();
                openLogin();
            }
        });
    }

    private void initViews() {
        tvAdminWelcome = findViewById(R.id.tvAdminWelcome);
        tvAdminRole = findViewById(R.id.tvAdminRole);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void showAdminInfo() {
        tvAdminWelcome.setText("Welcome, " + sessionManager.getFullName());
        tvAdminRole.setText("Roles: " + sessionManager.getRolesText());
    }

    private void openLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
