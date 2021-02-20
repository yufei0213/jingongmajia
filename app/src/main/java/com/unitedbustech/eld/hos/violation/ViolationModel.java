package com.unitedbustech.eld.hos.violation;

import com.unitedbustech.eld.App;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.domain.entry.Rule;
import com.unitedbustech.eld.hos.core.HosHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyu
 * @date 2018/2/7
 * @description 违规事件
 */
public class ViolationModel {

    private int shift;
    private int driving;
    private int cycle;
    private int off;

    protected String limitString;

    private List<ViolationEvent> events = new ArrayList<>();

    public ViolationModel() {
    }

    /**
     * 重置违规提醒字符串
     */
    public void resetLimitString() {

        Rule rule = HosHandler.getInstance().getRule();
        //有了违规提醒
        this.limitString = "";
        if (shift > 0) {
            this.limitString += App.getContext().getResources().getString(R.string.hos_alert_shift)
                    .replace("#pilot#", String.valueOf(rule.getMaxDutyForDriving() / 60));
        }
        if (driving > 0) {
            this.limitString += App.getContext().getResources().getString(R.string.hos_alert_drive)
                    .replace("#pilot#", String.valueOf(rule.getMaxDriving() / 60));
        }
        if (cycle > 0) {
            this.limitString += App.getContext().getResources().getString(R.string.hos_alert_cycle)
                    .replace("#pilot#", String.valueOf(rule.getDutyTime() / 60));
        }
    }

    public void addEvent(List<ViolationEvent> events) {

        if (events != null && !events.isEmpty()) {

            this.events.addAll(events);
        }
    }

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public int getDriving() {
        return driving;
    }

    public void setDriving(int driving) {
        this.driving = driving;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public int getOff() {
        return off;
    }

    public void setOff(int off) {
        this.off = off;
    }

    public List<ViolationEvent> getEvents() {
        return events;
    }

    public void setEvents(List<ViolationEvent> events) {
        this.events = events;
    }

    public String getLimitString() {
        return limitString;
    }

    public void setLimitString(String limitString) {
        this.limitString = limitString;
    }
}
