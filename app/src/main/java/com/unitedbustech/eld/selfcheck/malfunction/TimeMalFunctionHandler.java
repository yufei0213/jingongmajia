package com.unitedbustech.eld.selfcheck.malfunction;

import android.support.annotation.WorkerThread;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.DiagnosticType;
import com.unitedbustech.eld.common.MalfunctionType;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.eventbus.GetAlertsEvent;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.selfcheck.BaseHandler;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

/**
 * @author yufei0213
 * @date 2018/2/11
 * @description 时间故障处理类
 */
public class TimeMalFunctionHandler extends BaseHandler {

    private static final String TAG = "TimeMalFunctionHandler";

    /**
     * 生成时间故障的时间差阈值
     */
    private static final long TIME_MALFUNCTION_DURATION = 10 * 60 * 1000l;

    public TimeMalFunctionHandler() {
    }

    public void destroy() {

        if (getMalFunctionRequest != null) {

            getMalFunctionRequest.cancel();
        }

        hasInit = false;
        isProduce = false;
    }

    /**
     * 检查时间是否有异常
     */
    @WorkerThread
    public void checkTime() {

        User user = SystemHelper.getUser();

        HttpRequest httpRequest = new HttpRequest.Builder()
                .url(Constants.API_GET_SERVICE_TIME)
                .addParam("access_token", user.getAccessToken())
                .build();

        HttpResponse httpResponse = httpRequest.get();
        if (httpResponse.isSuccess()) {

            JSONObject timeObj = JsonUtil.parseObject(httpResponse.getData());
            long serverTime = JsonUtil.getLong(timeObj, "currentTimeMillis");
            long localTime = new Date().getTime();

            //如果本地时间与服务器时间相差大于十分钟，则产生时间异常
            if (Math.abs(serverTime - localTime) > TIME_MALFUNCTION_DURATION && this.execute(true)) {

                EventBus.getDefault().post(new GetAlertsEvent());
            }
        }
    }

    private boolean execute(boolean isProduce) {

        if (hasInit && this.isProduce == isProduce) {

            return false;
        } else {

            hasInit = true;
            this.isProduce = isProduce;
        }

        User user = SystemHelper.getUser();

        getMalFunctionRequest = new HttpRequest.Builder()
                .url(Constants.API_GET_MALFUNCTION_LIST)
                .addParam("access_token", user.getAccessToken())
                .addParam("vehicle_id", Integer.toString(user.getVehicleId()))
                .addParam("code", MalfunctionType.TIMING_COMPLIANCE.getCode())
                .build();

        Logger.d(TAG, "请求时间故障列表参数：access_token=" + user.getAccessToken() +
                ", vehicle_id=" + user.getVehicleId() +
                ", code=" + DiagnosticType.ENGINE_SYNCHRONIZATION.getCode());

        HttpResponse httpResponse = getMalFunctionRequest.get();

        if (httpResponse.isSuccess()) {

            JSONObject dataObj = JsonUtil.parseObject(httpResponse.getData());
            JSONArray malfunctionArray = JsonUtil.getJsonArray(dataObj, "malfunctions");

            Logger.d(TAG, "请求时间故障列表成功，data=" + httpResponse.getData());

            if (malfunctionArray.size() > 0 && !this.isProduce) {

                Logger.d(TAG, "当前有时间故障，需要清除，上报Api");
                EventCenter.getInstance().malfunctionEvent(MalfunctionType.TIMING_COMPLIANCE, true);
            }

            if (malfunctionArray.size() == 0 && this.isProduce) {

                Logger.d(TAG, "当前没有时间故障，需要生成新的故障诊断，上报Api");
                EventCenter.getInstance().malfunctionEvent(MalfunctionType.TIMING_COMPLIANCE, false);
            }
        } else {

            Logger.d(TAG, "请求当前是否有时间故障失败，code=" + httpResponse.getCode() + ", msg=" + httpResponse.getMsg());
        }

        return true;
    }
}
