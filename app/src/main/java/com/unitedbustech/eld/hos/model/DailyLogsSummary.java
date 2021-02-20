package com.unitedbustech.eld.hos.model;

/**
 * @author zhangyu
 * @date 2018/1/16
 * @description 每天的日志的汇总。用于显示汇总页
 */
public class DailyLogsSummary {

    /**
     * Date类型的日期。
     */
    private String date;

    /**
     * on duty的总时间，单位：秒
     */
    private int ondutySecond;

    /**
     * 是否需要edit
     */
    private boolean needEdit;

    /**
     * 是否需要签名
     */
    private boolean needSign;

    /**
     * 是否违规
     */
    private boolean isViolation;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getOndutySecond() {
        return ondutySecond;
    }

    public void setOndutySecond(int ondutySecond) {
        this.ondutySecond = ondutySecond;
    }

    public boolean isNeedEdit() {
        return needEdit;
    }

    public void setNeedEdit(boolean needEdit) {
        this.needEdit = needEdit;
    }

    public boolean isNeedSign() {
        return needSign;
    }

    public void setNeedSign(boolean needSign) {
        this.needSign = needSign;
    }

    public boolean isViolation() {
        return isViolation;
    }

    public void setViolation(boolean violation) {
        isViolation = violation;
    }
}
