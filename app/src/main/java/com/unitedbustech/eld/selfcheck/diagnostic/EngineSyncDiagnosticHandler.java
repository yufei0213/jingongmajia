package com.unitedbustech.eld.selfcheck.diagnostic;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.WorkerThread;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.App;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.DiagnosticType;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.SelfCheckEventType;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.datacollector.DataCollectorHandler;
import com.unitedbustech.eld.datacollector.device.ConfigOption;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Vehicle;
import com.unitedbustech.eld.eventbus.DashBoardMalFunctionEvent;
import com.unitedbustech.eld.eventbus.GetAlertsEvent;
import com.unitedbustech.eld.eventbus.SelfCheckEvent;
import com.unitedbustech.eld.eventbus.VehicleSelectEvent;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.selfcheck.BaseHandler;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author yufei0213
 * @date 2018/2/11
 * @description EngineSyncDiagnosticHandler
 */
public class EngineSyncDiagnosticHandler extends BaseHandler {

    private static final String TAG = "EngineSyncDiagnosticHandler";

    /**
     * ECM连接出现异常后，尝试再次连接的时间间隔
     */
    private static final long ECM_LINK_DURATION = 30 * 1000;

    /**
     * ECM连接连续失败次数达到此阈值后生成故障
     * <p>
     * 30秒连接一次，20次大概十分钟
     */
    private static final int ECM_LINK_MALFUNCTION_COUNT = 20;

    /**
     * ECM数据读取异常达到此阈值后生成故障
     * <p>
     * 2秒读取一次数据，450次大概15分钟
     */
    private static final int ECM_DATA_MALFUNCTION_COUNT = 450;

    private Handler handler;

    /**
     * 连接Ecm的任务
     */
    private Runnable ecmLinkRunnable;

    /**
     * ecm连接失败次数累计
     */
    private int ecmLinkFailedCount;

    /**
     * ecm数据读取异常
     */
    private int ecmDataFailedCount;

    public EngineSyncDiagnosticHandler() {

        handler = new Handler(Looper.getMainLooper());

        if (EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().unregister(this);
        }

        EventBus.getDefault().register(this);
    }

