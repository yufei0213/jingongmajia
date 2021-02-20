package com.unitedbustech.eld.common.vo;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.hos.model.GridModel;

import java.util.List;

/**
 * Created by Administrator
 * on 2018/1/29.
 */

public class AlertAssignedVo {

    private String startId;
    private JSONObject startJson;
    private String endId;
    private JSONObject endJson;

    private String vehicleNo;

    private String intervalTime; // 0h 0min
    private String intervalTimeStr;

    private String intervalOdometer;

    private String startLocation;
    private String endLocation;

    private String assignedName;
    private String assignedId;

    private String state;
    private String startTime;
    private String endTime;

    private String start_end_odometer;

    private String engineTime;
    private String comment;
    private long datetime;

    private List<GridModel> gridModelList;
    private GridModel gridModel;

    public String getStartId() {
        return startId;
    }

    public void setStartId(String startId) {
        this.startId = startId;
    }

    public String getEndId() {
        return endId;
    }

    public void setEndId(String endId) {
        this.endId = endId;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(String intervalTime) {
        this.intervalTime = intervalTime;
    }

    public String getIntervalTimeStr() {
        return intervalTimeStr;
    }

    public void setIntervalTimeStr(String intervalTimeStr) {
        this.intervalTimeStr = intervalTimeStr;
    }

    public JSONObject getStartJson() {
        return startJson;
    }

    public void setStartJson(JSONObject startJson) {
        this.startJson = startJson;
    }

    public JSONObject getEndJson() {
        return endJson;
    }

    public void setEndJson(JSONObject endJson) {
        this.endJson = endJson;
    }

    public String getIntervalOdometer() {
        return intervalOdometer;
    }

    public void setIntervalOdometer(String intervalOdometer) {
        this.intervalOdometer = intervalOdometer;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getAssignedName() {
        return assignedName;
    }

    public void setAssignedName(String assignedName) {
        this.assignedName = assignedName;
    }

    public String getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(String assignedId) {
        this.assignedId = assignedId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStart_end_odometer() {
        return start_end_odometer;
    }

    public void setStart_end_odometer(String start_end_odometer) {
        this.start_end_odometer = start_end_odometer;
    }

    public String getEngineTime() {
        return engineTime;
    }

    public void setEngineTime(String engineTime) {
        this.engineTime = engineTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<GridModel> getGridModelList() {
        return gridModelList;
    }

    public void setGridModelList(List<GridModel> gridModelList) {
        this.gridModelList = gridModelList;
    }

    public GridModel getGridModel() {
        return gridModel;
    }

    public void setGridModel(GridModel gridModel) {
        this.gridModel = gridModel;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }
}
