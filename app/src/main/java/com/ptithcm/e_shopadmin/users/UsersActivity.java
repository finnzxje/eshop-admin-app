package com.ptithcm.e_shopadmin.users;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.common.AdminBaseActivity;

public class UsersActivity extends AdminBaseActivity {
    private TextView tvPlaceholderTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireAdminSession()) {
            return;
        }

        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Only ADMIN can open User Management.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_placeholder);

        tvPlaceholderTitle = findViewById(R.id.tvPlaceholderTitle);
        tvPlaceholderTitle.setText("Users");
    }
}
