package com.unitedbustech.eld.datacollector.common;

import android.location.Location;
import android.text.TextUtils;

import com.unitedbustech.eld.common.EngineEvent;
import com.unitedbustech.eld.common.EngineState;
import com.unitedbustech.eld.common.VehicleDataItem;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.common.VehicleState;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.util.ConvertUtil;
import com.unitedbustech.eld.util.CoordinateUtil;
import com.unitedbustech.eld.util.TimeUtil;

import java.util.Date;

/**
 * @author yufei0213
 * @date 2018/1/10
 * @description 车辆信息记录，维护车辆信息
 */
public class VehicleDataRecorder {

    private static final String TAG = "VehicleDataRecorder";

    /**
     * -1代表永不过期
     */
    private static final long FOREVER = -1L;
    /**
     * engineState有效时间
     */
    private static final long ENGINE_STATE_VALID_TIME = 30 * 1000L;
    /**
     * engineEvent有效时间
     */
    private static final long ENGINE_EVENT_VALID_TIME = 2 * 1000L;
    /**
     * vehicleState有效时间
     */
    private static final long VEHICLE_STATE_VALID_TIME = 60 * 1000L;
    /**
     * odometer有效时间
     */
    private static final long ODOMETER_VALID_TIME = FOREVER;
    /**
     * engineHours有效时间
     */
    private static final long ENGINE_HOURS_VALID_TIME = FOREVER;
    /**
     * utcTime有效时间
     */
    private static final long UTC_VALID_TIME = FOREVER;
    /**
     * gps有效时间
     */
    private static final long GPS_VALID_TIME = 10 * 1000L;
    /**
     * vin有效时间
     */
    private static final long VIN_VALID_TIME = FOREVER;
    /**
     * 转速有效时间
     */
    private static final long RPM_VALID_TIME = 30 * 1000L;
    /**
     * speed有效时间
     */
    private static final long SPEED_VALID_TIME = 30 * 1000L;

    /**
     * 车辆id
     */
    private int vehicleId;

    /**
     * 引擎状态
     */
    private int engineState;
    /**
     * 引擎状态更新时间
     */
    private long engineStateUpdateTime;
    /**
     * 引擎事件，打火和熄火
     */
    private int engineEvent;
    /**
     * 引擎事件更新时间
     */
    private long engineEventUpdateTime;
    /**
     * 车辆状态
     */
    private int vehicleState;
    /**
     * 车辆状态更新时间
     */
    private long vehicleStateUpdateTime;

    /**
     * 总里程，单位英里
     */
    private double totalOdometer;
    /**
     * 总里程更新时间
     */
    private long odometerUpdateTime;
    /**
     * 总里程，单位英里
     * 记录引擎打火时读取到的里程，用于计算累计里程
     */
    private double initTotalOdometer;
    /**
     * 累计里程，根据初始里程和当前里程计算得到，单位英里
     */
    private double accumulatedOdometer;
    /**
     * 里程偏移量
     */
    private double offsetOdometer;
    /**
     * 里程偏移量的上报时间
     */
    private String odoOffsetUpdateTime;
    /**
     * 里程偏移量是否生效
     */
    private boolean isOdoOffsetValid;

    /**
     * 总引擎时间，单位小时
     */
    private double totalEngineHours;
    /**
     * 引擎时间更新时间
     */
    private long engineHoursUpdateTime;
    /**
     * 初始总引擎时间，单位小时
     * 引擎打火时读到的引擎时间，用于计算累计引擎时间
     */
    private double initTotalEngineHours;
    /**
     * 累计引擎时间，单位小时
     * 根据初始总引擎时间和当前总引擎时间计算得到
     */
    private double accumulatedEngineHours;

    /**
     * utc时间
     */
    private long utcTime;
    /**
     * utc时间更新时间
     */
    private long utcTimeUpdateTime;

    /**
     * 纬度
     */
    private double latitude;
    /**
     * 经度
     */
    private double longitude;
    /**
     * 距离上次已知地点的距离。
     * 单位：英里（mi）
     */
    private double lastDistance;
    /**
     * gps更新时间
     */
    private long gpsUpdateTime;

    /**
     * Vehicle ID Number
     */
    private String vin;
    /**
     * vin更新时间
     */
    private long vinUpdateTime;

    /**
     * 引擎转速
     */
    private double rpm;
    /**
     * vin更新时间
     */
    private long rpmUpdateTime;

