package com.unitedbustech.eld.eventbus;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.util.JsonUtil;

/**
 * @author yufei0213
 * @date 2018/4/19
 * @description Alerts内容更新
 */
public class AlertsRefreshViewEvent {

    private int notCertifiedLogsCnt;

    private int requestedEditsCnt;

    private int assignedCnt;

    public AlertsRefreshViewEvent() {
    }

    public AlertsRefreshViewEvent(JSONObject summaryObj) {

        this.notCertifiedLogsCnt = JsonUtil.getInt(summaryObj, "notCertifiedLogsCnt");
        this.requestedEditsCnt = JsonUtil.getInt(summaryObj, "requestedEditsCnt");
        this.assignedCnt = JsonUtil.getInt(summaryObj, "assignedCnt");
    }

    public int getNotCertifiedLogsCnt() {
        return notCertifiedLogsCnt;
    }

    public void setNotCertifiedLogsCnt(int notCertifiedLogsCnt) {
        this.notCertifiedLogsCnt = notCertifiedLogsCnt;
    }

    public int getRequestedEditsCnt() {
        return requestedEditsCnt;
    }

    public void setRequestedEditsCnt(int requestedEditsCnt) {
        this.requestedEditsCnt = requestedEditsCnt;
    }

    public int getAssignedCnt() {
        return assignedCnt;
    }

    public void setAssignedCnt(int assignedCnt) {
        this.assignedCnt = assignedCnt;
    }
}
