package com.unitedbustech.eld.eventcenter.core;

import android.text.TextUtils;

import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;

import java.util.Date;

/**
 * @author zhangyu
 * @date 2018/1/19
 * @description 状态切换，状态修改等附加信息。
 */
public class StateAdditionInfo {

    private DDLOriginEnum origin;

    private int vehicleId;

    private double odometer;
    private double accumulatedOdometer;

    private double engineHour;
    private double accumulatedEngineHours;

    private boolean hasLocation;
    private String latitude;
    private String longitude;
    private String address;

    private String remark;

    private Date date;

    private boolean isModify;

    private StateAdditionInfo(Builder builder) {

        this.origin = builder.origin;
        this.vehicleId = builder.vehicleId;
        this.odometer = builder.odometer;
        this.accumulatedOdometer = builder.accumulatedOdometer;
        this.engineHour = builder.engineHour;
        this.accumulatedEngineHours = builder.accumulatedEngineHours;
        if (builder.hasLocation) {

            this.hasLocation = true;
            this.latitude = builder.latitude;
            this.longitude = builder.longitude;
            this.address = builder.address;
        }

        this.isModify = builder.isModify;

        this.remark = builder.remark;

        this.date = builder.date;
    }

    public DDLOriginEnum getOrigin() {
        return origin;
    }

    public void setOrigin(DDLOriginEnum origin) {
        this.origin = origin;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public double getOdometer() {
        return odometer;
    }

    public void setOdometer(double odometer) {
        this.odometer = odometer;
    }

    public double getAccumulatedOdometer() {
        return accumulatedOdometer;
    }

    public void setAccumulatedOdometer(double accumulatedOdometer) {
        this.accumulatedOdometer = accumulatedOdometer;
    }

    public double getEngineHour() {
        return engineHour;
    }

    public void setEngineHour(double engineHour) {
        this.engineHour = engineHour;
    }

    public double getAccumulatedEngineHours() {
        return accumulatedEngineHours;
    }

    public void setAccumulatedEngineHours(double accumulatedEngineHours) {
        this.accumulatedEngineHours = accumulatedEngineHours;
    }

    public boolean isHasLocation() {
        return hasLocation;
    }

    public void setHasLocation(boolean hasLocation) {
        this.hasLocation = hasLocation;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isModify() {
        return isModify;
    }

    public void setModify(boolean modify) {
        isModify = modify;
    }

    public static class Builder {

        private DDLOriginEnum origin;

        private int vehicleId;

        private double odometer;
        private double accumulatedOdometer;

        private double engineHour;
        private double accumulatedEngineHours;

        private boolean hasLocation;
        private String latitude;
        private String longitude;
        private String address;

        private String remark;

        private Date date;

        private boolean isModify = false;

        public Builder() {
        }

        public Builder origin(DDLOriginEnum origin) {

            this.origin = origin;
            return this;
        }

        public Builder vehicleId(int vehicleId) {

            this.vehicleId = vehicleId;
            return this;
        }

        public Builder odometer(double odometer) {

            this.odometer = odometer;
            return this;
        }

        public Builder accumulatedOdometer(double accumulatedOdometer) {

            this.accumulatedOdometer = accumulatedOdometer;
            return this;
        }

        public Builder accumulatedEngineHours(double accumulatedEngineHours) {

            this.accumulatedEngineHours = accumulatedEngineHours;
            return this;
        }

        public Builder engineHour(double engineHour) {

            this.engineHour = engineHour;
            return this;
        }

        public Builder location(String location, String latitude, String longitude) {

            location = location == null ? "" : location.trim();
            latitude = latitude == null ? "" : latitude.trim();
            longitude = longitude == null ? "" : longitude.trim();

            //如果此时有地址但是没有经纬度，则地址为司机手动填写，此时经纬度应该为M
            if (!TextUtils.isEmpty(location) && TextUtils.isEmpty(latitude) && TextUtils.isEmpty(longitude)) {

                Logger.w(Tags.LOCATION,"Lack of necessary information (latitude & longitude)");
            }

            if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {

                this.hasLocation = true;
                this.latitude = latitude;
                this.longitude = longitude;
            }

            this.address = location;
            return this;
        }

        public Builder remark(String remark) {

            this.remark = remark;
            return this;
        }

        public Builder date(Date date) {

            this.date = date;
            return this;
        }

        public Builder modify() {

            this.isModify = true;
            return this;
        }


        public StateAdditionInfo build() {

            //如果没有Date，则去当前时间
            if (this.date == null) {

                this.date = new Date();
            }

            return new StateAdditionInfo(this);
        }
    }
}
