package com.interest.calculator.ad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.interest.calculator.R;
import com.interest.calculator.activity.AssistActivity;
import com.interest.calculator.activity.BaseActivity;
import com.interest.calculator.view.UIWebView;

/**
 * @author yufei0213
 * @date 2017/12/6
 * @description web activity
 */
public class WebAdActivity extends BaseActivity {

    private static final String TAG = "WebAdActivity";

    /**
     * Intent url主键
     */
    public static final String EXTRA_FILE_NAME = "com.interest.eld.ad.url";
    private UIWebView uiWebView;
    private String url;

    public static Intent newIntent(Context context, String url) {

        Intent intent = new Intent(context, WebAdActivity.class);
        intent.putExtra(EXTRA_FILE_NAME, url);
        return intent;
    }

    @Override
    protected void initVariables() {
        url = getIntent().getStringExtra(EXTRA_FILE_NAME);
    }

    @Override
    protected View onCreateView(Bundle savedInstanceState) {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_web_ad, null);
        uiWebView = view.findViewById(R.id.webview);
        uiWebView.loadUrl(url);
        return view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AssistActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (uiWebView.canGoBack()) {
                uiWebView.goBack();
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
