package com.unitedbustech.eld.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author yufei0213
 * @date 2017/10/20
 * @description app工具类
 */
public final class AppUtil {

    private static final String TAG = "AppUtil";

    /**
     * 获取应用名
     *
     * @param context 上下文
     * @return 应用名
     */
    public static String getAppName(Context context) {

        PackageManager packageManager = context.getPackageManager();
        try {

            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;

            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取应用版本名
     *
     * @param context 上下文
     * @return 应用版本名
     */
    public static String getVersionName(Context context) {

        String versionName = null;

        try {

            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }

        return versionName;
    }

    /**
     * 获取应用版本号
     *
     * @param context 上下文
     * @return 应用版本号
     */
    public static int getVersionCode(Context context) {

        int versionCode = 0;

        try {

            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }

        return versionCode;
    }

    /**
     * 获取当前应用程序的包名
     * @param context 上下文对象
     * @return 返回包名
     */
    public static String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid) {

                return info.processName;//返回包名
            } else {

                continue;
            }
        }
        return "";
    }

    /**
     * 检查是否以获取指定权限
     *
     * @param context    上下文
     * @param permission 权限
     * @return 是否以获取权限
     */
    public static boolean checkPermission(Context context, String permission) {

        boolean result = false;

        if (Build.VERSION.SDK_INT >= 23) {

            try {

                Class clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);

                result = rest == PackageManager.PERMISSION_GRANTED ? true : false;

            } catch (Exception e) {

                result = false;
            }
        } else {

            PackageManager pm = context.getPackageManager();

            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {

                result = true;
            }
        }

        return result;
    }

    /**
     * 获取应用Setting页面的Intent
     *
     * @param context 上下文
     * @return Intent
     */
    public static Intent getAppSettingIntent(Context context) {

        Uri packageURI = Uri.parse("package:" + context.getPackageName());
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);

        return intent;
    }

    /**
     * 获取系统定位服务页面的Intent
     *
     * @param context 上下文
     * @return Intent
     */
    public static Intent getLocationSettingIntent(Context context) {

        return new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    }

    /**
     * 隐藏软键盘
     *
     * @param context 上下文
     * @param token   View.getWindowToken()
     */
    public static void hideSoftInput(Context context, IBinder token) {

        if (token != null) {

            InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 读取assets
     *
     * @param context  上下文
     * @param filePath 文件路径，相对于assets目录
     * @return
     */
    public static String readAssetsFile(Context context, String filePath) {

        StringBuilder stringBuilder = new StringBuilder();
        try {

            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(filePath)));
            String line;
            while ((line = bf.readLine()) != null) {

                stringBuilder.append(line);
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    /**
     * 获取可用外部存储大小
     * @return
     */
    public static long getSDCardSize() {

        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)) {

            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSizeLong();
            long availCount = sf.getAvailableBlocksLong();
            return availCount * blockSize / 1024;//单位是KB
        } else {

            return 0;
        }
    }
}
