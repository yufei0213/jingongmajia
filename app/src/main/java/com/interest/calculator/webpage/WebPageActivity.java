package com.interest.calculator.webpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.interest.calculator.R;
import com.interest.calculator.activity.AssistActivity;
import com.interest.calculator.activity.BaseActivity;
import com.interest.calculator.util.JsonUtil;
import com.interest.calculator.view.TitleBar;
import com.interest.calculator.view.UIWebView;

/**
 * @author yufei0213
 * @date 2017/12/6
 * @description web activity
 */
public class WebPageActivity extends BaseActivity {

    private static final String TAG = "WebPageActivity";

    /**
     * Intent url主键
     */
    public static final String EXTRA_FILE_NAME = "com.interest.eld.ad.data";
    private TitleBar titleBar;
    private UIWebView uiWebView;
    private WebPageData data;

    public static Intent newIntent(Context context, String data) {

        Intent intent = new Intent(context, WebPageActivity.class);
        intent.putExtra(EXTRA_FILE_NAME, data);
        return intent;
    }

    @Override
    protected void initVariables() {
        data = JsonUtil.parseObject(getIntent().getStringExtra(EXTRA_FILE_NAME), WebPageData.class);
    }

    @Override
    protected View onCreateView(Bundle savedInstanceState) {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_web_page, null);
        titleBar = view.findViewById(R.id.title_bar);
        uiWebView = view.findViewById(R.id.webview);
        if (!data.isHasTitleBar()) {
            titleBar.setVisibility(View.GONE);
        } else {
            titleBar.setBackground(data.getTitleColor());
            titleBar.setTitleColor(data.getTitleTextColor());
            titleBar.setTitle(data.getTitle());
        }
        if (!TextUtils.isEmpty(data.getHtml())) {
            uiWebView.loadDataWithBaseURL(null, data.getHtml(), "text/html", "utf-8", null);
        } else {
            if (!TextUtils.isEmpty(data.getUrl())) {
                if (!TextUtils.isEmpty(data.getPostData())) {
                    uiWebView.postUrl(data.getUrl(), data.getPostData().getBytes());
                } else {
                    uiWebView.loadUrl(data.getUrl());
                }
            }
        }
//        uiWebView.loadUrl(url);
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
            if (data.isWebBack() && uiWebView.canGoBack()) {
                uiWebView.goBack();
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
