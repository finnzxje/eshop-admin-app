package com.ptithcm.e_shopadmin.model;

import java.util.ArrayList;

public class OrderPaymentDetail {
    private PaymentTransaction transaction;
    private ArrayList<PaymentTransaction> relatedTransactions;

    public OrderPaymentDetail() {
        relatedTransactions = new ArrayList<>();
    }

    public PaymentTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(PaymentTransaction transaction) {
        this.transaction = transaction;
    }

    public ArrayList<PaymentTransaction> getRelatedTransactions() {
        return relatedTransactions;
    }

    public void setRelatedTransactions(ArrayList<PaymentTransaction> relatedTransactions) {
        this.relatedTransactions = relatedTransactions;
    }
}
