package com.interest.calculator.launcher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.interest.calculator.R;
import com.interest.calculator.fragment.BaseFragment;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description 启动界面
 */
public class LauncherFragment extends BaseFragment {

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
        return inflater.inflate(R.layout.fragment_launcher, null);
    }

    @Override
    protected void initStatusBar() {

        setStatusBar(R.color.transparent, true);
    }
}
