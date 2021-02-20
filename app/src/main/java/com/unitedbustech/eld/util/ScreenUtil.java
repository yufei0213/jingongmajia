package com.unitedbustech.eld.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewConfiguration;

import java.lang.reflect.Method;

/**
 * @author yufei0213
 * @date 2017/10/20
 * @description 屏幕相关工具类
 */
public final class ScreenUtil {

    private static final String TAG = "ScreenUtil";

    /**
     * dp转px
     *
     * @param context 上下文
     * @param dp      dp值
     * @return px值
     */
    public static int dp2px(Context context, float dp) {

        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param context 上下文
     * @param sp      sp值
     * @return px值
     */
    public static int sp2px(Context context, float sp) {

        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }

    /**
     * px转dp
     *
     * @param context 上下文
     * @param px      px值
     * @return dp值
     */
    public static int px2dp(Context context, float px) {

        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * px转sp
     *
     * @param context 上下文
     * @param px      px值
     * @return sp值
     */
    public static int px2sp(Context context, float px) {

        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / fontScale + 0.5f);
    }

    /**
     * 获取状态栏高度，单位：像素
     *
     * @param context 上下文
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {

        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {

            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    /**
     * 获取内容宽度，单位：像素
     *
     * @param context 上下文
     * @return 屏幕宽度
     */
    public static int getContentWidth(Context context) {

        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取内容高度，单位：像素
     *
     * @param context 上下文
     * @return 屏幕高度
     */
    public static int getContentHeight(Context context) {

        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕高度，不包含虚拟按键，单位：像素
     *
     * @param context 上下文
     * @return 屏幕高度
     */
    public static int getScreenContentHeight(Context context) {

        Activity activity = (Activity) context;

        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        return height;
    }

    /**
     * 获取屏幕高度，包含虚拟按键，单位：像素
     *
     * @param context 上下文
     * @return 屏幕高度
     */
    public static int getScreenHeight(Context context) {

        Activity activity = (Activity) context;

        int height = 0;
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();

        @SuppressWarnings("rawtypes")
        Class c;

        try {

            c = Class.forName("android.view.Display");

            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);

            height = dm.heightPixels;
        } catch (Exception e) {

            e.printStackTrace();
        }

        return height;
    }

    /**
     * 获取底部导航栏高度
     *
     * @param context 上下文
     * @return 导航栏高度 单位：像素
     */
    public static int getNavigationBarHeight(Context context) {

        int result = 0;
        if (hasNavBar(context)) {

            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {

                result = res.getDimensionPixelSize(resourceId);
            }
        }

        return result;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {

        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {

            boolean hasNav = res.getBoolean(resourceId);

            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {

                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {

                hasNav = true;
            }

            return hasNav;
        } else {

            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    private static String getNavBarOverride() {

        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            try {

                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {

                e.printStackTrace();
            }
        }

        return sNavBarOverride;
    }
}
