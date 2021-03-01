package com.interest.calculator.jsinterface;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.interest.calculator.activity.Activity;
import com.interest.calculator.system.UUIDS;
import com.interest.calculator.util.LocalDataStorageUtil;
import com.interest.calculator.view.VestUIWebView;
import com.interest.calculator.web.BaseJsInterface;
import com.interest.calculator.web.JsInterface;
import com.interest.calculator.webpage.WebPageActivity;

import java.io.IOException;

/**
 * @author yufei0213
 * @date 2017/12/6
 * @description webview 接口基类
 */
@JsInterface(name = "AppJs")
public class AppJsInterface extends BaseJsInterface {

    public static final String FORBID_BACK_FOR_JS = "forbidBackForJS";
    public static final String GET_DEVICE_ID = "getDeviceId";
    public static final String GET_GA_ID = "getGaId";
    public static final String GET_GOOGLE_ID = "getGoogleId";
    public static final String IS_CONTAINS_NAME = "isContainsName";
    public static final String OPEN_BROWSER = "openBrowser";
    public static final String OPEN_GOOGLE = "openGoogle";
    public static final String OPEN_PAY_TM = "openPayTm";
    public static final String OPEN_PURE_BROWSER = "openPureBrowser";
    public static final String SHOULD_FORBID_SYS_BACK_PRESS = "shouldForbidSysBackPress";
    public static final String SHOW_TITLE_BAR = "showTitleBar";
    public static final String TAKE_CHANNEL = "takeChannel";
    public static final String TAKE_FCM_PUSH_ID = "takeFCMPushId";
    public static final String TAKE_PORTRAIT_PICTURE = "takePortraitPicture";
    public static final String TAKE_PUSH_ID = "takePushId";


    private AppMethodListener listener;

    public AppJsInterface(Context context, VestUIWebView uiWebView) {
        super(context, uiWebView);
    }

    public void setAppMethodListener(AppMethodListener listener) {
        this.listener = listener;
    }

    @Override
    @JavascriptInterface
    public void method(String params) {

    }

    @JavascriptInterface
    public String isNewEdition() {
        return "true";
    }

    @JavascriptInterface
    @Nullable
    public String callMethod(String data) {
        JSONObject dataObj = JSON.parseObject(data);
        String methodName = dataObj.getString("name");
//        JSONObject paraObj = dataObj.getJSONObject("parameter");
//        String paraStr = dataObj.getString("parameter");
        switch (methodName) {
            case FORBID_BACK_FOR_JS:
                forbidBackForJS(dataObj.getInteger("parameter"), methodName);
                return null;
            case GET_DEVICE_ID:
                return getDeviceId();
            case GET_GA_ID:
                return getGaId();
            case GET_GOOGLE_ID:
                return getGoogleId();
            case IS_CONTAINS_NAME:
                isContainsName(methodName, dataObj.getString("parameter"));
                return null;
            case OPEN_BROWSER:
                openBrowser(dataObj.getString("parameter"));
                return null;
            case OPEN_GOOGLE:
                openGoogle(dataObj.getString("parameter"));
                return null;
            case OPEN_PAY_TM:
                openPayTm(dataObj.getString("parameter"));
                return null;
            case OPEN_PURE_BROWSER:
                openPureBrowser(dataObj.getString("parameter"));
                return null;
            case SHOULD_FORBID_SYS_BACK_PRESS:
                shouldForbidSysBackPress(dataObj.getInteger("parameter"));
                return null;
            case SHOW_TITLE_BAR:
                showTitleBar(dataObj.getBoolean("parameter"));
                return null;
            case TAKE_CHANNEL:
                return takeChannel();
            case TAKE_FCM_PUSH_ID:
                return takeFCMPushId();
            case TAKE_PORTRAIT_PICTURE:
                takePortraitPicture(dataObj.getString("parameter"));
                return null;
            case TAKE_PUSH_ID:
                return takePushId();
            default:
                return null;
        }

    }

    /**
     * 获取设备id
     * 必须保证有值
     * 获取不到的时候生成一个UUID
     */
    @NonNull
    public String getDeviceId() {
        return UUIDS.getUUID();
    }

    /**
     * 获取个推设备id
     * 传空串就行
     */
    public String takePushId() {
        return "";
    }

    /**
     * 获取fcm 令牌
     * 看FCM推送的文档，有监听和获取令牌的方法
     * 详情见第八点
     */
    public String takeFCMPushId() {
        //fcm生成的注册令牌
        //TODOString fireBaseToken = FirebaseInstanceId.getInstance().getToken()
//        return FirebaseInstanceId.getInstance().getToken();
        return LocalDataStorageUtil.getString("fcmToken");
    }

    /**
     * 获取渠道
     */
    public String takeChannel() {
        return "google";
    }

    /**
     * 获取ANDROID_ID
     * public static final String ANDROID_ID
     */
    public String getGoogleId() {
        return UUIDS.getUUID();
//        return LocalDataStorageUtil.getString("googleId");
    }

