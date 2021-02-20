package com.unitedbustech.eld.view;

import android.view.View;

/**
 * @author yufei0213
 * @date 2018/2/26
 * @description 点击事件代理方法，防止过快点击
 */
public class ClickProxy implements View.OnClickListener {

    private static final long CLICK_DURATION_LIMIT = 1000;

    private View.OnClickListener origin;

    private long lastClickTime = 0;

    public ClickProxy(View.OnClickListener origin) {

        this.origin = origin;
    }

    @Override
    public void onClick(View v) {

        if (System.currentTimeMillis() - lastClickTime >= CLICK_DURATION_LIMIT) {

            origin.onClick(v);
            lastClickTime = System.currentTimeMillis();
        }
    }
}
