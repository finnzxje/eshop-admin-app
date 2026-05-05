package com.ptithcm.e_shopadmin.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SupportMessage {
    private String id;
    private String conversationId;
    private String senderType;
    private SupportUser sender;
    private String body;
    private ArrayList<String> attachmentUrls;
    private String readAt;
    private String createdAt;

    public SupportMessage() {
        attachmentUrls = new ArrayList<>();
    }

    public static SupportMessage fromJson(JSONObject object) {
        SupportMessage message = new SupportMessage();
        if (object == null) {
            return message;
        }

        message.setId(object.optString("id", ""));
        message.setConversationId(object.optString("conversationId", ""));
        message.setSenderType(object.optString("senderType", ""));
        message.setSender(SupportUser.fromJson(object.optJSONObject("sender")));
        message.setBody(object.optString("body", ""));
        message.setReadAt(object.optString("readAt", ""));
        message.setCreatedAt(object.optString("createdAt", ""));

        JSONArray attachments = object.optJSONArray("attachmentUrls");
        if (attachments != null) {
            for (int i = 0; i < attachments.length(); i++) {
                String url = attachments.optString(i, "");
                if (!url.trim().isEmpty()) {
                    message.getAttachmentUrls().add(url);
                }
            }
        }
        return message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public SupportUser getSender() {
        return sender;
    }

    public void setSender(SupportUser sender) {
        this.sender = sender;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ArrayList<String> getAttachmentUrls() {
        return attachmentUrls;
    }

    public void setAttachmentUrls(ArrayList<String> attachmentUrls) {
        this.attachmentUrls = attachmentUrls;
    }

    public String getReadAt() {
        return readAt;
    }

    public void setReadAt(String readAt) {
        this.readAt = readAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
