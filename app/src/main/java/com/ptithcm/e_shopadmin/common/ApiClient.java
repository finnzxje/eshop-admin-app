package com.ptithcm.e_shopadmin.common;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;

public class ApiClient {
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 15000;

    private ApiClient() {
    }

    public static ApiResponse postJson(String path, JSONObject body) throws Exception {
        URL url = new URL(ApiConfig.BASE_URL + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(body.toString());
            writer.flush();
            writer.close();
            outputStream.close();

            int statusCode = connection.getResponseCode();
            InputStream inputStream;
            if (statusCode >= 200 && statusCode < 300) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            return new ApiResponse(statusCode, readStream(inputStream));
        } finally {
            connection.disconnect();
        }
    }

    public static ApiResponse postJson(String path, JSONObject body, String accessToken) throws Exception {
        return sendJson("POST", path, body, accessToken);
    }

    public static ApiResponse putJson(String path, JSONObject body, String accessToken) throws Exception {
        return sendJson("PUT", path, body, accessToken);
    }

    public static ApiResponse patchJson(String path, JSONObject body, String accessToken) throws Exception {
        return sendJson("PATCH", path, body, accessToken);
    }

    public static ApiResponse get(String path, String accessToken) throws Exception {
        URL url = new URL(ApiConfig.BASE_URL + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestProperty("Accept", "application/json");
            if (accessToken != null && !accessToken.trim().isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            }

            int statusCode = connection.getResponseCode();
            InputStream inputStream;
            if (statusCode >= 200 && statusCode < 300) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            return new ApiResponse(statusCode, readStream(inputStream));
        } finally {
            connection.disconnect();
        }
    }

    public static ApiResponse getOrderPaymentList(String accessToken, String status, String orderNumber, int page, int size) throws Exception {
        String path = "/api/admin/payments/transactions?page=" + page + "&size=" + size + "&sort=createdAt,desc";

        if (status != null && !status.trim().isEmpty()) {
            path += "&status=" + URLEncoder.encode(status.trim(), "UTF-8");
        }

        if (orderNumber != null && !orderNumber.trim().isEmpty()) {
            path += "&orderNumber=" + URLEncoder.encode(orderNumber.trim(), "UTF-8");
        }

        return get(path, accessToken);
    }

    public static ApiResponse getOrderPaymentDetail(String accessToken, String transactionId) throws Exception {
        String path = "/api/admin/payments/transactions/" + URLEncoder.encode(transactionId, "UTF-8");
        return get(path, accessToken);
    }

    public static ApiResponse getOrderTransactions(String accessToken, String orderNumber) throws Exception {
        String path = "/api/admin/orders/" + URLEncoder.encode(orderNumber, "UTF-8") + "/transactions";
        return get(path, accessToken);
    }

    public static ApiResponse getSupportConversations(String accessToken, boolean assignedOnly, String status, int page, int size) throws Exception {
        String path;
        if (assignedOnly) {
            path = "/api/admin/support/conversations/assigned?page=" + page + "&size=" + size;
        } else {
            path = "/api/admin/support/conversations?page=" + page + "&size=" + size;
            if (status != null && !status.trim().isEmpty()) {
                path += "&status=" + URLEncoder.encode(status.trim(), "UTF-8");
            }
        }
        return get(path, accessToken);
    }

    public static ApiResponse getSupportMessages(String accessToken, String conversationId) throws Exception {
        String path = "/api/admin/support/conversations/" + URLEncoder.encode(conversationId, "UTF-8") + "/messages";
        return get(path, accessToken);
    }

    public static ApiResponse sendSupportMessage(String accessToken, String conversationId, JSONObject body) throws Exception {
        String path = "/api/admin/support/conversations/" + URLEncoder.encode(conversationId, "UTF-8") + "/messages";
        return postJson(path, body, accessToken);
    }

    public static ApiResponse updateSupportConversationStatus(String accessToken, String conversationId, JSONObject body) throws Exception {
        String path = "/api/admin/support/conversations/" + URLEncoder.encode(conversationId, "UTF-8") + "/status";
        return patchJson(path, body, accessToken);
    }

    public static ApiResponse getCurrentUserProfile(String accessToken) throws Exception {
        return get("/api/account/profile", accessToken);
    }

    public static ApiResponse updateCurrentUserProfile(String accessToken, JSONObject body) throws Exception {
        return putJson("/api/account/profile", body, accessToken);
    }

    public static ApiResponse changePassword(String accessToken, JSONObject body) throws Exception {
        return patchJson("/api/account/profile/password", body, accessToken);
    }

    private static ApiResponse sendJson(String method, String path, JSONObject body, String accessToken) throws Exception {
        URL url = new URL(ApiConfig.BASE_URL + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod(method);
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            if (accessToken != null && !accessToken.trim().isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            }
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(body.toString());
            writer.flush();
            writer.close();
            outputStream.close();

            int statusCode = connection.getResponseCode();
            InputStream inputStream;
            if (statusCode >= 200 && statusCode < 300) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            return new ApiResponse(statusCode, readStream(inputStream));
        } finally {
            connection.disconnect();
        }
    }

    private static String readStream(InputStream inputStream) throws Exception {
        if (inputStream == null) {
            return "";
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }
}
