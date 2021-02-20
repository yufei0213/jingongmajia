package com.unitedbustech.eld.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.unitedbustech.eld.App;
import com.unitedbustech.eld.logs.Logger;

/**
 * @author yufei0213
 * @date 2018/1/5
 * @description BLE处理类
 */
public class BluetoothHandler implements BluetoothAdapter.LeScanCallback {

    private static final String TAG = "BluetoothHandler";

    /**
     * 蓝牙扫描周期
     */
    private static final long SCAN_PERIOD = 5 * 1000;

    /**
     * 蓝牙扫描回调
     */
    private BluetoothScanCallback bluetoothLeScanCallback;

    private Handler handler;

    private Runnable scanRunnable;

    private BluetoothAdapter bluetoothAdapter;

    private static BluetoothHandler instance = null;

    private BluetoothHandler() {

        handler = new Handler();
    }

    /**
     * 获取单例引用
     *
     * @return BluetoothHandler
     */
    public static BluetoothHandler getInstance() {

        if (instance == null) {

            instance = new BluetoothHandler();
        }

        return instance;
    }

    /**
     * 初始化
     * app启动时执行
     */
    public void init() {

        if (bluetoothAdapter == null) {

            final BluetoothManager bluetoothManager = (BluetoothManager) App.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (bluetoothAdapter != null) {

            if (!bluetoothAdapter.isEnabled()) {

                //如果蓝牙未打开，自动开启蓝牙
                bluetoothAdapter.enable();
            }
        }
    }

    /**
     * app退出时调用
     */
    public void destroy() {

        try {

            if (scanRunnable != null) {

                stopScan();
            }

            handler = null;
            bluetoothAdapter = null;
        } catch (Exception e) {

            e.printStackTrace();
        }finally {

            instance = null;
        }
    }

    /**
     * 判断蓝牙是否可用
     *
     * @return 蓝牙是否可用
     */
    public boolean isEnable() {

        if (bluetoothAdapter != null) {

            return bluetoothAdapter.isEnabled();
        } else {

            return false;
        }
    }

    /**
     * 扫描蓝牙设备
     *
     * @param callback 回调
     */
    public void startScan(@Nullable BluetoothScanCallback callback) {

        if (bluetoothAdapter == null || scanRunnable != null) {

            return;
        }

        this.bluetoothLeScanCallback = callback;

        scanRunnable = new Runnable() {
            @Override
            public void run() {

                scanRunnable = null;
                bluetoothAdapter.stopLeScan(BluetoothHandler.getInstance());

                if (bluetoothLeScanCallback != null) {

                    bluetoothLeScanCallback.onFinish();
                }
            }
        };

        handler.postDelayed(scanRunnable, SCAN_PERIOD);

        bluetoothAdapter.startLeScan(BluetoothHandler.getInstance());
    }

    /**
     * 停止扫描
     */
    public void stopScan() {

        if (scanRunnable != null) {

            if (handler != null) {

                handler.removeCallbacks(scanRunnable);
            }

            scanRunnable = null;
            if (bluetoothAdapter != null) {

                bluetoothAdapter.stopLeScan(BluetoothHandler.getInstance());
            }

            bluetoothLeScanCallback = null;
        }
    }

    /**
     * 根据address获取蓝牙设备
     *
     * @param address 蓝牙地址
     * @return
     */
    public BluetoothDevice getRemoteDevice(@NonNull String address) {

        if (TextUtils.isEmpty(address)) {

            return null;
        } else {

            return bluetoothAdapter.getRemoteDevice(address);
        }
    }

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {

        Logger.d(TAG, bluetoothDevice.getName() + ", " + bluetoothDevice.getAddress());

        if (bluetoothLeScanCallback != null) {

            bluetoothLeScanCallback.onLeScan(bluetoothDevice);
        }
    }
}
