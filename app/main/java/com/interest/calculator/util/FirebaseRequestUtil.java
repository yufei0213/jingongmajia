package com.interest.calculator.util;

import android.app.Activity;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * @author mamw
 * @date 2018/02/07
 * @description Firebase工具类
 */
public final class FirebaseRequestUtil {

    private static final String TAG = "FirebaseRequestUtil";

    private static final String EVENT_CATEGORY = "eventCategory";
    private static final String EVENT_ACTION = "eventAction";
    private static final String EVENT_LABEL = "eventLabel";
    private static final String EVENT_VALUE = "eventValue";
    private static final String SCREEN_NAME = "screenName";

    public static final String EVENT_CATEGORY_LOGIN = "login";
    public static final String EVENT_CATEGORY_VCONNECT = "vconnect";
    public static final String EVENT_CATEGORY_LOGOUT = "logout";
    public static final String EVENT_CATEGORY_INSPECTION = "inspection";
    public static final String EVENT_CATEGORY_DOT = "dot";

    public static final String EVENT_ACTION_CLICK = "click";
    public static final String EVENT_ACTION_ALERT = "alert";

    private static FirebaseRequestUtil instance = null;

    private Activity mActivity;
    private FirebaseAnalytics mFirebaseAnalytics = null;

    private FirebaseRequestUtil(Activity activity) {

        this.mActivity = activity;
        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.mActivity);
    }

    public static FirebaseRequestUtil getInstance(Activity activity) {

        instance = new FirebaseRequestUtil(activity);

        return instance;
    }

    /**
     * 上报事件
     *
     * @param jsonString
     */
    public void logEventForJsonString(String jsonString) {

        // 获取参数
        JSONObject jsonObject = JsonUtil.parseObject(jsonString);
        String category = JsonUtil.getString(jsonObject, EVENT_CATEGORY);
        String action = JsonUtil.getString(jsonObject, EVENT_ACTION);
        String label = JsonUtil.getString(jsonObject, EVENT_LABEL);
        String value = JsonUtil.getString(jsonObject, EVENT_VALUE);
        // 创建Bundle
        Bundle bundle = new Bundle();
        bundle.putString(EVENT_ACTION, action);
        bundle.putString(EVENT_LABEL, label);
        bundle.putString(EVENT_VALUE, value);
        // 上报事件
//        this.mFirebaseAnalytics.logEvent(category, bundle);
    }

    /**
     * 上报事件
     *
     * @param eventCategory
     * @param eventAction
     * @param eventLabel
     * @param eventValue
     */
    public void logEventForString(String eventCategory, String eventAction, String eventLabel, String eventValue) {

        // 创建Bundle
        Bundle bundle = new Bundle();
        bundle.putString(EVENT_ACTION, eventAction);
        bundle.putString(EVENT_LABEL, eventLabel);
        bundle.putString(EVENT_VALUE, eventValue);
        // 上报事件
//        this.mFirebaseAnalytics.logEvent(eventCategory, bundle);
    }

    /**
     * 上报屏幕
     *
     * @param jsonString
     */
    public void logScreenForJsonString(String jsonString) {

        // 获取参数
        JSONObject jsonObject = JsonUtil.parseObject(jsonString);
        String screenName = JsonUtil.getString(jsonObject, SCREEN_NAME);
        // 上报屏幕
//        this.mFirebaseAnalytics.setCurrentScreen(this.mActivity, screenName, this.mActivity.getClass().getSimpleName());
    }

    /**
     * 上报屏幕
     *
     * @param pageName
     */
    public void logScreenForString(String pageName) {

        // 上报屏幕
//        this.mFirebaseAnalytics.setCurrentScreen(this.mActivity, pageName, this.mActivity.getClass().getSimpleName());
    }

}
