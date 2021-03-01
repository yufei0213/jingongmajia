package com.interest.calculator.vest_mock;

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
public class VestMockActivity extends BaseActivity {

    private static final String TAG = "WebActivity";
    private UIWebView uiWebView;
    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, VestMockActivity.class);
        return intent;
    }

    @Override
    protected void initVariables() {
//        url = getIntent().getStringExtra(EXTRA_FILE_NAME);
    }

    @Override
    protected View onCreateView(Bundle savedInstanceState) {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_vest_mock, null);
        uiWebView = view.findViewById(R.id.webview);
//        uiWebView.loadUrl("file:///android_asset/calculator_for_house_loan-master/jquery/index.html");
        uiWebView.loadUrl("file:///android_asset/loancalculator-master/index.html");
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
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
        return true;
    }
}
