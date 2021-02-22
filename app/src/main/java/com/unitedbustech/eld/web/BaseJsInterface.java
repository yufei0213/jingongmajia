package com.unitedbustech.eld.web;

import android.content.Context;

import com.unitedbustech.eld.view.UIWebView;
import com.unitedbustech.eld.view.VestUIWebView;

/**
 * @author yufei0213
 * @date 2018/1/8
 * @description JsInterface基类
 */
public abstract class BaseJsInterface {

    private static final String TAG = "BaseJsInterface";

    protected static final int CALLBACK_COMPLETE = 2;
    protected static final int CALLBACK_SUCCESS = 1;
    protected static final int CALLBACK_FAILURE = -1;

    protected static final String METHOD_KEY = "method";
    protected static final String DATA_KEY = "data";
    protected static final String CALLBACK_KEY = "callBack";

    protected Context context;
    protected VestUIWebView uiWebView;

    public BaseJsInterface(Context context, VestUIWebView uiWebView) {

        this.context = context;
        this.uiWebView = uiWebView;
    }

    public abstract void method(String params);
}
