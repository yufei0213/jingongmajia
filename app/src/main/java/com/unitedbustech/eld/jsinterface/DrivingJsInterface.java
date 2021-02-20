package com.unitedbustech.eld.jsinterface;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.activity.BaseActivity;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.VehicleDataItem;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.common.VehicleState;
import com.unitedbustech.eld.datacollector.DataCollectorHandler;
import com.unitedbustech.eld.datacollector.DataCollectorSubscriber;
import com.unitedbustech.eld.datacollector.common.CollectorType;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.eventcenter.core.StateAdditionInfo;
import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.hos.core.HosHandler;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.hos.model.HosDataModel;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.view.DrivingCountDownDialog;
import com.unitedbustech.eld.view.TabMenu;
import com.unitedbustech.eld.view.UIWebView;
import com.unitedbustech.eld.web.BaseJsInterface;
import com.unitedbustech.eld.web.JsInterface;

import java.util.Date;

/**
 * @author yufei0213
 * @date 2018/1/29
 * @description DrivingJsInterface
 */
@JsInterface(name = "driving")
public class DrivingJsInterface extends BaseJsInterface implements DataCollectorSubscriber {

    private static final String TAG = "DrivingJsInterface";

    private final String SWITCH_ODND_MANUAL_REMARK = "driver manual switch to ODND after 5 min in stationary";
    private final String SWITCH_ODND_AUTO_REMARK = "auto switch to ODND after 5 min in stationary";

    private static final int CONFIRM_CODE = 1;
    private static final int REFUSE_CODE = -1;

    private static final String GET_DRIVER_STATUS = "getDriverStatus";
    @Deprecated
    private static final String SHOW_DIALOG = "showDialog";
    private static final String SET_VEHICLE_LISTENER = "setVehicleStatusListener";
    private static final String CANCEL_VEHICLE_LISTENER = "cancelVehicleStatusListener";

    private String dataRefreshCallback;
    private int vehicleState;

    private StateAdditionInfo.Builder stateAdditionInfoBuilder;
    private DrivingCountDownDialog drivingCountDownDialog;

    private Handler handler;
    private Runnable countDownRunnable;
    private static long DURATION = 5 * 60 * 1000L;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (((Activity) context).isFinishing()) {

                return;
            }

