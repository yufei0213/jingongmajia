package com.unitedbustech.eld.ifta.update;

import com.unitedbustech.eld.common.vo.IftaDetailVo;
import com.unitedbustech.eld.mvp.BasePresenter;
import com.unitedbustech.eld.mvp.BaseView;

import java.util.List;
import java.util.Map;

/**
 * @author yufei0213
 * @date 2018/6/25
 * @description IFTA presenter和view的接口协议
 */
public interface IftaUpdateContract {

    interface View extends BaseView<Presenter> {

        void updateFuelSuccess();

        void updateFuelFailed(int code, String msg);

        void deleteFuelSuccess();

        void deleteFuelFailed(int code, String msg);
    }

    interface Presenter extends BasePresenter {

        void updateFuel(Map<String, String> params, List<String> receiptPics, IftaDetailVo iftaDetailVo);

        void deleteFuel(IftaDetailVo iftaDetailVo);
    }
}
