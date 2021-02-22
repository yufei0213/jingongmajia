package com.unitedbustech.eld.http;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.http.certs.TrustAllCerts;
import com.unitedbustech.eld.http.certs.TrustAllHostnameVerifier;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.util.ConvertUtil;
import com.unitedbustech.eld.util.JsonUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * @author yufei0213
 * @date 2018/3/17
 * @description HttpFileRequest
 */
public class HttpFileRequest {

    private static final String TAG = "HttpFileRequest";

    private static final String CODE_KEY = "code";
    private static final String MSG_KEY = "msg";
    private static final String DATA_KEY = "result";

    private static final long CONNECT_TIMEOUT = 10 * 1000L;
    private static final long READ_TIMEOUT = 30 * 1000L;
    private static final long WRITE_TIMEOUT = 30 * 1000L;

    private OkHttpClient okHttpClient;
    private Call call;

    private HttpResponse httpResponse;
    private HttpResponse.Builder httpResponseBuilder;

    private String url;
    private Map<String, Object> params;
    private HttpFileDownloadCallback downloadListener;
    private HttpFileUploadCallback uploadListener;

    public HttpFileRequest(Builder builder) {

        this.url = builder.url;
        this.params = builder.params;
        this.downloadListener = builder.downloadListener;
        this.uploadListener = builder.uploadListener;

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

    public String getUrl() {
        return url;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setUrl(String url) {

        this.url = url;
    }

    @WorkerThread
    public HttpResponse upload() {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        for (String key : params.keySet()) {

            Object value = params.get(key);
            if (value instanceof String) {

                builder.addFormDataPart(key, (String) value);
            } else if (value instanceof File) {

                builder.addFormDataPart(key, ((File) value).getName(), RequestBody.create(null, (File) value));
            }
        }

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .post(requestBody)
                .build();

        return handleRequest(request);
    }

    public void uploadAsync() {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        for (String key : params.keySet()) {

            Object value = params.get(key);
            if (value instanceof String) {

                builder.addFormDataPart(key, (String) value);
            } else if (value instanceof File) {

                builder.addFormDataPart(key, ((File) value).getName(), RequestBody.create(null, (File) value));
            }
        }

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .post(new ProgressRequestBody(requestBody))
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
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

                if (uploadListener != null) {

                    uploadListener.onUploadFailed();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (call.isCanceled()) {

                    return;
                }

                if (response.code() == 200) {

                    if (uploadListener != null) {

                        uploadListener.onUploadSuccess();
                    }
                } else {

                    if (uploadListener != null) {

                        uploadListener.onUploadFailed();
                    }
                }
            }
        });
    }

    @WorkerThread
    public void download() {

        Request request = new Request.Builder()
                .url(this.url)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        handleDownloadRequest(request);
    }

    private void handleDownloadRequest(Request request) {

        this.call = this.okHttpClient.newCall(request);

        this.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                if (downloadListener != null) {
                    downloadListener.onDownloadFailed();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (call.isCanceled()) {
                    return;
                }

                if (response.code() == 200) {
                    saveFile(response);
                } else {
                    downloadListener.onDownloadFailed();
                }
            }
        });
    }

    private void saveFile(Response response) throws IOException {

        //获取文件名
        String fileName = url.substring(url.lastIndexOf("/") + 1);

        //创建文件夹
        File folder = new File(Constants.STORAGE_PATH);
        if (!folder.exists()) {

            folder.mkdirs();
        }
        //创建文件
        File file = new File(Constants.STORAGE_PATH, fileName);
        if (file.exists()) {

            file.delete();
        }
        file.createNewFile();

        byte[] buf = new byte[1024];

        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {

            inputStream = response.body().byteStream();
            long total = response.body().contentLength();

            outputStream = new FileOutputStream(file);
            int len = 0;
            long sum = 0;
            int currentProgress = 0;
            while ((len = inputStream.read(buf)) != -1) {

                outputStream.write(buf, 0, len);
                sum += len;
                int progress = (int) (sum * 1.0f / total * 100);

                if (progress - currentProgress >= 5) {

                    downloadListener.onDownloading(progress);
                    currentProgress = progress;
                }
            }
            outputStream.flush();

            downloadListener.onDownloadSuccess(Constants.STORAGE_PATH + "/" + fileName);
        } catch (Exception e) {

            downloadListener.onDownloadFailed();
        } finally {

            try {

                if (inputStream != null) {

                    inputStream.close();
                }
            } catch (IOException e) {

                e.printStackTrace();
            }
            try {

                if (outputStream != null) {

                    outputStream.close();
                }
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
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
        private Map<String, Object> params;
        private HttpFileDownloadCallback downloadListener;
        private HttpFileUploadCallback uploadListener;

        private List<Integer> ignoreCodes;

        public Builder() {

            params = new HashMap<>();
            ignoreCodes = new ArrayList<>();
        }

        public Builder url(String url) {

            this.url = url;
            return this;
        }

        public Builder addParam(String key, Object value) {

            this.params.put(key, value);
            return this;
        }

        public Builder downloadListener(HttpFileDownloadCallback downloadListener) {

            this.downloadListener = downloadListener;
            return this;
        }

        public Builder uploadListener(HttpFileUploadCallback uploadListener) {

            this.uploadListener = uploadListener;
            return this;
        }

        public HttpFileRequest.Builder ignore(int code) {

            ignoreCodes.add(code);
            return this;
        }

        public HttpFileRequest build() {

            return new HttpFileRequest(this);
        }
    }

    private class ProgressRequestBody extends RequestBody {

        private RequestBody requestBody;
        private BufferedSink bufferedSink;

        public ProgressRequestBody(RequestBody requestBody) {

            this.requestBody = requestBody;
        }

        @Override
        public long contentLength() throws IOException {

            return requestBody.contentLength();
        }

        @Nullable
        @Override
        public MediaType contentType() {

            return requestBody.contentType();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {

            if (bufferedSink == null) {
                //包装
                bufferedSink = Okio.buffer(sink(sink));
            }
            //写入
            requestBody.writeTo(bufferedSink);
            //必须调用flush，否则最后一部分数据可能不会被写入
            bufferedSink.flush();
        }

        private Sink sink(BufferedSink sink) {

            return new ForwardingSink(sink) {

                //当前写入字节数
                long bytesWritten = 0L;
                //总字节长度，避免多次调用contentLength()方法
                long contentLength = 0L;

                @Override
                public void write(Buffer source, long byteCount) throws IOException {

                    super.write(source, byteCount);
                    if (contentLength == 0) {
                        //获得contentLength的值，后续不再调用
                        contentLength = contentLength();
                    }
                    //增加当前写入的字节数
                    bytesWritten += byteCount;
                    //回调
                    if (uploadListener != null) {

                        uploadListener.onUploading((int) (ConvertUtil.decimal2Point(bytesWritten / (double) contentLength) * 100));
                    }
                }
            };
        }
    }
}
