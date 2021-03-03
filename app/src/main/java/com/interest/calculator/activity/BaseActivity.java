package com.interest.calculator.activity;

import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import android.view.View;

import com.interest.calculator.view.ContentView;

/**
 * @author yufei0213
 * @date 2017/10/20
 * @description activity基类
 */
public abstract class BaseActivity extends Activity {

    /**
     * 页面容器
     */
    private ContentView contentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //初始化变量
        initVariables();
        //初始化视图
        initViews(savedInstanceState);
    }

    /**
     * 初始化状态栏
     */
    protected void initStatusBar() {

    }

    /**
     * 设置状态栏样式
     *
     * @param colorId      状态栏颜色
     * @param isFullScreen 页面内容是否延伸到状态栏
     */
    protected void setStatusBar(@ColorRes int colorId, boolean isFullScreen) {

        contentView.setStatusBarColor(this, colorId, isFullScreen);
    }

    protected void setStatusBar(String color, boolean isFullScreen) {

        contentView.setStatusBarColor(this, color, isFullScreen);
    }

    protected void setStatusBarColor(String color) {

        contentView.setAndroidNativeLightStatusBar(this, "white".equals(color));
    }

    /**
     * 初始化变量（不包含视图类变量）
     */
    protected abstract void initVariables();

    /**
     * 创建视图
     *
     * @param savedInstanceState 保存的页面状态
     * @return 视图
     */
    protected abstract View onCreateView(@Nullable Bundle savedInstanceState);

    /**
     * 初始化视图
     *
     * @param savedInstanceState 保存的页面状态
     */
    private void initViews(@Nullable Bundle savedInstanceState) {

        contentView = new ContentView(this);
        View view = onCreateView(savedInstanceState);
        contentView.setContentView(view);

        initStatusBar();

        setContentView(contentView);
    }
}
