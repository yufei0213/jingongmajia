package com.unitedbustech.eld.hos.model;

import android.support.annotation.NonNull;

/**
 * @author mamw
 * @date 2018/1/26
 * @description Dot Review 事件模型
 */
public class DotReviewEventModel implements Comparable<DotReviewEventModel> {

    private int id;
    private String time;
    private String location;
    private String odometer;
    private String engineHour;
    private String type;
    private String origin;
    private String remark;

    /**
     * 时间
     */
    protected Long dateTime;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        if (time == null) {
            time = "";
        }
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (location == null) {
            location = "";
        }
        this.location = location;
    }

    public String getOdometer() {
        return odometer;
    }

    public void setOdometer(String odometer) {
        if (odometer == null) {
            odometer = "";
        }
        this.odometer = odometer;
    }

    public String getEngineHour() {
        return engineHour;
    }

    public void setEngineHour(String engineHour) {
        if (engineHour == null) {
            engineHour = "";
        }
        this.engineHour = engineHour;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null) {
            type = "";
        }
        this.type = type;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        if (origin == null) {
            origin = "";
        }
        this.origin = origin;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        if (remark == null) {
            remark = "";
        }
        this.remark = remark;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public int compareTo(@NonNull DotReviewEventModel dotReviewEventModel) {

        return this.dateTime.compareTo(dotReviewEventModel.dateTime);
    }
}
