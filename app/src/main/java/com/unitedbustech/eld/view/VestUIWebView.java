package com.unitedbustech.eld.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unitedbustech.eld.jsinterface.AppJsInterface;
import com.unitedbustech.eld.jsinterface.AppMethodListener;
import com.unitedbustech.eld.web.JsInterface;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author yufei0213
 * @date 2017/10/20
 * @description 自定义WebView
 */
public class VestUIWebView extends WebView {

    private Context context;

    private UiWebViewClient uiWebViewClient;

    private AppJsInterface appJsInterface;

    public VestUIWebView(Context context) {

        this(context, null);
    }

    public VestUIWebView(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        this.context = context;

        initWebViewParams();
        appJsInterface = new AppJsInterface(context, this);
        addJavascriptInterface(appJsInterface, "AppJs");

//        this.addJsInterface(AppJsInterface.class);
    }

    public void setClient(UiWebViewClient uiWebViewClient) {
        this.uiWebViewClient = uiWebViewClient;
    }

    public void setAppMethodListener(AppMethodListener listener) {
        appJsInterface.setAppMethodListener(listener);
    }

    /**
     * 注册js接口
     *
     * @param classList class列表
     */
    public void addJsInterface(@NonNull Class<?>... classList) {

        for (Class entry : classList) {

            JsInterface jsInterface = (JsInterface) entry.getAnnotation(JsInterface.class);
            if (jsInterface == null) {

                continue;
            }

            String name = jsInterface.name();
            if (TextUtils.isEmpty(name)) {

                name = entry.getSimpleName();
            }

            try {

                Constructor constructor = entry.getConstructor(Context.class, this.getClass());
                Object object = constructor.newInstance(this.context, this);

                Method method = this.getClass().getMethod("addJavascriptInterface", Object.class, String.class);
                method.invoke(this, object, name);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    /**
     * 使用loadUrl方法。
     * 只写具体的html文件名。
     * 解决国际化的问题。
     *
     * @param fileName 文件名
     */
    public void loadFile(String fileName) {

//        String url;
//        if (LanguageUtil.getInstance().getLanguageType() == LanguageUtil.LANGUAGE_ZH) {
//
//            url = Constants.WEB_PATH_ZH + fileName;
//        } else {
//
//            url = Constants.WEB_PATH_EN + fileName;
//        }
//
//        this.loadUrl(url);
    }

    /**
     * 初始化webview
     */
    protected void initWebViewParams() {

        WebSettings webSettings = getSettings();

        String userAgentString = webSettings.getUserAgentString();
        userAgentString = "ANDROID_AGENT_NATIVE/2.0" + " " + userAgentString;
        getSettings().setUserAgentString(userAgentString);
//        webView.addJavascriptInterface(new AppJs(this), "AppJs");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.NORMAL);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(context.getExternalCacheDir().getPath());
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setEnableSmoothTransition(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setAppCacheMaxSize(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(false);
        }

        clearHistory();
        setDrawingCacheEnabled(true);
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = VestUIWebView.this.getHitTestResult();
                if (result != null) {
                    int type = result.getType();
                    if (type == WebView.HitTestResult.IMAGE_TYPE) {
                        uiWebViewClient.onLongClickSavePic(result.getExtra());
                    }
                }
                return false;
            }
        });
        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimeType, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            }
        });

        setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                uiWebViewClient.onPageFinished(view.getTitle());
            }
        });
        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
                uiWebViewClient.onShowFileChooser(webView, valueCallback, fileChooserParams);
                return true;
            }
        });
    }
}
