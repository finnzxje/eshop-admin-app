package com.ptithcm.e_shopadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.model.User;

import java.util.List;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.row_user, null);
        }

        TextView tvUserEmail = view.findViewById(R.id.tvUserEmail);
        TextView tvUserFullName = view.findViewById(R.id.tvUserFullName);
        TextView tvUserRoles = view.findViewById(R.id.tvUserRoles);
        TextView tvUserEnabled = view.findViewById(R.id.tvUserEnabled);
        TextView tvUserCreatedAt = view.findViewById(R.id.tvUserCreatedAt);

        User user = userList.get(position);
        tvUserEmail.setText(user.getEmail());
        tvUserFullName.setText(user.getFullName());
        tvUserRoles.setText("Roles: " + user.getRolesText());
        tvUserEnabled.setText(user.isEnabled() ? "Active" : "Disabled");
        tvUserCreatedAt.setText(formatCreatedAt(user.getCreatedAt()));

        return view;
    }

    private String formatCreatedAt(String createdAt) {
        if (createdAt == null || createdAt.trim().isEmpty()) {
            return "Created: Unknown";
        }
        return "Created: " + createdAt;
    }
}
