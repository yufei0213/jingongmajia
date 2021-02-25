package com.interest.calculator.view;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * @author yufei0213
 * @date 2021/2/21
 * @description TODO
 */
public interface UiWebViewClient {

    void onPageFinished(String title);

    void onShowFileChooser(WebView webView,
                      ValueCallback<Uri[]> filePathCallback,
                      WebChromeClient.FileChooserParams fileChooserParams);

    void onLongClickSavePic(String picUrl);
}
