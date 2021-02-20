package com.unitedbustech.eld.common;

import com.unitedbustech.eld.eventbus.TeamWorkServiceEvent;

/**
 * @author yufei0213
 * @date 2018/2/8
 * @description 团队驾驶时状态维护类
 */
public class TeamWorkState {

    private int userRole;

    private int teamId;

    private int switchTeamId;

    private int partnerId;

    private TeamWorkServiceEvent event;

    public TeamWorkState() {
    }

    public int getUserRole() {
        return userRole;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getSwitchTeamId() {
        return switchTeamId;
    }

    public void setSwitchTeamId(int switchTeamId) {
        this.switchTeamId = switchTeamId;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    public TeamWorkServiceEvent getEvent() {
        return event;
    }

    public void setEvent(TeamWorkServiceEvent event) {
        this.event = event;
    }
}
