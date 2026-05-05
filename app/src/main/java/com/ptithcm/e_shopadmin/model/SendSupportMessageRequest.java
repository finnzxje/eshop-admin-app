package com.ptithcm.e_shopadmin.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class SendSupportMessageRequest {
    private String body;

    public SendSupportMessageRequest(String body) {
        this.body = body;
    }

    public JSONObject toJson() throws Exception {
        JSONObject object = new JSONObject();
        object.put("body", body);
        object.put("attachmentUrls", new JSONArray());
        return object;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
