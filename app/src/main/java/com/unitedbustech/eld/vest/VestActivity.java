package com.unitedbustech.eld.vest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.BaseActivity;
import com.unitedbustech.eld.activity.WebActivity;
import com.unitedbustech.eld.common.VestData;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.view.TitleBar;
import com.unitedbustech.eld.view.UIWebView;
import com.unitedbustech.eld.view.UiWebViewClient;

/**
 * @author yufei0213
 * @date 2021/2/21
 * @description TODO
 */
public class VestActivity extends BaseActivity implements UiWebViewClient, TitleBar.TitleBarListener {

    public static final String EXTRA_DATA = "com.unitedbustech.eld.vest.data";

    /**
     * 自定义TitleBar
     */
    private TitleBar titleBar;
    /**
     * 自定义webview
     */
    private UIWebView uiWebView;

    private VestData vestData;

    public static Intent newIntent(Context context, String data) {

        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(EXTRA_DATA, data);
        return intent;
    }

    @Override
    protected void initVariables() {

        vestData = JsonUtil.parseObject(getIntent().getStringExtra(EXTRA_DATA),VestData.class);
    }

    @Override
    protected View onCreateView(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(this).inflate(R.layout.activity_web_vest, null);

        titleBar = view.findViewById(R.id.title_bar);

//        titleBar.setBackAvailable(backAvailable);
        titleBar.setListener(this);

        uiWebView = view.findViewById(R.id.webview);
//        uiWebView.loadFile(fileName);

        return view;
    }

    @Override
    public void onPageFinished(String title) {

        titleBar.setTitle(title);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onLeftBtnClick();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLeftBtnClick() {

        if (uiWebView.canGoBack()) {
            uiWebView.goBack();
        }
        finish();
    }
}
