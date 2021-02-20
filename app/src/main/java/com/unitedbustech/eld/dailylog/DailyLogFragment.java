package com.unitedbustech.eld.dailylog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.fragment.BaseFragment;
import com.unitedbustech.eld.view.UIWebView;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description DailyLog
 */
public class DailyLogFragment extends BaseFragment {

    private boolean hasPause;

    private UIWebView webView;

    public static DailyLogFragment newInstance() {

        DailyLogFragment fragment = new DailyLogFragment();
        return fragment;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected View initViews(LayoutInflater inflater, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dailylog, null);

        webView = view.findViewById(R.id.webview);
        webView.loadFile("ddl.html");

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

        if (hasPause) {

            webView.reload();
        }
    }

    @Override
    public void onPause() {

        super.onPause();

        hasPause = true;
        if (webView != null) {

            webView.pause();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        if (hidden) {

            hasPause = true;
            webView.pause();
        } else {

            webView.reload();
        }
    }
}
