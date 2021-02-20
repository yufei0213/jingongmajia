package com.unitedbustech.eld.mvp;

/**
 * @author yufei0213
 * @date 2018/1/5
 * @description BaseView
 */
public interface BaseView<T> {

    /**
     * Presenter赋值给View
     *
     * @param presenter Presenter
     */
    void setPresenter(T presenter);
}
