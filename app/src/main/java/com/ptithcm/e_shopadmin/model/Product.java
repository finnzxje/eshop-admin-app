package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class Product {
    private String id;
    private String name;
    private String slug;
    private String description;
    private double basePrice;
    private String status;
    private boolean featured;
    private String gender;
    private String productType;
    private String createdAt;
    private String updatedAt;
    private String categoryName;

    public Product() {
    }

    public static Product fromJson(JSONObject object) {
        Product product = new Product();
        product.setId(object.optString("id", ""));
        product.setName(object.optString("name", ""));
        product.setSlug(object.optString("slug", ""));
        product.setDescription(object.optString("description", ""));
        product.setBasePrice(object.optDouble("basePrice", 0));
        product.setStatus(object.optString("status", ""));
        product.setFeatured(object.optBoolean("featured", false));
        product.setGender(object.optString("gender", ""));
        product.setProductType(object.optString("productType", ""));
        product.setCreatedAt(object.optString("createdAt", ""));
        product.setUpdatedAt(object.optString("updatedAt", ""));

        JSONObject category = object.optJSONObject("category");
        if (category != null) {
            product.setCategoryName(category.optString("name", ""));
        }

        return product;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
