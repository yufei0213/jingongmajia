package com.unitedbustech.eld.domain.entry;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description HOS规则
 */
@Entity(tableName = "rule")
public class Rule {

    /**
     * 主键
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    /**
     * 天数，单位天
     */
    @ColumnInfo(name = "duty_days")
    private int dutyDays;

    /**
     * 分钟数，单位，分钟
     */
    @ColumnInfo(name = "duty_time")
    private int dutyTime;

    /**
     * 一个off-duty周期内，最大能driving时间
     * <p>
     * 单位，分钟
     */
    @ColumnInfo(name = "max_driving")
    private int maxDriving;

    /**
     * 一个off-duty周期内，限制driving时间的最大on-duty时间
     * <p>
     * 单位，分钟
     */
    @ColumnInfo(name = "max_duty_for_driving")
    private int maxDutyForDriving;

    /**
     * 能够重置off-duty周期的最小时间
     * <p>
     * 单位，分钟
     */
    @ColumnInfo(name = "min_offduty_for_driving")
    private int minOffDutyForDriving;

    /**
     * 允许的最大特殊驾驶时间
     * 单位，分钟
     */
    @ColumnInfo(name = "special_driving")
    private int specialDriving;

    /**
     * 该规则需要重置时的时间
     * 单位，分钟
     */
    @ColumnInfo(name = "reset_cycle_duty_time")
    private int resetCycleDutyTime;

    /**
     * 该规则使用的驾照类型
     */
    @ColumnInfo(name = "license_type")
    private int licenseType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDutyDays() {
        return dutyDays;
    }

    public void setDutyDays(int dutyDays) {
        this.dutyDays = dutyDays;
    }

    public int getDutyTime() {
        return dutyTime;
    }

    public void setDutyTime(int dutyTime) {
        this.dutyTime = dutyTime;
    }

    public int getMaxDriving() {
        return maxDriving;
    }

    public void setMaxDriving(int maxDriving) {
        this.maxDriving = maxDriving;
    }

    public int getMaxDutyForDriving() {
        return maxDutyForDriving;
    }

    public void setMaxDutyForDriving(int maxDutyForDriving) {
        this.maxDutyForDriving = maxDutyForDriving;
    }

    public int getMinOffDutyForDriving() {
        return minOffDutyForDriving;
    }

    public void setMinOffDutyForDriving(int minOffDutyForDriving) {
        this.minOffDutyForDriving = minOffDutyForDriving;
    }

    public int getSpecialDriving() {
        return specialDriving;
    }

    public void setSpecialDriving(int specialDriving) {
        this.specialDriving = specialDriving;
    }

    public int getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(int licenseType) {
        this.licenseType = licenseType;
    }

    public int getResetCycleDutyTime() {
        return resetCycleDutyTime;
    }

    public void setResetCycleDutyTime(int resetCycleDutyTime) {
        this.resetCycleDutyTime = resetCycleDutyTime;
    }
}
