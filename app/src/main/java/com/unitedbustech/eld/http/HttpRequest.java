package com.unitedbustech.eld.http;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.http.certs.TrustAllCerts;
import com.unitedbustech.eld.http.certs.TrustAllHostnameVerifier;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.util.JsonUtil;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author yufei0213
 * @date 2018/1/6
 * @description http请求器
 */
public class HttpRequest {

    private static final String TAG = "HttpRequest";

    private static final String CODE_KEY = "code";
    private static final String MSG_KEY = "msg";
    private static final String DATA_KEY = "data";

    private static final long CONNECT_TIMEOUT = 10 * 1000L;
    private static final long READ_TIMEOUT = 30 * 1000L;
    private static final long WRITE_TIMEOUT = 30 * 1000L;

    private OkHttpClient okHttpClient;
    private Call call;

    private HttpResponse httpResponse;
    private HttpResponse.Builder httpResponseBuilder;

    private String url;
    private Map<String, String> params;

    private HttpRequest(Builder builder) {

        this.url = builder.url;
        this.params = builder.params;

        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new TrustAllHostnameVerifier())
                .build();

        httpResponseBuilder = new HttpResponse.Builder()
                .ignore(builder.ignoreCodes);
    }

    public void cancel() {

        if (call != null) {

            try {

                call.cancel();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    @MainThread
    public void get(final HttpRequestCallback callback) {

        String requestUrl = url + "?";

        for (Map.Entry<String, String> entry : params.entrySet()) {

            requestUrl += entry.getKey() + "=" + entry.getValue() + "&";
        }

        requestUrl = requestUrl.substring(0, requestUrl.length() - 1);

        Request request = new Request.Builder()
                .url(requestUrl)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        handleAsyncRequest(callback, request);
    }

    @WorkerThread
    public HttpResponse get() {

        String requestUrl = url + "?";

        for (Map.Entry<String, String> entry : params.entrySet()) {

            requestUrl += entry.getKey() + "=" + entry.getValue() + "&";
        }

        requestUrl = requestUrl.substring(0, requestUrl.length() - 1);

        Request request = new Request.Builder()
                .url(requestUrl)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        return handleRequest(request);
    }

    @MainThread
    public void post(final HttpRequestCallback callback) {

        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {

            builder.add(key, params.get(key));
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .post(requestBody)
                .build();

        handleAsyncRequest(callback, request);
    }

    @WorkerThread
    public HttpResponse post() {

        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {

            builder.add(key, params.get(key));
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .post(requestBody)
                .build();

        return handleRequest(request);
    }

    private HttpResponse handleRequest(Request request) {

        call = okHttpClient.newCall(request);

        try {

            Response response = call.execute();

            if (response.code() == 200) {

                JSONObject result = JsonUtil.parseObject(response.body().string());
                httpResponse = httpResponseBuilder
                        .status(JsonUtil.getInt(result, CODE_KEY))
                        .data(JsonUtil.getString(result, DATA_KEY))
                        .build();
            } else {

                httpResponse = httpResponseBuilder
                        .status(RequestStatus.UNTREATED_ERROR.getCode())
                        .build();

                Logger.w(Tags.HTTP, url);
                Logger.w(Tags.HTTP, "http request code " + response.code() + ", body " + response.body().string());
            }
        } catch (IOException e) {

            if (!call.isCanceled()) {

                if (e instanceof SocketTimeoutException) {

                    httpResponse = httpResponseBuilder
                            .status(RequestStatus.REQUEST_TIMEOUT.getCode())
                            .build();
                } else if (e instanceof ConnectException) {

                    httpResponse = httpResponseBuilder
                            .status(RequestStatus.NETWORK_CONNECT_ERROR.getCode())
                            .build();
                } else {

                    httpResponse = httpResponseBuilder
                            .status(RequestStatus.UNTREATED_ERROR.getCode())
                            .build();
                }
            }

            Logger.w(Tags.HTTP, url);
            Logger.w(Tags.HTTP, e);
        }

        return httpResponse;
    }

    private void handleAsyncRequest(final HttpRequestCallback callback, Request request) {

        call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Logger.w(Tags.HTTP, url);
                Logger.w(Tags.HTTP, e);

                if (call.isCanceled()) {

                    return;
                }

                if (e instanceof SocketTimeoutException) {

                    httpResponse = httpResponseBuilder
                            .status(RequestStatus.REQUEST_TIMEOUT.getCode())
                            .build();
                } else if (e instanceof ConnectException) {

                    httpResponse = httpResponseBuilder
                            .status(RequestStatus.NETWORK_CONNECT_ERROR.getCode())
                            .build();
                } else {

                    httpResponse = httpResponseBuilder
                            .status(RequestStatus.UNTREATED_ERROR.getCode())
                            .build();
                }

                callback.onRequestFinish(httpResponse);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (call.isCanceled()) {

                    return;
                }

                if (response.code() == 200) {

                    JSONObject result = JsonUtil.parseObject(response.body().string());

                    httpResponse = httpResponseBuilder
                            .status(JsonUtil.getInt(result, CODE_KEY))
                            .data(JsonUtil.getString(result, DATA_KEY))
                            .build();
                } else {

                    httpResponse = httpResponseBuilder
                            .status(RequestStatus.UNTREATED_ERROR.getCode())
                            .build();

                    Logger.w(Tags.HTTP, url);
                    Logger.w(Tags.HTTP, "http request code " + response.code() + ", body " + response.body().string());
                }

                callback.onRequestFinish(httpResponse);
            }
        });
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    private SSLSocketFactory createSSLSocketFactory() {

        SSLSocketFactory ssfFactory = null;

        try {

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {

            Logger.w(Tags.HTTP, e);
        }

        return ssfFactory;
    }

    public static class Builder {

        private String url;
        private Map<String, String> params;

        private List<Integer> ignoreCodes;

        public Builder() {

            params = new HashMap<>();
            ignoreCodes = new ArrayList<>();
        }

        public Builder url(String url) {

            this.url = url;
            return this;
        }

        public Builder addParam(String key, String value) {

            this.params.put(key, value);
            return this;
        }

        public Builder ignore(int code) {

            ignoreCodes.add(code);
            return this;
        }

        public HttpRequest build() {

            return new HttpRequest(this);
        }
    }
}
