package com.unitedbustech.eld.datacollector.gps;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.VehicleDataItem;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.common.VehicleState;
import com.unitedbustech.eld.datacollector.common.CollectorItemCallback;
import com.unitedbustech.eld.datacollector.common.CollectorType;
import com.unitedbustech.eld.datacollector.common.VehicleDataRecorder;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Vehicle;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.location.LocationSubscriber;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.ConvertUtil;
import com.unitedbustech.eld.util.CoordinateUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.util.TimeUtil;

import java.util.Date;

/**
 * @author yufei0213
 * @date 2018/1/12
 * @description GPS数据中心
 */
public class GpsDataCollector implements LocationSubscriber {

    private static final String TAG = "GpsDataCollector";

    /**
     * 判定车辆移动的速度阈值
     * <p>
     * 单位 m/s
     */
    private static final float SPEED_THRESHOLD = (float) ConvertUtil.mph2ms(Constants.SPEED_THRESHOLD);
    /**
     * 判断车辆静止的时间阈值
     */
    private static final long VEHICLE_STATIC_THRESHOLD = 3 * 1000L;

    /**
     * 距离上一个Location大于此阈值时，odometer计算有效
     * <p>
     * 单位 m
     */
    private static final double ODOMETER_THRESHOLD = 20D;

    /**
     * 距离上一个engine-hour更新时间大于此阈值时，engine_hours计算有效
     * <p>
     * 单位，分钟
     */
    private static final double ENGINE_HOURS_THRESHOLD = 1D;

    /**
     * 周期性订阅，数据回调周期
     */
    private static final long SUBSCRIBE_PERIOD_DURATION = 2 * 1000L;

    /**
     * 当前是否正在工作
     */
    private boolean isWorking;

    /**
     * 当前速度，单位m/s
     */
    private double currentSpeed;

    /**
     * 当前里程，单位m
     */
    private double currentOdometer;

    /**
     * 上一个Location
     */
    private Location lastOdometerLocation;

    /**
     * 当前engine_hour
     */
    private double currentEngineHour;

    /**
     * 上次engine_hours更新时间
     */
    private Date lastEngineHourUpdateTime;

    /**
     * 用于判断车辆静止
     */
    private Runnable vehicleStaticRunnable;

    /**
     * 主线程Handler
     */
    private Handler handler;

    /**
     * 数据读取 runnable
     */
    private Runnable readRunnable;

    /**
     * 工作状态监听
     */
    private CollectorItemCallback collectorItemCallback;

    /**
     * 数据模型
     */
    private VehicleDataRecorder vehicleDataRecorder;

    public GpsDataCollector() {
    }

    /**
     * 初始化
     *
     * @param callback 回调
     */
    public GpsDataCollector init(@NonNull VehicleDataRecorder recorder, @NonNull CollectorItemCallback callback) {

        vehicleDataRecorder = recorder;
        collectorItemCallback = callback;

        handler = new Handler(Looper.getMainLooper());

        LocationHandler.getInstance().subscribe(TAG, this);

        return this;
    }

    /**
     * 销毁，回收资源等
     */
    public void destroy() {

        if (isWorking) {

            this.stop();
        }
        LocationHandler.getInstance().unSubscribe(TAG);

        this.collectorItemCallback = null;

        vehicleDataRecorder = null;

        handler = null;
    }

