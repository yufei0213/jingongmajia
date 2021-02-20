package com.unitedbustech.eld.eventcenter.model;

import android.location.Location;
import android.text.TextUtils;

import com.unitedbustech.eld.common.HistoryEventType;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.datacollector.DataCollectorHandler;
import com.unitedbustech.eld.eventcenter.core.StateAdditionInfo;
import com.unitedbustech.eld.eventcenter.enums.EventItem;
import com.unitedbustech.eld.eventcenter.enums.LatLngSpecialEnum;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.SystemHelper;

import java.util.Date;
import java.util.UUID;

/**
 * @author lzh
 * @date 2019/10/30
 * @description 历史事件
 */
public class HistoryModel {

    /**
     * 事件Id
     */
    protected String id;//

    /**
     * 事件原始创建时间
     */
    private long createTime;//

    /**
     *当前用户ID
     */
    private String creatorId;//

    /**
     * 公司id
     */
    protected int customerId;//

    private int deviceTypeCode;//

    /**
     * 引擎时间
     */
    private double engineHours;//

    /**
     *
     */
    private int eventTypeCode;//

    /**
     * 里程数
     */
    private double odometer;//

    /**
     * 车辆id
     */
    protected int vehicleId;//

    /**
     * 时区
     */
    private String timeZone;//

    /**
     * 纬度
     */
    protected String latitude;//
    /**
     * 经度
     */
    protected String longitude;//

    /**
     * 日期 MMddyy
     */
    protected String date;//
    /**
     * 时间 HHmmss
     */
    protected String time;//

    /**
     * 时间戳
     */
    protected long datetime;//

    /**
     * 默认构造方法。
     * 用于将web转化为本地实例
     */
    public HistoryModel() {

    }

    /**
     * 使用构造器构造方法
     *
     * @param builder 内部类的构造器
     */
    public HistoryModel(Builder builder) {

        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.timeZone = SystemHelper.getUser().getTimeZone();
        this.customerId = SystemHelper.getUser().getCarriedId();
        this.vehicleId = SystemHelper.getUser().getVehicleId();
        this.customerId = SystemHelper.getUser().getDriverId();

        VehicleDataModel dataModel = DataCollectorHandler.getInstance().getDataModel();

        if (builder.hasLocation) {

            this.latitude = builder.latitude;
            this.longitude = builder.longitude;

            if ((this.latitude.equals(LatLngSpecialEnum.M.getCode()) || this.longitude.equals(LatLngSpecialEnum.M.getCode())) && !SystemHelper.isPositionNormal()) {

                this.latitude = LatLngSpecialEnum.X.getCode();
                this.longitude = LatLngSpecialEnum.X.getCode();
            }

        } else {

            if (((int) (dataModel.getLatitude() * 1000)) != 0 && ((int) (dataModel.getLatitude() * 1000)) != 0) {

                this.latitude = String.valueOf(dataModel.getLatitude());
                this.longitude = String.valueOf(dataModel.getLongitude());
            } else {

                Location location = LocationHandler.getInstance().getCurrentLocation();
                if (location != null) {

                    this.latitude = String.valueOf(location.getLatitude());
                    this.longitude = String.valueOf(location.getLongitude());
                } else {

                    if (SystemHelper.hasPositionMalFunction()) {

                        this.latitude = LatLngSpecialEnum.E.getCode();
                        this.longitude = LatLngSpecialEnum.E.getCode();
                    } else {

                        this.latitude = LatLngSpecialEnum.X.getCode();
                        this.longitude = LatLngSpecialEnum.X.getCode();
                    }
                }
            }
        }

        this.datetime = builder.date.getTime();
        this.deviceTypeCode = builder.deviceType;

        this.getAdditionInfo(builder.additionInfo);
    }

