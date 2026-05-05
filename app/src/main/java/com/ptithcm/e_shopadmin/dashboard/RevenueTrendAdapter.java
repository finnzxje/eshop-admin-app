package com.ptithcm.e_shopadmin.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.model.RevenueTrendItem;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class RevenueTrendAdapter extends BaseAdapter {
    private Context context;
    private List<RevenueTrendItem> revenueTrendItems;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormatter;

    public RevenueTrendAdapter(Context context, List<RevenueTrendItem> revenueTrendItems) {
        this.context = context;
        this.revenueTrendItems = revenueTrendItems;
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                .withZone(ZoneId.systemDefault());
    }

    @Override
    public int getCount() {
        return revenueTrendItems.size();
    }

    @Override
    public Object getItem(int position) {
        return revenueTrendItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_revenue_trend, null);
        }

        TextView tvTrendDate = convertView.findViewById(R.id.tvTrendDate);
        TextView tvTrendGross = convertView.findViewById(R.id.tvTrendGross);
        TextView tvTrendOrders = convertView.findViewById(R.id.tvTrendOrders);
        TextView tvTrendNet = convertView.findViewById(R.id.tvTrendNet);

        RevenueTrendItem item = revenueTrendItems.get(position);
        tvTrendDate.setText(formatDate(item.getBucketStart()));
        tvTrendGross.setText(currencyFormat.format(item.getGross()));
        tvTrendOrders.setText(item.getOrderCount() + " orders");
        tvTrendNet.setText("Net " + currencyFormat.format(item.getNet()) + " | Refunds " + currencyFormat.format(item.getRefunds()));

        return convertView;
    }

    private String formatDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "No date";
        }

        try {
            return dateFormatter.format(Instant.parse(value));
        } catch (Exception ex) {
            return value;
        }
    }
}
