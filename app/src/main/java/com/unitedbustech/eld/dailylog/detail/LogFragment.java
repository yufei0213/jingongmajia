package com.unitedbustech.eld.dailylog.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.fragment.BaseFragment;
import com.unitedbustech.eld.view.UIWebView;

/**
 * @author yufei0213
 * @date 2017/10/20
 * @description LogFragment
 */
public class LogFragment extends BaseFragment {

    private UIWebView webView;

    private boolean hasPause;
    private boolean needReloadFile;

    public static LogFragment newInstance() {

        LogFragment fragment = new LogFragment();
        return fragment;
    }

    @Override
    protected void initVariables() {
    }

    @Override
    protected View initViews(LayoutInflater inflater, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dailylog_detail, null);

        webView = view.findViewById(R.id.webview);
        webView.loadFile("ddl-detail.html");

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

        if (hasPause) {

            if (needReloadFile) {

                reloadFile();
            } else {

                webView.reload();
            }
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

            if (needReloadFile) {

                reloadFile();
            } else {

                webView.reload();
            }
        }
    }

    public void reloadFile() {

        needReloadFile = false;
        webView.loadFile("ddl-detail.html");
    }

    public void needReloadFile() {

        this.needReloadFile = true;
    }
}
