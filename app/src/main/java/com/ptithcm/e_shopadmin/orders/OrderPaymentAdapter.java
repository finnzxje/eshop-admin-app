package com.ptithcm.e_shopadmin.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.model.OrderPaymentItem;
import com.ptithcm.e_shopadmin.model.PaymentCustomer;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class OrderPaymentAdapter extends BaseAdapter {
    private Context context;
    private List<OrderPaymentItem> orderPaymentItems;
    private DateTimeFormatter dateFormatter;

    public OrderPaymentAdapter(Context context, List<OrderPaymentItem> orderPaymentItems) {
        this.context = context;
        this.orderPaymentItems = orderPaymentItems;
        dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
                .withZone(ZoneId.systemDefault());
    }

    @Override
    public int getCount() {
        return orderPaymentItems.size();
    }

    @Override
    public Object getItem(int position) {
        return orderPaymentItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order_payment, null);
        }

        TextView tvOrderNumber = convertView.findViewById(R.id.tvOrderNumber);
        TextView tvOrderAmount = convertView.findViewById(R.id.tvOrderAmount);
        TextView tvOrderCustomer = convertView.findViewById(R.id.tvOrderCustomer);
        TextView tvOrderStatus = convertView.findViewById(R.id.tvOrderStatus);
        TextView tvOrderMethod = convertView.findViewById(R.id.tvOrderMethod);
        TextView tvOrderDate = convertView.findViewById(R.id.tvOrderDate);

        OrderPaymentItem item = orderPaymentItems.get(position);
        tvOrderNumber.setText(emptyToDash(item.getOrderNumber()));
        tvOrderAmount.setText(formatCurrency(item.getAmount(), item.getCurrency()));
        tvOrderCustomer.setText(formatCustomer(item.getCustomer()));
        tvOrderStatus.setText("Payment: " + emptyToDash(item.getStatus()));
        tvOrderMethod.setText(emptyToDash(item.getMethod()) + " | " + emptyToDash(item.getProvider()));
        tvOrderDate.setText(formatDate(item.getCreatedAt()));

        return convertView;
    }

    private String formatCurrency(double value, String currency) {
        try {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
            format.setCurrency(java.util.Currency.getInstance(currency));
            return format.format(value);
        } catch (Exception ex) {
            return value + " " + emptyToDash(currency);
        }
    }

    private String formatCustomer(PaymentCustomer customer) {
        if (customer == null) {
            return "Customer: -";
        }

        String email = customer.getEmail();
        if (email == null || email.trim().isEmpty()) {
            return "Customer: " + emptyToDash(customer.getDisplayName());
        }
        return "Customer: " + email;
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
