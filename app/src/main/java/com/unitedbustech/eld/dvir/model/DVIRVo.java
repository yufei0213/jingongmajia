package com.unitedbustech.eld.dvir.model;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.TimeUtil;

import java.util.List;

/**
 * 司机车辆检查报告的Vo
 * <p>
 * <p>
 * Created by fusw on 2016/12/26.
 */
public class DVIRVo {

    /**
     * 检查时间
     */
    private String time;
    /**
     * 车辆code
     */
    private String busId;
    /**
     * 检查结果，failed|pass
     */
    private String inspResult;

    /**
     * 类型
     */
    private int type;

    /**
     * 地址
     */
    private String location;

    /**
     * 里程表
     */
    private double odometer;

    /**
     * 创建人名称
     */
    private String creatorName;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 司机签名
     */
    private String driverSign;

    /**
     * 检查项
     */
    private List<JSONObject> defects;

    public DVIRVo(JSONObject jsonObject) {

        time = TimeUtil.utcToLocal(jsonObject.getLong("time"),
                SystemHelper.getUser().getTimeZone(),
                TimeUtil.STANDARD_FORMAT);
        busId = DataBaseHelper.getDataBase().vehicleDao().getVehicle(jsonObject.getInteger("vehicle_id")).getCode();
        inspResult = jsonObject.getString("result");
        type = jsonObject.getInteger("type");
        location = jsonObject.getString("location");
        odometer = jsonObject.getDouble("odometer");
        creatorName = jsonObject.getString("driverName");
        remark = jsonObject.getString("remark");
        driverSign = jsonObject.getString("driver_sign");
        defects = JsonUtil.parseArray(jsonObject.getString("defects"), JSONObject.class);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getInspResult() {
        return inspResult;
    }

    public void setInspResult(String inspResult) {
        this.inspResult = inspResult;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getOdometer() {
        return odometer;
    }

    public void setOdometer(double odometer) {
        this.odometer = odometer;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDriverSign() {
        return driverSign;
    }

    public void setDriverSign(String driverSign) {
        this.driverSign = driverSign;
    }

    public List<JSONObject> getDefects() {
        return defects;
    }

    public void setDefects(List<JSONObject> defects) {
        this.defects = defects;
    }
}
