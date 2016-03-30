package com.jacr.currencyconverter.model;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * HttpClient
 * Created by Jesus Castro on 09/10/2015.
 */
public class HttpClient {

    //<editor-fold desc="Constants & Variables">

    private static HttpClient singleton;
    private final OkHttpClient client;

    //</editor-fold>

    private HttpClient() {
        client = new OkHttpClient();
    }

    public static HttpClient getInstance() {
        if (singleton == null) {
            singleton = new HttpClient();
        }
        return singleton;
    }

    //<editor-fold desc="Header">

    private Headers buildHeaders(Map<String, String> headers) {
        Headers.Builder builder = new Headers.Builder();
        Set<String> keys = headers.keySet();
        for (String key : keys) {
            builder.add(key, headers.get(key));
        }
        return builder.build();
    }

    private Map<String, String> createDefaultHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }

    //</editor-fold>

    //<editor-fold desc="GET Requests">

    public void get(String url, Map<String, String> parameters, Callback callback) {
        get(url, parameters, createDefaultHeader(), callback);
    }

    private void get(String url, Map<String, String> parameters, Map<String, String> headers, Callback callback) {
        Request.Builder builder = new Request.Builder();
        builder.url(url + (parameters.size() != 0 ? buildGetParameters(parameters) : ""));
        builder.headers(buildHeaders(headers));
        Request request = builder.build();
        client.newCall(request).enqueue(callback);
    }

    private String buildGetParameters(Map<String, String> params) {
        String string = "?";
        Set<String> keys = params.keySet();
        for (String key : keys) {
            string += String.format("%s=%s&", key, params.get(key));
        }
        return string.substring(0, string.lastIndexOf("&"));
    }

    //</editor-fold>

}
