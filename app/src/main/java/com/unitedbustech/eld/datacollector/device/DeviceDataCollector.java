package com.unitedbustech.eld.datacollector.device;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.EngineEvent;
import com.unitedbustech.eld.common.EngineState;
import com.unitedbustech.eld.common.VehicleDataItem;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.common.VehicleState;
import com.unitedbustech.eld.datacollector.ConnectVehicleCallback;
import com.unitedbustech.eld.datacollector.common.CollectorItemCallback;
import com.unitedbustech.eld.datacollector.common.VehicleDataRecorder;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;

/**
 * @author zhangyu
 * @date 2018/5/21
 * @description 设备数据采集器
 */
public abstract class DeviceDataCollector {

    /**
     * 周期性订阅，数据回调周期
     */
    protected static final long SUBSCRIBE_PERIOD_DURATION = 2 * 1000L;

    protected Handler handler;

    /**
     * 周期性回调数据runnable
     */
    protected Runnable callbackRunnable;

    /**
     * 判断车辆静止的Runnable
     */
    protected Runnable vehicleStaticRunnable;

    /**
     * 车辆连接回调函数
     */
    protected ConnectVehicleCallback connectVehicleCallback;

    /**
     * 工作状态监听
     */
    protected CollectorItemCallback collectorItemCallback;

    /**
     * 数据模型
     */
    protected VehicleDataRecorder vehicleDataRecorder;

