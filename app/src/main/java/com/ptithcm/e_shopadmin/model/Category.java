package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class Category {
    private int id;
    private String name;
    private String slug;

    public Category() {
    }

    public static Category fromJson(JSONObject object) {
        Category category = new Category();
        category.setId(object.optInt("id", 0));
        category.setName(object.optString("name", ""));
        category.setSlug(object.optString("slug", ""));
        return category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
