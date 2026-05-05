package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class RevenueTrendItem {
    private String bucketStart;
    private String bucketEnd;
    private long orderCount;
    private double gross;
    private double net;
    private double refunds;

    public static RevenueTrendItem fromJson(JSONObject object) {
        RevenueTrendItem item = new RevenueTrendItem();
        item.setBucketStart(object.optString("bucketStart", ""));
        item.setBucketEnd(object.optString("bucketEnd", ""));
        item.setOrderCount(object.optLong("orderCount", 0));
        item.setGross(object.optDouble("gross", 0));
        item.setNet(object.optDouble("net", 0));
        item.setRefunds(object.optDouble("refunds", 0));
        return item;
    }

    public String getBucketStart() {
        return bucketStart;
    }

    public void setBucketStart(String bucketStart) {
        this.bucketStart = bucketStart;
    }

    public String getBucketEnd() {
        return bucketEnd;
    }

    public void setBucketEnd(String bucketEnd) {
        this.bucketEnd = bucketEnd;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(long orderCount) {
        this.orderCount = orderCount;
    }

    public double getGross() {
        return gross;
    }

    public void setGross(double gross) {
        this.gross = gross;
    }

    public double getNet() {
        return net;
    }

    public void setNet(double net) {
        this.net = net;
    }

    public double getRefunds() {
        return refunds;
    }

    public void setRefunds(double refunds) {
        this.refunds = refunds;
    }
}
