package com.unitedbustech.eld.eventcenter.model;

import android.location.Location;
import android.text.TextUtils;

import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.datacollector.DataCollectorHandler;
import com.unitedbustech.eld.eventcenter.core.StateAdditionInfo;
import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.eventcenter.enums.DDLStatusEnum;
import com.unitedbustech.eld.eventcenter.enums.EventItem;
import com.unitedbustech.eld.eventcenter.enums.LatLngSpecialEnum;
import com.unitedbustech.eld.hos.model.HosAdverseDriveEventModel;
import com.unitedbustech.eld.hos.model.HosDriveEventModel;
import com.unitedbustech.eld.hos.model.HosEventModel;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.EventUtil;
import com.unitedbustech.eld.util.TimeUtil;

import java.util.Date;
import java.util.UUID;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description 事件模型基类，处理事件模型的基本实现
 */
public class EventModel {

    /**
     * 事件Id
     */
    protected String id;

    /**
     * 原始事件的Id
     */
    private String originator;

    /**
     * 公司id
     */
    protected int customerId;
    /**
     * 司机id
     */
    protected int driverId;

    /**
     * 车辆id
     */
    protected int vehicleId;

    /**
     * 时区偏移量
     */
    protected int timeZoneOffset;

    /**
     * 运单号
     */
    protected String shippingDocumentNumber;

    /**
     * 事件类型
     */
    protected int type;
    /**
     * 事件码
     */
    protected int code;
    /**
     * 事件来源
     */
    protected int origin;

    /**
     * 总里程
     */
    protected double totalOdometer;

    /**
     * 累计里程
     */
    protected double accumulatedOdometer;

    /**
     * 引擎小时数
     */
    protected double totalEngineHours;

    /**
     * 累计引擎小时数
     */
    protected double accumulatedEngineHours;

    /**
     * 纬度
     */
    protected String latitude;
    /**
     * 经度
     */
    protected String longitude;
    /**
     * 地址
     */
    protected String location;

    /**
     * 距离上次事件上报时的距离
     */
    protected double lastDistance;

    /**
     * 当前是否有故障
     */
    private int malfunctionStatus;

    /**
     * 当前是否有诊断
     */
    private int dataDiagnostic;

    /**
     * 备注
     */
    protected String comment;

    /**
     * 是否可用
     */
    protected int status;

    /**
     * 日期 MMddyy
     */
    protected String date;
    /**
     * 时间 HHmmss
     */
    protected String time;

    /**
     * 时间戳
     */
    protected long datetime;

    /**
     * 默认构造方法。
     * 用于将web转化为本地实例
     */
    public EventModel() {

    }

