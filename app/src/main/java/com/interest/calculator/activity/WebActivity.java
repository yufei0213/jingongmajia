package com.interest.calculator.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.interest.calculator.R;
import com.interest.calculator.view.TitleBar;
import com.interest.calculator.view.UIWebView;

/**
 * @author yufei0213
 * @date 2017/12/6
 * @description web activity
 */
public class WebActivity extends BaseActivity implements TitleBar.TitleBarListener {

    private static final String TAG = "WebActivity";

    /**
     * Intent url主键
     */
    public static final String EXTRA_FILE_NAME = "com.interest.applib.activity.fileName";
    /**
     * Intent title 主键
     */
    public static final String EXTRA_TITLE = "com.interest.applib.activity.title";
    /**
     * Intent backAvailable 主键
     */
    public static final String EXTRA_BACKAVAILABLE = "com.interest.applib.activity.backAvailable";

    /**
     * 可以返回
     */
    public static final int CAN_BACK = 1;
    /**
     * 不能返回
     */
    public static final int CANNOT_BACK = -1;

    /**
     * 初始化参数中，backType的主键
     */
    public static final String BACK_TYPE_KEY = "backType";
    /**
     * 需要给父界面传递数据
     */
    public static final int START_FOR_RESULT = 1;
    /**
     * 不需要给父界面传递参数
     */
    public static final int START_NO_RESULT = -1;

    /**
     * 能否返回
     */
    private boolean backAvailable;

    /**
     * 上一个状态是否是pause状态
     */
    private boolean hasPause;

    /**
     * 要加载的url
     */
    private String fileName;
    /**
     * 页面标题
     */
    private String title;
    /**
     * 自定义TitleBar
     */
    private TitleBar titleBar;
    /**
     * 自定义webview
     */
    private UIWebView uiWebView;

    /**
     * 创建Intent
     *
     * @param context  上下文
     * @param fileName 文件名
     * @return Intent
     */
    public static Intent newIntent(Context context, String fileName, String title) {

        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        intent.putExtra(EXTRA_TITLE, title);

        return intent;
    }

    /**
     * 创建Intent
     *
     * @param context  上下文
     * @param fileName 文件名
     * @param params   传递给页面的参数
     * @return Intent
     */
    public static Intent newIntent(Context context, String fileName, String title, @Nullable String params) {

        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        intent.putExtra(EXTRA_TITLE, title);
        if (!TextUtils.isEmpty(params)) {

            intent.putExtra(UIWebView.EXTRA_PARAMS, params);
        }

        return intent;
    }

    /**
     * 创建Intent
     *
     * @param context  上下文
     * @param fileName 文件名
     * @param params   传递给页面的参数
     * @return Intent
     */
    public static Intent newIntent(Context context, String fileName, String title, int backAvailable, @Nullable String params) {

        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_BACKAVAILABLE, backAvailable);
        if (!TextUtils.isEmpty(params)) {

            intent.putExtra(UIWebView.EXTRA_PARAMS, params);
        }

        return intent;
    }

    @Override
    protected void initVariables() {

        fileName = getIntent().getStringExtra(EXTRA_FILE_NAME);
        title = getIntent().getStringExtra(EXTRA_TITLE);
        backAvailable = getIntent().getIntExtra(EXTRA_BACKAVAILABLE, CAN_BACK) == CAN_BACK;
    }

    @Override
    protected View onCreateView(Bundle savedInstanceState) {

        View view = LayoutInflater.from(this).inflate(R.layout.activity_web, null);

        titleBar = view.findViewById(R.id.title_bar);

        titleBar.setTitle(title);
        titleBar.setBackAvailable(backAvailable);
        titleBar.setListener(this);

        uiWebView = view.findViewById(R.id.webview);
        uiWebView.loadFile(fileName);

        return view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        new AssistActivity(this);
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (hasPause) {

            uiWebView.reload();
            hasPause = false;
        }
    }

    @Override
    protected void onPause() {

        super.onPause();

        hasPause = true;
        if (uiWebView != null) {

            uiWebView.pause();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        hasPause = true;
        if (uiWebView != null) {

            uiWebView.destroy();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (backAvailable) {

                onLeftBtnClick();
            } else {

                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLeftBtnClick() {

        uiWebView.leftBtnClick();
    }
}
