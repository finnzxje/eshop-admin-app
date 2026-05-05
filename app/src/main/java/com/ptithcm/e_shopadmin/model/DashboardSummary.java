package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class DashboardSummary {
    private double revenue;
    private long orders;
    private double capturedPayments;
    private long newCustomers;
    private double averageOrderValue;
    private double conversionRate;

    public static DashboardSummary fromJson(JSONObject object) {
        DashboardSummary summary = new DashboardSummary();
        summary.setRevenue(object.optDouble("revenue", 0));
        summary.setOrders(object.optLong("orders", 0));
        summary.setCapturedPayments(object.optDouble("capturedPayments", 0));
        summary.setNewCustomers(object.optLong("newCustomers", 0));
        summary.setAverageOrderValue(object.optDouble("averageOrderValue", 0));
        summary.setConversionRate(object.optDouble("conversionRate", 0));
        return summary;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public long getOrders() {
        return orders;
    }

    public void setOrders(long orders) {
        this.orders = orders;
    }

    public double getCapturedPayments() {
        return capturedPayments;
    }

    public void setCapturedPayments(double capturedPayments) {
        this.capturedPayments = capturedPayments;
    }

    public long getNewCustomers() {
        return newCustomers;
    }

    public void setNewCustomers(long newCustomers) {
        this.newCustomers = newCustomers;
    }

    public double getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(double averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }
}
