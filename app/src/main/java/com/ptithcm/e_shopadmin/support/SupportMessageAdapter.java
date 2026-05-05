package com.ptithcm.e_shopadmin.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.model.SupportMessage;
import com.ptithcm.e_shopadmin.model.SupportUser;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SupportMessageAdapter extends BaseAdapter {
    private Context context;
    private List<SupportMessage> messages;
    private DateTimeFormatter dateFormatter;

    public SupportMessageAdapter(Context context, List<SupportMessage> messages) {
        this.context = context;
        this.messages = messages;
        dateFormatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm")
                .withZone(ZoneId.systemDefault());
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_support_message, null);
        }

        TextView tvMessageSender = convertView.findViewById(R.id.tvMessageSender);
        TextView tvMessageBody = convertView.findViewById(R.id.tvMessageBody);
        TextView tvMessageTime = convertView.findViewById(R.id.tvMessageTime);

        SupportMessage message = messages.get(position);
        tvMessageSender.setText(formatSender(message));
        tvMessageBody.setText(emptyToDash(message.getBody()));
        tvMessageTime.setText(formatDate(message.getCreatedAt()));

        if ("STAFF".equals(message.getSenderType())) {
            tvMessageSender.setTextColor(context.getColor(R.color.black));
        } else {
            tvMessageSender.setTextColor(context.getColor(R.color.adminTextSecondary));
        }

        return convertView;
    }

    private String formatSender(SupportMessage message) {
        String type = emptyToDash(message.getSenderType());
        SupportUser sender = message.getSender();
        if (sender == null) {
            return type;
        }

        String name = sender.getDisplayName();
        if (name == null || name.trim().isEmpty()) {
            return type;
        }
        return type + ": " + name;
    }

    private String formatDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        try {
            return dateFormatter.format(Instant.parse(value));
        } catch (Exception ex) {
            return value;
        }
    }

    private String emptyToDash(String value) {
        if (value == null || value.trim().isEmpty() || "null".equals(value)) {
            return "-";
        }
        return value;
    }
}
