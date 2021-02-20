package com.unitedbustech.eld.datacollector;

import com.pt.sdk.Sdk;
import com.pt.sdk.TrackerService;
import com.unitedbustech.eld.App;
import com.unitedbustech.eld.bluetooth.BluetoothHandler;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.SelfCheckEventType;
import com.unitedbustech.eld.common.VehicleAutoConnectType;
import com.unitedbustech.eld.common.VehicleConnectType;
import com.unitedbustech.eld.common.VehicleDataItem;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.datacollector.common.CollectorItemCallback;
import com.unitedbustech.eld.datacollector.common.CollectorType;
import com.unitedbustech.eld.datacollector.common.DeviceType;
import com.unitedbustech.eld.datacollector.common.VehicleDataRecorder;
import com.unitedbustech.eld.datacollector.device.ConfigOption;
import com.unitedbustech.eld.datacollector.device.DeviceDataCollector;
import com.unitedbustech.eld.datacollector.device.bluelink.BlueLinkDeviceDataCollector;
import com.unitedbustech.eld.datacollector.device.pacific.PT30DeviceCollector;
import com.unitedbustech.eld.datacollector.gps.GpsDataCollector;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Vehicle;
import com.unitedbustech.eld.eventbus.SelfCheckEvent;
import com.unitedbustech.eld.eventbus.VehicleAutoConnectEvent;
import com.unitedbustech.eld.eventbus.VehicleConnectEvent;
import com.unitedbustech.eld.eventbus.VehicleSelectEvent;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yufei0213
 * @date 2018/1/12
 * @description 数据采集处理类
 */
public class DataCollectorHandler implements CollectorItemCallback {

    private final String TAG = "DataCollectorHandler";

    /**
     * 当前collector类型
     */
    private CollectorType currentCollectorType;

    /**
     * 车辆信息维护器
     */
    private VehicleDataRecorder vehicleDataRecorder;

    /**
     * 外部设备数据采集
     */
    private DeviceDataCollector deviceDataCollector;

    /**
     * gps数据采集
     */
    private GpsDataCollector gpsDataCollector;

    /**
     * 状态监听者
     */
    private Map<String, DataCollectorListener> dataCollectorListenerMap;

    /**
     * 订阅者
     */
    private Map<String, DataCollectorSubscriber> dataCollectorSubscriberMap;

    /**
     * 判断当前是否是手动请求连接车辆
     */
    private boolean isConnectVehicleRequest;

    private static DataCollectorHandler instance = null;

    private DataCollectorHandler() {

    }

    public static DataCollectorHandler getInstance() {

        if (instance == null) {

            instance = new DataCollectorHandler();
        }

        return instance;
    }

    /**
     * 初始化
     */
    public void init() {

        vehicleDataRecorder = new VehicleDataRecorder();

        gpsDataCollector = new GpsDataCollector()
                .init(vehicleDataRecorder, this);

        dataCollectorListenerMap = new ConcurrentHashMap<>();
        dataCollectorSubscriberMap = new ConcurrentHashMap<>();

        if (EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().unregister(this);
        }

        EventBus.getDefault().register(this);

        //无论是否使用Pt30,都先执行一次全局初始化。
        //因为Pt30的sdk内部需要一定的时间绑定Service，因此不能在使用时再初始化。
        Sdk.getInstance().initialize(App.getContext());
        TrackerService.getInstance().initialize(App.getContext());
    }

