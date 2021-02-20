package com.unitedbustech.eld.login;

import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.mvp.BasePresenter;
import com.unitedbustech.eld.mvp.BaseView;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description view presenter 联调
 */
public interface LoginContract {

    interface View extends BaseView<Presenter> {

        void initInputValue(User user);

        void showLoading();

        void hideLoading();

        void loginSuccess();

        void accountOnline(int code, String msg);

        void accountInvalid(int code, String msg);

        void loadSuccess();

        void loadFailed(int code, String msg);
    }

    interface Presenter extends BasePresenter {

        void loadUser();

        void login(String carriedId, String driverId, String password, boolean isForce);

        void loadData();

        void cancelLogin();
    }
}
