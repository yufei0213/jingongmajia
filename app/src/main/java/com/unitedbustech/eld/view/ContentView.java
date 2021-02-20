package com.unitedbustech.eld.view;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.util.ScreenUtil;

/**
 * @author yufei0213
 * @date 2017/10/20
 * @description 视图容器
 */
public class ContentView extends LinearLayout {

    private View statusBar;
    private View statusBarTop;

    private FrameLayout container;

    public ContentView(Activity activity) {

        this(activity, null);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {

            return;
        }
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setStatusBarColor(activity, R.color.colorPrimary, false);
    }

    public ContentView(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_content, this, true);

        statusBar = this.findViewById(R.id.status_bar);
        statusBarTop = this.findViewById(R.id.status_bar_top);
        container = this.findViewById(R.id.container);
    }

    /**
     * 设置视图
     *
     * @param view 视图
     */
    public void setContentView(@NonNull View view) {

        container.addView(view);
    }

    /**
     * 隐藏状态栏
     */
    public void hideStatusBar() {

        LayoutParams statusBarParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        FrameLayout.LayoutParams statusBarTopParams = new FrameLayout
                .LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0);

        statusBar.setLayoutParams(statusBarParams);
        statusBarTop.setLayoutParams(statusBarTopParams);
    }

    /**
     * 设置状态栏样式
     *
     * @param context      上下文
     * @param colorId      颜色id
     * @param isFullScreen 是否全屏
     */
    public void setStatusBarColor(Context context, @ColorRes int colorId, boolean isFullScreen) {

        int height = ScreenUtil.getStatusBarHeight(context);

        if (isFullScreen) {

            LayoutParams statusBarParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
            FrameLayout.LayoutParams statusBarTopParams = new FrameLayout
                    .LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height);

            statusBar.setLayoutParams(statusBarParams);

            statusBarTop.setLayoutParams(statusBarTopParams);
            statusBarTop.setBackgroundColor(getResources().getColor(colorId));
        } else {

            LayoutParams statusBarParams = new LayoutParams(LayoutParams.MATCH_PARENT, height);
            FrameLayout.LayoutParams statusBarTopParams = new FrameLayout
                    .LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0);

            statusBarTop.setLayoutParams(statusBarTopParams);

            statusBar.setLayoutParams(statusBarParams);
            statusBar.setBackgroundColor(getResources().getColor(colorId));
        }
    }
}
