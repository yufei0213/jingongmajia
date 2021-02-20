package com.unitedbustech.eld.prepare;

import com.unitedbustech.eld.mvp.BasePresenter;
import com.unitedbustech.eld.mvp.BaseView;

/**
 * @author yufei0213
 * @date 2018/1/28
 * @description PrepareContract
 */
public interface PrepareContract {

    interface View extends BaseView<Presenter> {

        void showLoadingPage();

        void hideLoadingPage();

        void setUserInfo(String carrierName, String driverName);

        void startLoading();

        void endLoading();

        void loadDataFinish(boolean success);
    }

    interface Presenter extends BasePresenter {

        void getUserInfo();

        void loadData();
    }
}
