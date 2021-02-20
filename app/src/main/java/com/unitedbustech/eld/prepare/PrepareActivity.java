package com.unitedbustech.eld.prepare;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.BaseFragmentActivity;
import com.unitedbustech.eld.mvp.BasePresenter;

/**
 * @author yufei0213
 * @date 2018/1/28
 * @description 数据加载页面
 */
public class PrepareActivity extends BaseFragmentActivity {

    private static final String EXTRA_ORIGIN = "com.unitedbustech.eld.prepare.login.prepare.origin";

    public static final String ORIGIN_LOGIN = "origin";
    public static final String ORIGIN_WELCOME = "welcome";

    private PrepareFragment fragment;

    public static Intent newIntent(Context context, String origin) {

        Intent intent = new Intent(context, PrepareActivity.class);

        intent.putExtra(EXTRA_ANIM_IN_IN_ID, R.anim.fade_in);
        intent.putExtra(EXTRA_ANIM_IN_OUT_ID, R.anim.fade_out);
        intent.putExtra(EXTRA_ANIM_OUT_IN_ID, R.anim.fade_in);
        intent.putExtra(EXTRA_ANIM_OUT_OUT_ID, R.anim.fade_out);

        intent.putExtra(EXTRA_ORIGIN, origin);

        return intent;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected BasePresenter initPresenter() {

        return new PreparePresenter(PrepareActivity.this, fragment);
    }

    @Override
    protected Fragment createFragment() {

        String origin = getIntent().getStringExtra(EXTRA_ORIGIN);
        switch (origin) {

            case ORIGIN_LOGIN:

                fragment = PrepareFragment.newInstance(PrepareFragment.ORIGIN_LOGIN);
                break;
            case ORIGIN_WELCOME:

                fragment = PrepareFragment.newInstance(PrepareFragment.ORIGIN_WELCOME);
                break;
            default:

                fragment = PrepareFragment.newInstance("");
        }

        return fragment;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
