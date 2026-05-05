package com.ptithcm.e_shopadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.model.Product;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.row_product, null);
        }

        TextView tvProductName = view.findViewById(R.id.tvProductName);
        TextView tvProductSlug = view.findViewById(R.id.tvProductSlug);
        TextView tvProductStatus = view.findViewById(R.id.tvProductStatus);
        TextView tvProductPrice = view.findViewById(R.id.tvProductPrice);
        TextView tvProductCategory = view.findViewById(R.id.tvProductCategory);
        TextView tvProductUpdatedAt = view.findViewById(R.id.tvProductUpdatedAt);

        Product product = productList.get(position);
        tvProductName.setText(product.getName());
        tvProductSlug.setText(product.getSlug());
        tvProductStatus.setText(formatStatus(product.getStatus()));
        tvProductPrice.setText(String.format(Locale.US, "$%.2f", product.getBasePrice()));
        tvProductCategory.setText(formatCategory(product.getCategoryName()));
        tvProductUpdatedAt.setText(formatUpdatedAt(product.getUpdatedAt()));

        return view;
    }

    private String formatStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "Status: Unknown";
        }
        return "Status: " + status.toUpperCase(Locale.US);
    }

    private String formatCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return "Category: No category";
        }
        return "Category: " + categoryName;
    }

    private String formatUpdatedAt(String updatedAt) {
        if (updatedAt == null || updatedAt.trim().isEmpty()) {
            return "Updated: Unknown";
        }
        return "Updated: " + updatedAt;
    }
}
