package com.unitedbustech.eld.hos.model;

import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.TimeUtil;

/**
 * @author zhangyu
 * @date 2018/1/30
 * @description TODO
 */
public class HosEventModelVo {

    /**
     * 该事件的开始时间。
     * 单位:秒。
     * 意义:由于该模型依附于HosDayModel，因此该时间是从一天的0点开始经过的秒数。
     * 例如:上午10:00发生的事件。该值为 10 * 60 * 60 = 36000;
     */
    protected int startSecond;

    /**
     * 当前状态描述.例如。登录事件，该值为 Login
     */
    protected String stateString;

    /**
     * 时间
     */
    protected String date;

    /**
     * 文字的地址。格式化后的地址
     */
    protected String location;

    /**
     * 纬度
     */
    protected Double latitude;

    /**
     * 经度
     */
    protected Double longitude;

    /**
     * 所属的车辆。
     */
    protected String vehicle;

    /**
     * 备注信息
     */
    protected String remark;

    /**
     * 本地缓存的ID，用于数据关系查询
     */
    protected Long localId;

    /**
     * 如果是驾驶状态，还会有状态字段
     */
    protected DriverState driverState;

    /**
     * 如果是驾驶状态，还会有来源字段
     */
    protected int origin;

    public HosEventModelVo() {

    }

    public HosEventModelVo(HosEventModel hosEventModel) {
        this.startSecond = hosEventModel.startSecond;
        this.stateString = hosEventModel.stateString;

        this.date = TimeUtil.utcToLocal(hosEventModel.getDate().getTime(),
                SystemHelper.getUser().getTimeZone(),
                TimeUtil.HH_MM_AA) + " " + SystemHelper.getUser().getTimeZoneAlias();
        this.location = hosEventModel.location;
        this.latitude = hosEventModel.latitude;
        this.longitude = hosEventModel.longitude;
        this.vehicle = hosEventModel.vehicle;
        this.remark = hosEventModel.remark;
        this.localId = hosEventModel.localId;
        if (hosEventModel instanceof HosDriveEventModel) {
            this.driverState = ((HosDriveEventModel) hosEventModel).getDriverState();
            this.origin = ((HosDriveEventModel) hosEventModel).getOrigin();
        }
    }

    public DriverState getDriverState() {
        return driverState;
    }

    public void setDriverState(DriverState driverState) {
        this.driverState = driverState;
    }

    public int getStartSecond() {
        return startSecond;
    }

    public void setStartSecond(int startSecond) {
        this.startSecond = startSecond;
    }

    public String getStateString() {
        return stateString;
    }

    public void setStateString(String stateString) {
        this.stateString = stateString;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }
}
