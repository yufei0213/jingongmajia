package com.unitedbustech.eld.hos.model;

import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.domain.entry.Rule;

import java.util.Date;

/**
 * @author zhangyu
 * @date 2018/1/16
 * @description 当前Hos计算结果
 */
public class HosDataModel {

    /**
     * 当前的状态是什么。
     */
    private DriverState currentState;

    /**
     * 累计的cycle的总时间。单位：秒。
     * 例如：1:15 ，该值为75.
     */
    private long cycleSecond;

    /**
     * shift时间。单位：秒。
     * 例如：1:15 ，该值为75.
     */
    private long shiftSecond;

    /**
     * 累计工作的时间。单位：秒。
     * 例如：1:15 ，该值为75.
     */
    private long odndSecond;

    /**
     * 今日累计工作时间。单位：秒
     * 例如：1:15 ，该值为75.
     */
    private long todayOdndSecond;

    /**
     * 累计的break时间。单位：秒。
     * 例如：1:15 ，该值为75.
     */
    private long breakSecond;

    /**
     * 剩余的驾驶时间。单位:秒。
     * 例如：1:15 ，该值为75.
     */
    private long driveSecond;

    /**
     * 已经驾驶的时间。单位:秒。
     * 例如：1:15 ，该值为75.
     */
    private long pastSecond;

    /**
     * 今日累计驾驶时间。单位：秒
     * 例如：1:15 ，该值为75.
     */
    private long todayDriveSecond;

    /**
     * Shift周期内，最大驾驶时间
     */
    private long maxDriving;

    /**
     * 本数据的计算时间
     */
    private Date updateTime;

    public HosDataModel() {

        updateTime = new Date();
    }

    public HosDataModel(Rule rule) {

        this();
        this.maxDriving = rule.getMaxDriving() * 60;
    }

    public DriverState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(DriverState currentState) {
        this.currentState = currentState;
    }

    public long getCycleSecond() {
        return cycleSecond;
    }

    public void setCycleSecond(long cycleSecond) {
        this.cycleSecond = cycleSecond;
    }

    public long getShiftSecond() {
        return shiftSecond;
    }

    public void setShiftSecond(long shiftSecond) {
        this.shiftSecond = shiftSecond;
    }

    public long getOdndSecond() {
        return odndSecond;
    }

    public void setOdndSecond(long odndSecond) {
        this.odndSecond = odndSecond;
    }

    public long getTodayOdndSecond() {
        return todayOdndSecond;
    }

    public void setTodayOdndSecond(long todayOdndSecond) {
        this.todayOdndSecond = todayOdndSecond;
    }

    public long getBreakSecond() {
        return breakSecond;
    }

    public void setBreakSecond(long breakSecond) {
        this.breakSecond = breakSecond;
    }

    public long getDriveSecond() {
        return driveSecond;
    }

    public void setDriveSecond(long driveSecond) {
        this.driveSecond = driveSecond;
    }

    public long getPastSecond() {
        return pastSecond;
    }

    public void setPastSecond(long pastSecond) {
        this.pastSecond = pastSecond;
    }

    public long getTodayDriveSecond() {
        return todayDriveSecond;
    }

    public void setTodayDriveSecond(long todayDriveSecond) {
        this.todayDriveSecond = todayDriveSecond;
    }

    public long getMaxDriving() {
        return maxDriving;
    }

    public void setMaxDriving(long maxDriving) {
        this.maxDriving = maxDriving;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
