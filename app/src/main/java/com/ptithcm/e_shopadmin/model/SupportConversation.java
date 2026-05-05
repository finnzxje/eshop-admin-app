package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class SupportConversation {
    private String id;
    private String status;
    private String subject;
    private String lastMessageAt;
    private SupportUser customer;
    private SupportUser assignedStaff;
    private SupportMessage lastMessage;
    private long unreadCount;

    public static SupportConversation fromJson(JSONObject object) {
        SupportConversation conversation = new SupportConversation();
        conversation.setId(object.optString("id", ""));
        conversation.setStatus(object.optString("status", ""));
        conversation.setSubject(object.optString("subject", ""));
        conversation.setLastMessageAt(object.optString("lastMessageAt", ""));
        conversation.setCustomer(SupportUser.fromJson(object.optJSONObject("customer")));
        conversation.setAssignedStaff(SupportUser.fromJson(object.optJSONObject("assignedStaff")));
        conversation.setLastMessage(SupportMessage.fromJson(object.optJSONObject("lastMessage")));
        conversation.setUnreadCount(object.optLong("unreadCount", 0));
        return conversation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(String lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public SupportUser getCustomer() {
        return customer;
    }

    public void setCustomer(SupportUser customer) {
        this.customer = customer;
    }

    public SupportUser getAssignedStaff() {
        return assignedStaff;
    }

    public void setAssignedStaff(SupportUser assignedStaff) {
        this.assignedStaff = assignedStaff;
    }

    public SupportMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(SupportMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
