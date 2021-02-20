package com.unitedbustech.eld.ifta.create;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;

import com.unitedbustech.eld.activity.BaseFragmentActivity;
import com.unitedbustech.eld.mvp.BasePresenter;

/**
 * @author yufei0213
 * @date 2018/6/25
 * @description 创建IFTA的界面
 */
public class IftaCreateActivity extends BaseFragmentActivity {

    private IftaCreateFragment fragment;

    public static Intent newIntent(Context context) {

        Intent intent = new Intent(context, IftaCreateActivity.class);
        return intent;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected BasePresenter initPresenter() {

        return new IftaCreatePresenter(IftaCreateActivity.this, fragment);
    }

    @Override
    protected Fragment createFragment() {

        fragment = IftaCreateFragment.newInstance();
        return fragment;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