    /**
     * 集成branch包的时候已经带有Google Play Service核心jar包
     * 获取gpsadid 谷歌广告id
     * AdvertisingIdClient.getAdvertisingIdInfo() 异步方法
     */
    public String getGaId() {

        try {
            return AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
        } catch (IOException | GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * H5调用原生谷歌登录
     * 后续流程看第七点
     *
     * @param data {"sign":"","host":"https://bb.skr.today"}
     */
    public void openGoogle(String data) {
        listener.openGoogle(data);
    }

    /**
     * 打开paytm
     * 本地有paytm打开应用/没有打开web版 paytm支付(web版)需要新开一个页面
     *
     * @param data {"textToken":"","orderId":"","mid":"","amount":0.0}
     */
    public void openPayTm(String data) {
        listener.openPayTm(data);
    }

    /**
     * 头像获取
     * 流程:H5调用方法 - 自己打开图片选择器 - 回调返回H5
     * base64使用格式：Base64.NO_WRAP
     *
     * @param callbackMethod 回传图片时调用H5的方法名
     */
    public void takePortraitPicture(String callbackMethod) {

        listener.takePortraitPicture(callbackMethod);
        // 参考实现：成员变量记录下js方法名，图片转成base64字符串后调用该js方法传递给H5
        // 下面一段代码仅供参考，能实现功能即可
//        if (!TextUtils.isEmpty(callbackMethod)) {
//            StringBuilder builder = new StringBuilder(callbackMethod).append("(");
//            builder.append("'").append("data:image/png;base64,").append(str).append("'");
//            builder.append(")");
//            String method = builder.toString();
//            String javaScript = "javascript:" + method;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                uiWebView.evaluateJavascript(javaScript, null);
//            } else {
//                uiWebView.loadUrl(javaScript);
//            }
//        }
    }

    /**
     * 控制webview是否显示 TitleBar
     * （点击返回键webview 后退）
     *
     * @param visible
     */
    public void showTitleBar(boolean visible) {
        listener.showTitleBar(visible);
    }

    /**
     * AppJs是否存在交互方法 告诉H5是否存在传入的对应方法
     *
     * @param name 方法名
     */
    private void isContainsName(String callbackMethod, String name) {
        boolean has = false;
        switch (name) {
            case FORBID_BACK_FOR_JS:
                has = true;
                break;
            case GET_DEVICE_ID:
                has = true;
                break;
            case GET_GA_ID:
                has = true;
                break;
            case GET_GOOGLE_ID:
                has = true;
                break;
            case IS_CONTAINS_NAME:
                has = true;
                break;
            case OPEN_BROWSER:
                has = true;
                break;
            case OPEN_GOOGLE:
                has = true;
                break;
            case OPEN_PAY_TM:
                has = true;
                break;
            case OPEN_PURE_BROWSER:
                has = true;
                break;
            case SHOULD_FORBID_SYS_BACK_PRESS:
                has = true;
                break;
            case SHOW_TITLE_BAR:
                has = true;
                break;
            case TAKE_CHANNEL:
                has = true;
                break;
            case TAKE_FCM_PUSH_ID:
                has = true;
                break;
            case TAKE_PORTRAIT_PICTURE:
                has = true;
                break;
            case TAKE_PUSH_ID:
                has = true;
                break;
            default:
                break;
        }

        boolean finalHas = has;
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uiWebView.loadUrl("javascript:" + callbackMethod + "(" + finalHas + ");");
            }
        });
    }

    /**
     * 由h5控制是否禁用系统返回键
     *
     * @param forbid 是否禁止返回键 1:禁止
     */
    public void shouldForbidSysBackPress(int forbid) {
        //WebActivity成员变量记录下是否禁止
//        context.setShouldForbidBackPress(forbid);
        //WebActivity 重写onBackPressed方法 变量为1时禁止返回操作
        listener.shouldForbidSysBackPress(forbid);
    }

    /**
     * 由h5控制返回键功能
     *
     * @param forbid     是否禁止返回键 1:禁止
     * @param methodName 反回时调用的h5方法 例如:detailBack() webview需要执行javascrept:detailBack()
     */
    public void forbidBackForJS(int forbid, String methodName) {
//        context.setShouldForbidBackPress(forbid);
        //同上
//        context.setBackPressJSMethod(methodName);
        //WebActivity成员变量记录下js方法名 在禁止返回时调用js方法
        listener.forbidBackForJS(forbid, methodName);
    }

    /**
     * 使用手机里面的浏览器打开 url
     *
     * @param url 打开 url
     */
    public void openBrowser(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    /**
     * 打开一个基本配置的webview （不修改UA、不设置AppJs、可以缓存）
     * 打开新页面
     * 加载webview的情况分类(判断依据：url、postData、html)
     * |-------1、只有url：webView.loadUrl()
     * |-------2、有url和postData：webView.postUrl()
     * |-------3、有html webView.loadDataWithBaseURL()
     *
     * @param json 打开web传参
     *             {"title":"", 标题
     *             "url":"", 加载的地址
     *             "hasTitleBar":false, 是否显示标题栏
     *             "rewriteTitle":true, 是否通过加载的Web重写标题
     *             "stateBarTextColor":"black", 状态栏字体颜色 black|white
     *             "titleTextColor":"#FFFFFF", 标题字体颜色
     *             "titleColor":"#FFFFFF", 状态栏和标题背景色
     *             "postData":"", webView post方法时会用到
     *             "html":"", 加载htmlCode（例如：<body></body>）,
     *             "webBack":true, true:web回退(点击返回键webview可以回退就回退，无法回退的时候关闭该页面)|false(点击返回键关闭该页面) 直接关闭页面
     *             }
     */
    public void openPureBrowser(String json) {
        Intent intent = WebPageActivity.newIntent(context, json);
        context.startActivity(intent);
    }
}
