package com.unitedbustech.eld.dot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.fragment.BaseFragment;
import com.unitedbustech.eld.view.UIWebView;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description Dot
 */
public class DotFragment extends BaseFragment {

    private boolean hasPause;

    private UIWebView webView;

    public static DotFragment newInstance() {

        DotFragment fragment = new DotFragment();
        return fragment;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected View initViews(LayoutInflater inflater, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dot, null);

        webView = view.findViewById(R.id.webview);
        webView.loadFile("dot.html");

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
