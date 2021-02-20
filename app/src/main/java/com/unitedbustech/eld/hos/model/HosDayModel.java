package com.unitedbustech.eld.hos.model;

import com.unitedbustech.eld.App;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.dailylog.model.Profile;
import com.unitedbustech.eld.util.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zhangyu
 * @date 2018/1/16
 * @description Hos一天数据的模型。一天的基本数据以及一天的基本计算存储在本模型中。
 */
public class HosDayModel implements Cloneable {

    /**
     * 本天发生的所有的[司机状态变化]的event。
     * 注意：该List不包含从00：00开始的事件。
     * 本天的初始状态由startDriverState决定
     */
    private List<HosDriveEventModel> hosDriveEventModels;

    /**
     * 本天发生的[与司机驾驶状态无关]的event.
     */
    private List<HosEventModel> hosEventModels;

    /**
     * 本天的初始状态。
     */
    private DriverState startDriverState;

    /**
     * 初始状态的来源。
     */
    private int startEventOrigin;

    /**
     * 日期
     */
    private Date date;

    /**
     * 表头信息
     */
    private Profile profile;

    /**
     * 签名信息
     */
    private String sign;

    public HosDayModel() {
        this.hosDriveEventModels = new CopyOnWriteArrayList<>();
        this.hosEventModels = new CopyOnWriteArrayList<>();
    }

    /**
     * 计算今天的上班时间。包括On Duty Not Driving 以及 Driving
     *
     * @return 上班时间，单位：秒
     */
    public int getOnDutyTime() {

        return getOnDutyTime(new Date());
    }

    /**
     * 计算今天的上班时间。包括On Duty Not Driving 以及 Driving
     *
     * @param now 用now代替new Date()
     * @return
     */
    public int getOnDutyTime(Date now) {

        List<HosDriveEventModel> tempList = new CopyOnWriteArrayList<>();
        tempList.addAll(hosDriveEventModels);

        int onDutyTime = 0;
        //如果一天都没有发生状态改变，则直接返回
        if (tempList.size() == 0) {

            if (DriverState.isOnDuty(startDriverState)) {

                //如果是今天的，并且为空的情况
                if (TimeUtil.isSameDay(now, getDate())) {

                    Date dayBegin = TimeUtil.getDayBegin(now);
                    return (int) ((now.getTime() - dayBegin.getTime()) / 1000);
                } else {

                    return TimeUtil.getTotalHours(date) * 60 * 60;
                }
            } else {

                return 0;
            }
        }
        //计算本天的初始状态
        if (DriverState.isOnDuty(startDriverState)) {

            onDutyTime += tempList.get(tempList.size() - 1).getStartSecond();
        }
        //计算两个点的间隔
        for (int i = tempList.size() - 1; i > 0; i--) {

            //如果这段时间是onDuty
            if (DriverState.isOnDuty(tempList.get(i).getDriverState())) {

                onDutyTime += tempList.get(i - 1).getStartSecond() - tempList.get(i).getStartSecond();
            }
        }
        //计算本天结尾的时间
        if (DriverState.isOnDuty(tempList.get(0).getDriverState())) {
            int endSecond = TimeUtil.getTotalHours(date) * 60 * 60;
            if (TimeUtil.isSameDay(date, now)) {
                endSecond = (int) TimeUtil.intervalSecond(now, TimeUtil.getDayBegin(now));
            }
            onDutyTime += endSecond - tempList.get(0).getStartSecond();
        }
        return onDutyTime;
    }

    /**
     * 计算当天的签名次数
     *
     * @return
     */
    public int countCerNumber() {

        int result = 0;
        for (HosEventModel hosEventModel : hosEventModels) {
            if (hosEventModel.stateString.equals(App.getContext().getString(R.string.ddl_sign))) {
                result++;
            }
        }
        return result;
    }

    /**
     * 对本天的数据进行汇总
     *
     * @return 当天的汇总数据
     */
    public DailyLogsSummary summary() {

        DailyLogsSummary dailyLogsSummary = new DailyLogsSummary();
        dailyLogsSummary.setDate(TimeUtil.getDailylogFromat(this.date, true).toUpperCase());
        dailyLogsSummary.setNeedEdit(false);
        dailyLogsSummary.setNeedSign(false);
        dailyLogsSummary.setViolation(false);
        dailyLogsSummary.setOndutySecond(getOnDutyTime());
        return dailyLogsSummary;
    }

