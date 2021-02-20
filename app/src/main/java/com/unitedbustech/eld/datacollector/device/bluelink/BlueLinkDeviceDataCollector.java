package com.unitedbustech.eld.datacollector.device.bluelink;

import android.bluetooth.BluetoothDevice;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.unitedbustech.eld.bluetooth.BluetoothHandler;
import com.unitedbustech.eld.bluetooth.BluetoothScanCallback;
import com.unitedbustech.eld.common.SelfCheckEventType;
import com.unitedbustech.eld.common.VehicleDataItem;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.datacollector.ConnectVehicleCallback;
import com.unitedbustech.eld.datacollector.common.CollectorType;
import com.unitedbustech.eld.datacollector.common.History;
import com.unitedbustech.eld.datacollector.device.ConfigOption;
import com.unitedbustech.eld.datacollector.device.DeviceDataCollector;
import com.unitedbustech.eld.datacollector.device.analyer.DeviceAnalyser;
import com.unitedbustech.eld.datacollector.device.analyer.DeviceAnalyserCallback;
import com.unitedbustech.eld.datacollector.device.analyer.ReadHistoryCallback;
import com.unitedbustech.eld.datacollector.device.connector.DeviceConnector;
import com.unitedbustech.eld.datacollector.device.connector.DeviceConnectorCallback;
import com.unitedbustech.eld.eventbus.SelfCheckEvent;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.DataCacheService;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author yufei0213
 * @date 2018/1/10
 * @description 设备数据中心，用于从设备中读取数据
 */
public class BlueLinkDeviceDataCollector extends DeviceDataCollector implements DeviceConnectorCallback, DeviceAnalyserCallback {

    private static final String TAG = "BlueLinkDeviceDataCollector";

    /**
     * BlueLink 蓝牙名前缀
     */
    private static final String BLUE_LINK_NAME_PREFIX = "BlueLink-";

    /**
     * 读取历史记录前，读取数据的uuid
     */
    private String analysisUuidBeforeHistory;

    /**
     * 分析器
     */
    private DeviceAnalyser deviceAnalyser;

    /**
     * 连接器
     */
    private DeviceConnector deviceConnector;

    /**
     * 车辆连接回调函数
     */
    private ConnectVehicleCallback connectVehicleCallback;

    public BlueLinkDeviceDataCollector() {

        super();
    }

