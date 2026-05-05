package com.ptithcm.e_shopadmin.common;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 15000;

    private ApiClient() {
    }

    public static ApiResponse get(String path, String token) throws Exception {
        URL url = new URL(ApiConfig.BASE_URL + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestProperty("Accept", "application/json");
            if (token != null && !token.trim().isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
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

    public static ApiResponse postJson(String path, JSONObject body) throws Exception {
        return postJson(path, body, null);
    }

    public static ApiResponse postJson(String path, JSONObject body, String token) throws Exception {
        return sendJson("POST", path, body, token);
    }

    public static ApiResponse putJson(String path, JSONObject body, String token) throws Exception {
        return sendJson("PUT", path, body, token);
    }

    private static ApiResponse sendJson(String method, String path, JSONObject body, String token) throws Exception {
        URL url = new URL(ApiConfig.BASE_URL + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod(method);
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            if (token != null && !token.trim().isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
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
