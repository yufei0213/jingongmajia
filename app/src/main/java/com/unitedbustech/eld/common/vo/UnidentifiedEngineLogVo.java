package com.unitedbustech.eld.common.vo;

import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.TimeUtil;

public class UnidentifiedEngineLogVo {

    /**
     *
     */
    private String vehicleCode;
    /**
     * id
     */
    private Integer vehicleId;
    /**
     * <##>
     */
    private String id;
    /**
     * 事件类型
     */
    private Integer type;
    /**
     * code
     */
    private Integer code;
    /**
     * 时间
     */
    private Long datetime;
    /**
     * 时间戳
     */
    private String datetimeStr;
    /**
     * 地点
     */
    private String location;
    /**
     * 里程
     */
    private Double totalOdometer;
    /**
     * 引擎时间
     */
    private Double totalEngineHours;

    public String getVehicleCode() {
        return vehicleCode;
    }

    public void setVehicleCode(String vehicleCode) {
        this.vehicleCode = vehicleCode;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
        this.datetimeStr = TimeUtil.utcToLocal(datetime, SystemHelper.getUser().getTimeZone(), TimeUtil.STANDARD_FORMAT);
    }

    public String getDatetimeStr() {
        return datetimeStr;
    }

    public void setDatetimeStr(String datetimeStr) {
        this.datetimeStr = datetimeStr;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getTotalOdometer() {
        return totalOdometer;
    }

    public void setTotalOdometer(Double totalOdometer) {
        this.totalOdometer = totalOdometer;
    }

    public Double getTotalEngineHours() {
        return totalEngineHours;
    }

    public void setTotalEngineHours(Double totalEngineHours) {
        this.totalEngineHours = totalEngineHours;
    }
}
