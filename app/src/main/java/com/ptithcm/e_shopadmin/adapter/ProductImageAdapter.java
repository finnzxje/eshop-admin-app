package com.ptithcm.e_shopadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.model.ProductImage;

import java.util.List;

public class ProductImageAdapter extends BaseAdapter {
    private Context context;
    private List<ProductImage> imageList;

    public ProductImageAdapter(Context context, List<ProductImage> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.row_product_image, null);
        }

        ImageView imgProductImage = view.findViewById(R.id.imgProductImage);
        TextView tvImageTitle = view.findViewById(R.id.tvImageTitle);
        TextView tvImageUrl = view.findViewById(R.id.tvImageUrl);
        TextView tvImageMeta = view.findViewById(R.id.tvImageMeta);
        TextView tvImageColor = view.findViewById(R.id.tvImageColor);

        ProductImage image = imageList.get(position);
        tvImageTitle.setText(image.isPrimary() ? "Primary image" : "Product image");
        tvImageUrl.setText(image.getImageUrl());
        tvImageMeta.setText("Order: " + image.getDisplayOrder() + " / Alt: " + safeText(image.getAltText()));
        tvImageColor.setText("Color: " + image.getColorName());
        loadProductImage(imgProductImage, image.getImageUrl());

        return view;
    }

    private void loadProductImage(ImageView imageView, String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            Glide.with(context).clear(imageView);
            imageView.setImageResource(R.drawable.ic_product_image_placeholder);
            return;
        }

        Glide.with(context)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_product_image_placeholder)
                .error(R.drawable.ic_product_image_placeholder)
                .into(imageView);
    }

    private String safeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        return value;
    }
}