    public void start(@NonNull final ConfigOption option, @Nullable ConnectVehicleCallback connectVehicleCallback) {

        if (option == null) {

            throw new RuntimeException("ConfigOption can't be null");
        }

        this.connectVehicleCallback = connectVehicleCallback;

        final BluetoothHandler bluetoothHandler = BluetoothHandler.getInstance();
        if (bluetoothHandler.isEnable()) {

            final Map<String, BluetoothDevice> bluetoothDeviceMap = new HashMap<>();

            Logger.i(Tags.ECM, "start scan ble device");
            bluetoothDeviceMap.clear();
            bluetoothHandler.startScan(new BluetoothScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice bluetoothDevice) {

                    if (!bluetoothDeviceMap.containsKey(bluetoothDevice.getName())) {

                        bluetoothDeviceMap.put(bluetoothDevice.getName(), bluetoothDevice);
                    }

                    //找到蓝牙后直接连接
                    if (validBluetoothDevice(bluetoothDevice, option.getVehicleNumber())) {

                        Logger.i(Tags.ECM, "found ble device, stop scan");
                        bluetoothHandler.stopScan();

                        startConnect(bluetoothDevice, option);
                    }
                }

                @Override
                public void onFinish() {

                    Logger.i(Tags.ECM, "scan finished");

                    //1、查找包含车号的蓝牙
                    BluetoothDevice bluetoothDevice = null;
                    for (Map.Entry<String, BluetoothDevice> item : bluetoothDeviceMap.entrySet()) {

                        BluetoothDevice temp = item.getValue();
                        if (validBluetoothDevice(temp, option.getVehicleNumber())) {

                            bluetoothDevice = temp;
                            break;
                        }
                    }

                    //2、找到蓝牙，构建Connector的config
                    if (bluetoothDevice != null) {

                        Logger.i(Tags.ECM, "found ble device");
                        startConnect(bluetoothDevice, option);
                    } else {

                        Logger.w(Tags.ECM, "not found ble device");

                        if (BlueLinkDeviceDataCollector.this.connectVehicleCallback != null) {

                            BlueLinkDeviceDataCollector.this.connectVehicleCallback.connectFailed();
                            BlueLinkDeviceDataCollector.this.connectVehicleCallback = null;
                        }

                        collectorItemCallback.onItemStateChange(false, CollectorType.DEVICE);
                    }
                }
            });
        } else {

            Logger.w(Tags.ECM, "Bluetooth is not open");

            if (BlueLinkDeviceDataCollector.this.connectVehicleCallback != null) {

                BlueLinkDeviceDataCollector.this.connectVehicleCallback.connectFailed();
                BlueLinkDeviceDataCollector.this.connectVehicleCallback = null;
            }

            collectorItemCallback.onItemStateChange(false, CollectorType.DEVICE);
        }
    }

    public void stop() {

        try {

            stopScheduleCallback();
            stopReadFromDevice();

            //如果正在扫描则停止扫描
            BluetoothHandler.getInstance().stopScan();
            //如果已经连接则断开连接
            if (deviceConnector != null) {

                deviceConnector.disconnect();
            }

            deviceConnector = null;
            deviceAnalyser = null;
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 验证蓝牙设备是否是目标设备
     *
     * @param bluetoothDevice 蓝牙设备
     * @param vehicleCode     车号
     * @return 是否是目标车
     */
    private boolean validBluetoothDevice(BluetoothDevice bluetoothDevice, String vehicleCode) {

        return !TextUtils.isEmpty(bluetoothDevice.getName()) && bluetoothDevice.getName().equals(BLUE_LINK_NAME_PREFIX + vehicleCode);
    }

    /**
     * 尝试连接蓝牙
     *
     * @param bluetoothDevice 蓝牙设备
     * @param option          配置信息
     */
    private void startConnect(BluetoothDevice bluetoothDevice, @NonNull ConfigOption option) {

        Logger.i(Tags.ECM, "start connect ble=" + bluetoothDevice.getAddress() + ", vehicleCode=" + option.getVehicleNumber());

        ConfigOption configOption = new ConfigOption.Builder()
                .bluetoothName(bluetoothDevice.getName())
                .bluetoothAddress(bluetoothDevice.getAddress())
                .vehicleNumber(option.getVehicleNumber())
                .build();

        BlueLinkAnalyser blueLinkAnalyser = new BlueLinkAnalyser();
        blueLinkAnalyser.init(configOption);

        deviceConnector = blueLinkAnalyser;
        deviceAnalyser = blueLinkAnalyser;

        deviceConnector.setCallback(this);
        deviceAnalyser.setCallback(this);

        Logger.i(Tags.ECM, "connecting");
        deviceConnector.connect();
    }

    /**
     * 读取某数据
     *
     * @param item 数据项
     */
    public VehicleDataModel read(@NonNull VehicleDataItem item) {

        if (item == null) {

            throw new RuntimeException("BlueLinkDeviceDataCollector read needs a VehicleDataItem");
        }

        return new VehicleDataModel(vehicleDataRecorder);
    }

    @Override
    public void onConnectionStateChange(boolean state) {

        //此处的回调需要首先调用
        collectorItemCallback.onItemStateChange(state, CollectorType.DEVICE);

        if (connectVehicleCallback != null) {

            if (state) {

                connectVehicleCallback.connectSuccess();
                connectVehicleCallback = null;

                readBaseInfo();
            } else {

                stop();
                connectVehicleCallback.connectFailed();
                connectVehicleCallback = null;
            }
        } else {

            if (state) {

                startReadFromDevice();
            } else {

                stop();
            }
        }
    }

    @Override
    public void onAnalysisFinish(@NonNull VehicleDataItem item, @Nullable VehicleDataModel model, boolean state) {

        //如果没有车辆信息，则不处理;
        if (vehicleDataRecorder.getVehicleId() == 0) {

            return;
        }

        if (state) {

            vehicleDataRecorder.updateData(item, model);
            collectorItemCallback.onDataItemChange(item, new VehicleDataModel(vehicleDataRecorder));
        } else {

            if (item == VehicleDataItem.GPS) {

                Location location = LocationHandler.getInstance().getCurrentLocation();
                if (location != null) {

                    model.setLatitude(location.getLatitude());
                    model.setLongitude(location.getLongitude());

                    vehicleDataRecorder.updateData(item, model);
                    collectorItemCallback.onDataItemChange(item, new VehicleDataModel(vehicleDataRecorder));
                }
            }
        }

        switch (item) {

            case ENGINE_STATE:
            case VEHICLE_STATE:
            case ENGINE_HOURS:
            case ODOMETER:

                EventBus.getDefault().post(new SelfCheckEvent(SelfCheckEventType.ECM_DATA, state));
            default:
                break;
        }
    }

    @Override
    public void onCyclicReadingFinish(String uuid) {

        if (!TextUtils.isEmpty(uuid) && uuid.equals(analysisUuidBeforeHistory)) {

            final long startTime = new Date().getTime();
            deviceAnalyser.readHistory(new ReadHistoryCallback() {
                @Override
                public void onReadFinish(List<History> history) {

                    startReadFromDevice();

                    DataCacheService.getInstance().handleUnidentifiedLogs(history, startTime, 1);
                }
            });
        }
    }

    /**
     * 读取History前，读取基本信息
     */
    private void readBaseInfo() {

        analysisUuidBeforeHistory = UUID.randomUUID().toString();
        deviceAnalyser.analysis(false, analysisUuidBeforeHistory,
                VehicleDataItem.ENGINE_STATE,
                VehicleDataItem.VEHICLE_STATE,
                VehicleDataItem.ENGINE_HOURS,
                VehicleDataItem.ODOMETER,
                VehicleDataItem.SPEED,
                VehicleDataItem.GPS,
                VehicleDataItem.RPM,
                VehicleDataItem.VIN);

        startScheduleCallback();
    }

    /**
     * 开始与设备通信
     */
    private void startReadFromDevice() {

        deviceAnalyser.analysis(true, null,
                VehicleDataItem.ENGINE_STATE,
                VehicleDataItem.VEHICLE_STATE,
                VehicleDataItem.ODOMETER,
                VehicleDataItem.ENGINE_HOURS,
                VehicleDataItem.SPEED,
                VehicleDataItem.GPS,
                VehicleDataItem.RPM,
                VehicleDataItem.VIN,
                VehicleDataItem.HISTORY);

        startScheduleCallback();
    }

    /**
     * 停止与设备通信
     */
    private void stopReadFromDevice() {

        stopScheduleCallback();
    }
}
