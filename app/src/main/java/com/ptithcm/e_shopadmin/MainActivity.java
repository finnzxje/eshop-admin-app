package com.ptithcm.e_shopadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.e_shopadmin.adapter.AdminMenuAdapter;
import com.ptithcm.e_shopadmin.common.SessionManager;
import com.ptithcm.e_shopadmin.dashboard.DashboardActivity;
import com.ptithcm.e_shopadmin.model.AdminMenuItem;
import com.ptithcm.e_shopadmin.orders.OrdersActivity;
import com.ptithcm.e_shopadmin.products.ProductsActivity;
import com.ptithcm.e_shopadmin.profile.ProfileActivity;
import com.ptithcm.e_shopadmin.support.SupportActivity;
import com.ptithcm.e_shopadmin.users.UsersActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView tvAdminWelcome;
    private TextView tvAdminRole;
    private ListView lvAdminMenu;
    private Button btnLogout;
    private SessionManager sessionManager;
    private ArrayList<AdminMenuItem> adminMenuItems;
    private AdminMenuAdapter adminMenuAdapter;

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
        setupAdminMenu();
        initListeners();
    }

    private void initViews() {
        tvAdminWelcome = findViewById(R.id.tvAdminWelcome);
        tvAdminRole = findViewById(R.id.tvAdminRole);
        lvAdminMenu = findViewById(R.id.lvAdminMenu);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void initListeners() {
        lvAdminMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                AdminMenuItem item = adminMenuItems.get(position);
                Intent intent = new Intent(MainActivity.this, item.getActivityClass());
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.clearSession();
                openLogin();
            }
        });
    }

    private void showAdminInfo() {
        tvAdminWelcome.setText("Welcome, " + sessionManager.getFullName());
        tvAdminRole.setText("Roles: " + sessionManager.getRolesText());
    }

    private void setupAdminMenu() {
        adminMenuItems = new ArrayList<>();
        addMenuItem(new AdminMenuItem("Dashboard", "Analytics summary and revenue overview", DashboardActivity.class, false));
        addMenuItem(new AdminMenuItem("Orders / Payments", "Manage orders and payment transactions", OrdersActivity.class, false));
        addMenuItem(new AdminMenuItem("Support", "Customer support conversations", SupportActivity.class, false));
        addMenuItem(new AdminMenuItem("Profile", "Admin account and password settings", ProfileActivity.class, false));
        addMenuItem(new AdminMenuItem("Products", "Product catalog management", ProductsActivity.class, false));
        addMenuItem(new AdminMenuItem("Users", "User management", UsersActivity.class, true));

        adminMenuAdapter = new AdminMenuAdapter(this, R.layout.item_admin_menu, adminMenuItems);
        lvAdminMenu.setAdapter(adminMenuAdapter);
    }

    private void addMenuItem(AdminMenuItem item) {
        if (!item.isAdminOnly() || sessionManager.isAdmin()) {
            adminMenuItems.add(item);
        }
    }

    private void openLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
