package com.ptithcm.e_shopadmin.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.model.SupportConversation;
import com.ptithcm.e_shopadmin.model.SupportMessage;
import com.ptithcm.e_shopadmin.model.SupportUser;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SupportConversationAdapter extends BaseAdapter {
    private Context context;
    private List<SupportConversation> conversations;
    private DateTimeFormatter dateFormatter;

    public SupportConversationAdapter(Context context, List<SupportConversation> conversations) {
        this.context = context;
        this.conversations = conversations;
        dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
                .withZone(ZoneId.systemDefault());
    }

    @Override
    public int getCount() {
        return conversations.size();
    }

    @Override
    public Object getItem(int position) {
        return conversations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_support_conversation, null);
        }

        TextView tvSupportSubject = convertView.findViewById(R.id.tvSupportSubject);
        TextView tvSupportCustomer = convertView.findViewById(R.id.tvSupportCustomer);
        TextView tvSupportStatus = convertView.findViewById(R.id.tvSupportStatus);
        TextView tvSupportLastMessage = convertView.findViewById(R.id.tvSupportLastMessage);
        TextView tvSupportTime = convertView.findViewById(R.id.tvSupportTime);
        TextView tvSupportUnread = convertView.findViewById(R.id.tvSupportUnread);

        SupportConversation conversation = conversations.get(position);
        tvSupportSubject.setText(emptyToDash(conversation.getSubject()));
        tvSupportCustomer.setText("Customer: " + formatUser(conversation.getCustomer()));
        tvSupportStatus.setText("Status: " + emptyToDash(conversation.getStatus()));
        tvSupportLastMessage.setText("Last: " + formatLastMessage(conversation.getLastMessage()));
        tvSupportTime.setText(formatDate(conversation.getLastMessageAt()));

        if (conversation.getUnreadCount() > 0) {
            tvSupportUnread.setText("Unread: " + conversation.getUnreadCount());
            tvSupportUnread.setVisibility(View.VISIBLE);
        } else {
            tvSupportUnread.setVisibility(View.GONE);
        }

        return convertView;
    }

    private String formatUser(SupportUser user) {
        if (user == null) {
            return "-";
        }
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            return user.getEmail();
        }
        return emptyToDash(user.getDisplayName());
    }

    private String formatLastMessage(SupportMessage message) {
        if (message == null || message.getBody() == null || message.getBody().trim().isEmpty()) {
            return "-";
        }
        return message.getBody();
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
