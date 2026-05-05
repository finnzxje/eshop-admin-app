package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class StockAdjustment {
    private String id;
    private int previousQuantity;
    private int newQuantity;
    private int delta;
    private String reason;
    private String notes;
    private String adjustedAt;
    private String adjustedByEmail;

    public StockAdjustment() {
    }

    public static StockAdjustment fromJson(JSONObject object) {
        StockAdjustment adjustment = new StockAdjustment();
        adjustment.setId(object.optString("id", ""));
        adjustment.setPreviousQuantity(object.optInt("previousQuantity", 0));
        adjustment.setNewQuantity(object.optInt("newQuantity", 0));
        adjustment.setDelta(object.optInt("delta", 0));
        adjustment.setReason(object.optString("reason", ""));
        adjustment.setNotes(object.optString("notes", ""));
        adjustment.setAdjustedAt(object.optString("adjustedAt", ""));

        JSONObject adjustedBy = object.optJSONObject("adjustedBy");
        if (adjustedBy != null) {
            adjustment.setAdjustedByEmail(adjustedBy.optString("email", ""));
        }

        return adjustment;
    }

    public String getSummary() {
        StringBuilder builder = new StringBuilder();
        builder.append(previousQuantity).append(" -> ").append(newQuantity);
        builder.append(" (").append(delta >= 0 ? "+" : "").append(delta).append(")");
        if (reason != null && !reason.trim().isEmpty()) {
            builder.append("\nReason: ").append(reason);
        }
        if (notes != null && !notes.trim().isEmpty()) {
            builder.append("\nNotes: ").append(notes);
        }
        if (adjustedByEmail != null && !adjustedByEmail.trim().isEmpty()) {
            builder.append("\nBy: ").append(adjustedByEmail);
        }
        if (adjustedAt != null && !adjustedAt.trim().isEmpty()) {
            builder.append("\nAt: ").append(adjustedAt);
        }
        return builder.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPreviousQuantity() {
        return previousQuantity;
    }

    public void setPreviousQuantity(int previousQuantity) {
        this.previousQuantity = previousQuantity;
    }

    public int getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(int newQuantity) {
        this.newQuantity = newQuantity;
    }

    public int getDelta() {
        return delta;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAdjustedAt() {
        return adjustedAt;
    }

    public void setAdjustedAt(String adjustedAt) {
        this.adjustedAt = adjustedAt;
    }

    public String getAdjustedByEmail() {
        return adjustedByEmail;
    }

    public void setAdjustedByEmail(String adjustedByEmail) {
        this.adjustedByEmail = adjustedByEmail;
    }
}