    public void destroy() {

        EventBus.getDefault().unregister(this);

        ecmLinkFailedCount = 0;
        if (handler != null) {

            handler.removeCallbacks(ecmLinkRunnable);
        }

        ecmLinkRunnable = null;
        handler = null;

        if (getMalFunctionRequest != null) {
            getMalFunctionRequest.cancel();
        }
        hasInit = false;
        isProduce = false;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onVehicleSelectEvent(VehicleSelectEvent vehicleSelectEvent) {

        if (!SystemHelper.hasVehicle()) {

            if (handler != null) {

                handler.removeCallbacks(ecmLinkRunnable);
            }

            ecmLinkFailedCount = 0;
            ecmDataFailedCount = 0;

            EventBus.getDefault().post(new DashBoardMalFunctionEvent(false));

            SystemHelper.setMalFunction(true);
            SystemHelper.setDiagnostic(true);
        }
    }

    /**
     * 处理ECM连接类的异常
     *
     * @param event 事件
     */
    @WorkerThread
    public void handleEcmLink(SelfCheckEvent event) {

        if (event.isEnable()) {

            Logger.i(Tags.ECM, "ecm-link enable");
            isNeedCancelEcmLinkDiagnostic(event);
        } else {

            Logger.w(Tags.ECM, "ecm-link not enable");
            isProduceEcmLinkDiagnostic(event);

            if (ecmLinkRunnable == null) {

                ecmLinkRunnable = new EngineSyncDiagnosticHandler.EcmLinkRunnable();
            }

            handler.postDelayed(ecmLinkRunnable, ECM_LINK_DURATION);
        }
    }

    /**
     * 处理ecm数据读取的异常
     *
     * @param event 事件
     */
    @WorkerThread
    public void handleEngineSync(SelfCheckEvent event) {

        if (event.isEnable()) {

            isNeedCancelEcmDataDiagnostic();
        } else {

            isProduceEcmDataDiagnostic();
        }
    }

    /**
     * 检查是否需要消除ECM连接故障
     */
    private void isNeedCancelEcmLinkDiagnostic(SelfCheckEvent event) {

        ecmLinkFailedCount = 0;
        if (event.isCreateGpsRemark()) {

            this.executeWithRemark(false);
        } else {

            this.execute(false);
        }
    }

    /**
     * 检查是否生成ECM连接故障
     */
    private void isProduceEcmLinkDiagnostic(SelfCheckEvent event) {

        if (event.isCreateGpsRemark()) {

            ecmDataFailedCount = ECM_LINK_MALFUNCTION_COUNT;
            if (this.executeWithRemark(true)) {

                EventBus.getDefault().post(new GetAlertsEvent());
            }
        } else {

            ecmLinkFailedCount++;
            if ((ecmLinkFailedCount - ECM_LINK_MALFUNCTION_COUNT) > 0) {

                return;
            }
            if (ecmLinkFailedCount == ECM_LINK_MALFUNCTION_COUNT && this.execute(true)) {

                EventBus.getDefault().post(new GetAlertsEvent());
            }
        }
    }

    /**
     * 是否需要消除ECM数据读取异常
     */
    private void isNeedCancelEcmDataDiagnostic() {

        ecmDataFailedCount = 0;
        if (this.execute(false)) {

            EventBus.getDefault().post(new GetAlertsEvent());
        }
    }

    /**
     * 检查是否生成ECM数据读取异常
     */
    private void isProduceEcmDataDiagnostic() {

        ecmDataFailedCount++;
        if ((ecmDataFailedCount - ECM_DATA_MALFUNCTION_COUNT) > 0) {

            return;
        }
        if (ecmDataFailedCount == ECM_DATA_MALFUNCTION_COUNT && this.execute(true)) {

            EventBus.getDefault().post(new GetAlertsEvent());
        }
    }

    private boolean executeWithRemark(boolean isProduce) {

        if (ModelCenter.getInstance().getCurrentDriverState() == DriverState.OFF_DUTY && isProduce) {

            Logger.d(TAG, "OFF_DUTY don't produce diagnostic");
            return true;
        } else {

            return handle(isProduce, true);
        }
    }

    private boolean execute(boolean isProduce) {

        if (ModelCenter.getInstance().getCurrentDriverState() == DriverState.OFF_DUTY && isProduce) {

            Logger.d(TAG, "OFF_DUTY don't produce diagnostic");
            return false;
        } else {

            return handle(isProduce, false);
        }
    }

    private boolean handle(boolean isProduce, boolean needRemark) {

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
                .addParam("code", DiagnosticType.ENGINE_SYNCHRONIZATION.getCode())
                .build();

        Logger.d(TAG, "请求引擎数据同步故障列表参数：access_token=" + user.getAccessToken() +
                ", vehicle_id=" + user.getVehicleId() +
                ", code=" + DiagnosticType.ENGINE_SYNCHRONIZATION.getCode());

        HttpResponse httpResponse = getMalFunctionRequest.get();

        if (httpResponse.isSuccess()) {

            JSONObject dataObj = JsonUtil.parseObject(httpResponse.getData());
            JSONArray malfunctionArray = JsonUtil.getJsonArray(dataObj, "malfunctions");

            Logger.d(TAG, "请求引擎数据同步故障列表成功，data=" + httpResponse.getData());

            if (malfunctionArray.size() > 0 && !this.isProduce) {

                Logger.d(TAG, "当前有引擎数据同步故障，需要清除，上报Api");
                EventCenter.getInstance().diagnosticEvent(DiagnosticType.ENGINE_SYNCHRONIZATION, true, needRemark ? App.getContext().getString(R.string.diag_page_log_end) : null);
            }

            if (malfunctionArray.size() == 0 && this.isProduce) {

                Logger.d(TAG, "当前没有引擎数据同步故障，需要生成新的故障诊断，上报Api");
                EventCenter.getInstance().diagnosticEvent(DiagnosticType.ENGINE_SYNCHRONIZATION, false, needRemark ? App.getContext().getString(R.string.diag_page_log_start) : null);
            }
        } else {

            Logger.d(TAG, "请求当前是否有引擎同步异常失败，code=" + httpResponse.getCode() + ", msg=" + httpResponse.getMsg());
        }

        return true;
    }

    private class EcmLinkRunnable implements Runnable {

        @Override
        public void run() {

            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    User user = SystemHelper.getUser();
                    final Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(user.getVehicleId());

                    if (vehicle != null) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                Logger.i(Tags.ECM, "try to connect ecm");

                                ConfigOption option = new ConfigOption.Builder()
                                        .vehicleNumber(vehicle.getCode())
                                        .bluetoothAddress(vehicle.getEcmSn())
                                        .deviceType(ConfigOption.getDeviceTypeByEcmLinkType(vehicle.getEcmLinkType()))
                                        .build();

                                boolean result = DataCollectorHandler.getInstance().startDeviceModel(option);
                                if (!result) {

                                    EventBus.getDefault().post(new SelfCheckEvent(SelfCheckEventType.ECM_LINK, false));
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}
