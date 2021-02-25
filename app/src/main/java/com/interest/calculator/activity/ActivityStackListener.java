package com.interest.calculator.activity;

/**
 * @author yufei0213
 * @date 2018/1/5
 * @description ActivityStack监听
 */
public interface ActivityStackListener {

    void onAppCreate();

    void onAppDestroy();

    void onServiceStart();

    void onServiceStop();
}
