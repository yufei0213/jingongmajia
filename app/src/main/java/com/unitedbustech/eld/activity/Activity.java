package com.unitedbustech.eld.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.system.AppStatusManager;
import com.unitedbustech.eld.util.AppUtil;
import com.unitedbustech.eld.util.LanguageUtil;

/**
 * @author yufei0213
 * @date 2018/2/22
 * @description 基础Activity
 */
public class Activity extends AppCompatActivity {

    /**
     * 打开新页面时，新页面的动画
     */
    public static final String EXTRA_ANIM_IN_IN_ID = "anim_in_in_id";
    /**
     * 打开新页面时，旧页面的动画
     */
    public static final String EXTRA_ANIM_IN_OUT_ID = "anim_in_out_id";
    /**
     * 关闭页面时，新页面的动画
     */
    public static final String EXTRA_ANIM_OUT_IN_ID = "anim_out_in_id";
    /**
     * 关闭页面时，旧页面的动画
     */
    public static final String EXTRA_ANIM_OUT_OUT_ID = "anim_out_out_id";

    /**
     * 打开新页面时，新页面的动画资源id
     */
    private int animInInId;
    /**
     * 打开新页面时，旧页面的动画资源id
     */
    private int animInOutId;
    /**
     * 关闭页面时，新页面的动画资源id
     */
    private int animOutInId;
    /**
     * 关闭页面时，旧页面的动画资源id
     */
    private int animOutOutId;

    /**
     * 权限请求码
     */
    private int PERMISSION_REQUEST_CODE;

    /**
     * 请求权限失败时，给用户的提示信息
     */
    private String PERMISSION_MSG;

    /**
     * 当前Activity是否能打开
     */
    protected boolean isOpen;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageUtil.getInstance().setConfiguration(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActivityStack.getInstance().addActivity(this);

        switch (AppStatusManager.getInstance().getAppStatus()) {

            case AppStatusManager.FORCE_KILLED:

                protectApp();

                isOpen = false;
                break;
            case AppStatusManager.NORMAL:

                //初始化动画资源
                initAnimate();
                //加载进入动画
                animateIn();

                isOpen = true;
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {

        super.onStart();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {

        super.onPause();

        //离开动画
        animateOut();
    }

    @Override
    protected void onStop() {

        super.onStop();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        ActivityStack.getInstance().removeActivity(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                onPermissionDenied(PERMISSION_MSG, PERMISSION_REQUEST_CODE); //获取权限失败的回调
            } else {

                onPermissionGranted(PERMISSION_REQUEST_CODE); //获取到权限的回调
            }
        }
    }

    /**
     * 检查权限
     *
     * @param permission  请求的权限
     * @param msg         请求权限失败时，给用户的提示信息
     * @param requestCode 请求码
     */
    protected void checkPermission(String permission, String msg, int requestCode) {

        PERMISSION_MSG = msg;
        PERMISSION_REQUEST_CODE = requestCode;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            if (!AppUtil.checkPermission(this, permission)) {

                onPermissionDenied(PERMISSION_MSG, PERMISSION_REQUEST_CODE); //获取权限失败的回调
            } else {

                onPermissionGranted(PERMISSION_REQUEST_CODE); //获取到权限的回调
            }
        } else {

            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                    onPermissionDenied(PERMISSION_MSG, PERMISSION_REQUEST_CODE); //获取权限失败的回调
                } else {

                    ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE); //请求权限
                }
            } else {

                onPermissionGranted(PERMISSION_REQUEST_CODE); //获取到权限的回调
            }
        }
    }

    /**
     * 获取到权限的回调
     *
     * @param requestCode 请求码
     */
    protected void onPermissionGranted(int requestCode) {
    }

    /**
     * 获取权限失败的回调
     *
     * @param msg         请求权限失败时，给用户的提示信息
     * @param requestCode 请求码
     */
    protected void onPermissionDenied(String msg, int requestCode) {
    }

    /**
     * 初始化动画资源id
     */
    private void initAnimate() {

        animInInId = getIntent().getIntExtra(EXTRA_ANIM_IN_IN_ID, R.anim.slide_right_in);
        animInOutId = getIntent().getIntExtra(EXTRA_ANIM_IN_OUT_ID, R.anim.slide_left_out);
        animOutInId = getIntent().getIntExtra(EXTRA_ANIM_OUT_IN_ID, R.anim.slide_left_in);
        animOutOutId = getIntent().getIntExtra(EXTRA_ANIM_OUT_OUT_ID, R.anim.slide_right_out);
    }

    /**
     * 页面进入动画
     */
    protected void animateIn() {

        this.overridePendingTransition(animInInId, animInOutId);
    }

    /**
     * 页面离开动画
     */
    protected void animateOut() {

        this.overridePendingTransition(animOutInId, animOutOutId);
    }

    /**
     * App被强制杀死时调用此方法
     */
    protected void protectApp() {

        Intent intent = MainActivity.newIntent(Activity.this, MainActivity.ACTION_RESTART);
        startActivity(intent);
    }
}
