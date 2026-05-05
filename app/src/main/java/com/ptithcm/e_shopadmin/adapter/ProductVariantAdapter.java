package com.ptithcm.e_shopadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.model.ProductVariant;

import java.util.List;
import java.util.Locale;

public class ProductVariantAdapter extends BaseAdapter {
    private Context context;
    private List<ProductVariant> variantList;

    public ProductVariantAdapter(Context context, List<ProductVariant> variantList) {
        this.context = context;
        this.variantList = variantList;
    }

    @Override
    public int getCount() {
        return variantList.size();
    }

    @Override
    public Object getItem(int position) {
        return variantList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.row_product_variant, null);
        }

        TextView tvVariantTitle = view.findViewById(R.id.tvVariantTitle);
        TextView tvVariantSku = view.findViewById(R.id.tvVariantSku);
        TextView tvVariantStock = view.findViewById(R.id.tvVariantStock);
        TextView tvVariantPrice = view.findViewById(R.id.tvVariantPrice);
        TextView tvVariantStatus = view.findViewById(R.id.tvVariantStatus);

        ProductVariant variant = variantList.get(position);
        tvVariantTitle.setText(variant.getColorName() + " / Size " + safeText(variant.getSize()));
        tvVariantSku.setText("SKU: " + safeText(variant.getVariantSku()));
        tvVariantStock.setText("Stock: " + variant.getQuantityInStock());
        tvVariantPrice.setText(String.format(Locale.US, "$%.2f", variant.getPrice()));
        tvVariantStatus.setText(variant.isActive() ? "Status: Active" : "Status: Inactive");

        return view;
    }

    private String safeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        return value;
    }
}
