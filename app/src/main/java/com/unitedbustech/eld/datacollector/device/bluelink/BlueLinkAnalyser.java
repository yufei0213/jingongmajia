package com.unitedbustech.eld.datacollector.device.bluelink;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.unitedbustech.eld.App;
import com.unitedbustech.eld.bluetooth.BluetoothHandler;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.EngineEvent;
import com.unitedbustech.eld.common.EngineState;
import com.unitedbustech.eld.common.VehicleDataItem;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.common.VehicleState;
import com.unitedbustech.eld.datacollector.common.History;
import com.unitedbustech.eld.datacollector.device.ConfigOption;
import com.unitedbustech.eld.datacollector.device.analyer.DeviceAnalyser;
import com.unitedbustech.eld.datacollector.device.analyer.DeviceAnalyserCallback;
import com.unitedbustech.eld.datacollector.device.analyer.ReadHistoryCallback;
import com.unitedbustech.eld.datacollector.device.bluelink.analysis.AnalyserFactory;
import com.unitedbustech.eld.datacollector.device.bluelink.analysis.ReadDataKey;
import com.unitedbustech.eld.datacollector.device.bluelink.uuid.ConfigUUID;
import com.unitedbustech.eld.datacollector.device.bluelink.uuid.DataUUID;
import com.unitedbustech.eld.datacollector.device.bluelink.uuid.ServiceUUID;
import com.unitedbustech.eld.datacollector.device.connector.DeviceConnector;
import com.unitedbustech.eld.datacollector.device.connector.DeviceConnectorCallback;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.util.ConvertUtil;
import com.unitedbustech.eld.util.HexUtil;
import com.unitedbustech.eld.util.JsonUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author yufei0213
 * @date 2018/1/10
 * @description BlueLink 分析器，兼连接器
 */
public class BlueLinkAnalyser extends BluetoothGattCallback implements DeviceAnalyser, DeviceConnector {

    private static final String TAG = "BlueLinkAnalyser";

    /**
     * 操作的间隔时间
     */
    private static final long OPERATION_DURATION = 500L;

    /**
     * 读数据时间间隔
     */
    private static final long READ_DURATION = 10 * 1000L;

    /**
     * 蓝牙的GATT
     */
    private BluetoothGatt bluetoothGatt;
    /**
     * 数据读取的 Service
     */
    private BluetoothGattService bluetoothDataGattService;
    /**
     * 配置的Service
     */
    private BluetoothGattService bluetoothConfigGattService;

    /**
     * 初始化工作是否结束
     */
    private boolean hasInitComplete;
    /**
     * 读取操作的时间戳
     */
    private long readOperationTime;
    /**
     * 分析器基本配置
     */
    private ConfigOption configOption;
    /**
     * 链接回调
     */
    private DeviceConnectorCallback deviceConnectorCallback;
    /**
     * 分析结果回调
     */
    private DeviceAnalyserCallback deviceAnalyserCallback;

    /**
     * 存储历史记录
     */
    private List<History> historyList;
    /**
     * BlueLink历史记录读取回调
     * 刚连接上车时使用，使用完毕后置null
     */
    private ReadHistoryCallback readHistoryCallback;

    /**
     * 是否循环读取
     */
    private boolean isCyclicReading;
    /**
     * 当前正在读取的某次操作
     */
    private String cyclicUuid;
    /**
     * 需要读取的数据列表
     * 每次调用analysis方法时刷新
     */
    private List<DataUUID> vehicleDataItemList;
    /**
     * 等待读取的数据列表
     * 数据来源于DataUUID，每读取一项从该列表总删除一项
     */
    private List<DataUUID> needReadVehicleDataItemList;

