package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class PaymentTransaction {
    private String id;
    private String orderId;
    private String orderNumber;
    private String provider;
    private String providerTransactionId;
    private String idempotencyKey;
    private double amount;
    private String currency;
    private String status;
    private String method;
    private double capturedAmount;
    private String rawResponse;
    private String errorCode;
    private String errorMessage;
    private String createdAt;
    private String updatedAt;
    private PaymentCustomer customer;

    public static PaymentTransaction fromJson(JSONObject object) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setId(object.optString("id", ""));
        transaction.setOrderId(object.optString("orderId", ""));
        transaction.setOrderNumber(object.optString("orderNumber", ""));
        transaction.setProvider(object.optString("provider", ""));
        transaction.setProviderTransactionId(object.optString("providerTransactionId", ""));
        transaction.setIdempotencyKey(object.optString("idempotencyKey", ""));
        transaction.setAmount(object.optDouble("amount", 0));
        transaction.setCurrency(object.optString("currency", "USD"));
        transaction.setStatus(object.optString("status", ""));
        transaction.setMethod(object.optString("method", ""));
        transaction.setCapturedAmount(object.optDouble("capturedAmount", 0));
        transaction.setRawResponse(object.optString("rawResponse", ""));
        transaction.setErrorCode(object.optString("errorCode", ""));
        transaction.setErrorMessage(object.optString("errorMessage", ""));
        transaction.setCreatedAt(object.optString("createdAt", ""));
        transaction.setUpdatedAt(object.optString("updatedAt", ""));
        transaction.setCustomer(PaymentCustomer.fromJson(object.optJSONObject("customer")));
        return transaction;
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

    public String getProviderTransactionId() {
        return providerTransactionId;
    }

    public void setProviderTransactionId(String providerTransactionId) {
        this.providerTransactionId = providerTransactionId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
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

    public double getCapturedAmount() {
        return capturedAmount;
    }

    public void setCapturedAmount(double capturedAmount) {
        this.capturedAmount = capturedAmount;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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

    public PaymentCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(PaymentCustomer customer) {
        this.customer = customer;
    }
}