    /**
     * 重置HOSDriveModel的hide开关。
     *
     * @param preState 前一天最后结束的状态
     */
    public void resetHideCase(DriverState preState) {

        if (hosDriveEventModels == null || hosDriveEventModels.size() == 0 || startDriverState == null) {

            return;
        }
        int lastIndex = hosDriveEventModels.size() - 1;
        if (preState == startDriverState && TimeUtil.getDayBegin(date).getTime() == hosDriveEventModels.get(lastIndex).getDate().getTime()) {

            hosDriveEventModels.get(lastIndex).setHide(true);
        }
        if (startDriverState.equals(hosDriveEventModels.get(lastIndex).getDriverState()) &&
                TimeUtil.getDayBegin(date).getTime() != hosDriveEventModels.get(lastIndex).getDate().getTime() &&
                !startDriverState.equals(DriverState.DRIVING)) {

            hosDriveEventModels.get(lastIndex).setHide(true);
        }
        for (int i = lastIndex; i > 0; i--) {

            if (hosDriveEventModels.get(i - 1).getDriverState().equals(hosDriveEventModels.get(i).getDriverState()) &&
                    !hosDriveEventModels.get(i - 1).equals(DriverState.DRIVING)) {

                hosDriveEventModels.get(i - 1).setHide(true);
            } else {

                hosDriveEventModels.get(i - 1).setHide(false);
            }
        }
        for (HosDriveEventModel hosDriveEventModel : hosDriveEventModels) {
            if (hosDriveEventModel.getDriverState() == DriverState.DRIVING) {

                hosDriveEventModel.setHide(false);
            }
        }
    }

    /**
     * 为了能够进行违规计算
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() {

        HosDayModel result = null;
        try {
            result = (HosDayModel) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        result.hosDriveEventModels = new ArrayList<>();

        for (HosDriveEventModel hosDriveEventModel : this.hosDriveEventModels) {

            result.hosDriveEventModels.add(hosDriveEventModel.clone());
        }
        return result;
    }

    /**
     * 设置地址
     *
     * @param loctionId
     * @param location
     */
    public void setModelLocation(long loctionId, String location) {

        if (hosDriveEventModels != null) {
            for (HosDriveEventModel model : hosDriveEventModels) {
                if (loctionId == model.getLocalId()) {
                    model.setLocation(location);
                    return;
                }
            }
        }

        if (hosEventModels != null) {
            for (HosEventModel model : hosEventModels) {
                if (loctionId == model.getLocalId()) {
                    model.setLocation(location);
                    return;
                }
            }
        }
    }

    public boolean removeHosDriveEventModel(long localId) {

        boolean result = false;
        for (HosDriveEventModel hosDriveEventModel : hosDriveEventModels) {

            if (hosDriveEventModel.getLocalId() == localId) {

                hosDriveEventModels.remove(hosDriveEventModel);
                result = true;
                break;
            }
        }

        return result;
    }

    public void sortHosDriveEvent() {

        List arrayList = Arrays.asList(hosDriveEventModels.toArray());
        Collections.sort(arrayList);
        hosDriveEventModels.clear();
        hosDriveEventModels.addAll(arrayList);
    }

    public void sortHosEvent() {

        List arrayList = Arrays.asList(hosEventModels.toArray());
        Collections.sort(arrayList);
        hosEventModels.clear();
        hosEventModels.addAll(arrayList);
    }

    /**
     * getter and setter
     */
    public List<HosDriveEventModel> getHosDriveEventModels() {
        return hosDriveEventModels;
    }

    public void setHosDriveEventModels(List<HosDriveEventModel> hosDriveEventModels) {
        this.hosDriveEventModels = hosDriveEventModels;
    }

    public List<HosEventModel> getHosEventModels() {
        return hosEventModels;
    }

    public void setHosEventModels(List<HosEventModel> hosEventModels) {
        this.hosEventModels = hosEventModels;
    }

    public DriverState getStartDriverState() {
        return startDriverState;
    }

    public void setStartDriverState(DriverState startDriverState) {
        this.startDriverState = startDriverState;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getStartEventOrigin() {
        return startEventOrigin;
    }

    public void setStartEventOrigin(int startEventOrigin) {
        this.startEventOrigin = startEventOrigin;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

}
