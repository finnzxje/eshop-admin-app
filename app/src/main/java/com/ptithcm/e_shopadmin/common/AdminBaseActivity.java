package com.ptithcm.e_shopadmin.common;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.e_shopadmin.LoginActivity;

public class AdminBaseActivity extends AppCompatActivity {
    protected SessionManager sessionManager;

    protected boolean requireAdminSession() {
        sessionManager = new SessionManager(this);
        if (!sessionManager.hasValidAdminSession()) {
            openLogin();
            return false;
        }
        return true;
    }

    protected void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
