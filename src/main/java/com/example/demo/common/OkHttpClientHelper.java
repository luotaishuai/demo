package com.example.demo.common;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ccl
 * @time 2018-04-10 17:03
 * @name OkHttpClientHelper
 * @desc:
 */
@Slf4j
public class OkHttpClientHelper {
    private static final int STATE_OK = 200;
    private static final String STR_MARK_QUESTION = "?";
    private static final String STR_MARK_AND = "&";
    private static final String STR_MARK_EQUAL = "=";
    private static final String STR_MARK_PARAMS = "?params=";

    private static final String CONTENT_TYPE = "Content-type";
    private static OkHttpClient mOkHttpClient = new OkHttpClient.Builder().build();

    public static void proxy(String ip, int port) {
        if (null == mOkHttpClient.proxy()) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port)))
                    .build();
        }
    }

    public static String get(String url, Map<String, String> header, Map<String, Object> params) {
        log.info("Request url = {}", url);
        // 解析头部
        Request.Builder builder = new Request.Builder();
        if (null != header) {
            for(Map.Entry<String, String> entry : header.entrySet()){
                // 组装成 OkHttp 的 Header
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        if (!url.contains(STR_MARK_QUESTION) && null != params) {
            StringBuilder stringBuilder = new StringBuilder(url);
            stringBuilder.append(STR_MARK_QUESTION);
            for(Map.Entry<String,Object> entry : params.entrySet()){
                stringBuilder.append(entry.getKey()).append(STR_MARK_EQUAL).append(entry.getValue()).append(STR_MARK_AND);
            }
            url = stringBuilder.toString();
            url = url.substring(0, url.length() - 1);
        }

        builder.url(url).get();
        Request okRequest = builder.build();
        return execute(okRequest);
    }

    public static String get(String url, Map<String, String> header, String params) {
        // 解析头部
        Request.Builder builder = new Request.Builder();
        if (null != header) {
            for(Map.Entry<String, String> entry : header.entrySet()){
                builder.header(entry.getKey(), entry.getValue());
            }
        }

        if (!url.contains(STR_MARK_QUESTION) && null != params) {
            url = url + STR_MARK_PARAMS + params;
        }

        builder.url(url).get();
        Request okRequest = builder.build();
        return execute(okRequest);
    }

    public static String post(String url, Map<String, Object> params) {
        Map<String, String> header = new HashMap<>(1);
        header.put("Content-type", "application/json");
        return post(url, header, params);
    }

    public static String post(String url, Map<String, String> header, Map<String, Object> params) {
        log.info("Request url = {}", url);
        log.info("Request params = {}", params);
        Request.Builder builder = new Request.Builder();
        MediaType mediaType = MediaType
                .parse("application/json; charset=utf-8");
        String bodyStr = JSON.toJSONString(params);
        RequestBody body = RequestBody.create(mediaType, bodyStr);
        for(Map.Entry<String, String> entry : header.entrySet()){
            builder.header(entry.getKey(), entry.getValue());
        }
        builder.url(url).post(body);
        Request okRequest = builder.build();
        return execute(okRequest);
    }

    public static String post(String url,String jsonBody) throws Exception{
        log.info("Request url = {}", url);
        log.info("Request params = {}", jsonBody);
        Request.Builder builder = new Request.Builder();
        MediaType mediaType = MediaType
                .parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, jsonBody);
        builder.url(url).post(body);
        Request okRequest = builder.build();
        return execute(okRequest);
    }

    public static String post(String url, Map<String, String> header, String jsonStr) {
        log.info("Request url = {}", url);
        Request.Builder builder = new Request.Builder();
        if (null != header && header.containsKey(CONTENT_TYPE)) {

        } else {
            builder.header(CONTENT_TYPE, "application/json");
        }
        MediaType mediaType = MediaType
                .parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, jsonStr);
        if (null != header) {
            for(Map.Entry<String, String> entry : header.entrySet()){
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        builder.url(url).post(body);
        Request okRequest = builder.build();
        return execute(okRequest);
    }

    private static String execute(Request request) {
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            log.info(JSON.toJSONString(response));
            if (STATE_OK == response.code()) {
                String body = response.body().string();
                return body;
            } else {
                throw new Exception("status code is not 200");
            }
        } catch (IOException e) {
            log.error("OkHttp Request Error: ", e);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}