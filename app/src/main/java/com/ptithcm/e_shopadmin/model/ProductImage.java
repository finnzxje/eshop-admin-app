package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class ProductImage {
    private String id;
    private String imageUrl;
    private String altText;
    private int displayOrder;
    private boolean primary;
    private String createdAt;
    private Color color;

    public ProductImage() {
    }

    public static ProductImage fromJson(JSONObject object) {
        ProductImage image = new ProductImage();
        image.setId(object.optString("id", ""));
        image.setImageUrl(object.optString("imageUrl", ""));
        image.setAltText(object.optString("altText", ""));
        image.setDisplayOrder(object.optInt("displayOrder", 0));
        image.setPrimary(object.optBoolean("primary", false));
        image.setCreatedAt(object.optString("createdAt", ""));

        JSONObject colorObject = object.optJSONObject("color");
        if (colorObject != null) {
            image.setColor(Color.fromJson(colorObject));
        }

        return image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
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
