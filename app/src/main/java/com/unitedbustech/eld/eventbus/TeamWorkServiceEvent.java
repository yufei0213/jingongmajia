package com.unitedbustech.eld.eventbus;

import com.unitedbustech.eld.common.vo.TeamWorkMsgVo;

/**
 * @author yufei0213
 * @date 2018/2/6
 * @description 团队驾驶事件，从服务端收到消息后，通过此事件将数据发送给TeamWorkService
 */
public class TeamWorkServiceEvent {

    /**
     * 消息id
     */
    private int teamId;

    /**
     * 消息类型
     */
    private int type;

    /**
     * 主驾驶id
     */
    private int pilotId;

    /**
     * 主驾驶名字
     */
    private String pilotName;

    /**
     * 副驾驶id
     */
    private int copilotId;

    /**
     * 副驾驶名字
     */
    private String copilotName;

    /**
     * 车辆id
     */
    private int vehicleId;

    /**
     * 车辆名称
     */
    private String vehicleCode;

    /**
     * 服务端发送消息时间
     */
    private long dateTime;

    public TeamWorkServiceEvent() {
    }

    public TeamWorkServiceEvent(TeamWorkMsgVo teamWorkMsgVo) {

        this.teamId = teamWorkMsgVo.getTeamId();
        this.type = teamWorkMsgVo.getCode();
        this.pilotId = teamWorkMsgVo.getDriverId();
        this.pilotName = teamWorkMsgVo.getDriverName();
        this.copilotId = teamWorkMsgVo.getCoDriverId();
        this.copilotName = teamWorkMsgVo.getCoDriverName();
        this.vehicleId = teamWorkMsgVo.getVehicleId();
        this.vehicleCode = teamWorkMsgVo.getVehicleName();
        this.dateTime = teamWorkMsgVo.getDatetime();
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPilotId() {
        return pilotId;
    }

    public void setPilotId(int pilotId) {
        this.pilotId = pilotId;
    }

    public String getPilotName() {
        return pilotName;
    }

    public void setPilotName(String pilotName) {
        this.pilotName = pilotName;
    }

    public int getCopilotId() {
        return copilotId;
    }

    public void setCopilotId(int copilotId) {
        this.copilotId = copilotId;
    }

    public String getCopilotName() {
        return copilotName;
    }

    public void setCopilotName(String copilotName) {
        this.copilotName = copilotName;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleCode() {
        return vehicleCode;
    }

    public void setVehicleCode(String vehicleCode) {
        this.vehicleCode = vehicleCode;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
}
