package com.ptithcm.e_shopadmin.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ptithcm.e_shopadmin.MainActivity;
import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.adapter.UserAdapter;
import com.ptithcm.e_shopadmin.common.AdminBaseActivity;
import com.ptithcm.e_shopadmin.common.ApiClient;
import com.ptithcm.e_shopadmin.common.ApiResponse;
import com.ptithcm.e_shopadmin.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class UsersActivity extends AdminBaseActivity {
    private EditText edtUserSearch;
    private Spinner spUserRole;
    private Spinner spUserEnabled;
    private Button btnBackToNavigator;
    private Button btnSearchUsers;
    private Button btnPreviousUserPage;
    private Button btnNextUserPage;
    private TextView tvUserListStatus;
    private TextView tvUserPageInfo;
    private ListView lvUsers;

    private ArrayList<User> userList;
    private UserAdapter userAdapter;
    private int currentPage = 0;
    private int totalPages = 1;
    private int pageSize = 10;
    private String selectedRole = "";
    private String selectedEnabled = "";

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

        setContentView(R.layout.activity_users);

        initViews();
        setupSpinners();
        setupUserList();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userAdapter != null) {
            loadUsers();
        }
    }

    private void initViews() {
        edtUserSearch = findViewById(R.id.edtUserSearch);
        spUserRole = findViewById(R.id.spUserRole);
        spUserEnabled = findViewById(R.id.spUserEnabled);
        btnBackToNavigator = findViewById(R.id.btnBackToNavigator);
        btnSearchUsers = findViewById(R.id.btnSearchUsers);
        btnPreviousUserPage = findViewById(R.id.btnPreviousUserPage);
        btnNextUserPage = findViewById(R.id.btnNextUserPage);
        tvUserListStatus = findViewById(R.id.tvUserListStatus);
        tvUserPageInfo = findViewById(R.id.tvUserPageInfo);
        lvUsers = findViewById(R.id.lvUsers);
    }

    private void setupSpinners() {
        String[] roleLabels = {"All roles", "ADMIN", "STAFF", "CUSTOMER"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roleLabels);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUserRole.setAdapter(roleAdapter);

        String[] enabledLabels = {"All statuses", "Active", "Disabled"};
        ArrayAdapter<String> enabledAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, enabledLabels);
        enabledAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUserEnabled.setAdapter(enabledAdapter);
    }

    private void setupUserList() {
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList);
        lvUsers.setAdapter(userAdapter);
    }

    private void initListeners() {
        btnBackToNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UsersActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        spUserRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    selectedRole = "";
                } else {
                    selectedRole = adapterView.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spUserEnabled.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 1) {
                    selectedEnabled = "true";
                } else if (position == 2) {
                    selectedEnabled = "false";
                } else {
                    selectedEnabled = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btnSearchUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage = 0;
                loadUsers();
            }
        });

        btnPreviousUserPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage > 0) {
                    currentPage--;
                    loadUsers();
                }
            }
        });

        btnNextUserPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage + 1 < totalPages) {
                    currentPage++;
                    loadUsers();
                }
            }
        });

        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                User user = userList.get(position);
                Intent intent = new Intent(UsersActivity.this, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.EXTRA_USER_ID, user.getId());
                startActivity(intent);
            }
        });
    }

    private void loadUsers() {
        setLoading(true, "Loading users...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiResponse response = ApiClient.get(buildUsersPath(), sessionManager.getAccessToken());

                    if (response.isSuccessful()) {
                        parseUsers(response.getBody());
                        showUsers();
                    } else if (response.getStatusCode() == 401) {
                        sessionManager.clearSession();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UsersActivity.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                                openLogin();
                            }
                        });
                    } else if (response.getStatusCode() == 403) {
                        showError("Access denied. User management requires ADMIN access.");
                    } else {
                        showError(response.getErrorMessage());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Could not load users. Please try again.");
                }
            }
        });
        thread.start();
    }

    private String buildUsersPath() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("/api/admin/users?");
        builder.append("page=").append(currentPage);
        builder.append("&size=").append(pageSize);
        builder.append("&sort=").append(URLEncoder.encode("createdAt,desc", "UTF-8"));

        String search = edtUserSearch.getText().toString().trim();
        if (!search.isEmpty()) {
            builder.append("&search=").append(URLEncoder.encode(search, "UTF-8"));
        }

        if (!selectedRole.isEmpty()) {
            builder.append("&role=").append(URLEncoder.encode(selectedRole, "UTF-8"));
        }

        if (!selectedEnabled.isEmpty()) {
            builder.append("&enabled=").append(URLEncoder.encode(selectedEnabled, "UTF-8"));
        }

        return builder.toString();
    }

    private void parseUsers(String body) throws Exception {
        JSONObject object = new JSONObject(body);
        JSONArray content = object.optJSONArray("content");

        userList.clear();
        if (content != null) {
            for (int i = 0; i < content.length(); i++) {
                userList.add(User.fromJson(content.getJSONObject(i)));
            }
        }

        totalPages = object.optInt("totalPages", 1);
        currentPage = object.optInt("page", currentPage);
        if (totalPages < 1) {
            totalPages = 1;
        }
    }

    private void showUsers() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoading(false, "");
                userAdapter.notifyDataSetChanged();
                updatePageButtons();

                if (userList.isEmpty()) {
                    tvUserListStatus.setText("No users found.");
                    tvUserListStatus.setVisibility(View.VISIBLE);
                } else {
                    tvUserListStatus.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoading(false, "");
                userList.clear();
                userAdapter.notifyDataSetChanged();
                tvUserListStatus.setText(message);
                tvUserListStatus.setVisibility(View.VISIBLE);
                updatePageButtons();
            }
        });
    }

    private void setLoading(boolean loading, String message) {
        btnBackToNavigator.setEnabled(!loading);
        btnSearchUsers.setEnabled(!loading);
        btnPreviousUserPage.setEnabled(!loading && currentPage > 0);
        btnNextUserPage.setEnabled(!loading && currentPage + 1 < totalPages);

        if (loading) {
            tvUserListStatus.setText(message);
            tvUserListStatus.setVisibility(View.VISIBLE);
        }
    }

    private void updatePageButtons() {
        tvUserPageInfo.setText("Page " + (currentPage + 1) + " of " + totalPages);
        btnPreviousUserPage.setEnabled(currentPage > 0);
        btnNextUserPage.setEnabled(currentPage + 1 < totalPages);
    }
}
