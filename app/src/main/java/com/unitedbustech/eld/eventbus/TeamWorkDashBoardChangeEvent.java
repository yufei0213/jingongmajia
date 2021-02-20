package com.unitedbustech.eld.eventbus;

/**
 * @author yufei0213
 * @date 2018/2/6
 * @description 团队驾驶事件，通知dashboard页面刷新界面
 */
public class TeamWorkDashBoardChangeEvent {

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

    public TeamWorkDashBoardChangeEvent() {
    }

    public TeamWorkDashBoardChangeEvent(int type) {
        this.type = type;
    }

    public TeamWorkDashBoardChangeEvent(TeamWorkServiceEvent event, int type) {

        this.teamId = event.getTeamId();
        this.pilotId = event.getPilotId();
        this.pilotName = event.getPilotName();
        this.copilotId = event.getCopilotId();
        this.copilotName = event.getCopilotName();
        this.vehicleId = event.getVehicleId();
        this.vehicleCode = event.getVehicleCode();

        this.type = type;
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
}