    /**
     * 处理各类消息
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case MsgType.CONNECTED:

                    if (deviceConnectorCallback != null) {

                        deviceConnectorCallback.onConnectionStateChange(true);
                    }

                    Message msg1 = new Message();
                    msg1.what = MsgType.SET_UTC_TIME;
                    handler.sendMessageDelayed(msg1, OPERATION_DURATION);
                    break;
                case MsgType.DIS_CONNECTED:

                    if (deviceConnectorCallback != null) {

                        deviceConnectorCallback.onConnectionStateChange(false);
                    }

                    disconnect();
                    break;
                case MsgType.DISCOVER_SERVICE:

                    bluetoothGatt.discoverServices();
                    break;
                case MsgType.SET_UTC_TIME:

                    setUtcTime();
                    break;
                case MsgType.SET_NOTIFY:

                    setNotify();
                    break;
                case MsgType.READ_DATA:

                    //如果还没有初始化完成，或者距离上一侧读取操作时间不超过OPERATION_DURATION，则将本次读取操作延迟
                    if (!hasInitComplete || (new Date().getTime() - readOperationTime < OPERATION_DURATION)) {

                        //移除全部读取操作的消息，发送新的消息
                        handler.removeMessages(MsgType.READ_DATA);
                        Message msg2 = new Message();
                        msg2.what = MsgType.READ_DATA;
                        handler.sendMessageDelayed(msg2, OPERATION_DURATION);
                        return;
                    }

                    if (readHistoryCallback != null) {

                        readOperationTime = new Date().getTime();
                        read(DataUUID.HISTORY);
                    } else {

                        if (!needReadVehicleDataItemList.isEmpty()) {

                            DataUUID dataUUID = needReadVehicleDataItemList.get(0);
                            needReadVehicleDataItemList.remove(0);

                            readOperationTime = new Date().getTime();
                            read(dataUUID);
                        } else {

                            if (!isCyclicReading) {

                                vehicleDataItemList.clear();
                                if (deviceAnalyserCallback != null)
                                    deviceAnalyserCallback.onCyclicReadingFinish(cyclicUuid);
                            } else {

                                if (!vehicleDataItemList.isEmpty()) {

                                    needReadVehicleDataItemList.addAll(vehicleDataItemList);
                                    DataUUID dataUUID = needReadVehicleDataItemList.get(0);
                                    needReadVehicleDataItemList.remove(0);

                                    readOperationTime = new Date().getTime();
                                    read(dataUUID);
                                }
                            }
                        }
                    }
                    break;
                case MsgType.INIT_COMPLETE:

                    hasInitComplete = true;
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 蓝牙回调匿名类，处理与蓝牙通讯相关的操作
     */
    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            Message msg = new Message();
            msg.what = newState == BluetoothProfile.STATE_CONNECTED ? MsgType.DISCOVER_SERVICE : MsgType.DIS_CONNECTED;
            handler.sendMessageDelayed(msg, OPERATION_DURATION);

            if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                gatt.close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            bluetoothDataGattService = gatt.getService(UUID.fromString(ServiceUUID.DATA.getUuid()));
            bluetoothConfigGattService = gatt.getService(UUID.fromString(ServiceUUID.CONFIG.getUuid()));

