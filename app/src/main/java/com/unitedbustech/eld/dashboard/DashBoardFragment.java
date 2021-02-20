package com.unitedbustech.eld.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.UserFunction;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.fragment.BaseFragment;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.view.UIWebView;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description Dashboard
 */
public class DashBoardFragment extends BaseFragment {

    private boolean hasPause;

    private UIWebView webView;

    public static DashBoardFragment newInstance() {

        DashBoardFragment fragment = new DashBoardFragment();
        return fragment;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected View initViews(LayoutInflater inflater, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, null);

        webView = view.findViewById(R.id.webview);
        int userFunction = SystemHelper.getExemptionFunc();
        if (userFunction == UserFunction.DAYS_EXEMPTION) {

            webView.loadFile("dashboard-exemption-days.html");
            EventCenter.getInstance().newExemptionEvent(UserFunction.DAYS_EXEMPTION);
        } else if (userFunction == UserFunction.MILES_EXEMPTION) {

            webView.loadFile("dashboard-exemption-miles.html");
            EventCenter.getInstance().newExemptionEvent(UserFunction.MILES_EXEMPTION);
        } else {

            webView.loadFile("dashboard.html");
        }

        webView.subscribeMsg();

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

    @Override
    public void onDestroy() {

        if (webView != null) {

            webView.unSubscribeMsg();
        }

        super.onDestroy();
    }
}
