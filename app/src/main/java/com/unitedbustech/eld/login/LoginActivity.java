package com.unitedbustech.eld.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.BaseFragmentActivity;
import com.unitedbustech.eld.bluetooth.BluetoothHandler;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.mvp.BasePresenter;
import com.unitedbustech.eld.system.HeartBeatService;
import com.unitedbustech.eld.update.UpdatePresenter;
import com.unitedbustech.eld.view.HorizontalDialog;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description 登录
 */
public class LoginActivity extends BaseFragmentActivity {

    public static final String EXTRA_PARAMS_TIPS = "com.unitedbustech.eld.login.params.tips";

    public static final int SESSION_TIMEOUT = 1;
    public static final int SESSION_DIAGNOSTIC = 2;
    public static final int SESSION_DIAGNOSTIC_OFFLINE = 3;
    public static final int LOGIN_INFO_ERROR = 4;

    private LoginFragment loginFragment;

    public static Intent newIntent(Context context) {

        Intent intent = new Intent(context, LoginActivity.class);

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

        new UpdatePresenter(LoginActivity.this, loginFragment);
        return new LoginPresenter(LoginActivity.this, loginFragment);
    }

    @Override
    protected Fragment createFragment() {

        loginFragment = LoginFragment.newInstance();
        return loginFragment;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        showTip();
    }

    private void showTip() {

        int type = getIntent().getIntExtra(EXTRA_PARAMS_TIPS, 0);

        switch (type) {

            case SESSION_TIMEOUT:

                new HorizontalDialog.Builder(LoginActivity.this)
                        .setIcon(R.drawable.ic_emoji_love)
                        .setText(R.string.login_again)
                        .setPositiveBtn(R.string.ok, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(HorizontalDialog dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .build()
                        .show();
                if (loginFragment != null) {

                    loginFragment.setCarrierIdAndDriverIdShow(true);
                }
                break;
            case SESSION_DIAGNOSTIC:

                new HorizontalDialog.Builder(LoginActivity.this)
                        .setIcon(R.drawable.ic_emoji_love)
                        .setText(R.string.login_on_other)
                        .setPositiveBtn(R.string.ok, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(HorizontalDialog dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                if (loginFragment != null) {

                    loginFragment.setCarrierIdAndDriverIdShow(true);
                }
                break;
            case SESSION_DIAGNOSTIC_OFFLINE:

                new HorizontalDialog.Builder(LoginActivity.this)
                        .setIcon(R.drawable.ic_emoji_love)
                        .setText(R.string.login_on_other_offline)
                        .setPositiveBtn(R.string.ok, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(HorizontalDialog dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                if (loginFragment != null) {

                    loginFragment.setCarrierIdAndDriverIdShow(true);
                }
                break;
            case LOGIN_INFO_ERROR:

                new HorizontalDialog.Builder(LoginActivity.this)
                        .setIcon(R.drawable.ic_emoji_love)
                        .setText(R.string.login_info_error)
                        .setPositiveBtn(R.string.ok, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(HorizontalDialog dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .build()
                        .show();
                break;
            default:
                break;
        }
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
