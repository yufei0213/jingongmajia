package com.unitedbustech.eld.launcher;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.BaseFragmentActivity;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.login.LoginActivity;
import com.unitedbustech.eld.mvp.BasePresenter;
import com.unitedbustech.eld.system.AppStatusManager;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.system.UUIDS;
import com.unitedbustech.eld.view.HorizontalDialog;
import com.unitedbustech.eld.welcome.WelcomeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description 启动页面
 */
public class LauncherActivity extends BaseFragmentActivity {

    private final String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA
    };

    public static final int PERMISSION_REQUEST_CODE = 1;
    public static final int APP_SETTING_REQUEST_CODE = 2;
    public static final int GPS_SETTING_REQUEST_CODE = 3;

    private boolean isNeedCheck = true;

    private static final int MSG_CODE = 1;

    private LauncherFragment launcherFragment;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == MSG_CODE) {

                Intent intent = null;
                User user = SystemHelper.getUser();
                if (!SystemHelper.hasLogout() && user != null) {

                    intent = WelcomeActivity.newIntent(LauncherActivity.this);
                } else {

                    intent = LoginActivity.newIntent(LauncherActivity.this);
                }

                startActivity(intent);

                UUIDS.init();

                finish();
            }
        }
    };

    public static Intent newIntent(Context context) {

        Intent intent = new Intent(context, LauncherActivity.class);
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

        if (!isTaskRoot()) {

            if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {

                finish();
                return null;
            }
        }

        launcherFragment = LauncherFragment.newInstance();
        return launcherFragment;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        AppStatusManager.getInstance().setAppStatus(AppStatusManager.NORMAL);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (isNeedCheck) {

            checkPermissions(permissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] paramArrayOfInt) {

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (!verifyPermissions(paramArrayOfInt)) {

                showPermissionTips();
                isNeedCheck = false;
            } else {

                checkGps();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APP_SETTING_REQUEST_CODE) {

            checkPermissions(permissions);
        } else if (requestCode == GPS_SETTING_REQUEST_CODE) {

            checkGps();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void animateIn() {

        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void animateOut() {

        this.overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }

    public void openAnimation() {

        launcherFragment.showAnimation();
    }

    public void closeAnimation() {

        handler.sendEmptyMessage(MSG_CODE);
    }

    /**
     * 检查GPS开关是否打开
     */
    private void checkGps() {

        if (LocationHandler.getInstance().isAvailable()) {

            LocationHandler.getInstance().init();
            launcherFragment.loadAnimation();
        } else {

            showLocationServiceTips();
        }
    }

    /**
     * 检查权限
     *
     * @param permissions 权限集合
     */
    private void checkPermissions(String... permissions) {

        List<String> result = findDeniedPermissions(permissions);
        if (null != result && result.size() > 0) {

            ActivityCompat.requestPermissions(this,
                    result.toArray(new String[result.size()]),
                    PERMISSION_REQUEST_CODE);
        } else {

            checkGps();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions 权限集合
     * @return 没有获取到的权限集合
     */
    private List<String> findDeniedPermissions(String[] permissions) {

        List<String> result = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {

                result.add(perm);
            }
        }

        return result;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param results 请求结果
     * @return 是否已经获取到全部权限
     */
    private boolean verifyPermissions(int[] results) {

        for (int result : results) {

            if (result != PackageManager.PERMISSION_GRANTED) {

                return false;
            }
        }

        return true;
    }

    /**
     * 权限申请的弹窗
     */
    private void showPermissionTips() {

        new HorizontalDialog.Builder(LauncherActivity.this)
                .setIcon(R.drawable.ic_emoji_msg)
                .setText(getString(R.string.permission_tips))
                .setPositiveBtn(R.string.setting, new HorizontalDialog.OnClickListener() {
                    @Override
                    public void onClick(HorizontalDialog dialog, int which) {

                        Intent intent = new Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, APP_SETTING_REQUEST_CODE);

                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .build()
                .show();
    }

    /**
     * 提示用户打开gps服务
     */
    public void showLocationServiceTips() {

        new HorizontalDialog.Builder(LauncherActivity.this)
                .setIcon(R.drawable.ic_emoji_msg)
                .setText(getString(R.string.location_service_tip))
                .setPositiveBtn(R.string.setting, new HorizontalDialog.OnClickListener() {
                    @Override
                    public void onClick(HorizontalDialog dialog, int which) {

                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_SETTING_REQUEST_CODE);

                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .build()
                .show();
    }
}
