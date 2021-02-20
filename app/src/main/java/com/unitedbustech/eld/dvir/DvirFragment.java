package com.unitedbustech.eld.dvir;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.fragment.BaseFragment;
import com.unitedbustech.eld.view.UIWebView;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description Dvir
 */
public class DvirFragment extends BaseFragment {

    private boolean hasPause;

    private UIWebView webView;

    public static DvirFragment newInstance() {

        DvirFragment fragment = new DvirFragment();
        return fragment;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected View initViews(LayoutInflater inflater, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dvir, null);

        webView = view.findViewById(R.id.webview);
        webView.loadFile("inspection.html");

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
