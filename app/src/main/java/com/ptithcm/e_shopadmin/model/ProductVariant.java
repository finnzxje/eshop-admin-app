package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class ProductVariant {
    private String id;
    private String variantSku;
    private double price;
    private int quantityInStock;
    private boolean active;
    private String size;
    private String fit;
    private String currency;
    private String createdAt;
    private Color color;

    public ProductVariant() {
    }

    public static ProductVariant fromJson(JSONObject object) {
        ProductVariant variant = new ProductVariant();
        variant.setId(object.optString("id", ""));
        variant.setVariantSku(object.optString("variantSku", ""));
        variant.setPrice(object.optDouble("price", 0));
        variant.setQuantityInStock(object.optInt("quantityInStock", 0));
        variant.setActive(object.optBoolean("active", false));
        variant.setSize(object.optString("size", ""));
        variant.setFit(object.optString("fit", ""));
        variant.setCurrency(object.optString("currency", "USD"));
        variant.setCreatedAt(object.optString("createdAt", ""));

        JSONObject colorObject = object.optJSONObject("color");
        if (colorObject != null) {
            variant.setColor(Color.fromJson(colorObject));
        }

        return variant;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVariantSku() {
        return variantSku;
    }

    public void setVariantSku(String variantSku) {
        this.variantSku = variantSku;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFit() {
        return fit;
    }

    public void setFit(String fit) {
        this.fit = fit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getColorName() {
        if (color == null) {
            return "No color";
        }
        return color.getDisplayName();
    }
}
