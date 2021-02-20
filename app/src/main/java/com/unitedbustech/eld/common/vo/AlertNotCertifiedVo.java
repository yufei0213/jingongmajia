package com.unitedbustech.eld.common.vo;

import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.util.TimeUtil;

import java.util.Date;

/**
 * Created by Administrator
 * on 2018/1/29.
 */

public class AlertNotCertifiedVo {

    private int id;
    private long timeStamp;
    private String backDateStr;      //MMddYY
    private String showDateStr; // MON, JAN 8
    private String onDutyTime; // 单位为second

    private int showDetail;

    public void isShowDetail() {

        Date firstDate = TimeUtil.getPreviousDate(new Date(), Constants.DDL_DAYS - 1);
        long firstDateStartTime = TimeUtil.getDayBegin(firstDate).getTime();
        if (firstDateStartTime <= timeStamp) {

            showDetail = 1;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getBackDateStr() {
        return backDateStr;
    }

    public void setBackDateStr(String backDateStr) {
        this.backDateStr = backDateStr;
    }

    public String getShowDateStr() {
        return showDateStr;
    }

    public void setShowDateStr(String showDateStr) {
        this.showDateStr = showDateStr;
    }

    public String getOnDutyTime() {
        return onDutyTime;
    }

    public void setOnDutyTime(String onDutyTime) {
        this.onDutyTime = onDutyTime;
    }

    public int getShowDetail() {
        return showDetail;
    }

    public void setShowDetail(int showDetail) {
        this.showDetail = showDetail;
    }
}
