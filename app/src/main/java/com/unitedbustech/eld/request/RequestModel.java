package com.unitedbustech.eld.request;

import com.unitedbustech.eld.domain.entry.RequestCache;
import com.unitedbustech.eld.http.HttpFileRequest;
import com.unitedbustech.eld.http.HttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyu
 * @date 2018/3/23
 * @description 具备离线上报模式的httprequest
 */
public class RequestModel {

    /**
     * 一次http请求
     */
    HttpRequest httpRequest;

    /**
     * 一次http请求，带文件
     */
    HttpFileRequest httpFileRequest;

    /**
     * 离线上报模式
     */
    RequestType requestType;

    /**
     * 原始的缓存
     */
    List<RequestCache> requestCaches;

    public RequestModel(HttpRequest httpRequest, RequestType requestType, List<RequestCache> requestCaches) {

        this.httpRequest = httpRequest;
        this.requestType = requestType;
        this.requestCaches = requestCaches;
    }

    public RequestModel(HttpRequest httpRequest, RequestType requestType, RequestCache requestCache) {

        this.httpRequest = httpRequest;
        this.requestType = requestType;

        this.requestCaches = new ArrayList<>();
        this.requestCaches.add(requestCache);
    }

    public RequestModel(HttpFileRequest httpRequest, RequestType requestType, List<RequestCache> requestCaches) {

        this.httpFileRequest = httpRequest;
        this.requestType = requestType;
        this.requestCaches = requestCaches;
    }

    public RequestModel(HttpFileRequest httpRequest, RequestType requestType, RequestCache requestCache) {

        this.httpFileRequest = httpRequest;
        this.requestType = requestType;

        this.requestCaches = new ArrayList<>();
        this.requestCaches.add(requestCache);
    }

    public boolean isDvir() {

        return RequestType.OTHERS.equals(requestType);
    }

    public boolean isDailylog() {

        return RequestType.UNIDENTIFIED.equals(requestType)
                || RequestType.DAILYLOG.equals(requestType);
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public HttpFileRequest getHttpFileRequest() {
        return httpFileRequest;
    }

    public void setHttpFileRequest(HttpFileRequest httpFileRequest) {
        this.httpFileRequest = httpFileRequest;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public List<RequestCache> getRequestCaches() {
        return requestCaches;
    }

    public void setRequestCaches(List<RequestCache> requestCaches) {
        this.requestCaches = requestCaches;
    }
}
