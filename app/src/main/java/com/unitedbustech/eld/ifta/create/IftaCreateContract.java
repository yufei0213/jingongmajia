package com.unitedbustech.eld.ifta.create;

import com.unitedbustech.eld.mvp.BasePresenter;
import com.unitedbustech.eld.mvp.BaseView;

import java.util.List;
import java.util.Map;

/**
 * @author yufei0213
 * @date 2018/6/25
 * @description IFTA presenter和view的接口协议
 */
public interface IftaCreateContract {

    interface View extends BaseView<Presenter> {

        void createFuelSuccess();

        void createFuelFailed(int code, String msg);
    }

    interface Presenter extends BasePresenter {

        void createFuel(Map<String, String> params, List<String> receiptPics);
    }
}
