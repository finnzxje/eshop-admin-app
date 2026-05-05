package com.ptithcm.e_shopadmin.orders;

import android.os.Bundle;
import android.widget.TextView;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.common.AdminBaseActivity;

public class OrdersActivity extends AdminBaseActivity {
    private TextView tvPlaceholderTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireAdminSession()) {
            return;
        }

        setContentView(R.layout.activity_placeholder);

        tvPlaceholderTitle = findViewById(R.id.tvPlaceholderTitle);
        tvPlaceholderTitle.setText("Orders / Payments");
    }
}