    /**
     * 使用构造器构造方法
     *
     * @param builder 内部类的构造器
     */
    public EventModel(Builder builder) {

        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.timeZoneOffset = TimeUtil.getTimezoneOffset();
        this.status = DDLStatusEnum.ACTIVE.getCode();
        this.customerId = SystemHelper.getUser().getCarriedId();
        this.driverId = SystemHelper.getUser().getDriverId();

        this.vehicleId = SystemHelper.getUser().getVehicleId();
        this.shippingDocumentNumber = "";

        this.type = eventItem().getCode();
        this.code = getCode(builder);
        this.origin = DDLOriginEnum.EDIT_BY_DRIVER.getCode();

        VehicleDataModel dataModel = DataCollectorHandler.getInstance().getDataModel();

        this.lastDistance = dataModel.getLastDistance();
        this.totalOdometer = dataModel.getTotalOdometer();
        this.accumulatedOdometer = dataModel.getAccumulatedOdometer();

        this.totalEngineHours = dataModel.getTotalEngineHours();
        this.accumulatedEngineHours = dataModel.getAccumulatedEngineHours();

        if (builder.hasLocation) {

            this.latitude = builder.latitude;
            this.longitude = builder.longitude;

            if ((this.latitude.equals(LatLngSpecialEnum.M.getCode()) || this.longitude.equals(LatLngSpecialEnum.M.getCode())) && !SystemHelper.isPositionNormal()) {

                this.latitude = LatLngSpecialEnum.X.getCode();
                this.longitude = LatLngSpecialEnum.X.getCode();
            }

            this.location = builder.location;
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


        this.malfunctionStatus = SystemHelper.hasMalFunction() ? 1 : 0;
        this.dataDiagnostic = SystemHelper.hasDiagnostic() ? 1 : 0;

        this.comment = builder.comment;

        this.datetime = builder.date.getTime();
    }

    /**
     * 将本模型转换为Hos模型
     *
     * @return HosEventModel
     */
    public HosEventModel convertLocalHosModel() {

        HosEventModel hosEventModel;
        if (type == EventItem.SPECIAL_WORK_STATE.getCode() || type == EventItem.DRIVER_WORK_STATE.getCode()) {

            HosDriveEventModel hosDriveEventModel = new HosDriveEventModel();
            hosDriveEventModel.setDriverState(DriverState.getStateByCodeAndType(type, code));
            hosDriveEventModel.setOrigin(origin);
            hosEventModel = hosDriveEventModel;
        } else if (type == EventItem.ADVERSE_DRIVING.getCode()) {

            HosAdverseDriveEventModel adverseDriveEventModel = new HosAdverseDriveEventModel();
            hosEventModel = adverseDriveEventModel;
        } else {

            hosEventModel = new HosEventModel();
        }
        Date date = new Date(datetime);
        hosEventModel.setDate(new Date(datetime));
        hosEventModel.setStartSecond((int) ((date.getTime() - TimeUtil.getDayBegin(date).getTime()) / 1000));
        if (type == EventItem.SELF_CHECK.getCode() && this instanceof SelfCheckModel) {

            SelfCheckModel selfCheckModel = (SelfCheckModel) this;
            String abnormalCode = selfCheckModel.getAbnormalCode();
            hosEventModel.setStateString(EventUtil.getSelfCheckModelTitle(code, abnormalCode, false));
        } else {

            hosEventModel.setStateString(EventUtil.getHosTitle(type, code, false));
        }
        hosEventModel.setOdometer(totalOdometer);
        hosEventModel.setEngineHour(totalEngineHours);
        hosEventModel.setType(type);
        hosEventModel.setCode(code);
        return hosEventModel;
    }

    protected void getAdditionInfo(StateAdditionInfo additionInfo) {

        if (additionInfo != null) {

            DDLOriginEnum ddlOriginEnum = additionInfo.getOrigin();
            if (ddlOriginEnum != null) {

                this.origin = ddlOriginEnum.getCode();
            }

            if (additionInfo.getVehicleId() != 0 || additionInfo.isModify()) {

                this.vehicleId = additionInfo.getVehicleId();
            }

            if (((int) (additionInfo.getOdometer() * 1000)) != 0 || additionInfo.isModify()) {

                this.totalOdometer = additionInfo.getOdometer();
            }
            if (((int) (additionInfo.getAccumulatedOdometer() * 1000)) != 0 || additionInfo.isModify()) {

                this.accumulatedOdometer = additionInfo.getAccumulatedOdometer();
            }
            if (((int) (additionInfo.getEngineHour() * 1000)) != 0 || additionInfo.isModify()) {

                this.totalEngineHours = additionInfo.getEngineHour();
            }
            if (((int) (additionInfo.getAccumulatedEngineHours() * 1000)) != 0 || additionInfo.isModify()) {

                this.accumulatedEngineHours = additionInfo.getAccumulatedEngineHours();
            }

            if (additionInfo.isHasLocation()) {

                this.latitude = additionInfo.getLatitude();
                this.longitude = additionInfo.getLongitude();

                if ((this.latitude.equals(LatLngSpecialEnum.M.getCode()) || this.longitude.equals(LatLngSpecialEnum.M.getCode())) && !SystemHelper.isPositionNormal()) {

                    this.latitude = LatLngSpecialEnum.X.getCode();
                    this.longitude = LatLngSpecialEnum.X.getCode();
                }

                this.location = additionInfo.getAddress();
            }

            if (additionInfo.getDate() != null) {

                this.datetime = additionInfo.getDate().getTime();
            }

            this.comment = additionInfo.getRemark();
        }
    }

    /**
     * 子类重写该类。
     *
     * @return EventItem
     */
    EventItem eventItem() {
        return null;
    }

    /**
     * 获取事件对应的code
     * 子类重写该类
     *
     * @return code
     */
    int getCode(Builder builder) {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getShippingDocumentNumber() {
        return shippingDocumentNumber;
    }

    public void setShippingDocumentNumber(String shippingDocumentNumber) {
        this.shippingDocumentNumber = shippingDocumentNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public double getTotalOdometer() {
        return totalOdometer;
    }

    public void setTotalOdometer(double totalOdometer) {
        this.totalOdometer = totalOdometer;
    }

    public double getAccumulatedOdometer() {
        return accumulatedOdometer;
    }

    public void setAccumulatedOdometer(double accumulatedOdometer) {
        this.accumulatedOdometer = accumulatedOdometer;
    }

    public double getTotalEngineHours() {
        return totalEngineHours;
    }

    public void setTotalEngineHours(double totalEngineHours) {
        this.totalEngineHours = totalEngineHours;
    }

    public double getAccumulatedEngineHours() {
        return accumulatedEngineHours;
    }

    public void setAccumulatedEngineHours(double accumulatedEngineHours) {
        this.accumulatedEngineHours = accumulatedEngineHours;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLastDistance() {
        return lastDistance;
    }

    public void setLastDistance(double lastDistance) {
        this.lastDistance = lastDistance;
    }

    public int getMalfunctionStatus() {
        return malfunctionStatus;
    }

    public void setMalfunctionStatus(int malfunctionStatus) {
        this.malfunctionStatus = malfunctionStatus;
    }

    public int getDataDiagnostic() {
        return dataDiagnostic;
    }

    public void setDataDiagnostic(int dataDiagnostic) {
        this.dataDiagnostic = dataDiagnostic;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public static abstract class Builder {

        private boolean hasLocation;
        private String latitude;
        private String longitude;
        private String location;

        private double lastDistance;

        protected String comment;

        protected Date date;

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

        public EventModel build() {

            if (date == null) {

                date = new Date();
            }

            return buildModel();
        }

        protected abstract EventModel buildModel();
    }

    public int getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(int timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }
}

