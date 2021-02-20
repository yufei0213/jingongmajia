package com.unitedbustech.eld.dot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.DotMode;
import com.unitedbustech.eld.fragment.BaseFragment;
import com.unitedbustech.eld.hos.core.HosHandler;
import com.unitedbustech.eld.view.UIWebView;

/**
 * @author mamw
 * @date 2018/1/24
 * @description DotSendFragment
 */
public class DotSendFragment extends BaseFragment implements View.OnTouchListener {

    private UIWebView webView;

    public static DotSendFragment newInstance() {

        DotSendFragment fragment = new DotSendFragment();
        return fragment;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected View initViews(LayoutInflater inflater, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dot_detail, null);

        webView = view.findViewById(R.id.webview);
        int dotModeType = DotMode.getModeType(HosHandler.getInstance().getRule().getId());
        if (dotModeType == DotMode.Canada_14_120) {

            webView.loadFile("dot-send-canada-14d.html");
        } else if (dotModeType == DotMode.Canada_7_60) {

            webView.loadFile("dot-send-canada-7d.html");
        } else {

            webView.loadFile("dot-send-america.html");
        }

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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        return false;
    }
}