            Message msg = new Message();
            msg.what = bluetoothDataGattService == null ? MsgType.DIS_CONNECTED : MsgType.CONNECTED;
            handler.sendMessage(msg);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            handleRead(characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            handleNotify(characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            if (characteristic.getUuid().equals(UUID.fromString(ConfigUUID.UTC.getUuid()))) {

                if (status == BluetoothGatt.GATT_SUCCESS) {

                    Logger.d(Tags.ECM, "set utcTime: success.");
                } else {

                    Logger.w(Tags.ECM, "set utcTime: failed.");
                }

                Message msg = new Message();
                msg.what = MsgType.SET_NOTIFY;
                handler.sendMessageDelayed(msg, OPERATION_DURATION);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

            if (descriptor.getUuid().equals(UUID.fromString(ConfigUUID.NOTIFY.getUuid()))) {

                if (status == BluetoothGatt.GATT_SUCCESS) {

                    Logger.d(Tags.ECM, "set notify: success.");
                } else {

                    Logger.w(Tags.ECM, "set notify: failed.");
                }

                Message msg = new Message();
                msg.what = MsgType.INIT_COMPLETE;
                handler.sendMessage(msg);
            }
        }
    };

    @Override
    public void init(ConfigOption configOption) {

        this.configOption = configOption;

        this.vehicleDataItemList = new CopyOnWriteArrayList<>();
        this.needReadVehicleDataItemList = new CopyOnWriteArrayList<>();
    }

    @Override
    public void setCallback(@NonNull DeviceConnectorCallback callback) {

        this.deviceConnectorCallback = callback;
    }

    @Override
    public void setCallback(@NonNull DeviceAnalyserCallback callback) {

        this.deviceAnalyserCallback = callback;
    }

    @Override
    public void connect() {

        //检测参数是否正常
        if (!(configOption != null && !TextUtils.isEmpty(configOption.getDeviceAddress()))) {

            Logger.w(Tags.ECM, "connect: vehicle config is not valid. connect vehicle failed.");
            Message msg = new Message();
            msg.what = MsgType.DIS_CONNECTED;
            handler.sendMessage(msg);
        } else {

            Logger.i(Tags.ECM, "connect：call android api to connect ble.");
            BluetoothDevice device = BluetoothHandler.getInstance().getRemoteDevice(configOption.getDeviceAddress());
            bluetoothGatt = device.connectGatt(App.getContext(), false, bluetoothGattCallback);
        }
    }

    @Override
    public void disconnect() {

        if (bluetoothGatt != null) {

            bluetoothGatt.close();
            bluetoothGatt = null;

            Logger.i(Tags.ECM, "connect: call android api to disconnect ble, release source.");
        }

        bluetoothConfigGattService = null;
        bluetoothDataGattService = null;

        configOption = null;

        deviceConnectorCallback = null;
        deviceAnalyserCallback = null;
        readHistoryCallback = null;

        if (vehicleDataItemList != null) {

            vehicleDataItemList.clear();
        }
        if (needReadVehicleDataItemList != null) {

            needReadVehicleDataItemList.clear();
        }
    }

    @Override
    public void readHistory(@NonNull ReadHistoryCallback readHistoryCallback) {

        this.readHistoryCallback = readHistoryCallback;
        this.historyList = new ArrayList<>();

        Logger.i(Tags.UNIDENTIFIED, "start read history...");

        Message msg = new Message();
        msg.what = MsgType.READ_DATA;
        handler.sendMessage(msg);
    }

    @Override
    public void analysis(boolean isCyclicReading, String uuid, @NonNull VehicleDataItem... items) {

        vehicleDataItemList.clear();

        boolean isReadHistory = false;
        for (VehicleDataItem item : items) {

            if (item == VehicleDataItem.HISTORY) {

                isReadHistory = true;
            }
        }

        vehicleDataItemList.add(DataUUID.COMBINED);
        if (isReadHistory) {

            vehicleDataItemList.add(DataUUID.HISTORY);
        }

        needReadVehicleDataItemList.clear();
        needReadVehicleDataItemList.addAll(vehicleDataItemList);

        if (!needReadVehicleDataItemList.isEmpty()) {

            this.isCyclicReading = isCyclicReading;
            this.cyclicUuid = uuid;

            Message msg = new Message();
            msg.what = MsgType.READ_DATA;
            handler.sendMessage(msg);
        }
    }

    /**
     * 处理读取操作
     *
     * @param characteristic BluetoothGattCharacteristic
     */
    private void handleRead(BluetoothGattCharacteristic characteristic, int status) {

        if (status == BluetoothGatt.GATT_SUCCESS) {

            Logger.d(Tags.ECM, "read result: success");
            if (readHistoryCallback != null) {

                analysisReadHistoryResult(characteristic);
            } else {

                analysisReadResult(characteristic);
            }
        } else {

            Logger.w(Tags.ECM, "read result: failed");
        }

        Message msg = new Message();
        msg.what = MsgType.READ_DATA;
        //如果需要读取未认领日志，则快速读取。未认领日志读取完毕后，放慢读取速度
        handler.sendMessageDelayed(msg, readHistoryCallback != null ? OPERATION_DURATION : READ_DURATION);
    }

    /**
     * 分析历史记录数据
     *
     * @param characteristic BluetoothGattCharacteristic
     */
    private void analysisReadHistoryResult(BluetoothGattCharacteristic characteristic) {

        Map<String, Object> result = AnalyserFactory.getAnalyser(characteristic.getUuid().toString())
                .analysis(HexUtil.bytesToHexString(characteristic.getValue()));

        Object historyObj = result.get(ReadDataKey.hasHistory);
        if ((historyObj == null || !((Boolean) historyObj)) && this.readHistoryCallback != null) {

            Logger.i(Tags.UNIDENTIFIED, "analysis history: read history complete");

            List<History> tempList = new ArrayList<>();
            tempList.addAll(historyList);
            this.readHistoryCallback.onReadFinish(tempList);

            this.readHistoryCallback = null;
            this.historyList.clear();
        } else {

            History history = new History(result);
            historyList.add(history);

            Logger.i(Tags.UNIDENTIFIED, "analysis history: get one history");
        }
    }

    /**
     * 回数的时候，通过该方法分析结果，读取原始结果后，分析需要的值。
     *
     * @param characteristic BluetoothGattCharacteristic
     */
    private void analysisReadResult(BluetoothGattCharacteristic characteristic) {

        Map<String, Object> result = AnalyserFactory.getAnalyser(characteristic.getUuid().toString())
                .analysis(HexUtil.bytesToHexString(characteristic.getValue()));

        VehicleDataModel resultModel = new VehicleDataModel();

        switch (DataUUID.getByUuid(characteristic.getUuid())) {

            case COMBINED: {

                boolean isVehicleMove = false;
                String stateString = (String) result.get(ReadDataKey.state);
                if (TextUtils.isEmpty(stateString)) {

                    Logger.w(Tags.ECM, "analysis read result: get state failed");
                    deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.ENGINE_STATE, resultModel, false);
                    deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.VEHICLE_STATE, resultModel, false);
                } else {

                    if (stateString.equals(State.ON)) {

                        Logger.i(Tags.ECM, "analysis read result: get engine_state: on");
                        resultModel.setEngineState(EngineState.WORK);
                        deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.ENGINE_STATE, resultModel, true);
                    } else if (stateString.equals(State.OFF)) {

                        Logger.i(Tags.ECM, "analysis read result: get engine_state: off");
                        resultModel.setEngineState(EngineState.STOP);
                        deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.ENGINE_STATE, resultModel, true);
                    } else if (stateString.equals(State.MOVE)) {

                        Logger.i(Tags.ECM, "analysis read result: get vehicle_state: move");
                        isVehicleMove = true;
//                        resultModel.setVehicleState(VehicleState.MOVING);
//                        deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.VEHICLE_STATE, resultModel, true);
                    } else if (stateString.equals(State.STOP)) {

                        Logger.i(Tags.ECM, "analysis read result: get vehicle_state: stopped");
                        resultModel.setVehicleState(VehicleState.STATIC);
                        deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.VEHICLE_STATE, resultModel, true);
                    }
                }

                Object odometer = result.get(ReadDataKey.odometer);
                if (odometer == null || ((int) (((Double) odometer) * 1000)) == 0) {

                    Logger.w(Tags.ECM, "analysis read result: get odometer failed");
                    deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.ODOMETER, resultModel, false);
                } else {

                    Logger.d(Tags.ECM, "analysis read result: get odometer: " + odometer);
                    resultModel.setTotalOdometer((Double) odometer);
                    deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.ODOMETER, resultModel, true);
                }

                Object engineHour = result.get(ReadDataKey.engineHours);
                if (engineHour == null || ((int) (((Double) engineHour) * 1000)) == 0) {

                    Logger.w(Tags.ECM, "analysis read result: get engine_hours failed");
                    deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.ENGINE_HOURS, resultModel, false);
                } else {

                    Logger.d(Tags.ECM, "analysis read result: get engine_hours: " + engineHour);
                    resultModel.setTotalEngineHours((Double) engineHour);
                    deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.ENGINE_HOURS, resultModel, true);
                }

                Object lat = result.get(ReadDataKey.latitude);
                Object lng = result.get(ReadDataKey.longitude);
                if (lat == null || lng == null) {

                    Logger.w(Tags.ECM, "analysis read result: get GPS failed");
                    deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.GPS, resultModel, false);
                } else {

                    Logger.d(Tags.ECM, "analysis read result: get GPS: lat=" + lat + ", lng" + lng);
                    resultModel.setLatitude((Double) lat);
                    resultModel.setLongitude((Double) lng);
                    deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.GPS, resultModel, true);
                }

                Object rpm = result.get(ReadDataKey.rpm);
                if (rpm == null) {

                    Logger.w(Tags.ECM, "analysis read result: get rpm failed");
                    deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.RPM, resultModel, false);
                    break;
                } else {

                    Logger.d(Tags.ECM, "analysis read result: get rpm: " + rpm);
                    resultModel.setRpm((Double) rpm);
                    deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.RPM, resultModel, true);
                }

                Object originSpeed = result.get(ReadDataKey.speed);
                if (originSpeed == null) {

                    Logger.w(Tags.ECM, "analysis read result: get speed failed");
                    deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.SPEED, resultModel, false);
                } else {

                    double speed = ConvertUtil.kh2mph((Double) originSpeed);
                    Logger.i(Tags.ECM, "analysis read result: get speed: " + speed + " mile/h");
                    resultModel.setSpeed(speed);
                    deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.SPEED, resultModel, true);

                    //如果车辆状态为Moving并且当前速度大于阈值，则认为车辆状态为移动
                    if (isVehicleMove && speed >= Constants.SPEED_THRESHOLD) {

                        Logger.i(Tags.ECM, "analysis read result: vehicle state is moving and speed is " + speed + " mile/h, so vehicle final state is moving.");
                        resultModel.setVehicleState(VehicleState.MOVING);
                        deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.VEHICLE_STATE, resultModel, true);
                    }
                }
                break;
            }
            case HISTORY: {

                Object historyObj = result.get(ReadDataKey.hasHistory);
                if (historyObj == null || !((Boolean) historyObj)) {

                    Logger.d("analysis read result: normal read, no history");
                } else {

                    String historyStr = JsonUtil.toJSONString(new History(result));
                    Logger.d("analysis read result: normal read, get history: " + historyStr + ", drop it");
                }
                break;
            }
            default:
                break;
        }
    }

    /**
     * 处理通知的回调
     *
     * @param characteristic BluetoothGattCharacteristic
     */
    private void handleNotify(BluetoothGattCharacteristic characteristic) {

        if (characteristic.getUuid().equals(UUID.fromString(DataUUID.STATE.getUuid()))) {

            VehicleDataModel model = new VehicleDataModel();

            Map<String, Object> result = AnalyserFactory.getAnalyser(DataUUID.STATE.getUuid())
                    .analysis(HexUtil.bytesToHexString(characteristic.getValue()));
            String state = (String) result.get(ReadDataKey.state);
            if (state.equals(State.ON)) {

                Logger.i(Tags.ECM, "notify: get engine_event: on");
                model.setEngineEvent(EngineEvent.POWER_ON);
                deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.ENGINE_EVENT, model, true);
            }
            if (state.equals(State.OFF)) {

                Logger.i(Tags.ECM, "notify: get engine_event: off");
                model.setEngineEvent(EngineEvent.POWER_OFF);
                deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.ENGINE_EVENT, model, true);
            }
            if (state.equals(State.MOVE)) {

                Logger.i(Tags.ECM, "notify: get vehicle_event: move");
//                model.setVehicleState(VehicleState.MOVING);
//                deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.VEHICLE_STATE, model, true);
            }
            if (state.equals(State.STOP)) {

                Logger.i(Tags.ECM, "notify: get vehicle_event: stop");
//                model.setVehicleState(VehicleState.STATIC);
//                deviceAnalyserCallback.onAnalysisFinish(VehicleDataItem.VEHICLE_STATE, model, true);
            }
        }
    }

    /**
     * 设置UTC时间
     */
    public void setUtcTime() {

        if (bluetoothConfigGattService == null) {

            Message msg = new Message();
            msg.what = MsgType.INIT_COMPLETE;
            handler.sendMessage(msg);

            Logger.w(Tags.ECM, "set utcTime: ConfigGattService is null, set utcTime failed.");
        } else {

            BluetoothGattCharacteristic characteristic = bluetoothConfigGattService.getCharacteristic(UUID.fromString(ConfigUUID.UTC.getUuid()));
            if (characteristic != null) {

                Date value = new Date();
                long UtcTimeValue = value.getTime() / 1000;
                byte[] sendbuf = new byte[4];
                sendbuf[0] = (byte) (UtcTimeValue & 0xff);
                sendbuf[1] = (byte) ((UtcTimeValue >> 8) & 0xff);
                sendbuf[2] = (byte) ((UtcTimeValue >> 16) & 0xff);
                sendbuf[3] = (byte) ((UtcTimeValue >> 24) & 0xff);
                characteristic.setValue(sendbuf);
                if (!bluetoothGatt.writeCharacteristic(characteristic)) {

                    Message msg = new Message();
                    msg.what = MsgType.SET_NOTIFY;
                    handler.sendMessageDelayed(msg, OPERATION_DURATION);

                    Logger.w(Tags.ECM, "set utcTime: writeCharacteristic failed.");
                }
            } else {

                Logger.w(Tags.ECM, "set utcTime: characteristic is null.");
            }
        }
    }

    /**
     * 开始启用notify
     */
    public void setNotify() {

        if (bluetoothDataGattService == null) {

            Message msg = new Message();
            msg.what = MsgType.INIT_COMPLETE;
            handler.sendMessage(msg);

            Logger.w(Tags.ECM, "set notify: ConfigGattService is null, set notify failed.");
        } else {

            BluetoothGattCharacteristic characteristic = bluetoothDataGattService.getCharacteristic(UUID.fromString(DataUUID.STATE.getUuid()));
            if (characteristic != null) {

                if (bluetoothGatt.setCharacteristicNotification(characteristic, true)) {

                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(ConfigUUID.NOTIFY.getUuid()));
                    if (descriptor != null) {

                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        if (!bluetoothGatt.writeDescriptor(descriptor)) {

                            Message msg = new Message();
                            msg.what = MsgType.INIT_COMPLETE;
                            handler.sendMessage(msg);

                            Logger.w(Tags.ECM, "set notify: writeDescriptor Failure");
                        }
                    } else {

                        Message msg = new Message();
                        msg.what = MsgType.INIT_COMPLETE;
                        handler.sendMessage(msg);

                        Logger.w(Tags.ECM, "set notify: descriptor is null.");
                    }
                } else {

                    Message msg = new Message();
                    msg.what = MsgType.INIT_COMPLETE;
                    handler.sendMessage(msg);

                    Logger.w(Tags.ECM, "set notify: setCharacteristicNotification Failure");
                }
            } else {

                Message msg = new Message();
                msg.what = MsgType.INIT_COMPLETE;
                handler.sendMessage(msg);

                Logger.w(Tags.ECM, "set notify: characteristic is null");
            }
        }
    }

    /**
     * 调用开始读取
     *
     * @param dataUUID DataUUID
     */
    private void read(DataUUID dataUUID) {

        if (bluetoothDataGattService == null || bluetoothGatt == null) {

            Logger.w(Tags.ECM, "read operation: bluetoothDataGattService = null || BluetoothGatt == null");
        } else {

            BluetoothGattCharacteristic characteristic = bluetoothDataGattService.getCharacteristic(UUID.fromString(dataUUID.getUuid()));
            if (!bluetoothGatt.readCharacteristic(characteristic)) {

                Logger.w(Tags.ECM, "read operation: " + dataUUID.name() + " failed");
            } else {

                Logger.d(Tags.ECM, "read operation: " + dataUUID.name() + " success.");
            }
        }
    }
}