package com.unitedbustech.eld.jsinterface;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.bluetooth.BluetoothHandler;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.SelfCheckEventType;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.VehicleAutoConnectType;
import com.unitedbustech.eld.common.VehicleConnectType;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.common.vo.MalFunctionsVo;
import com.unitedbustech.eld.common.vo.VehicleVo;
import com.unitedbustech.eld.datacollector.ConnectVehicleCallback;
import com.unitedbustech.eld.datacollector.DataCollectorHandler;
import com.unitedbustech.eld.datacollector.common.VehicleDataRecorder;
import com.unitedbustech.eld.datacollector.device.ConfigOption;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.RecentVehicle;
import com.unitedbustech.eld.domain.entry.Vehicle;
import com.unitedbustech.eld.eventbus.SelfCheckEvent;
import com.unitedbustech.eld.eventbus.VehicleAutoConnectEvent;
import com.unitedbustech.eld.eventbus.VehicleConnectEvent;
import com.unitedbustech.eld.eventbus.VehicleSelectEvent;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.eventcenter.core.StateAdditionInfo;
import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.view.HorizontalDialog;
import com.unitedbustech.eld.view.PromptDialog;
import com.unitedbustech.eld.view.UIWebView;
import com.unitedbustech.eld.view.VehicleCommonDialog;
import com.unitedbustech.eld.view.VerticalDialog;
import com.unitedbustech.eld.web.BaseJsInterface;
import com.unitedbustech.eld.web.JsInterface;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author yufei0213
 * @date 2018/1/20
 * @description VehicleJsInterface
 */
@JsInterface(name = "vehicle")
public class VehicleJsInterface extends BaseJsInterface {

    private static final String TAG = "VehicleJsInterface";

    private static final String GET_VEHICLE_LIST = "getVehicleList";
    private static final String GET_CURRENT_VEHICLE = "getCurrentVehicle";
    private static final String CONNECT_VEHICLE = "connectVehicle";
    private static final String DISCONNECT_VEHICLE = "disconnectVehicle";
    private static final String DISCONNECT_VEHICLE_AUTO = "disconnectVehicleAuto";
    private static final String GET_VEHICLE_MALFUNCTION_LIST = "getVehicleMalfunctionList";
    private static final String CHANGE_ODOMETER = "changeOdometer";

    public VehicleJsInterface(Context context, UIWebView uiWebView) {

        super(context, uiWebView);
    }

    @Override
    @JavascriptInterface
    public void method(String params) {

        JSONObject obj = JsonUtil.parseObject(params);

        String method = JsonUtil.getString(obj, METHOD_KEY);
        String data = JsonUtil.getString(obj, DATA_KEY);

        switch (method) {

            case GET_VEHICLE_LIST:

                getVehicleList(data);
                break;
            case GET_CURRENT_VEHICLE:

                getCurrentVehicle(data);
                break;
            case CONNECT_VEHICLE:

                connectVehicle(data);
                break;
            case DISCONNECT_VEHICLE:

                disconnectVehicle(data);
                break;
            case DISCONNECT_VEHICLE_AUTO:

                disconnectVehicleAuto(data);
                break;
            case GET_VEHICLE_MALFUNCTION_LIST:

                getVehicleMalfunctionList(data);
                break;
            case CHANGE_ODOMETER:

                changeOdometer(data);
                break;
            default:
                break;
        }
    }