    /**
     * 开始工作
     */
    public void start() {

        this.isWorking = true;

        if (!vehicleDataRecorder.isNeedRead(VehicleDataItem.ODOMETER)) {

            //GPS应该是未修正的里程
            double totalOdometer = vehicleDataRecorder.getTotalOdometer();
            if (!TextUtils.isEmpty(vehicleDataRecorder.getOdoOffsetUpdateTime()) &&
                    TimeUtil.compareOdoOffsetUpdateTime(vehicleDataRecorder.getOdoOffsetUpdateTime()) &&
                    vehicleDataRecorder.getOffsetOdometer() != 0) {

                totalOdometer -= vehicleDataRecorder.getOffsetOdometer();
            }
            this.currentOdometer = ConvertUtil.mile2m(totalOdometer);
        } else {

            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(SystemHelper.getUser().getVehicleId());
                    if (vehicle != null) {

                        //GPS应该是未修正的里程
                        double totalOdometer = vehicle.getOdometer();
                        if (!TextUtils.isEmpty(vehicle.getOdoOffsetUpdateTime()) &&
                                TimeUtil.compareOdoOffsetUpdateTime(vehicle.getOdoOffsetUpdateTime()) &&
                                vehicle.getOdoOffset() != 0) {

                            totalOdometer -= vehicle.getOdoOffset();
                        }
                        GpsDataCollector.this.currentOdometer = ConvertUtil.mile2m(totalOdometer);
                    }
                }
            });
        }

        if (!vehicleDataRecorder.isNeedRead(VehicleDataItem.ENGINE_HOURS)) {

            this.currentEngineHour = ConvertUtil.hour2Minute(vehicleDataRecorder.getTotalEngineHours());
        } else {

            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(SystemHelper.getUser().getVehicleId());
                    if (vehicle != null) {

                        GpsDataCollector.this.currentEngineHour = ConvertUtil.hour2Minute(vehicle.getEngineHour());
                    }
                }
            });
        }

        if (readRunnable == null) {

            readRunnable = new Runnable() {
                @Override
                public void run() {

                    if (vehicleDataRecorder.getVehicleState() == VehicleState.UNKNOWN) {

                        //车辆状态未知，认为车辆静止，此种状况发生在车辆静止后，GPS不更新
                        DriverState driverState = ModelCenter.getInstance().getCurrentDriverState();
                        if (driverState != null && (driverState == DriverState.DRIVING || driverState == DriverState.PERSONAL_USE || driverState == DriverState.YARD_MOVE)) {

                            vehicleDataRecorder.setVehicleState(VehicleState.STATIC);
                        }

                        //车辆静止后GPS不更新，此处加一处分析，解决里程和引擎时间丢失问题
                        Location location = LocationHandler.getInstance().getCurrentLocation();
                        if (location != null) {

                            analysisOdometer(location);
                            analysisGps(location);
                        }

                        analysisEngineHours();
                    }

                    collectorItemCallback.onSchedule(new VehicleDataModel(vehicleDataRecorder));

                    handler.postDelayed(readRunnable, SUBSCRIBE_PERIOD_DURATION);
                }
            };

            handler.postDelayed(readRunnable, SUBSCRIBE_PERIOD_DURATION);
        }

        collectorItemCallback.onItemStateChange(true, CollectorType.GPS);
    }

    /**
     * 停止工作
     */
    public void stop() {

        this.isWorking = false;

        this.currentSpeed = 0;
        this.currentOdometer = 0;
        this.currentEngineHour = 0;

        this.lastEngineHourUpdateTime = null;
        this.lastOdometerLocation = null;

        if (readRunnable != null) {

            handler.removeCallbacks(readRunnable);
            readRunnable = null;
        }
    }

    /**
     * 读取某数据
     *
     * @param item 数据项
     */
    public VehicleDataModel read(@NonNull VehicleDataItem item) {

        if (item == null) {

            throw new RuntimeException("GpsDataCollector read needs a VehicleDataItem");
        }

        return new VehicleDataModel(vehicleDataRecorder);
    }

    @Override
    public void onLocationUpdate(Location location) {

        if (!isWorking) {

            return;
        }

        vehicleDataRecorder.setSpeed(location.getSpeed());
//        analysisVehicleState(location);

        analysisOdometer(location);

        analysisEngineHours();

        analysisGps(location);

        analysisUtcTime(location);
    }

    @Override
    public void onStateChange(boolean state) {

        if (isWorking) {

            collectorItemCallback.onItemStateChange(true, CollectorType.GPS);
        }
    }

    /**
     * 分析车辆状态
     *
     * @param location Location
     */
    private void analysisVehicleState(Location location) {

        currentSpeed = location.getSpeed();

        /**
         * 如果当前速度已经超过阈值，则将车辆状态置为移动状态
         * 如果当前在移动状态，并且速度值为0维持一段时间
         */
        if (currentSpeed > SPEED_THRESHOLD) {

            DriverState driverState = ModelCenter.getInstance().getCurrentDriverState();
            if (driverState != null && (driverState == DriverState.DRIVING || driverState == DriverState.PERSONAL_USE || driverState == DriverState.YARD_MOVE)) {

                vehicleDataRecorder.setVehicleState(VehicleState.MOVING);
                collectorItemCallback.onDataItemChange(VehicleDataItem.VEHICLE_STATE, new VehicleDataModel(vehicleDataRecorder));
            }

            if (vehicleStaticRunnable != null) {

                handler.removeCallbacks(vehicleStaticRunnable);
                vehicleStaticRunnable = null;
            }
        } else {

            if (currentSpeed < 1D && vehicleStaticRunnable == null) {

                vehicleStaticRunnable = new Runnable() {
                    @Override
                    public void run() {

                        if (currentSpeed < 1D) {

                            DriverState driverState = ModelCenter.getInstance().getCurrentDriverState();
                            if (driverState != null && (driverState == DriverState.DRIVING || driverState == DriverState.PERSONAL_USE || driverState == DriverState.YARD_MOVE)) {

                                vehicleDataRecorder.setVehicleState(VehicleState.STATIC);
                                collectorItemCallback.onDataItemChange(VehicleDataItem.VEHICLE_STATE, new VehicleDataModel(vehicleDataRecorder));
                            }
                        }
                    }
                };

                handler.postDelayed(vehicleStaticRunnable, VEHICLE_STATIC_THRESHOLD);
            }
        }
    }

    /**
     * 分析车辆里程
     *
     * @param location Location
     */
    private void analysisOdometer(Location location) {

        if (vehicleDataRecorder.getVehicleState() != VehicleState.MOVING && lastOdometerLocation != null) {

            double distance = CoordinateUtil.getDistance(lastOdometerLocation.getLatitude(),
                    lastOdometerLocation.getLongitude(),
                    location.getLatitude(),
                    location.getLongitude());

            this.currentOdometer += distance;
            this.lastOdometerLocation = null;

            vehicleDataRecorder.setTotalOdometer(ConvertUtil.decimal2Point(ConvertUtil.m2mile(this.currentOdometer)));
            collectorItemCallback.onDataItemChange(VehicleDataItem.ODOMETER, new VehicleDataModel(vehicleDataRecorder));
        } else {

            if (lastOdometerLocation == null) {

                lastOdometerLocation = location;
            }

            double distance = CoordinateUtil.getDistance(lastOdometerLocation.getLatitude(),
                    lastOdometerLocation.getLongitude(),
                    location.getLatitude(),
                    location.getLongitude());

            if (distance >= ODOMETER_THRESHOLD) {

                this.currentOdometer += distance;
                lastOdometerLocation = location;

                vehicleDataRecorder.setTotalOdometer(ConvertUtil.decimal2Point(ConvertUtil.m2mile(this.currentOdometer)));
                collectorItemCallback.onDataItemChange(VehicleDataItem.ODOMETER, new VehicleDataModel(vehicleDataRecorder));
            }
        }
    }

    /**
     * 分析引擎时间
     */
    private void analysisEngineHours() {

        if (vehicleDataRecorder.getVehicleState() != VehicleState.MOVING && lastEngineHourUpdateTime != null) {

            Date nowTime = new Date();
            double period = ConvertUtil.mill2Minute(nowTime.getTime() - lastEngineHourUpdateTime.getTime());

            this.currentEngineHour += period;
            this.lastEngineHourUpdateTime = null;

            vehicleDataRecorder.setTotalEngineHours(ConvertUtil.decimal2Point(ConvertUtil.minute2Hour(this.currentEngineHour)));
            collectorItemCallback.onDataItemChange(VehicleDataItem.ENGINE_HOURS, new VehicleDataModel(vehicleDataRecorder));
        } else {

            if (lastEngineHourUpdateTime == null) {

                lastEngineHourUpdateTime = new Date();
            }

            Date nowTime = new Date();
            double period = ConvertUtil.mill2Minute(nowTime.getTime() - lastEngineHourUpdateTime.getTime());
            if (period >= ENGINE_HOURS_THRESHOLD) {

                this.currentEngineHour += period;
                lastEngineHourUpdateTime = nowTime;

                vehicleDataRecorder.setTotalEngineHours(ConvertUtil.decimal2Point(ConvertUtil.minute2Hour(this.currentEngineHour)));
                collectorItemCallback.onDataItemChange(VehicleDataItem.ENGINE_HOURS, new VehicleDataModel(vehicleDataRecorder));
            }
        }
    }

    /**
     * 分析utc时间
     *
     * @param location Location
     */
    private void analysisUtcTime(Location location) {

        if (vehicleDataRecorder.isNeedRead(VehicleDataItem.UTC_TIME)) {

            vehicleDataRecorder.setUtcTime(location.getTime());
        }
    }

    /**
     * 分析gps
     *
     * @param location Location
     */
    private void analysisGps(Location location) {

        vehicleDataRecorder.setLatitude(location.getLatitude());
        vehicleDataRecorder.setLongitude(location.getLongitude());
    }
}
