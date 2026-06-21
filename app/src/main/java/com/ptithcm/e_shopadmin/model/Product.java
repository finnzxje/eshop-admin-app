package com.ptithcm.e_shopadmin.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
    private String imageUrl;
    private String createdAt;
    private String updatedAt;
    private int categoryId;
    private String categoryName;
    private ArrayList<String> tags = new ArrayList<>();

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
        product.setImageUrl(findImageUrl(object));
        product.setCreatedAt(object.optString("createdAt", ""));
        product.setUpdatedAt(object.optString("updatedAt", ""));

        JSONObject category = object.optJSONObject("category");
        if (category != null) {
            product.setCategoryId(category.optInt("id", 0));
            product.setCategoryName(category.optString("name", ""));
        }

        JSONArray tagsArray = object.optJSONArray("tags");
        if (tagsArray != null) {
            ArrayList<String> parsedTags = new ArrayList<>();
            for (int i = 0; i < tagsArray.length(); i++) {
                Object value = tagsArray.opt(i);
                if (value instanceof JSONObject) {
                    String tag = ((JSONObject) value).optString("tag", "");
                    if (!tag.trim().isEmpty()) {
                        parsedTags.add(tag);
                    }
                } else if (value != null) {
                    String tag = String.valueOf(value);
                    if (!tag.trim().isEmpty()) {
                        parsedTags.add(tag);
                    }
                }
            }
            product.setTags(parsedTags);
        }

        return product;
    }

    private static String findImageUrl(JSONObject object) {
        String url = object.optString("primaryImageUrl", "");
        if (url.trim().isEmpty()) {
            url = object.optString("thumbnailUrl", "");
        }
        if (url.trim().isEmpty()) {
            url = object.optString("imageUrl", "");
        }
        if (!url.trim().isEmpty()) {
            return url;
        }

        JSONArray images = object.optJSONArray("images");
        if (images == null || images.length() == 0) {
            return "";
        }

        String firstImageUrl = "";
        for (int i = 0; i < images.length(); i++) {
            JSONObject imageObject = images.optJSONObject(i);
            if (imageObject == null) {
                continue;
            }

            String imageUrl = imageObject.optString("imageUrl", "");
            if (imageUrl.trim().isEmpty()) {
                continue;
            }

            if (firstImageUrl.trim().isEmpty()) {
                firstImageUrl = imageUrl;
            }
            if (imageObject.optBoolean("primary", false)) {
                return imageUrl;
            }
        }

        return firstImageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