    /**
     * 车速
     */
    private double speed;
    /**
     * vin更新时间
     */
    private long speedUpdateTime;

    public VehicleDataRecorder() {
    }

    /**
     * 某个数据项是否需要读取
     *
     * @param item 数据项
     * @return 是否需要读取
     */
    public boolean isNeedRead(VehicleDataItem item) {

        boolean result = true;

        switch (item) {

            case ENGINE_STATE:

                result = !(this.getEngineState() != 0);
                break;
            case ENGINE_EVENT:

                result = !(this.getEngineEvent() != 0);
                break;
            case VEHICLE_STATE:

                result = !(this.getVehicleState() != 0);
                break;
            case ODOMETER:

                result = !(((int) (this.getTotalOdometer() * 1000)) != 0);
                break;
            case ENGINE_HOURS:

                result = !(((int) (this.getTotalEngineHours() * 1000)) != 0);
                break;
            case UTC_TIME:

                result = !(this.getUtcTime() != 0L);
                break;
            case GPS:

                result = !(((int) (this.getLatitude() * 1000)) != 0 || ((int) (this.getLongitude() * 1000)) != 0);
                break;
            case VIN:

                result = TextUtils.isEmpty(this.getVin());
                break;
            case RPM:

                result = !(((int) (this.getRpm() * 1000)) != 0);
                break;
            case SPEED:

                result = !(((int) (this.getSpeed() * 1000)) != 0);
                break;
            default:
                break;
        }

        return result;
    }

    /**
     * 刷新数据
     *
     * @param item  数据类型
     * @param model VehicleDataRecorder
     */
    public void updateData(VehicleDataItem item, VehicleDataModel model) {

        switch (item) {

            case ENGINE_STATE:

                this.setEngineState(model.getEngineState());
                break;
            case ENGINE_EVENT:

                this.setEngineEvent(model.getEngineEvent());
                break;
            case VEHICLE_STATE:

                this.setVehicleState(model.getVehicleState());
                break;
            case ODOMETER:

                this.setTotalOdometer(model.getTotalOdometer());
                break;
            case ENGINE_HOURS:

                this.setTotalEngineHours(model.getTotalEngineHours());
                break;
            case UTC_TIME:

                this.setUtcTime(model.getUtcTime());
                break;
            case GPS:

                setLastDistance(model.getLatitude(), model.getLongitude());
                this.setLatitude(model.getLatitude());
                this.setLongitude(model.getLongitude());
                break;
            case VIN:

                this.setVin(model.getVin());
                break;
            case RPM:

                this.setRpm(model.getRpm());
                break;
            case SPEED:

                this.setSpeed(model.getSpeed());
                break;
            default:
                break;
        }
    }

