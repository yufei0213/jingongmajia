package com.unitedbustech.eld.system;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.App;
import com.unitedbustech.eld.command.CommandExecutor;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.EngineState;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.datacollector.DataCollectorHandler;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Vehicle;
import com.unitedbustech.eld.eventbus.VehicleSelectEvent;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.util.AppUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.LanguageUtil;
import com.unitedbustech.eld.util.ThreadUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

/**
 * @author yufei0213
 * @date 2018/2/5
 * @description 心跳上报服务
 */
public class HeartBeatService {

    private static final String TAG = "HeartBeatService";

    private static final long DURATION = 5 * 60 * 1000L;

    private Handler handler;
    private HeartBeatRunnable heartBeatRunnable;

    private static HeartBeatService instance = null;

    private HeartBeatService() {
    }

    public static HeartBeatService getInstance() {

        if (instance == null) {

            instance = new HeartBeatService();
        }

        return instance;
    }

    /**
     * 初始化服务，开始上报心跳
     */
    public void start() {

        if (EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().unregister(this);
        }

        EventBus.getDefault().register(this);

        if (heartBeatRunnable == null) {

            heartBeatRunnable = new HeartBeatRunnable();
        }

        if (handler == null) {

            handler = new Handler(Looper.getMainLooper());
            handler.post(heartBeatRunnable);
        }
    }

    /**
     * 销毁服务，停止上报心跳
     */
    public void stop() {

        try {

            EventBus.getDefault().unregister(this);

            if (handler != null) {

                handler.removeCallbacks(heartBeatRunnable);
                handler = null;
                heartBeatRunnable = null;
            }
        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            instance = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onVehicleSelectEvent(VehicleSelectEvent vehicleSelectEvent) {

        if (heartBeatRunnable != null) {

            heartBeatRunnable.uploadHeartBeat();
        }
    }

    /**
     * 上报心跳的任务
     */
    private class HeartBeatRunnable implements Runnable {
        @Override
        public void run() {

            if (handler != null) {

                handler.postDelayed(this, DURATION);
            }

            uploadHeartBeat();
        }

        /**
         * 上报心跳
         */
        synchronized void uploadHeartBeat() {

            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    HttpRequest.Builder builder = new HttpRequest.Builder()
                            .url(Constants.API_HEARTBEAT);

                    User user = SystemHelper.getUser();

                    String access_token = user.getAccessToken();
                    builder.addParam("access_token", access_token);

                    builder.addParam("dev_id", UUIDS.getUUID());
                    builder.addParam("app_language", LanguageUtil.getInstance().getLanguageType() == LanguageUtil.LANGUAGE_ZH ? "zh" : "en");

                    int vehicle_id = user.getVehicleId();
                    if (vehicle_id != 0) {

                        VehicleDataModel vehicleDataModel = DataCollectorHandler.getInstance().getDataModel();

                        double odometer = vehicleDataModel.getTotalOdometer();
                        double engine_hours = vehicleDataModel.getTotalEngineHours();

                        if (Double.doubleToLongBits(odometer) != Double.doubleToLongBits(0)) {

                            Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(vehicle_id);
                            if (vehicle != null) {

                                vehicle.setOdometer(vehicleDataModel.getTotalOdometer());
                                vehicle.setEngineHour(vehicleDataModel.getTotalEngineHours());

                                DataBaseHelper.getDataBase().vehicleDao().update(vehicle);

                                Logger.i(Tags.SYSTEM, "update vehicle odometer&engineHour in database");
                            }

                            builder.addParam("vehicle_id", Integer.toString(vehicle_id));
                            builder.addParam("odometer", Double.toString(odometer));
                            builder.addParam("engine_hours", Double.toString(engine_hours));

                            double speed = vehicleDataModel.getSpeed();
                            builder.addParam("speed", Double.toString(speed));

                            int engineStatus = vehicleDataModel.getEngineState();
                            int engine_status = engineStatus == EngineState.WORK ? 1 : 0;
                            builder.addParam("engine_status", Integer.toString(engine_status));
                        } else {

                            Logger.w(Tags.SYSTEM, "Odometer || EngineHour equals zero");
                        }
                    }

                    Location location = LocationHandler.getInstance().getCurrentLocation();
                    if (location != null) {

                        builder.addParam("latitude", Double.toString(location.getLatitude()));
                        builder.addParam("longitude", Double.toString(location.getLongitude()));
                    }

                    builder.addParam("app_version", AppUtil.getVersionName(App.getContext()));
                    builder.addParam("os_type", "1");
                    builder.addParam("dev_type", "1");
                    builder.addParam("datetime", Long.toString(new Date().getTime()));

                    HttpResponse response = builder.build().post();
                    if (response.isSuccess()) {

                        try {

                            String data = response.getData();
                            Logger.i(Tags.COMMAND, "getCommand: data=" + data);

                            JSONObject dataObj = JsonUtil.parseObject(data);
                            JSONArray commands = dataObj.getJSONArray("commandList");

                            if (!commands.isEmpty()) {

                                Logger.i(Tags.COMMAND, "getCommand: commands=" + commands.toJSONString());
                                new CommandExecutor().execute(commands.toJSONString());
                            } else {

                                Logger.i(Tags.COMMAND, "getCommand: no command");
                            }
                        } catch (Exception e) {

                            Logger.w(Tags.COMMAND, "getCommand: getDate error: " + e.toString());
                        }

                        Logger.d(Tags.SYSTEM, "upload heart success");
                    } else {

                        Logger.w(Tags.SYSTEM, "upload heart failed: " + "code=" + response.getCode() + ", msg=" + response.getMsg());
                    }
                }
            });
        }
    }
}
