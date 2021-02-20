package com.unitedbustech.eld.driving;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.AssistActivity;
import com.unitedbustech.eld.activity.BaseActivity;
import com.unitedbustech.eld.view.UIWebView;

/**
 * @author yufei0213
 * @date 2018/1/27
 * @description 开车状态页面
 */
public class DrivingActivity extends BaseActivity {

    private static final String TAG = "DrivingActivity";

    private boolean hasPause;

    private UIWebView webView;

    public static Intent newIntent(Context context) {

        Intent intent = new Intent(context, DrivingActivity.class);
        return intent;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initStatusBar() {

        setStatusBar(R.color.driving_bg, false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (isOpen) {

            new AssistActivity(this);
        }
    }

    @Override
    protected View onCreateView(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(DrivingActivity.this).inflate(R.layout.activity_driving, null);

        webView = view.findViewById(R.id.webview);
        webView.setBackgroundColor(getResources().getColor(R.color.transparent));
        webView.loadFile("driving.html");

        webView.subscribeMsg();

        return view;
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (hasPause) {

            webView.reload();
            hasPause = false;
        }
    }

    @Override
    protected void onPause() {

        super.onPause();

        hasPause = true;
        if (webView != null) {

            webView.pause();
        }
    }

    @Override
    protected void onDestroy() {

        if (webView != null) {

            webView.unSubscribeMsg();
        }

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
