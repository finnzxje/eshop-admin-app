package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class Color {
    private int id;
    private String code;
    private String name;
    private String hex;

    public Color() {
    }

    public static Color fromJson(JSONObject object) {
        Color color = new Color();
        color.setId(object.optInt("id", 0));
        color.setCode(object.optString("code", ""));
        color.setName(object.optString("name", ""));
        color.setHex(object.optString("hex", ""));
        return color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getDisplayName() {
        if (code == null || code.trim().isEmpty()) {
            return name;
        }
        return name + " (" + code + ")";
    }
}