    /**
     * 获取车辆列表
     *
     * @param data 回调函数
     */
    private void getVehicleList(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                int carrierId = SystemHelper.getUser().getCarriedId();
                List<Integer> vehicleIds = DataBaseHelper.getDataBase().carrierVehicleDao().getVehicleIdList(carrierId);
                List<Integer> recentVehicleIds = DataBaseHelper.getDataBase().recentVehicleDao().getRecentVehicleList(carrierId);

                List<Vehicle> allVehicleList = DataBaseHelper.getDataBase().vehicleDao().getVehicleList(vehicleIds);
                List<Vehicle> recentVehicleList = DataBaseHelper.getDataBase().vehicleDao().getVehicleList(recentVehicleIds);

                List<VehicleVo> recentVehicleVoList = new ArrayList<>();
                List<VehicleVo> allVehicleVoList = new ArrayList<>();

                for (Vehicle vehicle : recentVehicleList) {

                    RecentVehicle recentVehicle = DataBaseHelper.getDataBase().recentVehicleDao().get(vehicle.getId());
                    recentVehicleVoList.add(new VehicleVo(vehicle, recentVehicle));
                }

                for (Vehicle otherVehicle : allVehicleList) {

                    allVehicleVoList.add(new VehicleVo(otherVehicle));
                }

                Collections.sort(recentVehicleVoList);
                Collections.sort(allVehicleVoList);

                final String recentVehicleVoListStr = JsonUtil.toJsJSONString(recentVehicleVoList);
                final String allVehicleVoListStr = JsonUtil.toJsJSONString(allVehicleVoList);

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + recentVehicleVoListStr + "','" + allVehicleVoListStr + "');");
                    }
                });
            }
        });
    }

    /**
     * 获取当前的车辆
     *
     * @param data 回调函数
     */
    private void getCurrentVehicle(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final int vehicleId = SystemHelper.getUser().getVehicleId();

        if (vehicleId == 0) {

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    uiWebView.loadUrl("javascript:" + callback + "();");
                }
            });
        } else {

            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    VehicleDataModel vehicleDataModel = DataCollectorHandler.getInstance().getDataModel();
                    Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(vehicleId);

                    VehicleVo vehicleVo = new VehicleVo(vehicle, vehicleDataModel);
                    final String vehicleStr = JsonUtil.toJsJSONString(vehicleVo);

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + vehicleStr + "');");
                        }
                    });
                }
            });
        }
    }

    /**
     * 连接车辆
     *
     * @param data 回调函数
     */
    private void connectVehicle(final String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        String vehicleVoStr = JsonUtil.getString(jsonObject, "vehicle");
        final VehicleVo vehicleVo = JsonUtil.parseObject(vehicleVoStr, VehicleVo.class);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (BluetoothHandler.getInstance().isEnable()) {

                    String connectingDialogTitle = context.getString(R.string.vehicle_connect_dialog_title);
                    connectingDialogTitle = connectingDialogTitle.replace("#vehicle#", vehicleVo.getCode());
                    final VehicleCommonDialog connectingDialog = new VehicleCommonDialog.Builder(context)
                            .setIcon(R.drawable.ic_emoji_connect_gif)
                            .setTitle(connectingDialogTitle)
                            .setText(R.string.vehicle_connect_dialog_text)
                            .setCancelable(false)
                            .setPositiveBtn(R.string.cancel, new VehicleCommonDialog.OnClickListener() {
                                @Override
                                public void onClick(VehicleCommonDialog dialog, int which) {

                                    //取消设备连接
                                    DataCollectorHandler.getInstance().stopDeviceModel();
                                    dialog.dismiss();
                                }
                            })
                            .build();

                    connectingDialog.show();
                    Logger.i(Tags.ECM, "show connect vehicle dialog.");
                    ConfigOption option = new ConfigOption.Builder()
                            .vehicleNumber(vehicleVo.getCode())
                            .bluetoothAddress(vehicleVo.getEcmSn())
                            .deviceType(ConfigOption.getDeviceTypeByEcmLinkType(vehicleVo.getEcmLinkType()))
                            .build();

                    DataCollectorHandler.getInstance().connectVehicle(option, new ConnectVehicleCallback() {
                        @Override
                        public void connectSuccess() {

                            Logger.i(Tags.ECM, "connect success, vehicleId=" + vehicleVo.getId() + ", vehicleCode=" + vehicleVo.getCode());

                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    connectingDialog.dismiss();

                                    new PromptDialog.Builder(context)
                                            .type(PromptDialog.SUCCESS)
                                            .listener(new PromptDialog.OnHideListener() {
                                                @Override
                                                public void onHide() {

                                                    uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                                                }
                                            })
                                            .build()
                                            .show();
                                }
                            });

                            //存储当前连接的车辆
                            SystemHelper.setVehicle(vehicleVo.getId());
                            //通知DashBoard
                            EventBus.getDefault().post(new VehicleSelectEvent());
                            //通知DashBoard
                            EventBus.getDefault().post(new VehicleConnectEvent(VehicleConnectType.CONNECTED));

                            ThreadUtil.getInstance().execute(new Runnable() {
                                @Override
                                public void run() {

                                    User user = SystemHelper.getUser();

                                    //记录车辆连接历史
                                    RecentVehicle recentVehicle = new RecentVehicle();
                                    recentVehicle.setVehicleId(user.getVehicleId());
                                    recentVehicle.setCarrierId(user.getCarriedId());
                                    recentVehicle.setConnectedTime(new Date().getTime());
                                    DataBaseHelper.getDataBase().recentVehicleDao().insert(recentVehicle);

                                    //通知自检服务
                                    EventBus.getDefault().post(new SelfCheckEvent(SelfCheckEventType.TIME));
                                    EventBus.getDefault().post(new SelfCheckEvent(SelfCheckEventType.ECM_LINK, true));
                                }
                            });
                        }

                        @Override
                        public void connectFailed() {

                            Logger.i(Tags.ECM, "connect vehicle failed, show failed dialog.");

                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    connectingDialog.dismiss();

                                    String connectFailedDialogTitle = context.getString(R.string.vehicle_connect_dialog_failed_title);
                                    connectFailedDialogTitle = connectFailedDialogTitle.replace("#vehicle#", vehicleVo.getCode());

                                    new VehicleCommonDialog.Builder(context)
                                            .setIcon(R.drawable.ic_emoji_connect_failed_gif)
                                            .setTitle(connectFailedDialogTitle)
                                            .setText(R.string.vehicle_connect_dialog_failed_text)
                                            .setNegativeBtn(R.string.cancel, new VehicleCommonDialog.OnClickListener() {
                                                @Override
                                                public void onClick(VehicleCommonDialog dialog, int which) {

                                                    Logger.i(Tags.ECM, "cancel connect vehicle.");
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setNeutralBtn(R.string.manual_mode, new VehicleCommonDialog.OnClickListener() {
                                                @Override
                                                public void onClick(final VehicleCommonDialog dialog, int which) {

                                                    Logger.i(Tags.ECM, "start manual mode, vehicleId=" + vehicleVo.getId() + ", vehicleCode=" + vehicleVo.getCode());

                                                    ThreadUtil.getInstance().execute(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            //存储当前连接的车辆
                                                            SystemHelper.setVehicle(vehicleVo.getId());
                                                            User user = SystemHelper.getUser();

                                                            //记录车辆连接历史
                                                            RecentVehicle recentVehicle = new RecentVehicle();
                                                            recentVehicle.setVehicleId(user.getVehicleId());
                                                            recentVehicle.setCarrierId(user.getCarriedId());
                                                            recentVehicle.setConnectedTime(new Date().getTime());
                                                            DataBaseHelper.getDataBase().recentVehicleDao().insert(recentVehicle);

                                                            //启动GPS模式
                                                            DataCollectorHandler.getInstance().startGpsModel();

                                                            //通知DashBoard
                                                            EventBus.getDefault().post(new VehicleSelectEvent());
                                                            EventBus.getDefault().post(new VehicleConnectEvent(VehicleConnectType.DIS_CONNECTED));

                                                            //通知自检服务，尝试不断尝试启动设备模式
                                                            EventBus.getDefault().post(new SelfCheckEvent(SelfCheckEventType.ECM_LINK, true, false));
                                                        }
                                                    });

                                                    ((Activity) context).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            dialog.dismiss();

                                                            new PromptDialog.Builder(context)
                                                                    .type(PromptDialog.SUCCESS)
                                                                    .listener(new PromptDialog.OnHideListener() {
                                                                        @Override
                                                                        public void onHide() {

                                                                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                                                                        }
                                                                    })
                                                                    .build()
                                                                    .show();
                                                        }
                                                    });
                                                }
                                            })
                                            .setCancelable(false)
                                            .build()
                                            .show();
                                }
                            });
                        }
                    });
                } else {

                    Logger.i(Tags.ECM, "bluetooth isn't open, show openTip dialog.");

                    new HorizontalDialog.Builder(context)
                            .setIcon(R.drawable.ic_emoji_love)
                            .setText(R.string.bluetooth_open_tip)
                            .setPositiveBtn(R.string.ok, new HorizontalDialog.OnClickListener() {
                                @Override
                                public void onClick(HorizontalDialog dialog, int which) {

                                    dialog.dismiss();
                                    BluetoothHandler.getInstance().init();
                                }
                            })
                            .build()
                            .show();
                }
            }
        });
    }

    /**
     * 与车辆连接断开
     */
    private void disconnectVehicle(final String data) {

        Logger.i(Tags.ECM, "show disconnect vehicle dialog.");

        new VerticalDialog.Builder(context)
                .setIcon(R.drawable.ic_emoji_msg)
                .setTipText(R.string.disconnect_vehicle_tip)
                .setNeutralBtn(R.string.disconnect_vehicle_neutral_btn, new VerticalDialog.OnClickListener() {
                    @Override
                    public void onClick(VerticalDialog dialog, int which) {

                        Logger.i(Tags.ECM, "cancel disconnect vehicle.");
                        dialog.dismiss();
                    }
                })
                .setNegativeBtn(R.string.disconnect_vehicle_negative_btn, new VerticalDialog.OnClickListener() {
                    @Override
                    public void onClick(VerticalDialog dialog, int which) {

                        Logger.i(Tags.ECM, "disconnect vehicle.");
                        dialog.dismiss();

                        //如果当前状态是driving或者是yardmove，自动切换为ODND
                        //如果当前状态是PC，自动切换为OFF
                        DriverState driverState = ModelCenter.getInstance().getCurrentDriverState();
                        Location location = LocationHandler.getInstance().getCurrentLocation();
                        StateAdditionInfo.Builder builder = new StateAdditionInfo.Builder();
                        builder.origin(DDLOriginEnum.EDIT_BY_DRIVER);
                        if (location != null) {

                            builder.location(null, Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
                        }
                        if (driverState == DriverState.DRIVING || driverState == DriverState.YARD_MOVE) {

                            EventCenter.getInstance().changeDriverState(DriverState.ON_DUTY_NOT_DRIVING, builder.build());
                        } else if (driverState == DriverState.PERSONAL_USE) {

                            EventCenter.getInstance().changeDriverState(DriverState.OFF_DUTY, builder.build());
                        }

                        //清除当前连接的车辆
                        SystemHelper.clearVehicle();

                        //停止数据采集
                        DataCollectorHandler.getInstance().stop();

                        EventBus.getDefault().post(new VehicleSelectEvent());
                        EventBus.getDefault().post(new VehicleConnectEvent(VehicleConnectType.CLEAR));

                        new PromptDialog.Builder(context)
                                .type(PromptDialog.SUCCESS)
                                .build()
                                .show();
                    }
                })
                .build()
                .show();
    }

    /**
     * 自动断开车辆连接
     * 当从其他状态切换为OFF时调用此方法
     *
     * @param data 数据体
     */
    private void disconnectVehicleAuto(String data) {

        Logger.i(Tags.ECM, "disconnect vehicle become driverState changeTo Off.");

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //清除当前连接的车辆
                SystemHelper.clearVehicle();

                //停止数据采集
                DataCollectorHandler.getInstance().stop();

                EventBus.getDefault().post(new VehicleSelectEvent());
                EventBus.getDefault().post(new VehicleConnectEvent(VehicleConnectType.CLEAR));
                EventBus.getDefault().post(new VehicleAutoConnectEvent(VehicleAutoConnectType.DISCONNECTED));
            }
        });
    }

    /**
     * 获取车辆故障列表
     *
     * @param data 回调函数
     */
    private void getVehicleMalfunctionList(final String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();

                HttpRequest getMalFunctionRequest = new HttpRequest.Builder()
                        .url(Constants.API_GET_MALFUNCTION_LIST)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("vehicle_id", Integer.toString(user.getVehicleId()))
                        .build();

                final HttpResponse httpResponse = getMalFunctionRequest.get();

                final List<MalFunctionsVo> malList = new ArrayList<>();
                final List<MalFunctionsVo> diagList = new ArrayList<>();

                if (httpResponse.isSuccess()) {

                    JSONObject dataObj = JsonUtil.parseObject(httpResponse.getData());
                    JSONArray malFunctionArray = JsonUtil.getJsonArray(dataObj, "malfunctions");

                    for (int i = 0; i < malFunctionArray.size(); i++) {

                        JSONObject malFunction = malFunctionArray.getJSONObject(i);
                        MalFunctionsVo malFunctionsVo = new MalFunctionsVo(malFunction);

                        if (malFunctionsVo.type == MalFunctionsVo.MALFUNCTION) {

                            malList.add(malFunctionsVo);
                        } else if (malFunctionsVo.type == MalFunctionsVo.DIAGNOSTIC) {

                            diagList.add(malFunctionsVo);
                        }
                    }
                }

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(malList) + "', '" + JsonUtil.toJsJSONString(diagList) + "');");
                    }
                });
            }
        });
    }

    /**
     * 里程数修正
     *
     * @param data
     */
    private void changeOdometer(final String data) {

        final JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final String odo_offset = JsonUtil.getString(jsonObject, "odo_offset");

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();
                long update_time = new Date().getTime();

                HttpRequest changeOdometerRequest = new HttpRequest.Builder()
                        .url(Constants.API_CHANGE_ODOMETER)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("vehicle_id", Integer.toString(user.getVehicleId()))
                        .addParam("odo_offset", odo_offset)
                        .addParam("update_time", String.valueOf(update_time))
                        .build();

                final HttpResponse httpResponse = changeOdometerRequest.post();


                if (httpResponse.isSuccess()) {

                    //更新数据库
                    Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(user.getVehicleId());
                    if (vehicle != null) {

                        vehicle.setOdoOffset(Double.parseDouble(odo_offset));
                        vehicle.setOdoOffsetUpdateTime(String.valueOf(update_time));
                        DataBaseHelper.getDataBase().vehicleDao().update(vehicle);
                    }

                    //更新内存
                    VehicleDataRecorder vehicleDataRecorder = DataCollectorHandler.getInstance().getVehicleDataRecorder();
                    if (vehicleDataRecorder != null && vehicle != null) {

                        vehicleDataRecorder.setOffsetOdometer(Double.parseDouble(odo_offset));
                        vehicleDataRecorder.setOdoOffsetUpdateTime(String.valueOf(update_time));
                        vehicleDataRecorder.setTotalOdometer(vehicle.getOdometer());
                    }

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            new PromptDialog.Builder(context)
                                    .type(PromptDialog.SUCCESS)
                                    .listener(new PromptDialog.OnHideListener() {
                                        @Override
                                        public void onHide() {

                                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                                        }
                                    })
                                    .build()
                                    .show();
                        }
                    });
                } else {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            new PromptDialog.Builder(context)
                                    .type(PromptDialog.FAILURE)
                                    .listener(new PromptDialog.OnHideListener() {
                                        @Override
                                        public void onHide() {

                                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                                        }
                                    })
                                    .build()
                                    .show();
                        }
                    });
                }
            }
        });
    }
}
