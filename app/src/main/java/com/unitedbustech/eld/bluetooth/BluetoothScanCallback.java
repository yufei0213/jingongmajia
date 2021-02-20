package com.unitedbustech.eld.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * @author yufei0213
 * @date 2018/1/5
 * @description BLE扫描回调
 */
public interface BluetoothScanCallback {

    /**
     * BLE扫描结果
     *
     * @param bluetoothDevice BLE设备
     */
    void onLeScan(BluetoothDevice bluetoothDevice);

    /**
     * 扫描结束
     */
    void onFinish();
}
