package com.unitedbustech.eld.common.vo;

import com.unitedbustech.eld.hos.model.DotReviewEventModel;
import com.unitedbustech.eld.hos.model.GridModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mamw
 * @date 2018/1/25
 * @description 用于Dot Review 图表的类。给js画图表的模型类.
 * 在使用时，图标根据一个本类List绘制图表
 */
public class DotReviewVo {

    private String standDateStr;
    private String switchBarDateStr;
    private String location;

    private DailyLogDataHeadVo dotReviewDataHeadVo;

    private long datetime;

    private List<GridModel> gridModelList = new ArrayList<>();
    private List<DotReviewEventModel> dotReviewEventModelList = new ArrayList<>();

    public DotReviewVo() {
    }

    public String getStandDateStr() {
        return standDateStr;
    }

    public void setStandDateStr(String standDateStr) {
        this.standDateStr = standDateStr;
    }

    public String getSwitchBarDateStr() {
        return switchBarDateStr;
    }

    public void setSwitchBarDateStr(String switchBarDateStr) {
        this.switchBarDateStr = switchBarDateStr;
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

    public DailyLogDataHeadVo getDotReviewDataHeadVo() {
        return dotReviewDataHeadVo;
    }

    public void setDotReviewDataHeadVo(DailyLogDataHeadVo dailyLogDataHeadVo) {
        this.dotReviewDataHeadVo = dailyLogDataHeadVo;
    }

    public List<GridModel> getGridModelList() {
        return gridModelList;
    }

    public void setGridModelList(List<GridModel> gridModelList) {
        this.gridModelList = gridModelList;
    }

    public List<DotReviewEventModel> getDotReviewEventModelList() {
        return dotReviewEventModelList;
    }

    public void setDotReviewEventModelList(List<DotReviewEventModel> dotReviewEventModelList) {
        this.dotReviewEventModelList = dotReviewEventModelList;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }
}
