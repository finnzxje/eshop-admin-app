package com.ptithcm.e_shopadmin.common;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ApiClient {
    private static final int TIMEOUT_MS = 15000;
    private static RequestQueue requestQueue;
    private static Context appContext;

    private ApiClient() {
    }

    public static synchronized void init(Context context) {
        if (context == null) {
            return;
        }
        appContext = context.getApplicationContext();
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(appContext);
        }
    }

    public static ApiResponse get(String path, String token) throws Exception {
        return send(Request.Method.GET, path, null, token);
    }

    public static ApiResponse postJson(String path, JSONObject body) throws Exception {
        return postJson(path, body, null);
    }

    public static ApiResponse postJson(String path, JSONObject body, String token) throws Exception {
        return send(Request.Method.POST, path, body, token);
    }

    public static ApiResponse putJson(String path, JSONObject body, String token) throws Exception {
        return send(Request.Method.PUT, path, body, token);
    }

    public static ApiResponse patchJson(String path, JSONObject body, String token) throws Exception {
        return send(Request.Method.PATCH, path, body, token);
    }

    public static ApiResponse delete(String path, String token) throws Exception {
        return send(Request.Method.DELETE, path, null, token);
    }

    private static ApiResponse send(int method, String path, JSONObject body, String token) throws Exception {
        HashMap<String, String> headers = buildJsonHeaders(token);
        byte[] requestBody = null;
        if (body != null) {
            requestBody = body.toString().getBytes("UTF-8");
        }

        ApiRequest request = new ApiRequest(method, ApiConfig.BASE_URL + path, headers,
                "application/json; charset=utf-8", requestBody);
        return execute(request);
    }

    public static ApiResponse uploadProductImage(String token,
                                                 String productId,
                                                 Uri imageUri,
                                                 String fileName,
                                                 String altText,
                                                 String displayOrder,
                                                 boolean primary,
                                                 int colorId) throws Exception {
        ensureReady();

        String boundary = "AndroidBoundary" + System.currentTimeMillis();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(byteStream);
        writeTextPart(output, boundary, "altText", altText);
        writeTextPart(output, boundary, "displayOrder", displayOrder);
        writeTextPart(output, boundary, "primary", String.valueOf(primary));
        if (colorId > 0) {
            writeTextPart(output, boundary, "colorId", String.valueOf(colorId));
        }
        writeFilePart(output, boundary, imageUri, fileName);
        output.writeBytes("--" + boundary + "--\r\n");
        output.flush();
        output.close();

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        if (token != null && !token.trim().isEmpty()) {
            headers.put("Authorization", "Bearer " + token);
        }

        String path = "/api/admin/catalog/products/" + productId + "/images";
        ApiRequest request = new ApiRequest(Request.Method.POST, ApiConfig.BASE_URL + path, headers,
                "multipart/form-data; boundary=" + boundary, byteStream.toByteArray());
        return execute(request);
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

    private static ApiResponse execute(ApiRequest request) throws Exception {
        ensureReady();
        RequestFuture<ApiResponse> future = RequestFuture.newFuture();
        request.setListener(future);
        request.setErrorListener(future);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

        try {
            return future.get();
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof VolleyError) {
                VolleyError volleyError = (VolleyError) cause;
                if (volleyError.networkResponse != null) {
                    return buildResponse(volleyError.networkResponse);
                }
                if (volleyError.getCause() instanceof UnknownHostException) {
                    throw (UnknownHostException) volleyError.getCause();
                }
                throw new Exception(volleyError);
            }
            throw ex;
        }
    }

    private static void ensureReady() {
        if (requestQueue == null) {
            throw new IllegalStateException("ApiClient.init(context) must be called before API requests.");
        }
    }

    private static HashMap<String, String> buildJsonHeaders(String token) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        if (token != null && !token.trim().isEmpty()) {
            headers.put("Authorization", "Bearer " + token);
        }
        return headers;
    }

    private static ApiResponse buildResponse(NetworkResponse response) {
        String body = "";
        if (response.data != null) {
            try {
                body = new String(response.data, "UTF-8");
            } catch (Exception ex) {
                body = new String(response.data);
            }
        }
        return new ApiResponse(response.statusCode, body);
    }

    private static void writeTextPart(DataOutputStream output, String boundary, String name, String value) throws Exception {
        output.writeBytes("--" + boundary + "\r\n");
        output.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
        output.writeBytes(value == null ? "" : value);
        output.writeBytes("\r\n");
    }

    private static void writeFilePart(DataOutputStream output, String boundary, Uri imageUri, String fileName) throws Exception {
        ContentResolver resolver = appContext.getContentResolver();
        String contentType = resolver.getType(imageUri);
        if (contentType == null || contentType.trim().isEmpty()) {
            contentType = "image/jpeg";
        }

        output.writeBytes("--" + boundary + "\r\n");
        output.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n");
        output.writeBytes("Content-Type: " + contentType + "\r\n\r\n");

        InputStream inputStream = resolver.openInputStream(imageUri);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while (inputStream != null && (bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        if (inputStream != null) {
            inputStream.close();
        }
        output.writeBytes("\r\n");
    }

    private static class ApiRequest extends Request<ApiResponse> {
        private final Map<String, String> headers;
        private final String contentType;
        private final byte[] body;
        private Response.Listener<ApiResponse> listener;
        private Response.ErrorListener errorListener;

        ApiRequest(int method, String url, Map<String, String> headers, String contentType, byte[] body) {
            super(method, url, null);
            this.headers = headers;
            this.contentType = contentType;
            this.body = body;
        }

        void setListener(Response.Listener<ApiResponse> listener) {
            this.listener = listener;
        }

        void setErrorListener(Response.ErrorListener errorListener) {
            this.errorListener = errorListener;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return headers;
        }

        @Override
        public String getBodyContentType() {
            return contentType;
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            return body;
        }

        @Override
        protected Response<ApiResponse> parseNetworkResponse(NetworkResponse response) {
            return Response.success(buildResponse(response), null);
        }

        @Override
        protected void deliverResponse(ApiResponse response) {
            if (listener != null) {
                listener.onResponse(response);
            }
        }

        @Override
        public void deliverError(VolleyError error) {
            if (errorListener != null) {
                errorListener.onErrorResponse(error);
            }
        }
    }
}
