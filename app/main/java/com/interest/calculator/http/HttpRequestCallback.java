package com.interest.calculator.http;

/**
 * @author yufei0213
 * @date 2018/1/6
 * @description http请求异步回调
 */
public interface HttpRequestCallback {

    void onRequestFinish(HttpResponse response);
}
