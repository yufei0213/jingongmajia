package com.unitedbustech.eld.dot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.fragment.BaseFragment;
import com.unitedbustech.eld.view.UIWebView;

/**
 * @author mamw
 * @date 2018/1/24
 * @description DotReviewFragment
 */
public class DotReviewFragment extends BaseFragment {

    /**
     * UIWebView
     */
    private UIWebView webView;

    public static DotReviewFragment newInstance() {

        DotReviewFragment fragment = new DotReviewFragment();
        return fragment;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected View initViews(LayoutInflater inflater, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dot_detail, null);

        webView = view.findViewById(R.id.webview);
        webView.loadFile("dot-review.html");

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        if (hidden) {

            webView.pause();
        } else {

            webView.reload();
        }
    }
}
