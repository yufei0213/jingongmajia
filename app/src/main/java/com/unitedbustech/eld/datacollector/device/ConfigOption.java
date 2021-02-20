package com.unitedbustech.eld.datacollector.device;

import com.unitedbustech.eld.datacollector.common.DeviceType;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;

/**
 * @author yufei0213
 * @date 2018/1/10
 * @description 硬件配置项
 */
public class ConfigOption {

    /**
     * 车号
     */
    private String vehicleNumber;

    /**
     * 蓝牙名
     */
    private String deviceName;

    /**
     * 蓝牙地址
     */
    private String deviceAddress;

    /**
     * 设备类型
     */
    private DeviceType deviceType;

    private ConfigOption(Builder builder) {

        this.vehicleNumber = builder.vehicleNumber;
        this.deviceName = builder.bluetoothName;
        this.deviceAddress = builder.bluetoothAddress;
        DeviceType deviceType = builder.deviceType;
        if (deviceType == null) {

            //默认是BlueLink
            this.deviceType = DeviceType.DEVICE_TYPE_BLUELINK;
        } else {

            this.deviceType = builder.deviceType;
        }
    }

    public String getVehicleNumber() {

        return vehicleNumber;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    /**
     * 构建器
     */
    public static class Builder {

        private String vehicleNumber;

        private String bluetoothName;

        private String bluetoothAddress;

        private DeviceType deviceType;

        public Builder() {
        }

        /**
         * 配置车号
         *
         * @param vehicleNumber 车号
         * @return Builder
         */
        public Builder vehicleNumber(String vehicleNumber) {

            //全部转换为小写
            vehicleNumber = vehicleNumber.toLowerCase();

            this.vehicleNumber = vehicleNumber;
            return this;
        }

        /**
         * 配置蓝牙名
         *
         * @param bluetoothName 蓝牙名字
         * @return Builder
         */
        public Builder bluetoothName(String bluetoothName) {

            this.bluetoothName = bluetoothName;
            return this;
        }

        /**
         * 配置蓝牙地址
         *
         * @param bluetoothAddress 蓝牙地址
         * @return Builder
         */
        public Builder bluetoothAddress(String bluetoothAddress) {

            this.bluetoothAddress = bluetoothAddress;
            return this;
        }

        /**
         * 配置设备类型
         *
         * @param deviceType 设备类型
         * @return Builder
         */
        public Builder deviceType(DeviceType deviceType) {

            this.deviceType = deviceType;
            return this;
        }

        /**
         * 构建ConfigOption
         *
         * @return ConfigOption
         */
        public ConfigOption build() {

            return new ConfigOption(this);
        }
    }

    public static DeviceType getDeviceTypeByEcmLinkType(String linkType) {

        try {

            int type = Integer.valueOf(linkType);
            for (DeviceType deviceType : DeviceType.values()) {

                if (type == deviceType.getCode()) {

                    return deviceType;
                }
            }
        } catch (NumberFormatException e) {

            //防止出现数字类型异常，例如服务端返回字段类型发生变化
            Logger.w(Tags.ECM, "Server EcmLinkType Param Error:" + linkType);
        }

        //如果识别不了，默认BlueLink
        return DeviceType.DEVICE_TYPE_BLUELINK;
    }
}
