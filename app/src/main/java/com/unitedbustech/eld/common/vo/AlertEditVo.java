package com.unitedbustech.eld.common.vo;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.hos.model.GridModel;
import com.unitedbustech.eld.util.TimeUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator
 * on 2018/1/29.
 */
public class AlertEditVo {

    private String id;
    private String createDateStr; // MON,JAN 8
    private String createTimeStr;// O6：23AM EST
    private String editorId;
    private String editor;
    private String editDateStr;
    private String editTypeStr;
    private String comment;
    private String state;
    private String odometer;
    private String location;
    private String vehicle;
    private long datetime;//时间戳
    private JSONObject logJson;//原事件的json。用于接收成功后的本地修改模型。

    private List<GridModel> gridModelList;
    private GridModel gridModel;

    private int showGrid;

    public void resetGrid() {

        Date firstDate = TimeUtil.getPreviousDate(new Date(), Constants.DDL_DAYS - 1);
        long firstDateStartTime = TimeUtil.getDayBegin(firstDate).getTime();
        if (firstDateStartTime <= datetime) {

            showGrid = 1;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getEditorId() {
        return editorId;
    }

    public void setEditorId(String editorId) {
        this.editorId = editorId;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getEditDateStr() {
        return editDateStr;
    }

    public void setEditDateStr(String editDateStr) {
        this.editDateStr = editDateStr;
    }

    public String getEditTypeStr() {
        return editTypeStr;
    }

    public void setEditTypeStr(String editTypeStr) {
        this.editTypeStr = editTypeStr;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOdometer() {
        return odometer;
    }

    public void setOdometer(String odometer) {
        this.odometer = odometer;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public JSONObject getLogJson() {
        return logJson;
    }

    public void setLogJson(JSONObject logJson) {
        this.logJson = logJson;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public int getShowGrid() {
        return showGrid;
    }

    public void setShowGrid(int showGrid) {
        this.showGrid = showGrid;
    }
}


