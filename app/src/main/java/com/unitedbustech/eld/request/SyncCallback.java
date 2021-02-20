package com.unitedbustech.eld.request;

/**
 * @author yufei0213
 * @date 2018/8/15
 * @description 数据同步回调函数
 */
public interface SyncCallback {

    void onSuccess();

    void onFailed();
}