    protected void getAdditionInfo(StateAdditionInfo additionInfo) {

        if (additionInfo != null) {

            if (additionInfo.getVehicleId() != 0 || additionInfo.isModify()) {

                this.vehicleId = additionInfo.getVehicleId();
            }

            if (((int) (additionInfo.getOdometer() * 1000)) != 0 || additionInfo.isModify()) {

                this.odometer = additionInfo.getOdometer();
            }
            if (additionInfo.isHasLocation()) {

                this.latitude = additionInfo.getLatitude();
                this.longitude = additionInfo.getLongitude();

                if ((this.latitude.equals(LatLngSpecialEnum.M.getCode()) || this.longitude.equals(LatLngSpecialEnum.M.getCode())) && !SystemHelper.isPositionNormal()) {

                    this.latitude = LatLngSpecialEnum.X.getCode();
                    this.longitude = LatLngSpecialEnum.X.getCode();
                }

            }
            if(additionInfo.getEngineHour() != 0) {

                this.engineHours = additionInfo.getEngineHour();
            }

            if (additionInfo.getDate() != null) {

                this.datetime = additionInfo.getDate().getTime();
                this.createTime = additionInfo.getDate().getTime();
            }
        }
    }

    /**
     *
     * @return EventItem
     */
    EventItem eventItem() {
        return EventItem.DRIVER_WORK_STATE;
    }

    /**
     * @return code
     */
    int getCode(Builder builder) {
        switch (((Builder) builder).historyEventType) {
            case POWER_ON:
                return 1;
            case POWER_OFF:
                return 2;
            case MOVE:
                return 3;
            case STOP:
                return 4;
        }
        return 5;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public int getDeviceTypeCode() {
        return deviceTypeCode;
    }

    public void setDeviceTypeCode(int deviceTypeCode) {
        this.deviceTypeCode = deviceTypeCode;
    }

    public double getEngineHours() {
        return engineHours;
    }

    public void setEngineHours(double engineHours) {
        this.engineHours = engineHours;
    }

    public int getEventTypeCode() {
        return eventTypeCode;
    }

    public void setEventTypeCode(int eventTypeCode) {
        this.eventTypeCode = eventTypeCode;
    }

    public double getOdometer() {
        return odometer;
    }

    public void setOdometer(double odometer) {
        this.odometer = odometer;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public static class Builder {

        private boolean hasLocation;
        private String latitude;
        private String longitude;
        private String location;

        private double lastDistance;

        protected String comment;

        protected Date date;

        private HistoryEventType historyEventType;
        private int deviceType;
        private StateAdditionInfo additionInfo;

        public Builder state(HistoryEventType type, int deviceType) {

            this.historyEventType = type;
            this.deviceType = deviceType;
            return this;
        }

        public Builder addition(StateAdditionInfo stateAdditionInfo) {

            this.additionInfo = stateAdditionInfo;
            this.date = stateAdditionInfo.getDate();
            return this;
        }

        public Builder location(String location, String latitude, String longitude) {

            location = location == null ? "" : location.trim();
            latitude = latitude == null ? "" : latitude.trim();
            longitude = longitude == null ? "" : longitude.trim();

            //如果此时有地址但是没有经纬度，则地址为司机手动填写，此时经纬度应该为M
            if (!TextUtils.isEmpty(location) && TextUtils.isEmpty(latitude) && TextUtils.isEmpty(longitude)) {

                Logger.w(Tags.LOCATION, "Lack of necessary information (latitude & longitude)");
            }

            if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {

                this.hasLocation = true;
                this.latitude = latitude;
                this.longitude = longitude;
            }

            this.location = location;

            return this;
        }

        public Builder comment(String comment) {

            this.comment = comment;
            return this;
        }

        public Builder date(Date date) {

            this.date = date;
            return this;
        }

        public HistoryModel build() {

            if (date == null) {

                date = new Date();
            }

            return buildModel();
        }

        protected HistoryModel buildModel() {

            return new HistoryModel(this);
        }
    }

}

