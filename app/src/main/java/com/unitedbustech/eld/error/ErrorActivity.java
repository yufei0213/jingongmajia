package com.unitedbustech.eld.error;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.BaseFragmentActivity;
import com.unitedbustech.eld.mvp.BasePresenter;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description 启动页面
 */
public class ErrorActivity extends BaseFragmentActivity {

    public static Intent newIntent(Context context) {

        Intent intent = new Intent(context, ErrorActivity.class);
        return intent;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected Fragment createFragment() {
        return ErrorFragment.newInstance();
    }

    @Override
    protected void animateIn() {

        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void animateOut() {

        this.overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }
}
