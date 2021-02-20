package com.unitedbustech.eld.welcome;

import com.unitedbustech.eld.mvp.BasePresenter;
import com.unitedbustech.eld.mvp.BaseView;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description WelcomeContract
 */
public interface WelcomeContract {

    interface View extends BaseView<Presenter> {

        void showLoading();

        void hideLoading();

        void loginSuccess();

        void loginFailed(int code, String msg);

        void accountOnline(int code, String msg);

        void setUserInfo(String carrierName, String driverName);

        void loadSuccess();

        void loadFailed(int code, String msg);
    }

    interface Presenter extends BasePresenter {

        void login(boolean isForce);

        void loadData();

        void getUserInfo();
    }
}
