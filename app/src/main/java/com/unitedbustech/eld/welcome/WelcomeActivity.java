package com.unitedbustech.eld.welcome;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.BaseFragmentActivity;
import com.unitedbustech.eld.bluetooth.BluetoothHandler;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.mvp.BasePresenter;
import com.unitedbustech.eld.system.HeartBeatService;
import com.unitedbustech.eld.update.UpdatePresenter;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description WelcomeActivity
 */
public class WelcomeActivity extends BaseFragmentActivity {

    private WelcomeFragment welcomeFragment;

    public static Intent newIntent(Context context) {

        Intent intent = new Intent(context, WelcomeActivity.class);

        intent.putExtra(EXTRA_ANIM_IN_IN_ID, R.anim.fade_in);
        intent.putExtra(EXTRA_ANIM_IN_OUT_ID, R.anim.fade_out);
        intent.putExtra(EXTRA_ANIM_OUT_IN_ID, R.anim.fade_in);
        intent.putExtra(EXTRA_ANIM_OUT_OUT_ID, R.anim.fade_out);

        return intent;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected BasePresenter initPresenter() {

        new UpdatePresenter(WelcomeActivity.this, welcomeFragment);
        return new WelcomePresenter(WelcomeActivity.this, welcomeFragment);
    }

    @Override
    protected Fragment createFragment() {

        welcomeFragment = WelcomeFragment.newInstance();
        return welcomeFragment;
    }

    @Override
    public void onBackPressed() {

        //关闭蓝牙
        BluetoothHandler.getInstance().destroy();
        //关闭Gps信息
        LocationHandler.getInstance().destroy();
        //关闭心跳
        HeartBeatService.getInstance().stop();
        super.onBackPressed();
    }
}
