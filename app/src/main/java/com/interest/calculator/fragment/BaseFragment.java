package com.interest.calculator.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.interest.calculator.view.ContentView;

/**
 * @author yufei0213
 * @date 2018/1/4
 * @description fragment基类
 */
public abstract class BaseFragment extends Fragment {

    protected ContentView contentView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initVariables();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (contentView == null) {

            contentView = new ContentView(getActivity());
            View view = initViews(inflater, savedInstanceState);
            contentView.setContentView(view);

            initStatusBar();
        } else {

            //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
            ViewGroup parent = (ViewGroup) contentView.getParent();

            if (parent != null) {

                parent.removeView(contentView);
            }
        }

        return contentView;
    }

    /**
     * 初始化状态栏
     */
    protected void initStatusBar() {

        contentView.hideStatusBar();
    }

    /**
     * 设置状态栏样式
     *
     * @param colorId      状态栏颜色
     * @param isFullScreen 页面内容是否延伸到状态栏
     */
    protected void setStatusBar(@ColorRes int colorId, boolean isFullScreen) {

        contentView.setStatusBarColor(getActivity(), colorId, isFullScreen);
    }

    /**
     * 初始化变量
     */
    protected abstract void initVariables();

    /**
     * 初始化视图
     *
     * @param inflater           LayoutInflater
     * @param savedInstanceState Bundle
     * @return View
     */
    protected abstract View initViews(LayoutInflater inflater, Bundle savedInstanceState);
}
