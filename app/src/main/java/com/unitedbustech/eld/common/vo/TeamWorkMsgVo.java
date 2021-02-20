package com.unitedbustech.eld.common.vo;

/**
 * @author yufei0213
 * @date 2018/2/6
 * @description 服务端下发的关于团队驾驶的Vo
 */
public class TeamWorkMsgVo {

    /**
     * 消息id
     */
    private int teamId;

    /**
     * 消息类型
     */
    private int code;

    /**
     * 主驾驶id
     */
    private int driverId;

    /**
     * 主驾驶名字
     */
    private String driverName;

    /**
     * 副驾驶id
     */
    private int coDriverId;

    /**
     * 副驾驶名字
     */
    private String coDriverName;

    /**
     * 车辆id
     */
    private int vehicleId;

    /**
     * 车辆名称
     */
    private String vehicleName;

    /**
     * 服务端发送消息时间
     */
    private long datetime;

    public TeamWorkMsgVo() {
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public int getCoDriverId() {
        return coDriverId;
    }

    public void setCoDriverId(int coDriverId) {
        this.coDriverId = coDriverId;
    }

    public String getCoDriverName() {
        return coDriverName;
    }

    public void setCoDriverName(String coDriverName) {
        this.coDriverName = coDriverName;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }
}
