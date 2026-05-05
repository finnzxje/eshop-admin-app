package com.ptithcm.e_shopadmin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.model.AdminMenuItem;

import java.util.List;

public class AdminMenuAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<AdminMenuItem> menuItems;

    public AdminMenuAdapter(Context context, int layout, List<AdminMenuItem> menuItems) {
        this.context = context;
        this.layout = layout;
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layout, null);

        TextView tvMenuTitle = convertView.findViewById(R.id.tvMenuTitle);
        TextView tvMenuDescription = convertView.findViewById(R.id.tvMenuDescription);

        AdminMenuItem item = menuItems.get(position);
        tvMenuTitle.setText(item.getTitle());
        tvMenuDescription.setText(item.getDescription());

        return convertView;
    }
}
