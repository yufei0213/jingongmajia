package com.unitedbustech.eld.datacollector.device.pacific;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.pt.sdk.BaseRequest;
import com.pt.sdk.DateTimeParam;
import com.pt.sdk.GeolocParam;
import com.pt.sdk.RequestHandler;
import com.pt.sdk.TrackerService;
import com.pt.sdk.request.SetSystemVar;
import com.pt.sdk.status.AckEvent;
import com.unitedbustech.eld.App;
import com.unitedbustech.eld.bluetooth.BluetoothHandler;
import com.unitedbustech.eld.bluetooth.BluetoothScanCallback;
import com.unitedbustech.eld.common.VehicleDataItem;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.datacollector.ConnectVehicleCallback;
import com.unitedbustech.eld.datacollector.common.CollectorType;
import com.unitedbustech.eld.datacollector.common.History;
import com.unitedbustech.eld.datacollector.common.HistoryType;
import com.unitedbustech.eld.datacollector.device.ConfigOption;
import com.unitedbustech.eld.datacollector.device.DeviceDataCollector;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.DataCacheService;
import com.unitedbustech.eld.util.ConvertUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.TimeUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangyu
 * @date 2018/5/21
 * @description 用于连接PT30的连接器。
 */
public class PT30DeviceCollector extends DeviceDataCollector implements RequestHandler {

    private final String TAG = "PT30DeviceCollector";

    /**
     * 开始读取记录的时间
     */
    private Date startReadHistoryTime;
    /**
     * 历史数据缓存
     */
    private List<History> histories;

    /**
     * 设备是否初始化完成
     * <p>
     * 防止设备多次通知初始化完成
     */
    private boolean deviceInitComplete;

    /**
     * 设备连接的回调
     */
    private PT30DeviceStateListener pt30DeviceStateListener = new PT30DeviceStateListener() {
        @Override
        public void onDeviceConnected() {

            //此处将该值重新初始化，理论上没有必要，但是为了保险，还是写下来
            deviceInitComplete = false;
            //此处的回调需要首先调用
            collectorItemCallback.onItemStateChange(true, CollectorType.DEVICE);

            startScheduleCallback();

            if (connectVehicleCallback != null) {

                connectVehicleCallback.connectSuccess();
                connectVehicleCallback = null;
            }

            Logger.i(Tags.ECM_PT30, "device connected.");
        }

        @Override
        public void onDeviceInitComplete() {

            if (deviceInitComplete) {

                Logger.i(Tags.ECM_PT30, "device deviceInitComplete connected repeated.");
            } else {

                deviceInitComplete = true;
                //Turn events ON - RSV
                SetSystemVar req = new SetSystemVar();
                req.setBoolean(SetSystemVar.Key.EVENT, true);
                TrackerService.getInstance().sendRequest(req, null, TrackerService.DEFAULT_REQ_TIME_OUT);

                Logger.i(Tags.ECM_PT30, "device deviceInitComplete connected.");
            }
        }

        @Override
        public void onDeviceDisconnected() {

            //重新赋值
            deviceInitComplete = false;
            //此处的回调需要首先调用
            collectorItemCallback.onItemStateChange(false, CollectorType.DEVICE);

            stop();

            if (connectVehicleCallback != null) {

                connectVehicleCallback.connectFailed();
                connectVehicleCallback = null;
            }

            Logger.i(Tags.ECM_PT30, "device disconnected.");
        }
    };

    public PT30DeviceCollector() {

        super();
    }

