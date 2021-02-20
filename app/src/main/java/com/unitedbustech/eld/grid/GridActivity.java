package com.unitedbustech.eld.grid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.AssistActivity;
import com.unitedbustech.eld.activity.BaseActivity;
import com.unitedbustech.eld.view.UIWebView;

/**
 * @author yufei0213
 * @date 2018/12/27
 * @description Grid展示页面
 */
public class GridActivity extends BaseActivity {

    private static final String TAG = "GridActivity";

    private boolean hasPause;

    private UIWebView webView;

    public static Intent newIntent(Context context, @Nullable String params) {

        Intent intent = new Intent(context, GridActivity.class);
        if (!TextUtils.isEmpty(params)) {

            intent.putExtra(UIWebView.EXTRA_PARAMS, params);
        }

        return intent;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initStatusBar() {

        setStatusBar(R.color.white, false);
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

        View view = LayoutInflater.from(GridActivity.this).inflate(R.layout.activity_grid, null);

        webView = view.findViewById(R.id.webview);
        webView.loadFile("zoom-grid.html");

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
}
