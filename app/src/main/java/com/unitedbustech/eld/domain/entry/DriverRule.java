package com.unitedbustech.eld.domain.entry;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description DriverRule
 */
@Entity(tableName = "driver_rule")
public class DriverRule {

    @PrimaryKey
    @ColumnInfo(name = "driver_id")
    private int driverId;

    @ColumnInfo(name = "rule_id")
    private int ruleId;

    @ColumnInfo(name = "create_time")
    private String createTime;

    @ColumnInfo(name = "date_time")
    private String dateTime;

    @ColumnInfo(name = "team_drive")
    private int teamDrive;

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getTeamDrive() {
        return teamDrive;
    }

    public void setTeamDrive(int teamDrive) {
        this.teamDrive = teamDrive;
    }
}