    public void start(@NonNull final ConfigOption configOption, @Nullable ConnectVehicleCallback connectVehicleCallback) {

        this.connectVehicleCallback = connectVehicleCallback;

        final BluetoothHandler bluetoothHandler = BluetoothHandler.getInstance();
        if (bluetoothHandler.isEnable()) {

            final Map<String, BluetoothDevice> bluetoothDeviceMap = new HashMap<>();

            Logger.i(Tags.ECM_PT30, "start scan ble device");
            bluetoothDeviceMap.clear();
            bluetoothHandler.startScan(new BluetoothScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice bluetoothDevice) {

                    if (!bluetoothDeviceMap.containsKey(bluetoothDevice.getName())) {

                        bluetoothDeviceMap.put(bluetoothDevice.getName(), bluetoothDevice);
                    }

                    //找到蓝牙后直接连接
                    if (validBluetoothDevice(bluetoothDevice, configOption.getDeviceAddress())) {

                        Logger.i(Tags.ECM_PT30, "found ble device, stop scan");
                        bluetoothHandler.stopScan();

                        histories = new ArrayList<>();

                        Logger.i(Tags.ECM_PT30, "device connecting...");
                        pt30DeviceStateListener.register(App.getContext());
                        TrackerService.getInstance().connect(bluetoothDevice.getAddress());
                        TrackerService.getInstance().setReqHandler(BaseRequest.Type.EVENT, PT30DeviceCollector.this);
                    }
                }

                @Override
                public void onFinish() {

                    Logger.i(Tags.ECM, "scan finished");

                    //1、查找包含车号的蓝牙
                    BluetoothDevice bluetoothDevice = null;
                    for (Map.Entry<String, BluetoothDevice> item : bluetoothDeviceMap.entrySet()) {

                        BluetoothDevice temp = item.getValue();
                        if (validBluetoothDevice(temp, configOption.getDeviceAddress())) {

                            bluetoothDevice = temp;
                            break;
                        }
                    }

                    //2、找到蓝牙，构建Connector的config
                    if (bluetoothDevice != null) {

                        histories = new ArrayList<>();

                        Logger.i(Tags.ECM, "scan finished");

                        Logger.i(Tags.ECM_PT30, "device connecting...");
                        pt30DeviceStateListener.register(App.getContext());
                        TrackerService.getInstance().connect(bluetoothDevice.getAddress());
                        TrackerService.getInstance().setReqHandler(BaseRequest.Type.EVENT, PT30DeviceCollector.this);
                    } else {

                        Logger.w(Tags.ECM, "not found ble device");

                        if (PT30DeviceCollector.this.connectVehicleCallback != null) {

                            PT30DeviceCollector.this.connectVehicleCallback.connectFailed();
                            PT30DeviceCollector.this.connectVehicleCallback = null;
                        }

                        collectorItemCallback.onItemStateChange(false, CollectorType.DEVICE);
                    }
                }
            });
        } else {

            Logger.w(Tags.ECM_PT30, "Bluetooth is not open");

            if (PT30DeviceCollector.this.connectVehicleCallback != null) {

                PT30DeviceCollector.this.connectVehicleCallback.connectFailed();
                PT30DeviceCollector.this.connectVehicleCallback = null;
            }

            collectorItemCallback.onItemStateChange(false, CollectorType.DEVICE);
        }
    }

    public void stop() {

        stopScheduleCallback();
        //如果还不曾连接上，调用close方法。否则调用disconnect方法
        if (connectVehicleCallback != null) {

            TrackerService.getInstance().close();
        } else {

            TrackerService.getInstance().disconnect();
        }
        pt30DeviceStateListener.unregister(App.getContext());

        //如果正在扫描则停止扫描
        BluetoothHandler.getInstance().stopScan();
    }

    /**
     * 验证蓝牙设备是否是目标设备
     *
     * @param bluetoothDevice 蓝牙设备
     * @param deviceAddress   车辆绑定的蓝牙地址
     * @return 是否是目标车
     */
    private boolean validBluetoothDevice(BluetoothDevice bluetoothDevice, String deviceAddress) {

        return !TextUtils.isEmpty(bluetoothDevice.getAddress()) && bluetoothDevice.getAddress().toLowerCase().replaceAll(":", "").equals(deviceAddress.toLowerCase());
    }

    @Override
    public void onRecv(@NonNull Context context, @NonNull BaseRequest request) {

        //如果没有车辆信息，则不处理;
        if (vehicleDataRecorder.getVehicleId() == 0) {

            return;
        }

        Logger.i(Tags.ECM_PT30, "event seq: " + request.getValue(BaseRequest.Key.SEQ));

        //判断是否是实时事件
        if (Integer.valueOf(request.getValue(BaseRequest.Key.LIVE_EVENT)) == 1) {

            Logger.i(Tags.ECM_PT30, "live event.........");

            //如果有历史事件，并且一分钟内没有新的历史事件，则认为历史事件读取完毕
            if (histories != null && startReadHistoryTime != null && (new Date().getTime() - startReadHistoryTime.getTime()) > 60 * 1000) {

                Logger.i(Tags.UNIDENTIFIED, "get history list: " + JsonUtil.toJSONString(histories));
                DataCacheService.getInstance().handleUnidentifiedLogs(new ArrayList<>(histories), startReadHistoryTime.getTime(), 2);

                startReadHistoryTime = null;
                histories = null;
            }

            handleEvent(request);
        } else {

            Logger.d(Tags.ECM_PT30, "not live event.........");

            History history = convertHistory(request);
            Logger.d(TAG, "history: " + JsonUtil.toJSONString(history));
            if (histories != null) {

                if (startReadHistoryTime == null) {

                    startReadHistoryTime = new Date();
                }
                histories.add(history);
            } else {

                Logger.w(Tags.ECM_PT30, "history collection is null.");
            }
        }

        //通知设备，将该事件从设备内存中删除
        AckEvent ack = new AckEvent(0, request.getValue(BaseRequest.Key.SEQ), request.getValue(BaseRequest.Key.DATE));
        TrackerService.getInstance().sendStatus(ack);
    }

    private void handleEvent(@NonNull BaseRequest request) {

        String event = request.getValue(BaseRequest.Key.EVENT);

        Logger.d(Tags.ECM_PT30, "new event: ======== " + event);
        switch (event) {

            case PT30Events.PERIOD:

                Logger.i(Tags.ECM_PT30, "event type: period");

                printLogs(request);
                refreshModel(request);
                break;
            case PT30Events.ENGINE_ON:

                Logger.i(Tags.ECM_PT30, "event type: engine_on");

                printLogs(request);
//                refreshModel(request);

//                vehicleDataRecorder.setEngineEvent(EngineEvent.POWER_ON);
//                vehicleDataRecorder.setEngineState(EngineState.WORK);
//                collectorItemCallback.onDataItemChange(VehicleDataItem.ENGINE_EVENT, new VehicleDataModel(vehicleDataRecorder));
                break;
            case PT30Events.ENGINE_OFF:

                Logger.i(Tags.ECM_PT30, "event type: engine_off");

                printLogs(request);
//                refreshModel(request);

//                vehicleDataRecorder.setEngineEvent(EngineEvent.POWER_OFF);
//                vehicleDataRecorder.setEngineState(EngineState.STOP);
//                collectorItemCallback.onDataItemChange(VehicleDataItem.ENGINE_EVENT, new VehicleDataModel(vehicleDataRecorder));
                break;
            case PT30Events.MOVE:

                Logger.i(Tags.ECM_PT30, "event type: vehicle_move");

                printLogs(request);
//                refreshModel(request);

//                vehicleDataRecorder.setVehicleState(VehicleState.MOVING);
//                collectorItemCallback.onDataItemChange(VehicleDataItem.VEHICLE_STATE, new VehicleDataModel(vehicleDataRecorder));
                break;
            case PT30Events.STOP:

                Logger.i(Tags.ECM_PT30, "event type: vehicle_stop");

                printLogs(request);
//                refreshModel(request);

//                vehicleDataRecorder.setVehicleState(VehicleState.STATIC);
//                collectorItemCallback.onDataItemChange(VehicleDataItem.VEHICLE_STATE, new VehicleDataModel(vehicleDataRecorder));
                break;
            default:
                break;
        }
    }

    /**
     * 将BaseRequest转换成为History
     *
     * @param request BaseRequest
     * @return History
     */
    private History convertHistory(BaseRequest request) {

        History result = new History();

        Double odometer = ConvertUtil.km2mile(Double.parseDouble(request.getValue(BaseRequest.Key.ODO)));
        result.setTotalOdometer(odometer);

        Double speed = ConvertUtil.kh2mph(Double.parseDouble(request.getValue(BaseRequest.Key.VELO)));
        result.setSpeed(speed.intValue());

        Double engineHour = Double.parseDouble(request.getValue(BaseRequest.Key.ENG_HOURS));
        result.setTotalEngineHours(engineHour);

        GeolocParam gp = new GeolocParam(request);
        result.setLatitude(String.valueOf(gp.latitude.setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue()));
        result.setLongitude(String.valueOf(gp.longitude.setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue()));

        DateTimeParam dtp = new DateTimeParam(request);
        String dateStr = dtp.date + " " + dtp.time;
        Logger.i(Tags.ECM_PT30, "history dataTime " + dateStr);
        result.setEventTime(TimeUtil.strToDate(dateStr, "yyyyMMdd HHmmss", "UTC").getTime());

        if (request.getValue(BaseRequest.Key.EVENT).equals(PT30Events.ENGINE_ON)) {

            Logger.i(Tags.ECM_PT30, "new history: engine_on");
            result.setType(HistoryType.ENGINE_ON);
        }
        if (request.getValue(BaseRequest.Key.EVENT).equals(PT30Events.ENGINE_OFF)) {

            Logger.i(Tags.ECM_PT30, "new history: engine_off");
            result.setType(HistoryType.ENGINE_OFF);
        }
        if (request.getValue(BaseRequest.Key.EVENT).equals(PT30Events.MOVE)) {

            Logger.i(Tags.ECM_PT30, "new history: move");
            result.setType(HistoryType.VEHICLE_MOVE);
        }
        if (request.getValue(BaseRequest.Key.EVENT).equals(PT30Events.STOP)) {

            Logger.i(Tags.ECM_PT30, "new history: stop");
            result.setType(HistoryType.VEHICLE_STATIC);
        }

        return result;
    }

    /**
     * 刷新本地数据模型
     *
     * @param request BaseRequest
     */
    private void refreshModel(BaseRequest request) {

        double odometer = ConvertUtil.km2mile(Double.parseDouble(request.getValue(BaseRequest.Key.ODO)));
        Logger.i(Tags.ECM_PT30, "odometer = " + odometer);

        VehicleDataModel odometerDataModel = new VehicleDataModel();
        odometerDataModel.setTotalOdometer(odometer);
        vehicleDataRecorder.updateData(VehicleDataItem.ODOMETER, odometerDataModel);
        collectorItemCallback.onDataItemChange(VehicleDataItem.ODOMETER, new VehicleDataModel(vehicleDataRecorder));

        double engineHour = Double.parseDouble(request.getValue(BaseRequest.Key.ENG_HOURS));
        Logger.i(Tags.ECM_PT30, "engineHour = " + engineHour);

        VehicleDataModel engineHourDataModel = new VehicleDataModel();
        engineHourDataModel.setTotalEngineHours(engineHour);
        vehicleDataRecorder.updateData(VehicleDataItem.ENGINE_HOURS, engineHourDataModel);
        collectorItemCallback.onDataItemChange(VehicleDataItem.ENGINE_HOURS, new VehicleDataModel(vehicleDataRecorder));

        double rpm = Double.parseDouble(request.getValue(BaseRequest.Key.RPM));
        Logger.i(Tags.ECM_PT30, "rpm = " + rpm);

        VehicleDataModel rpmDataModel = new VehicleDataModel();
        rpmDataModel.setRpm(rpm);
        vehicleDataRecorder.updateData(VehicleDataItem.RPM, rpmDataModel);
        collectorItemCallback.onDataItemChange(VehicleDataItem.RPM, new VehicleDataModel(vehicleDataRecorder));

        analysisEngine(rpm);

        double speed = ConvertUtil.kh2mph(Double.parseDouble(request.getValue(BaseRequest.Key.VELO)));
        //如果转速为0.但是速度不为0，则认为数据读取错误
        if (((int) (rpm * 1000)) == 0 && ((int) (speed * 1000)) != 0) {

            Logger.w(Tags.ECM_PT30, "speed = " + speed + ", but rpm=0, speed is error. set speed = 0");
            speed = 0;
        }

        Logger.i(Tags.ECM_PT30, "speed = " + speed + " mile/h");

        VehicleDataModel speedDataModel = new VehicleDataModel();
        speedDataModel.setSpeed(speed);
        vehicleDataRecorder.updateData(VehicleDataItem.SPEED, speedDataModel);
        collectorItemCallback.onDataItemChange(VehicleDataItem.SPEED, new VehicleDataModel(vehicleDataRecorder));

        analysisVehicleState(speed);

        GeolocParam gp = new GeolocParam(request);
        Logger.i(Tags.ECM_PT30, "lat = " + gp.latitude + ", lng = " + gp.longitude);

        VehicleDataModel gpsDataModel = new VehicleDataModel();
        gpsDataModel.setLatitude(gp.latitude.doubleValue());
        gpsDataModel.setLongitude(gp.longitude.doubleValue());
        vehicleDataRecorder.updateData(VehicleDataItem.GPS, gpsDataModel);
    }

    /**
     * 输出日志
     *
     * @param request BaseRequest
     */
    private void printLogs(BaseRequest request) {

        double odometer = ConvertUtil.km2mile(Double.parseDouble(request.getValue(BaseRequest.Key.VELO)));
        Logger.i(Tags.ECM_PT30, "odometer = " + odometer);

        double engineHour = Double.parseDouble(request.getValue(BaseRequest.Key.ENG_HOURS));
        Logger.i(Tags.ECM_PT30, "engineHour = " + engineHour);

        double rpm = Double.parseDouble(request.getValue(BaseRequest.Key.RPM));
        Logger.i(Tags.ECM_PT30, "rpm = " + rpm);

        double speed = ConvertUtil.kh2mph(Double.parseDouble(request.getValue(BaseRequest.Key.VELO)));
        //如果转速为0.但是速度不为0，则认为数据读取错误
        if (((int) (rpm * 1000)) == 0 && ((int) (speed * 1000)) != 0) {

            Logger.w(Tags.ECM_PT30, "speed = " + speed + ", but rpm = 0, speed is error. set speed = 0");
            speed = 0;
        }

        Logger.i(Tags.ECM_PT30, "speed = " + speed + " mile/h");

        GeolocParam gp = new GeolocParam(request);
        Logger.i(Tags.ECM_PT30, "lat = " + gp.latitude + ", lng = " + gp.longitude);
    }
}