    /**
     * 销毁，释放资源
     */
    public void destroy() {

        try {

            EventBus.getDefault().unregister(this);
            TrackerService.getInstance().destroy(App.getContext());

            vehicleDataRecorder.clear();

            currentCollectorType = null;

            if (deviceDataCollector != null) {

                deviceDataCollector.destroy();
            }
            if (gpsDataCollector != null) {

                gpsDataCollector.destroy();
            }

            deviceDataCollector = null;
            gpsDataCollector = null;

            if (dataCollectorListenerMap != null) {

                dataCollectorListenerMap.clear();
            }
            dataCollectorListenerMap = null;

            if (dataCollectorSubscriberMap != null) {

                dataCollectorSubscriberMap.clear();
            }
            dataCollectorSubscriberMap = null;
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onVehicleSelectEvent(VehicleSelectEvent vehicleSelectEvent) {

        int vehicleId = SystemHelper.getUser().getVehicleId();
        Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(vehicleId);
        if (vehicle != null) {

            vehicleDataRecorder.setVehicleId(vehicleId);
            vehicleDataRecorder.setOffsetOdometer(vehicle.getOdoOffset());
            vehicleDataRecorder.setOdoOffsetUpdateTime(vehicle.getOdoOffsetUpdateTime());
            vehicleDataRecorder.setTotalOdometer(vehicle.getOdometer());
            vehicleDataRecorder.setTotalEngineHours(vehicle.getEngineHour());

            Logger.i(Tags.ECM, "vehicle select: id=" + vehicle.getId() + ", code=" + vehicle.getCode());
        } else {

            vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(vehicleDataRecorder.getVehicleId());
            if (vehicle != null) {

                vehicle.setOdometer(vehicleDataRecorder.getTotalOdometer());
                vehicle.setEngineHour(vehicleDataRecorder.getTotalEngineHours());

                DataBaseHelper.getDataBase().vehicleDao().update(vehicle);

                Logger.i(Tags.ECM, "update vehicle odometer&engineHour in database");
            }

            vehicleDataRecorder.clear();

            Logger.i(Tags.ECM, "vehicle clear");
        }
    }

    /**
     * 获取当前使用的模式类型
     *
     * @return CollectorType
     */
    public CollectorType getCurrentCollectorType() {

        return currentCollectorType;
    }

    /**
     * 启动gps模式
     * <p>
     * 如果选择车辆时，不能正常连接车辆，提示用户是否要开启gps模式，如果选择是则调用此方法
     * 如果当前正在Driving，App与车辆的连接出现问题，自动启用gps模式
     * gps模式下，每隔一定时间尝试连接车辆，切换到设备模式
     */
    public synchronized void startGpsModel() {

        Logger.i(Tags.ECM, "start GPS Model!!");
        gpsDataCollector.start();
    }

    /**
     * 停止gps模式
     */
    public synchronized void stopGpsModel() {

        Logger.i(Tags.ECM, "stop GPS Model!!");
        gpsDataCollector.stop();
    }

    /**
     * 获取当前读取到的数据模型
     *
     * @return VehicleDataModel
     */
    public VehicleDataModel getDataModel() {

        return new VehicleDataModel(vehicleDataRecorder);
    }

    /**
     * 获取当前的车的数据模型
     *
     * @return
     */
    public VehicleDataRecorder getVehicleDataRecorder() {

        return vehicleDataRecorder;
    }

    /**
     * 启动设备模式
     */
    public synchronized boolean startDeviceModel(ConfigOption configOption) {

        if (SystemHelper.hasVehicle()) {

            Logger.i(Tags.ECM, "start Device Model!!");

            if (BluetoothHandler.getInstance().isEnable()) {

                checkDeviceDataCollector(configOption);
                deviceDataCollector.start(configOption, null);
                return true;
            } else {

                Logger.w(Tags.ECM, "bluetooth is not open, try to open it!");
                BluetoothHandler.getInstance().init();
                return false;
            }
        } else {

            return false;
        }
    }

    /**
     * 关闭设备模式
     */
    public synchronized void stopDeviceModel() {

        Logger.i(Tags.ECM, "stop Device Model!!");

        deviceDataCollector.stop();
        EventBus.getDefault().post(new VehicleConnectEvent(VehicleConnectType.CLEAR));
    }

    /**
     * 停止采集数据
     */
    public synchronized void stop() {

        if (currentCollectorType == null) {

            return;
        }

        if (currentCollectorType == CollectorType.GPS) {

            stopGpsModel();
        }

        if (currentCollectorType == CollectorType.DEVICE) {

            stopDeviceModel();
        }

        currentCollectorType = null;
    }

    /**
     * 主动连接车辆
     *
     * @param configOption 车辆链接配置
     * @param callback     连接车辆回调
     */
    public synchronized void connectVehicle(ConfigOption configOption, ConnectVehicleCallback callback) {

        Logger.i(Tags.ECM, "start connect vehicle, Config Option=" + JsonUtil.toJSONString(configOption));

        isConnectVehicleRequest = true; //当前启动请求为手动请求
        checkDeviceDataCollector(configOption);
        deviceDataCollector.start(configOption, callback);
    }

    /**
     * 注册状态监听
     *
     * @param name     监听者名字
     * @param listener 状态监听
     */
    public void setStateListener(String name, DataCollectorListener listener) {

        if (dataCollectorListenerMap.containsKey(name)) {

            dataCollectorListenerMap.remove(name);
        }

        dataCollectorListenerMap.put(name, listener);
    }

    /**
     * 解除状态监听
     *
     * @param name 监听者名字
     */
    public void cancelStateListener(String name) {

        if (dataCollectorListenerMap.containsKey(name)) {

            dataCollectorListenerMap.remove(name);
        }
    }

    /**
     * 订阅
     *
     * @param name       订阅者名字
     * @param subscriber 订阅者
     */
    public void subscribe(String name, DataCollectorSubscriber subscriber) {

        if (dataCollectorSubscriberMap.containsKey(name)) {

            dataCollectorSubscriberMap.remove(name);
        }

        dataCollectorSubscriberMap.put(name, subscriber);
    }

    /**
     * 解除订阅
     *
     * @param name 订阅者名字
     */
    public void unSubscribe(String name) {

        if (dataCollectorSubscriberMap != null && dataCollectorSubscriberMap.containsKey(name)) {

            dataCollectorSubscriberMap.remove(name);
        }
    }

    /**
     * 检查设备数据检查器
     */
    private void checkDeviceDataCollector(ConfigOption configOption) {

        DeviceType deviceType = configOption.getDeviceType();
        switch (deviceType) {

            case DEVICE_TYPE_BLUELINK:

                if (deviceDataCollector == null) {

                    deviceDataCollector = new BlueLinkDeviceDataCollector().init(vehicleDataRecorder, this);
                } else {

                    if (!(deviceDataCollector instanceof BlueLinkDeviceDataCollector)) {

                        deviceDataCollector.stop();
                        deviceDataCollector.destroy();
                        deviceDataCollector = new BlueLinkDeviceDataCollector().init(vehicleDataRecorder, this);
                        vehicleDataRecorder.clear();
                    }
                }
                break;
            case DEVICE_TYPE_PT30:

                if (deviceDataCollector == null) {

                    deviceDataCollector = new PT30DeviceCollector().init(vehicleDataRecorder, this);
                } else {

                    if (!(deviceDataCollector instanceof PT30DeviceCollector)) {

                        deviceDataCollector.stop();
                        deviceDataCollector.destroy();
                        deviceDataCollector = new PT30DeviceCollector().init(vehicleDataRecorder, this);
                        vehicleDataRecorder.clear();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemStateChange(boolean state, CollectorType type) {

        if (currentCollectorType == null) {

            currentCollectorType = type;
        }

        //如果不是请求的车辆链接，则是自动的切换。
        //只关注自动切换的状态
        if (!isConnectVehicleRequest && state) {

            if (type == CollectorType.DEVICE) {

                EventBus.getDefault().post(new VehicleAutoConnectEvent(VehicleAutoConnectType.ECM));
            } else if (type == CollectorType.GPS) {

                EventBus.getDefault().post(new VehicleAutoConnectEvent(VehicleAutoConnectType.GPS));
            }
        }

        switch (currentCollectorType) {

            case GPS:

                switch (type) {

                    case DEVICE:

                        //发送DeviceModel状态变化通知
                        EventBus.getDefault().post(new VehicleConnectEvent(state ? VehicleConnectType.CONNECTED : VehicleConnectType.DIS_CONNECTED));
                        //通知自检服务
                        SelfCheckEvent selfCheckEvent = new SelfCheckEvent(SelfCheckEventType.ECM_LINK, state);
                        if (state) {

                            selfCheckEvent.setCreateGpsRemark(true);
                        }
                        EventBus.getDefault().post(selfCheckEvent);

                        if (state) {

                            stopGpsModel();
                            currentCollectorType = CollectorType.DEVICE;

                            for (Map.Entry<String, DataCollectorListener> entry : dataCollectorListenerMap.entrySet()) {

                                entry.getValue().onTypeChange(currentCollectorType, state);
                            }
                        } else {

                            deviceDataCollector.stop();
                        }
                        break;
                    case GPS:

                        //理论上，GPS模式下不存在GPS模式的自动变化
                        break;
                    default:
                        break;
                }

                break;
            case DEVICE:

                switch (type) {

                    case DEVICE:

                        if (!isConnectVehicleRequest) {

                            //发送DeviceModel状态变化通知
                            EventBus.getDefault().post(new VehicleConnectEvent(state ? VehicleConnectType.CONNECTED : VehicleConnectType.DIS_CONNECTED));
                        }

                        //通知状态监听者，连接状态发生变化
                        for (Map.Entry<String, DataCollectorListener> entry : dataCollectorListenerMap.entrySet()) {

                            entry.getValue().onTypeChange(currentCollectorType, state);
                        }

                        if (!state) {

                            deviceDataCollector.stop();
                            currentCollectorType = null;
                            if (!isConnectVehicleRequest) {

                                EventBus.getDefault().post(new SelfCheckEvent(SelfCheckEventType.ECM_LINK, state));

                                //如果当前司机状态是(Driving||PersonalUse||YardMove) && 设备在非人为状态下断开连接，则自动启用GPS模式
                                DriverState driverState = ModelCenter.getInstance().getCurrentDriverState();
                                if (driverState != null && (driverState == DriverState.DRIVING || driverState == DriverState.PERSONAL_USE || driverState == DriverState.YARD_MOVE)) {

                                    startGpsModel();
                                }
                            }
                        } else {

                            if (!isConnectVehicleRequest) {

                                EventBus.getDefault().post(new SelfCheckEvent(SelfCheckEventType.TIME));
                                EventBus.getDefault().post(new SelfCheckEvent(SelfCheckEventType.ECM_LINK, state));
                            }
                        }

                        isConnectVehicleRequest = false;
                        break;
                    case GPS:

                        //理论中，不存在此种情况
                        Logger.e(TAG, "current model is device, couldn't start gps model.");
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDataItemChange(VehicleDataItem item, VehicleDataModel model) {

        if (dataCollectorSubscriberMap == null) {

            return;
        }
        for (Map.Entry<String, DataCollectorSubscriber> entry : dataCollectorSubscriberMap.entrySet()) {

            entry.getValue().onDataItemChange(item, model, currentCollectorType);
        }
    }

    @Override
    public void onSchedule(VehicleDataModel model) {

        if (dataCollectorSubscriberMap == null) {

            return;
        }
        for (Map.Entry<String, DataCollectorSubscriber> entry : dataCollectorSubscriberMap.entrySet()) {

            entry.getValue().onSchedule(model, currentCollectorType);
        }
    }
}
