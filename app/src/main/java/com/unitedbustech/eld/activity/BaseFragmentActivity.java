package com.unitedbustech.eld.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.mvp.BasePresenter;

/**
 * @author yufei0213
 * @date 2018/1/4
 * @description activity基类
 */
public abstract class BaseFragmentActivity extends Activity {

    private BasePresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (isOpen) {

            //初始化变量
            initVariables();
            //初始化视图
            initViews(savedInstanceState);

            presenter = initPresenter();
        }
    }

    @Override
    protected void onDestroy() {

        if (presenter != null) {

            presenter.onDestroy();
            presenter = null;
        }

        super.onDestroy();
    }

    /**
     * 初始化变量（不包含视图类变量）
     */
    protected abstract void initVariables();

    /**
     * 初始化Presenter
     */
    protected abstract BasePresenter initPresenter();

    /**
     * 创建视图
     *
     * @return 视图
     */
    protected abstract Fragment createFragment();

    /**
     * 初始化视图
     *
     * @param savedInstanceState 保存的页面状态
     */
    private void initViews(@Nullable Bundle savedInstanceState) {

        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {

            if ((fragment = createFragment()) != null) {

                fm.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
            }
        }
    }
}
