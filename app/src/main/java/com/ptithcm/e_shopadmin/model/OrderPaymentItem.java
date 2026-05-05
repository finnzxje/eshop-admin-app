package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class OrderPaymentItem {
    private String id;
    private String orderId;
    private String orderNumber;
    private String provider;
    private double amount;
    private String currency;
    private String status;
    private String method;
    private String createdAt;
    private PaymentCustomer customer;

    public static OrderPaymentItem fromJson(JSONObject object) {
        OrderPaymentItem item = new OrderPaymentItem();
        item.setId(object.optString("id", ""));
        item.setOrderId(object.optString("orderId", ""));
        item.setOrderNumber(object.optString("orderNumber", ""));
        item.setProvider(object.optString("provider", ""));
        item.setAmount(object.optDouble("amount", 0));
        item.setCurrency(object.optString("currency", "USD"));
        item.setStatus(object.optString("status", ""));
        item.setMethod(object.optString("method", ""));
        item.setCreatedAt(object.optString("createdAt", ""));
        item.setCustomer(PaymentCustomer.fromJson(object.optJSONObject("customer")));
        return item;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public PaymentCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(PaymentCustomer customer) {
        this.customer = customer;
    }
}
