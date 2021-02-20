package com.unitedbustech.eld.launcher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.fragment.BaseFragment;
import com.unitedbustech.eld.view.UIWebView;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description 启动界面
 */
public class LauncherFragment extends BaseFragment {

    private UIWebView webView;

    public static LauncherFragment newInstance() {

        Bundle args = new Bundle();

        LauncherFragment fragment = new LauncherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected View initViews(LayoutInflater inflater, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_launcher, null);
        webView = view.findViewById(R.id.webview);

        return view;
    }

    @Override
    protected void initStatusBar() {

        setStatusBar(R.color.transparent, true);
    }

    public void loadAnimation() {

        webView.loadFile("launcher-anim.html");
    }

    public void showAnimation() {

        webView.setVisibility(View.VISIBLE);
    }
}
