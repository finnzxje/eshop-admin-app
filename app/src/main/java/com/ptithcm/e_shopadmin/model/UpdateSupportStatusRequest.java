package com.ptithcm.e_shopadmin.model;

import org.json.JSONObject;

public class UpdateSupportStatusRequest {
    private String status;

    public UpdateSupportStatusRequest(String status) {
        this.status = status;
    }

    public JSONObject toJson() throws Exception {
        JSONObject object = new JSONObject();
        object.put("status", status);
        return object;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