            drivingCountDownDialog = new DrivingCountDownDialog.Builder(context)
                    .negativeBtnListener(new DrivingCountDownDialog.OnClickListener() {
                        @Override
                        public void onClick(DrivingCountDownDialog dialog, int which) {

                            if (!((Activity) context).isFinishing()) {

                                dialog.dismiss();
                            }
                            Logger.i(Tags.DRIVING_PAGE, "count down stop.");
                        }
                    })
                    .neutralBtnListener(new DrivingCountDownDialog.OnClickListener() {
                        @Override
                        public void onClick(DrivingCountDownDialog dialog, int which) {

                            if (!((Activity) context).isFinishing()) {

                                dialog.dismiss();
                            }
                            if (stateAdditionInfoBuilder != null) {
                                stateAdditionInfoBuilder.remark(SWITCH_ODND_MANUAL_REMARK);
                            }
                            switchToOdnd();
                            Logger.i(Tags.DRIVING_PAGE, "count down stop, switch to odnd manual.");
                            ((BaseActivity) context).openMainPage(TabMenu.DASHBORAD);
                            Logger.i(Tags.DRIVING_PAGE, "open main page");
                        }
                    })
                    .countDownListener(new DrivingCountDownDialog.OnCountDownListener() {
                        @Override
                        public void onCountDownEnd(DrivingCountDownDialog dialog) {

                            if (!((Activity) context).isFinishing()) {

                                dialog.dismiss();
                            }
                            if (stateAdditionInfoBuilder != null) {
                                stateAdditionInfoBuilder.remark(SWITCH_ODND_AUTO_REMARK);
                            }
                            switchToOdnd();
                            Logger.i(Tags.DRIVING_PAGE, "count down end, switch to odnd auto.");
                            ((BaseActivity) context).openMainPage(TabMenu.DASHBORAD);
                            Logger.i(Tags.DRIVING_PAGE, "open main page");
                        }
                    })
                    .build();
            drivingCountDownDialog.show();
            Logger.i(Tags.DRIVING_PAGE, "show count down dialog.");
        }
    };

    public DrivingJsInterface(Context context, UIWebView uiWebView) {

        super(context, uiWebView);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    @JavascriptInterface
    public void method(String params) {

        JSONObject obj = JsonUtil.parseObject(params);

        String method = JsonUtil.getString(obj, METHOD_KEY);
        String data = JsonUtil.getString(obj, DATA_KEY);

        switch (method) {

            case GET_DRIVER_STATUS:

                getDriverStatus(data);
                break;
            case SHOW_DIALOG:

                showDialog(data);
                break;
            case SET_VEHICLE_LISTENER:

                setVehicleStatusListener(data);
                break;
            case CANCEL_VEHICLE_LISTENER:

                cancelVehicleStatusListener(data);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSchedule(VehicleDataModel model, CollectorType type) {

        final int vehicleStateTemp = model.getVehicleState();

        Logger.i(Tags.VEHICLE, "DrivingJsInterface: current vehicleState [" + vehicleState + "]");
        Logger.i(Tags.VEHICLE, "DrivingJsInterface: new vehicleState [" + model.getVehicleState() + "]");
        if (vehicleStateTemp != 0 && vehicleStateTemp != vehicleState) {

            vehicleState = model.getVehicleState();

            Logger.i(Tags.VEHICLE, "DrivingJsInterface: modify vehicleState to [" + vehicleState + "]");
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    uiWebView.loadUrl("javascript:" + dataRefreshCallback + "('" + vehicleState + "');");
                }
            });

            if (vehicleState == VehicleState.STATIC) {

                VehicleDataModel vehicleDataModel = DataCollectorHandler.getInstance().getDataModel();

                stateAdditionInfoBuilder = new StateAdditionInfo.Builder();
                stateAdditionInfoBuilder.date(new Date())
                        .odometer(vehicleDataModel.getTotalOdometer())
                        .engineHour(vehicleDataModel.getTotalEngineHours())
                        .accumulatedOdometer(vehicleDataModel.getAccumulatedOdometer())
                        .accumulatedEngineHours(vehicleDataModel.getAccumulatedEngineHours())
                        .origin(DDLOriginEnum.EDIT_BY_DRIVER);

                Location location = LocationHandler.getInstance().getCurrentLocation();
                if (location != null) {

                    stateAdditionInfoBuilder.location("",
                            Double.toString(location.getLatitude()),
                            Double.toString(location.getLongitude()));
                }

                if (ModelCenter.getInstance().getCurrentDriverState() == DriverState.DRIVING && countDownRunnable == null) {

                    Logger.i(Tags.DRIVING_PAGE, "vehicle stopped, start 5 mins count down.");
                    countDownRunnable = runnable;
                    handler.postDelayed(countDownRunnable, DURATION);
                }
            } else if (vehicleState == VehicleState.MOVING) {

                if (countDownRunnable != null) {

                    Logger.i(Tags.DRIVING_PAGE, "vehicle moving, stop 5 mins count down.");
                    handler.removeCallbacks(countDownRunnable);
                    countDownRunnable = null;
                }
                stateAdditionInfoBuilder = null;
                if (drivingCountDownDialog != null) {

                    drivingCountDownDialog.dismiss();
                    drivingCountDownDialog = null;
                }
            }
        }

        if (vehicleStateTemp == 0) {

            Logger.i(Tags.VEHICLE, "DrivingJsInterface: modify vehicleState to [" + vehicleState + "]");
            vehicleState = model.getVehicleState();

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    uiWebView.loadUrl("javascript:" + dataRefreshCallback + "('" + vehicleState + "');");
                }
            });
        }
    }

    @Override
    public void onDataItemChange(VehicleDataItem item, VehicleDataModel model, CollectorType type) {

//        int vehicleStateTemp = model.getVehicleState();
//
//        if (item == VehicleDataItem.VEHICLE_STATE && vehicleStateTemp != 0 && model.getVehicleState() != vehicleState) {
//
//            vehicleState = model.getVehicleState();
//
//            ((Activity) context).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                    uiWebView.loadUrl("javascript:" + dataRefreshCallback + "('" + vehicleState + "');");
//                }
//            });
//
//            if (vehicleState == VehicleState.STATIC) {
//
//                VehicleDataModel vehicleDataModel = DataCollectorHandler.getInstance().getDataModel();
//
//                stateAdditionInfoBuilder = new StateAdditionInfo.Builder();
//                stateAdditionInfoBuilder.date(new Date())
//                        .odometer(vehicleDataModel.getTotalOdometer())
//                        .engineHour(vehicleDataModel.getTotalEngineHours())
//                        .accumulatedOdometer(vehicleDataModel.getAccumulatedOdometer())
//                        .accumulatedEngineHours(vehicleDataModel.getAccumulatedEngineHours())
//                        .origin(DDLOriginEnum.EDIT_BY_DRIVER);
//
//                Location location = LocationHandler.getInstance().getCurrentLocation();
//                if (location != null) {
//
//                    stateAdditionInfoBuilder.location("",
//                            Double.toString(location.getLatitude()),
//                            Double.toString(location.getLongitude()));
//                }
//            } else if (vehicleState == VehicleState.MOVING) {
//
//                stateAdditionInfoBuilder = null;
//                if (drivingCountDownDialog != null) {
//
//                    drivingCountDownDialog.dismiss();
//                    drivingCountDownDialog = null;
//                }
//            }
//        }
    }

    /**
     * 获取主页数据
     *
     * @param data 回调函数
     */
    private void getDriverStatus(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                HosDataModel hosDataModel = HosHandler.getInstance().getCalculator().calculateHosData();
                final String dataStr = JsonUtil.toJsJSONString(hosDataModel);

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + dataStr + "');");
                    }
                });
            }
        });
    }

    /**
     * 显示提示窗
     *
     * @param data 回调函数
     */
    @Deprecated
    private void showDialog(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        if (((Activity) context).isFinishing()) {

            return;
        }

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                drivingCountDownDialog = new DrivingCountDownDialog.Builder(context)
                        .negativeBtnListener(new DrivingCountDownDialog.OnClickListener() {
                            @Override
                            public void onClick(DrivingCountDownDialog dialog, int which) {

                                if (!((Activity) context).isFinishing()) {

                                    dialog.dismiss();
                                }

                                uiWebView.loadUrl("javascript:" + callback + "('" + REFUSE_CODE + "');");
                            }
                        })
                        .neutralBtnListener(new DrivingCountDownDialog.OnClickListener() {
                            @Override
                            public void onClick(DrivingCountDownDialog dialog, int which) {

                                if (!((Activity) context).isFinishing()) {

                                    dialog.dismiss();
                                }
                                if (stateAdditionInfoBuilder != null) {
                                    stateAdditionInfoBuilder.remark(SWITCH_ODND_MANUAL_REMARK);
                                }
                                switchToOdnd();
                                uiWebView.loadUrl("javascript:" + callback + "('" + CONFIRM_CODE + "');");
                            }
                        })
                        .countDownListener(new DrivingCountDownDialog.OnCountDownListener() {
                            @Override
                            public void onCountDownEnd(DrivingCountDownDialog dialog) {

                                if (!((Activity) context).isFinishing()) {

                                    dialog.dismiss();
                                }
                                if (stateAdditionInfoBuilder != null) {
                                    stateAdditionInfoBuilder.remark(SWITCH_ODND_AUTO_REMARK);
                                }
                                switchToOdnd();
                                uiWebView.loadUrl("javascript:" + callback + "('" + CONFIRM_CODE + "');");
                            }
                        })
                        .build();

                drivingCountDownDialog.show();
            }
        });
    }

    /**
     * 添加车辆状态监听
     *
     * @param data 回调函数
     */
    private void setVehicleStatusListener(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        this.dataRefreshCallback = callback;

        DataCollectorHandler.getInstance().subscribe(TAG, this);
    }

    /**
     * 解除车辆状态监听
     *
     * @param data 回调函数
     */
    private void cancelVehicleStatusListener(String data) {

        DataCollectorHandler.getInstance().unSubscribe(TAG);
    }

    /**
     * 切换到ODND
     */
    private void switchToOdnd() {

        if (stateAdditionInfoBuilder != null) {

            EventCenter.getInstance().changeDriverState(DriverState.ON_DUTY_NOT_DRIVING, stateAdditionInfoBuilder.build());
        }
    }
}
