package com.unitedbustech.eld.launcher;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.BaseFragmentActivity;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.error.ErrorActivity;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpRequestCallback;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.mvp.BasePresenter;
import com.unitedbustech.eld.system.UUIDS;
import com.unitedbustech.eld.util.AppUtil;
import com.unitedbustech.eld.vest.VestActivity;
import com.unitedbustech.eld.view.HorizontalDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description 启动页面
 */
public class LauncherActivity extends BaseFragmentActivity {

    private final String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static final int PERMISSION_REQUEST_CODE = 1;
    public static final int APP_SETTING_REQUEST_CODE = 2;

    private boolean isNeedCheck = true;

    private static final int MSG_CODE = 1;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == MSG_CODE) {

                HttpRequest.Builder builder = new HttpRequest.Builder();
                final HttpRequest request = builder.url(Constants.VEST_SIGN_URL)
                        .addParam("vestCode", Constants.VEST_CODE)
                        .addParam("channelCode", "google")
                        .addParam("version", AppUtil.getVersionName(LauncherActivity.this))
                        .addParam("deviceId", UUIDS.getUUID())
                        .addParam("timestamp", String.valueOf(new Date().getTime()))
                        .build();

                request.get(new HttpRequestCallback() {
                    @Override
                    public void onRequestFinish(HttpResponse response) {

                        if (response.isSuccess()) {

                            Intent intent = VestActivity.newIntent(LauncherActivity.this, response.getData());
                            startActivity(intent);
                        } else {

                            Intent intent = ErrorActivity.newIntent(LauncherActivity.this);
                            startActivity(intent);
                        }

                        finish();
                    }
                });
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
        return LauncherFragment.newInstance();
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
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APP_SETTING_REQUEST_CODE) {

            checkPermissions(permissions);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

//            this.finish();
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

            handler.sendEmptyMessageDelayed(MSG_CODE, 3000);
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
}