    /**
     * 清除车辆选择时，应该清空数据
     */
    public void clear() {

        this.vehicleId = 0;

        this.engineState = 0;
        this.engineEvent = 0;

        this.vehicleState = 0;

        this.totalOdometer = 0;
        this.initTotalOdometer = 0;
        this.accumulatedOdometer = 0;
        this.offsetOdometer = 0;

        this.totalEngineHours = 0;
        this.initTotalEngineHours = 0;
        this.accumulatedEngineHours = 0;

        this.latitude = 0;
        this.longitude = 0;

        this.utcTime = 0;
        this.vin = "";

        this.rpm = 0;
        this.speed = 0;

        this.engineStateUpdateTime = 0;
        this.engineEventUpdateTime = 0;

        this.vehicleStateUpdateTime = 0;

        this.odometerUpdateTime = 0;
        this.engineHoursUpdateTime = 0;

        this.gpsUpdateTime = 0;

        this.utcTimeUpdateTime = 0;
        this.vehicleStateUpdateTime = 0;

        this.rpmUpdateTime = 0;
        this.speedUpdateTime = 0;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getEngineState() {

        if (ENGINE_STATE_VALID_TIME != FOREVER && ENGINE_STATE_VALID_TIME < new Date().getTime() - engineStateUpdateTime) {

            this.engineState = 0;
        }

        return this.engineState;
    }

    public void setEngineState(int engineState) {

        this.engineState = engineState;
        this.engineStateUpdateTime = new Date().getTime();

        //如果车辆熄火，则认为车辆静止
        if (engineState == EngineState.STOP) {

            this.setVehicleState(VehicleState.STATIC);
        }
    }

    public int getEngineEvent() {

        if (ENGINE_EVENT_VALID_TIME != FOREVER && ENGINE_EVENT_VALID_TIME < new Date().getTime() - engineEventUpdateTime) {

            this.engineEvent = 0;
        }

        return engineEvent;
    }

    public void setEngineEvent(int engineEvent) {

        //0为无效状态
        if (engineEvent == 0) {

            Logger.d(TAG, "无效的engine_event");
            return;
        }
        //如果新事件与当前事件相同，则视为重复
        if (this.getEngineEvent() == engineEvent) {

            Logger.d(TAG, "重复的engine_event, code=" + engineEvent);
            return;
        }
        this.engineEvent = engineEvent;
        this.engineEventUpdateTime = new Date().getTime();

        if (engineEvent == EngineEvent.POWER_ON) {  //打火事件，累计里程和累计小时数开始初始化

            if (!isNeedRead(VehicleDataItem.ODOMETER) && ((int) (this.initTotalOdometer * 1000)) == 0) {

                this.initTotalOdometer = this.totalOdometer;
            }
            if (!isNeedRead(VehicleDataItem.ENGINE_HOURS) && ((int) (this.initTotalEngineHours * 1000)) == 0) {

                this.initTotalEngineHours = this.totalEngineHours;
            }
        } else if (engineEvent == EngineEvent.POWER_OFF) {  //熄火事件，累计里程和累计小时数统计停止

            this.initTotalOdometer = 0D;
            this.accumulatedOdometer = 0D;

            this.initTotalEngineHours = 0D;
            this.accumulatedEngineHours = 0D;
        }
    }

    public int getVehicleState() {

        if (VEHICLE_STATE_VALID_TIME != FOREVER && VEHICLE_STATE_VALID_TIME < new Date().getTime() - vehicleStateUpdateTime) {

            this.vehicleState = 0;
        }

        //如果不知道当前车辆状态，则通过GPS获取当前速度。如果速度小于1，则认为车辆静止；如果速度大于10mph则认为车辆移动
        if (this.vehicleState == VehicleState.UNKNOWN) {

            Location location = LocationHandler.getInstance().getCurrentLocation();
            if (location != null) {

                if (location.getSpeed() < 1f) {

                    this.vehicleState = VehicleState.STATIC;
                    this.vehicleStateUpdateTime = new Date().getTime();
                }
            }
        }

        return this.vehicleState;
    }

    public void setVehicleState(int vehicleState) {

        if (vehicleState == 0) {

            return;
        }

        this.vehicleState = vehicleState;
        this.vehicleStateUpdateTime = new Date().getTime();
    }

    public double getTotalOdometer() {

        if (ODOMETER_VALID_TIME != FOREVER && ODOMETER_VALID_TIME < new Date().getTime() - odometerUpdateTime) {

            this.totalOdometer = 0D;
        }

        return this.totalOdometer;
    }

    public void setTotalOdometer(final double totalOdometer) {

        //判断时效性,offsetOdometer每日0时生效
        if (TimeUtil.compareOdoOffsetUpdateTime(this.odoOffsetUpdateTime)) {

            this.totalOdometer = totalOdometer + this.offsetOdometer;
            //理论上不会为负数
            this.totalOdometer = this.totalOdometer <= 0 ? totalOdometer : this.totalOdometer;
            this.isOdoOffsetValid = true;
        } else {

            this.totalOdometer = totalOdometer;
            this.isOdoOffsetValid = false;
        }
        this.odometerUpdateTime = new Date().getTime();

        //更新累计里程
        if (this.getEngineState() == EngineState.WORK) {

            if (((int) (this.initTotalOdometer * 1000)) == 0) {

                this.initTotalOdometer = totalOdometer;
            }

            this.accumulatedOdometer = this.totalOdometer - this.initTotalOdometer;
        }
    }

    public double getAccumulatedOdometer() {

        return ConvertUtil.decimal2Point(this.accumulatedOdometer);
    }

    public double getOffsetOdometer() {
        return offsetOdometer;
    }

    public void setOffsetOdometer(double offsetOdometer) {
        this.offsetOdometer = offsetOdometer;
    }

    public String getOdoOffsetUpdateTime() {
        return odoOffsetUpdateTime;
    }

    public void setOdoOffsetUpdateTime(String odoOffsetUpdateTime) {
        this.odoOffsetUpdateTime = odoOffsetUpdateTime;
    }

    public boolean isOdoOffsetValid() {
        return isOdoOffsetValid;
    }

    public void setIsOdoOffsetValid(boolean valid) {
        isOdoOffsetValid = valid;
    }

    public double getTotalEngineHours() {

        if (ENGINE_HOURS_VALID_TIME != FOREVER && ENGINE_HOURS_VALID_TIME < new Date().getTime() - engineHoursUpdateTime) {

            this.totalEngineHours = 0D;
        }

        return this.totalEngineHours;
    }

    /**
     * 当gps数据更新时，更新数据的lastdistance字段.
     * 本方法为私有方法，只有在模型内部可以更新。
     *
     * @param newLat 新的纬度。
     * @param newLng 新的经度。
     */
    private void setLastDistance(double newLat, double newLng) {

        //判断新的位置和老的位置都是正确的经纬度位置。
        //判断方式是：经度和纬度的绝对值大小均大于double类型的0.01。
        if (Math.abs(this.latitude) > 0.01D && Math.abs(this.longitude) > 0.01D && Math.abs(newLat) > 0.01D && Math.abs(newLng) > 0.01D) {

            double tempDistance = ConvertUtil.m2mile(CoordinateUtil.getDistance(this.latitude, this.longitude, newLat, newLng));
            if (tempDistance > 6D) {
                //dot规定，lastDistance不得大于6
                tempDistance = 6D;
            }
            this.lastDistance = tempDistance;
            Logger.d(TAG, "set new last distance! value:[" + this.lastDistance + "]");
        } else {//一旦数据从有效变为无效数据。将lastdistance置为0

            Logger.d(TAG, "last distance set to value [0]");
            this.lastDistance = 0D;
        }
    }

    public double getLastDistance() {
        return lastDistance;
    }

    public void setTotalEngineHours(double totalEngineHours) {

        this.totalEngineHours = totalEngineHours;
        this.engineHoursUpdateTime = new Date().getTime();

        //更新累计里程
        if (this.getEngineState() == EngineState.WORK) {

            if (((int) (this.initTotalEngineHours * 1000)) == 0) {

                this.initTotalEngineHours = totalEngineHours;
            }

            this.accumulatedEngineHours = this.totalEngineHours - this.initTotalEngineHours;
        }
    }

    public double getAccumulatedEngineHours() {

        return ConvertUtil.decimal2Point(this.accumulatedEngineHours);
    }

    public long getUtcTime() {

        if (UTC_VALID_TIME != FOREVER && UTC_VALID_TIME < new Date().getTime() - utcTimeUpdateTime) {

            this.utcTime = 0L;
        }

        return utcTime;
    }

    public void setUtcTime(long utcTime) {

        this.utcTime = utcTime;
        this.utcTimeUpdateTime = new Date().getTime();
    }

    public double getLatitude() {

        if (GPS_VALID_TIME != FOREVER && GPS_VALID_TIME < new Date().getTime() - gpsUpdateTime) {

            this.latitude = 0D;
        }

        return latitude;
    }

    public void setLatitude(double latitude) {

        this.latitude = latitude;
        this.gpsUpdateTime = new Date().getTime();
    }

    public double getLongitude() {

        if (GPS_VALID_TIME != FOREVER && GPS_VALID_TIME < new Date().getTime() - gpsUpdateTime) {

            this.longitude = 0D;
        }

        return longitude;
    }

    public void setLongitude(double longitude) {

        this.longitude = longitude;
        this.gpsUpdateTime = new Date().getTime();
    }

    public String getVin() {

        if (VIN_VALID_TIME != FOREVER && VIN_VALID_TIME < new Date().getTime() - vinUpdateTime) {

            this.vin = "";
        }

        return vin;
    }

    public void setVin(String vin) {

        this.vin = vin;
        this.vinUpdateTime = new Date().getTime();
    }

    public double getRpm() {

        if (RPM_VALID_TIME < new Date().getTime() - rpmUpdateTime) {

            this.rpm = 0D;
        }

        return this.rpm;
    }

    public void setRpm(double rpm) {

        this.rpm = rpm;
        this.rpmUpdateTime = new Date().getTime();
    }

    public double getSpeed() {

        if (SPEED_VALID_TIME < new Date().getTime() - speedUpdateTime) {

            this.speed = 0D;
        }

        return speed;
    }

    public void setSpeed(double speed) {

        this.speed = speed;
        this.speedUpdateTime = new Date().getTime();
    }
}