    public DeviceDataCollector() {

        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 初始化
     *
     * @param recorder 数据维护器
     * @param callback 回调
     */
    public DeviceDataCollector init(@NonNull VehicleDataRecorder recorder, @NonNull CollectorItemCallback callback) {

        vehicleDataRecorder = recorder;
        collectorItemCallback = callback;

        return this;
    }

    /**
     * 启动
     * 调用此方法后，数据中心服务启动
     *
     * @param option ConfigOption
     */
    public abstract void start(@NonNull final ConfigOption option, @Nullable ConnectVehicleCallback connectVehicleCallback);

    /**
     * 停止服务
     */
    public abstract void stop();

    /**
     * 销毁时的操作
     */
    public void destroy() {

        stop();

        vehicleDataRecorder.clear();

        if (vehicleStaticRunnable != null) {

            handler.removeCallbacks(vehicleStaticRunnable);
        }

        handler = null;
        collectorItemCallback = null;
        connectVehicleCallback = null;

        vehicleDataRecorder = null;
    }

    /**
     * 周期性回调数据
     */
    protected void startScheduleCallback() {

        if (callbackRunnable == null) {

            callbackRunnable = new Runnable() {
                @Override
                public void run() {

                    collectorItemCallback.onSchedule(new VehicleDataModel(vehicleDataRecorder));
                    handler.postDelayed(callbackRunnable, SUBSCRIBE_PERIOD_DURATION);
                }
            };
            handler.postDelayed(callbackRunnable, SUBSCRIBE_PERIOD_DURATION);
        }
    }

    /**
     * 取消周期性回调
     */
    protected void stopScheduleCallback() {

        if (callbackRunnable != null) {

            handler.removeCallbacks(callbackRunnable);
        }

        callbackRunnable = null;
    }

    /**
     * 分析引擎状态
     *
     * @param rpm r/min
     */
    protected void analysisEngine(double rpm) {

        //根据转速判断打火熄火事件和引擎状态
        if (rpm > Constants.RPM_THRESHOLD) {

            if (vehicleDataRecorder.getEngineState() == EngineState.STOP) {

                Logger.i(Tags.ECM, "engine state is stopped, engine event [power on], rpm is [" + rpm + "]");

                VehicleDataModel engineEventModel = new VehicleDataModel();
                engineEventModel.setEngineEvent(EngineEvent.POWER_ON);

                vehicleDataRecorder.updateData(VehicleDataItem.ENGINE_EVENT, engineEventModel);
                collectorItemCallback.onDataItemChange(VehicleDataItem.ENGINE_EVENT, new VehicleDataModel(vehicleDataRecorder));
            }

            Logger.d(Tags.ECM, "engine state is [work]");

            VehicleDataModel engineStateModel = new VehicleDataModel();
            engineStateModel.setEngineState(EngineState.WORK);

            vehicleDataRecorder.updateData(VehicleDataItem.ENGINE_STATE, engineStateModel);
            collectorItemCallback.onDataItemChange(VehicleDataItem.ENGINE_STATE, new VehicleDataModel(vehicleDataRecorder));
        } else {

            if (vehicleDataRecorder.getEngineState() == EngineState.WORK) {

                Logger.i(Tags.ECM, "engine state is work, engine event [power off], rpm is [" + rpm + "]");

                VehicleDataModel engineEventModel = new VehicleDataModel();
                engineEventModel.setEngineEvent(EngineEvent.POWER_OFF);

                vehicleDataRecorder.updateData(VehicleDataItem.ENGINE_EVENT, engineEventModel);
                collectorItemCallback.onDataItemChange(VehicleDataItem.ENGINE_EVENT, new VehicleDataModel(vehicleDataRecorder));
            }

            Logger.d(Tags.ECM, "engine state is [stop]");

            VehicleDataModel engineStateModel = new VehicleDataModel();
            engineStateModel.setEngineState(EngineState.STOP);

            vehicleDataRecorder.updateData(VehicleDataItem.ENGINE_STATE, engineStateModel);
            collectorItemCallback.onDataItemChange(VehicleDataItem.ENGINE_STATE, new VehicleDataModel(vehicleDataRecorder));
        }
    }

    /**
     * 分析车辆状态
     *
     * @param speed mile/h
     */
    protected void analysisVehicleState(double speed) {

        if (speed > Constants.SPEED_THRESHOLD) {

            if (vehicleDataRecorder.getVehicleState() != VehicleState.MOVING) {

                Logger.i(Tags.ECM, "vehicle state become [moving], speed = " + speed);
            } else {

                Logger.d(Tags.ECM, "vehicle state [moving]");
            }

            VehicleDataModel vehicleStateModel = new VehicleDataModel();
            vehicleStateModel.setVehicleState(VehicleState.MOVING);

            vehicleDataRecorder.updateData(VehicleDataItem.VEHICLE_STATE, vehicleStateModel);
            collectorItemCallback.onDataItemChange(VehicleDataItem.VEHICLE_STATE, new VehicleDataModel(vehicleDataRecorder));

            if (vehicleStaticRunnable != null) {

                handler.removeCallbacks(vehicleStaticRunnable);
                vehicleStaticRunnable = null;
            }
        } else {

            //如果当前正在移动
            if (vehicleDataRecorder.getVehicleState() == VehicleState.MOVING) {

                if (speed < 1D && vehicleStaticRunnable == null) {

                    vehicleStaticRunnable = new Runnable() {
                        @Override
                        public void run() {

                            if (vehicleDataRecorder.getSpeed() < 1D) {

                                if (vehicleDataRecorder.getVehicleState() != VehicleState.STATIC) {

                                    Logger.i(Tags.ECM, "vehicle state become [stopped]");
                                } else {

                                    Logger.d(Tags.ECM, "vehicle state [stopped]");
                                }

                                VehicleDataModel vehicleStateModel = new VehicleDataModel();
                                vehicleStateModel.setVehicleState(VehicleState.STATIC);

                                vehicleDataRecorder.updateData(VehicleDataItem.VEHICLE_STATE, vehicleStateModel);
                                collectorItemCallback.onDataItemChange(VehicleDataItem.VEHICLE_STATE, new VehicleDataModel(vehicleDataRecorder));
                            }

                            vehicleStaticRunnable = null;
                        }
                    };

                    handler.postDelayed(vehicleStaticRunnable, Constants.VEHICLE_STATIC_THRESHOLD);
                }
            } else {

                if (vehicleDataRecorder.getVehicleState() != VehicleState.STATIC) {

                    Logger.i(Tags.ECM, "vehicle state become [stopped]");
                } else {

                    Logger.d(Tags.ECM, "vehicle state [stopped]");
                }

                VehicleDataModel vehicleStateModel = new VehicleDataModel();
                vehicleStateModel.setVehicleState(VehicleState.STATIC);

                vehicleDataRecorder.updateData(VehicleDataItem.VEHICLE_STATE, vehicleStateModel);
                collectorItemCallback.onDataItemChange(VehicleDataItem.VEHICLE_STATE, new VehicleDataModel(vehicleDataRecorder));
            }
        }
    }
}
