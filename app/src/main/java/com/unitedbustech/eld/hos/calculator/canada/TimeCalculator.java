package com.unitedbustech.eld.hos.calculator.canada;

import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.RuleType;
import com.unitedbustech.eld.domain.entry.Rule;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.hos.model.HosAdverseDriveEventModel;
import com.unitedbustech.eld.hos.model.HosDataModel;
import com.unitedbustech.eld.hos.model.HosDayModel;
import com.unitedbustech.eld.hos.model.HosDriveEventModel;
import com.unitedbustech.eld.util.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author yufei0213
 * @date 2018/7/30
 * @description TimeCalculator
 */
class TimeCalculator {

    private Rule rule;

    private List<HosDayModel> allHosDayModel;
    private List<HosDriveEventModel> allEvent;

    private Date now;
    private Date shiftStartTime;

    TimeCalculator(Rule rule) {

        allHosDayModel = new ArrayList<>();
        allEvent = new ArrayList<>();

        this.rule = rule;
    }

    public HosDataModel invoke() {

        HosDataModel hosDataModel = new HosDataModel(this.rule);

        for (HosDayModel hosDayModel : ModelCenter.getInstance().getAllHosDayModels()) {

            allHosDayModel.add((HosDayModel) hosDayModel.clone());
        }
        for (HosDayModel hosDayModel : allHosDayModel) {

            for (HosDriveEventModel hosDriveEventModel : hosDayModel.getHosDriveEventModels()) {

                allEvent.add(hosDriveEventModel);
            }
        }

        Collections.sort(allEvent);
        //删除全部重复事件
        HosDriveEventModel lastModel = null;
        List<HosDriveEventModel> removeList = new ArrayList<>();
        for (HosDriveEventModel model : allEvent) {

            if (lastModel != null && lastModel.getDriverState() == model.getDriverState()) {

                removeList.add(lastModel);
            }

            lastModel = model;
        }
        allEvent.removeAll(removeList);

        now = new Date();

        if (allEvent.isEmpty()) {

            //如果没有事件，默认当前状态为OFF
            hosDataModel.setCurrentState(DriverState.OFF_DUTY);
        } else {

            //列表中第一个事件，代表当前司机的状态
            hosDataModel.setCurrentState(allEvent.get(0).getDriverState());
        }

        boolean hasAdverse = false;
        //Shift周期内，全部驾驶相关的事件(排除重复事件)
        List<HosDriveEventModel> shiftHosDriveEventList = getShiftHosDriveModels(allEvent);

        if (!shiftHosDriveEventList.isEmpty()) {

            //判断是否有有效的Adverse
            hasAdverse = hasAdverse(shiftHosDriveEventList);
        }

        //Shift周期内的ODND和Driving
        calShiftOdndAndDriving(hosDataModel, shiftHosDriveEventList, now);
        //计算当天ODND和Driving
        calTodayOdndAndDriving(hosDataModel, now);
        //计算当前Break时间
        hosDataModel.setBreakSecond(calculateBreak());
        //计算Cycle时间
        hosDataModel.setCycleSecond(calculateCycle());
        //判断当前是否有Day的OFF、14天Cycle的OFF或者15天OFF的违规
        if (!hasViolation(hosDataModel)) {

            //计算剩余驾驶时间
            hosDataModel.setDriveSecond(calculateLeftDrive(hosDataModel, hasAdverse, shiftStartTime, now));
        }

        return hosDataModel;
    }

    /**
     * 判断是否有有效的Adverse
     *
     * @param eventModels Shift周期内的全部事件
     * @return 是否有有效的Adverse
     */
    private boolean hasAdverse(List<HosDriveEventModel> eventModels) {

        boolean result = false;

        if (!eventModels.isEmpty()) {

            List<HosAdverseDriveEventModel> adverseDriveEventModels = ModelCenter.getInstance()
                    .getAdverseDriving(eventModels.get(eventModels.size() - 1).getDate(), now);
            if (!adverseDriveEventModels.isEmpty()) {

                HosAdverseDriveEventModel firstAdverse = adverseDriveEventModels.get(adverseDriveEventModels.size() - 1);
                List<HosDriveEventModel> tempEvents = new ArrayList<>();
                for (HosDriveEventModel model : eventModels) {

                    if (model.getDate().getTime() <= firstAdverse.getDate().getTime()) {

                        tempEvents.add(model);
                    }
                }

                HosDataModel hosDataModel = new HosDataModel();

                calShiftOdndAndDriving(hosDataModel, tempEvents, firstAdverse.getDate());
                calTodayOdndAndDriving(hosDataModel, firstAdverse.getDate());

                result = calculateLeftDrive(hosDataModel, false, shiftStartTime, firstAdverse.getDate()) > 0;
            }
        }

        return result;
    }

    /**
     * 计算Shift周期内的ODND和Driving
     *
     * @param hosDataModel           HOS 计算结果
     * @param shiftHosDriveEventList Shift周期内的全部事件
     * @param endTime                计算截止时间
     */
    private void calShiftOdndAndDriving(HosDataModel hosDataModel, List<HosDriveEventModel> shiftHosDriveEventList, Date endTime) {

        if (!shiftHosDriveEventList.isEmpty()) {

            //计算Shift时长
            hosDataModel.setShiftSecond(TimeUtil.intervalSecond(endTime, shiftHosDriveEventList.get(shiftHosDriveEventList.size() - 1).getDate()));
            //如果当前状态是工作状态，计算本次连续工作时长
            if (DriverState.isOnDuty(shiftHosDriveEventList.get(0).getDriverState())) {

                hosDataModel.setOdndSecond(TimeUtil.intervalSecond(endTime, shiftHosDriveEventList.get(0).getDate()));
            }
            //如果当前状态是Driving，计算本次连续Driving时间
            if (shiftHosDriveEventList.get(0).getDriverState().equals(DriverState.DRIVING)) {

                //当前累计Drive时长
                hosDataModel.setPastSecond(TimeUtil.intervalSecond(endTime, shiftHosDriveEventList.get(0).getDate()));
            }
        }
        //Driving时长
        for (int i = 1; i < shiftHosDriveEventList.size(); i++) {

            //计算累计工作时长
            if (DriverState.isOnDuty(shiftHosDriveEventList.get(i).getDriverState())) {

                hosDataModel.setOdndSecond(hosDataModel.getOdndSecond() +
                        TimeUtil.intervalSecond(shiftHosDriveEventList.get(i - 1).getDate(), shiftHosDriveEventList.get(i).getDate()));
            }
            //计算累计Driving时长
            if (shiftHosDriveEventList.get(i).getDriverState().equals(DriverState.DRIVING)) {

                hosDataModel.setPastSecond(hosDataModel.getPastSecond() +
                        TimeUtil.intervalSecond(shiftHosDriveEventList.get(i - 1).getDate(), shiftHosDriveEventList.get(i).getDate()));
            }
        }
    }

    /**
     * 计算当天ODND和Driving时长
     *
     * @param hosDataModel HOS 计算结果
     */
    private void calTodayOdndAndDriving(HosDataModel hosDataModel, Date now) {

        //获取当天的模型
        HosDayModel hosDayModel = null;
        for (HosDayModel dayModel : allHosDayModel) {

            if (TimeUtil.isSameDay(now, dayModel.getDate())) {

                hosDayModel = (HosDayModel) dayModel.clone();
                break;
            }
        }
        if (hosDayModel == null) {

            return;
        }

        List<HosDriveEventModel> todayHosEventModels = hosDayModel.getHosDriveEventModels();
        //新建零点事件
        HosDriveEventModel hosDriveEventModel = new HosDriveEventModel();
        hosDriveEventModel.setDriverState(hosDayModel.getStartDriverState());
        hosDriveEventModel.setDate(TimeUtil.getDayBegin(hosDayModel.getDate()));
        todayHosEventModels.add(hosDriveEventModel);

        List<HosDriveEventModel> removeList = new ArrayList<>();
        for (HosDriveEventModel model : todayHosEventModels) {

            if (model.getDate().getTime() > now.getTime()) {

                removeList.add(model);
            }
        }
        todayHosEventModels.removeAll(removeList);

        //如果当前状态是工作状态，计算本次连续工作时长
        if (DriverState.isOnDuty(todayHosEventModels.get(0).getDriverState())) {

            //计算今天累计的工作时长
            if (TimeUtil.isSameDay(now, todayHosEventModels.get(0).getDate())) {

                hosDataModel.setTodayOdndSecond(TimeUtil.intervalSecond(now, todayHosEventModels.get(0).getDate()));
            } else {

                hosDataModel.setTodayOdndSecond(TimeUtil.intervalSecond(now, TimeUtil.getDayBegin(now)));
            }
        }
        //如果当前状态是Driving，计算本次连续Driving时间
        if (todayHosEventModels.get(0).getDriverState().equals(DriverState.DRIVING)) {

            //计算今天累计的Drive时长
            if (TimeUtil.isSameDay(now, todayHosEventModels.get(0).getDate())) {

                hosDataModel.setTodayDriveSecond(TimeUtil.intervalSecond(now, todayHosEventModels.get(0).getDate()));
            } else {

                hosDataModel.setTodayDriveSecond(TimeUtil.intervalSecond(now, TimeUtil.getDayBegin(now)));
            }
        }
        //Driving时长
        for (int i = 1; i < todayHosEventModels.size(); i++) {

            //计算累计工作时长
            if (DriverState.isOnDuty(todayHosEventModels.get(i).getDriverState())) {

                //计算今天累计的工作时长
                if (TimeUtil.isSameDay(now, todayHosEventModels.get(i).getDate())) {

                    hosDataModel.setTodayOdndSecond(hosDataModel.getTodayOdndSecond() +
                            TimeUtil.intervalSecond(todayHosEventModels.get(i - 1).getDate(), todayHosEventModels.get(i).getDate()));
                }
            }
            //计算累计Driving时长
            if (todayHosEventModels.get(i).getDriverState().equals(DriverState.DRIVING)) {

                //计算今天累计的Drive时长
                if (TimeUtil.isSameDay(now, todayHosEventModels.get(0).getDate())) {

                    hosDataModel.setTodayDriveSecond(hosDataModel.getTodayDriveSecond() +
                            TimeUtil.intervalSecond(todayHosEventModels.get(i - 1).getDate(), todayHosEventModels.get(i).getDate()));
                }
            }
        }
    }

    /**
     * 计算Break 时间
     *
     * @return
     */
    private long calculateBreak() {

        long result = 0;
        if (!allEvent.isEmpty()) {

            Date lastRestDate = now;
            for (HosDriveEventModel hosDriveEventModel : allEvent) {

                if (!DriverState.isOnDuty(hosDriveEventModel.getDriverState())) {

                    lastRestDate = hosDriveEventModel.getDate();
                    continue;
                } else {

                    break;
                }
            }

            result = TimeUtil.intervalSecond(now, lastRestDate);
        }

        return result;
    }

    /**
     * 计算当前Cycle时间
     *
     * @return Cycle时间
     */
    private long calculateCycle() {

        long result = 0;
        int days = rule.getDutyDays();
        if (allHosDayModel.size() < days) {

            return result;
        }

        List<HosDriveEventModel> hosDriveEventModelList = new ArrayList<>();
        for (HosDayModel hosDayModel : allHosDayModel) {

            for (HosDriveEventModel hosDriveEventModel : hosDayModel.getHosDriveEventModels()) {

                hosDriveEventModelList.add(hosDriveEventModel);
            }
        }

        if (hosDriveEventModelList.isEmpty()) {

            return result;
        }

        Collections.sort(hosDriveEventModelList);
        //删除全部重复事件
        HosDriveEventModel lastModel = null;
        List<HosDriveEventModel> removeList = new ArrayList<>();
        for (HosDriveEventModel model : hosDriveEventModelList) {

            if (lastModel != null && lastModel.getDriverState() == model.getDriverState()) {

                removeList.add(lastModel);
            }

            lastModel = model;
        }
        hosDriveEventModelList.removeAll(removeList);

        long restTime = 0;
        //判断最新的事件
        if (!DriverState.isOnDuty(hosDriveEventModelList.get(0).getDriverState())) {

            //如果最新的事件不是工作事件，并且持续时间大于等于36小时或者72小时
            if (now.getTime() - hosDriveEventModelList.get(0).getDate().getTime() >= rule.getResetCycleDutyTime() * 60 * 1000L) {

                return 0;
            }
            restTime += (now.getTime() - hosDriveEventModelList.get(0).getDate().getTime()) / 1000;
        }

        int index = 0;
        boolean findReset = false;
        //循环寻找是否已经休息足够36小时或者72小时
        for (int i = 1; i < hosDriveEventModelList.size(); i++) {

            if (!DriverState.isOnDuty(hosDriveEventModelList.get(i).getDriverState())) {

                restTime += TimeUtil.intervalSecond(hosDriveEventModelList.get(i - 1).getDate(), hosDriveEventModelList.get(i).getDate());
            } else {

                index = i;
                restTime = 0;
            }

            if (restTime >= rule.getResetCycleDutyTime() * 60) {

                findReset = true;
                break;
            }
        }

        if (findReset) {

            Date date = TimeUtil.getDayBegin(hosDriveEventModelList.get(index).getDate());
            Date defaultStartDate = TimeUtil.getDayBegin(TimeUtil.getPreviousDate(now, days - 1));

            //如果找到的重置时间，在Cycle的周期之外或者与Cycle的周期相同
            if (date.getTime() <= defaultStartDate.getTime()) {

                for (int i = 0; i < days; i++) {

                    HosDayModel hosDayModel = allHosDayModel.get(i);
                    result += hosDayModel.getOnDutyTime();
                }
            } else {

                days = TimeUtil.differentDays(date, now) + 1;
                for (int i = 0; i < days; i++) {

                    HosDayModel hosDayModel = allHosDayModel.get(i);
                    result += hosDayModel.getOnDutyTime();
                }
            }
        } else {

            for (int i = 0; i < days; i++) {

                HosDayModel hosDayModel = allHosDayModel.get(i);
                result += hosDayModel.getOnDutyTime();
            }
        }

        return result;
    }

    /**
     * 计算剩余的可驾驶时间
     * <p>
     * 单位：秒
     *
     * @param hosDataModel   Hos计算结果
     * @param hasAdverse     Shift周期内是否有Adverse Driving
     * @param shiftStartTime Shift开始时间
     * @param now            当前时间
     * @return 剩余的可驾驶时间
     */
    private long calculateLeftDrive(HosDataModel hosDataModel, boolean hasAdverse, Date shiftStartTime, Date now) {

        //13小时要求剩余时间(加上不利条件驾驶)
        long thirteenShiftLimitLeft = ((hasAdverse ? 2 * 60 : 0) + 13 * 60) * 60 - hosDataModel.getPastSecond();
        //16小时Shift要求剩余的时间
        long sixteenShiftLimitLeft = 0;
        //14小时Shift要求剩余时间
        long fourteenShiftLimitLeft = 14 * 60 * 60 - hosDataModel.getOdndSecond();
        //13小时Day要求剩余的时间
        long thirteenDayLimitLeft = ((hasAdverse ? 2 * 60 : 0) + 13 * 60) * 60 - hosDataModel.getTodayDriveSecond();
        //14小时Day要求的剩余时间
        long fourteenDayLimitLeft = 14 * 60 * 60 - hosDataModel.getTodayOdndSecond();
        //cycle要求的剩余时间
        long cycleLimitLeft = rule.getDutyTime() * 60 - hosDataModel.getCycleSecond();

        long result = thirteenShiftLimitLeft;
        if (shiftStartTime != null) {

            sixteenShiftLimitLeft = 16 * 60 * 60 - ((now.getTime() - shiftStartTime.getTime()) / 1000);
            if (result > sixteenShiftLimitLeft) {

                result = sixteenShiftLimitLeft;
            }
        }
        if (result > fourteenShiftLimitLeft) {

            result = fourteenShiftLimitLeft;
        }
        if (result > cycleLimitLeft) {

            result = cycleLimitLeft;
        }
        if (result > thirteenDayLimitLeft) {

            result = thirteenDayLimitLeft;
        }
        if (result > fourteenDayLimitLeft) {

            result = fourteenDayLimitLeft;
        }
        return result < 0 ? 0 : result;
    }

    /**
     * 检查当前是否有违规
     *
     * @param hosDataModel HOS计算结果
     * @return 是否有违规
     */
    private boolean hasViolation(HosDataModel hosDataModel) {

        return hasDayOffViolation() && hasOffViolation() && hasCycle24OffViolation(hosDataModel.getCycleSecond());
    }

    /**
     * 检查是否有Day的违规
     *
     * @return 是否有Day的违规
     */
    private boolean hasDayOffViolation() {

        boolean result = false;

        Date preDate = TimeUtil.getPreviousDate(now, 1);

        HosDayModel preHosDayModel = ModelCenter.getInstance().getHosDayModel(preDate); //前一天的HosDayModel
        HosDayModel hosDayModel = ModelCenter.getInstance().getHosDayModel(now); //当天的HosDayModel
        if (preHosDayModel == null || hosDayModel == null) {

            return result;
        }

        List<HosDriveEventModel> hosDriveEventModelList = new ArrayList<>();
        List<HosDriveEventModel> preHosDriveEventModelList = new ArrayList<>();

        hosDriveEventModelList.addAll(hosDayModel.getHosDriveEventModels());
        preHosDriveEventModelList.addAll(preHosDayModel.getHosDriveEventModels());

        //新建一个零点事件
        HosDriveEventModel hosDriveEventModel = new HosDriveEventModel();
        hosDriveEventModel.setDate(TimeUtil.getDayBegin(hosDayModel.getDate()));
        hosDriveEventModel.setDriverState(hosDayModel.getStartDriverState());
        hosDriveEventModelList.add(hosDriveEventModel);

        Collections.sort(hosDriveEventModelList);
        //删除重复事件
        List<HosDriveEventModel> removeList = new ArrayList<>();
        HosDriveEventModel lastModel = null;
        for (HosDriveEventModel model : hosDriveEventModelList) {

            if (lastModel != null && lastModel.getDriverState() == model.getDriverState()) {

                removeList.add(lastModel);
            }

            lastModel = model;
        }
        hosDriveEventModelList.removeAll(removeList);

        //新建零点事件
        HosDriveEventModel preHosDriveEventModel = new HosDriveEventModel();
        preHosDriveEventModel.setDate(TimeUtil.getDayBegin(preHosDayModel.getDate()));
        preHosDriveEventModel.setDriverState(preHosDayModel.getStartDriverState());
        preHosDriveEventModelList.add(preHosDriveEventModel);

        Collections.sort(preHosDriveEventModelList);
        //删除重复事件
        List<HosDriveEventModel> preMoveList = new ArrayList<>();
        HosDriveEventModel lasPreModel = null;
        for (HosDriveEventModel model : preHosDriveEventModelList) {

            if (lasPreModel != null && lasPreModel.getDriverState() == model.getDriverState()) {

                preMoveList.add(lasPreModel);
            }

            lasPreModel = model;
        }
        preHosDriveEventModelList.removeAll(preMoveList);

        //当天的结束时间
        long todayEndTime = TimeUtil.getDayBegin(TimeUtil.getNextDate(now, 1)).getTime();
        //昨天的结束时间
        long yesterdayEndTime = TimeUtil.getDayBegin(now).getTime();
        //计算当天剩余的时间
        long todayLeftTime = 0;
        Date temp = new Date();
        //如果是今天，则计算当天的剩余时间，否则当天的剩余时间为0
        if (TimeUtil.isSameDay(temp, now)) {

            todayLeftTime = (todayEndTime - temp.getTime()) / 1000;
        } else {

            temp = new Date(todayEndTime);
        }

        //当最后一个事件是OFF时，此字段有值
        long lastOffEventTime = 0;
        //当天最大单次休息时长，不包含最后一个事件
        long todayMaxRestTime = 0;
        //当天累计休息时间，每段休息时间都大于30分钟，不包含最后一个事件
        long todayRestTime = 0;
        //当天可能的最大单次连续休息时长
        long todayMaxRest = 0;
        //当天可能的总的最大休息时间
        long todayRestTotal = 0;
        //前一天最大单次休息时长
        long yesterdayMaxRestTime = 0;
        //前一天累计休息时间，每段休息都大于30分钟
        long yesterdayRestTime = 0;

        //计算最新的事件
        if (!DriverState.isOnDuty(hosDriveEventModelList.get(0).getDriverState())) {

            lastOffEventTime = (temp.getTime() - hosDriveEventModelList.get(0).getDate().getTime()) / 1000;
        }
        //计算全部事件
        for (int i = 1; i < hosDriveEventModelList.size(); i++) {

            if (!DriverState.isOnDuty(hosDriveEventModelList.get(i).getDriverState())) {

                long time = TimeUtil.intervalSecond(hosDriveEventModelList.get(i - 1).getDate(), hosDriveEventModelList.get(i).getDate());
                if (time > 30 * 60) {

                    todayRestTime += time;
                }
                if (todayMaxRestTime < time) {

                    todayMaxRestTime = time;
                }
            }
        }

        //计算最新的事件
        if (!DriverState.isOnDuty(preHosDriveEventModelList.get(0).getDriverState())) {

            long time = (yesterdayEndTime - preHosDriveEventModelList.get(0).getDate().getTime()) / 1000;
            if (time > 30 * 60) {

                yesterdayRestTime += time;
            }
            if (yesterdayMaxRestTime < time) {

                yesterdayMaxRestTime = time;
            }
        }
        //计算全部事件
        for (int i = 1; i < preHosDriveEventModelList.size(); i++) {

            if (!DriverState.isOnDuty(preHosDriveEventModelList.get(i).getDriverState())) {

                long time = TimeUtil.intervalSecond(preHosDriveEventModelList.get(i - 1).getDate(), preHosDriveEventModelList.get(i).getDate());
                if (time > 30 * 60) {

                    yesterdayRestTime += time;
                }
                if (yesterdayMaxRestTime < time) {

                    yesterdayMaxRestTime = time;
                }
            }
        }

        //最后一个可能的OFF事件可能的最大持续时间
        long lastOffTime = lastOffEventTime + todayLeftTime;

        todayRestTotal = lastOffTime > 30 * 60 ? todayRestTime + lastOffTime : todayRestTime;
        todayMaxRest = todayMaxRestTime >= lastOffTime ? todayMaxRestTime : lastOffTime;

        //如果当天累计休息时间加上今天的剩余时间不足10小时，并且当天最大单次连续休息时间不足8小时，则违反10小时规则
        if (todayRestTotal < 10 * 60 * 60 && todayMaxRest < 8 * 60 * 60) {

            result = true;
        }

        //如果前一天最大单次连续休息大于等于8小时，并且累计休息时间不足10小时，则开启20小时规则检测
        if (yesterdayMaxRestTime >= 8 * 60 * 60 && yesterdayRestTime < 10 * 60 * 60) {

            //要求的当天最小单次连续休息时间
            long todayMinRestTime = 10 * 60 * 60 - yesterdayMaxRestTime + 8 * 60 * 60;

            //如果当天最大单次连续休息时间不足，则违反20小时限制
            if (todayMaxRest < todayMinRestTime) {

                result = true;
            } else {

                //如果两天的总的休息时间不足20小时，则违反20小时限制
                if (yesterdayRestTime + todayRestTotal < 20 * 60 * 60) {

                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * 检查当前是否有OFF的违规
     *
     * @return 是否有OFF的违规
     */
    private boolean hasOffViolation() {

        boolean result = false;

        List<HosDayModel> hosDayModels = new ArrayList<>();
        hosDayModels.addAll(allHosDayModel);
        if (hosDayModels == null || hosDayModels.isEmpty()) {

            return result;
        }

        List<HosDriveEventModel> hosDriveEventModelList = new ArrayList<>();

        HosDayModel lastHosDayModel = hosDayModels.get(hosDayModels.size() - 1);
        //如果第一天的开始状态是OFF,则新建一个零点事件
        if (!DriverState.isOnDuty(lastHosDayModel.getStartDriverState())) {

            //新建一个零点事件
            HosDriveEventModel hosDriveEventModel = new HosDriveEventModel();
            hosDriveEventModel.setDate(TimeUtil.getDayBegin(hosDayModels.get(hosDayModels.size() - 1).getDate()));
            hosDriveEventModel.setDriverState(hosDayModels.get(hosDayModels.size() - 1).getStartDriverState());
            hosDriveEventModelList.add(hosDriveEventModel);
        }
        for (HosDayModel hosDayModel : hosDayModels) {

            if (hosDayModel.getHosDriveEventModels() != null && !hosDayModel.getHosDriveEventModels().isEmpty()) {

                hosDriveEventModelList.addAll(hosDayModel.getHosDriveEventModels());
            }
        }

        Collections.sort(hosDriveEventModelList);
        //删除重复事件
        List<HosDriveEventModel> removeList = new ArrayList<>();
        HosDriveEventModel lastModel = null;
        for (HosDriveEventModel model : hosDriveEventModelList) {

            if (lastModel != null && lastModel.getDriverState() == model.getDriverState()) {

                removeList.add(lastModel);
            }

            lastModel = model;
        }
        hosDriveEventModelList.removeAll(removeList);

        int size = hosDriveEventModelList.size();

        boolean find24Off = false;
        long restTime = 0;
        for (int i = size - 1; i > 0; i--) {

            HosDriveEventModel model = hosDriveEventModelList.get(i);
            if (!DriverState.isOnDuty(model.getDriverState())) {

                restTime += TimeUtil.intervalSecond(hosDriveEventModelList.get(i - 1).getDate(), model.getDate());
                if (restTime >= 24 * 60 * 60) {

                    find24Off = true;
                    break;
                }
            } else {

                restTime = 0;
            }
        }

        if (!find24Off) {

            if (!DriverState.isOnDuty(hosDriveEventModelList.get(0).getDriverState())) {

                restTime += TimeUtil.intervalSecond(new Date(), hosDriveEventModelList.get(0).getDate());
                if (restTime >= 24 * 60 * 60) {

                    find24Off = true;
                }
            }
        }
        if (!find24Off) {

            result = true;
        }

        return result;
    }

    /**
     * 检查当前Cycle的OFF违规
     * 注意，只有加拿大的14天Cycle情况下，此方法才有效
     *
     * @param cycleTime 当前cycle时间
     * @return
     */
    private boolean hasCycle24OffViolation(long cycleTime) {

        boolean result = false;

        if (rule.getId() == RuleType.CANADA_14D_120H && cycleTime >= 70 * 60 * 60) {

            if (!findCycle24Off()) {

                result = true;
            }
        }

        return result;
    }

    /**
     * 检查odnd满70小时前，是否有一个连续24小时的休息
     * <p>
     * 注意，调用此方法时，odnd时长一定大于等于70
     *
     * @return 是否有连续24小时的off
     */
    private boolean findCycle24Off() {

        boolean result = false;

        List<HosDriveEventModel> hosDriveEventModels = new ArrayList<>();
        for (HosDayModel hosDayModel : allHosDayModel) {

            for (HosDriveEventModel hosDriveEventModel : hosDayModel.getHosDriveEventModels()) {

                hosDriveEventModels.add(hosDriveEventModel);
            }
        }

        Collections.sort(hosDriveEventModels);
        //删除全部重复事件
        HosDriveEventModel lastModel = null;
        List<HosDriveEventModel> removeList = new ArrayList<>();
        for (HosDriveEventModel model : hosDriveEventModels) {

            if (lastModel != null && lastModel.getDriverState() == model.getDriverState()) {

                removeList.add(lastModel);
            }

            lastModel = model;
        }
        hosDriveEventModels.removeAll(removeList);

        int size = hosDriveEventModels.size();
        Date firstEventDate = TimeUtil.getDayBegin(hosDriveEventModels.get(size - 1).getDate()); //第一个事件的时间
        Date startDate = TimeUtil.getPreviousDate(now, rule.getDutyDays() - 1); //Cycle开始的时间
        //这里处理本地数据不满足Cycle天数的问题
        if (TimeUtil.differentDays(startDate, firstEventDate) > 0) {

            result = true;
        } else {

            HosDayModel hosDayModel = ModelCenter.getInstance().getHosDayModel(firstEventDate);

            //新建一个零点事件
            HosDriveEventModel hosDriveEventModel = new HosDriveEventModel();
            hosDriveEventModel.setDate(TimeUtil.getDayBegin(hosDayModel.getDate()));
            hosDriveEventModel.setDriverState(hosDayModel.getStartDriverState());
            hosDriveEventModels.add(hosDriveEventModel);

            int index = 0;
            boolean findIndex = false;
            long odndTime = 0;
            for (int i = size - 1; i > 0; i--) {

                HosDriveEventModel model = hosDriveEventModels.get(i);
                if (DriverState.isOnDuty(model.getDriverState())) {

                    odndTime += TimeUtil.intervalSecond(hosDriveEventModels.get(i - 1).getDate(), model.getDate());
                    if (odndTime >= 70 * 60 * 60) {

                        findIndex = true;
                        index = i;
                        break;
                    }
                }
            }
            if (!findIndex) {

                if (DriverState.isOnDuty(hosDriveEventModels.get(0).getDriverState())) {

                    odndTime += TimeUtil.intervalSecond(now, hosDriveEventModels.get(0).getDate());
                    if (odndTime >= 70 * 60 * 60) {

                        findIndex = true;
                    }
                }
            }

            if (findIndex) {

                hosDriveEventModels.subList(index, size);
                size = hosDriveEventModels.size();

                long restTime = 0;
                for (int i = size - 1; i > 0; i--) {

                    HosDriveEventModel model = hosDriveEventModels.get(i);
                    if (!DriverState.isOnDuty(model.getDriverState())) {

                        restTime += TimeUtil.intervalSecond(hosDriveEventModels.get(i - 1).getDate(), model.getDate());
                        if (restTime >= 24 * 60 * 60) {

                            result = true;
                            break;
                        }
                    } else {

                        restTime = 0;
                    }
                }
            }
        }

        return result;
    }

    /**
     * 获取全部驾驶相关的事件
     *
     * @param eventModelList 所有事件
     * @return 事件列表
     */
    private List<HosDriveEventModel> getShiftHosDriveModels(List<HosDriveEventModel> eventModelList) {

        //获取全部驾驶相关的事件
        List<HosDriveEventModel> allEvent = new ArrayList<>();
        if (eventModelList != null) {

            allEvent.addAll(eventModelList);
        }

        //如果没有事件
        if (allEvent.isEmpty()) {

            shiftStartTime = null;
            return new ArrayList<>();
        }

        //累计休息时间
        long accuRestTime = 0;
        //上一个SB事件的持续时间，lastSbTime >= 2
        long lastSbTime = 0;
        //是否找到上一个符合条件的SB
        boolean findLastSb = false;
        //判断最新的事件
        if (!DriverState.isOnDuty(allEvent.get(0).getDriverState())) {

            //如果最新的事件不是工作事件，并且持续时间大于等于8小时
            accuRestTime += (now.getTime() - allEvent.get(0).getDate().getTime()) / 1000;
            if (accuRestTime >= 8 * 60 * 60) {

                shiftStartTime = null;
                return new ArrayList<>();
            }
            //如果最新的状态是SB，并且持续时间大于等于2小时
            if (allEvent.get(0).getDriverState() == DriverState.SLEEPER_BERTH && accuRestTime >= 2 * 60 * 60) {

                lastSbTime = accuRestTime;
                findLastSb = true;
            }
        }
        //循环寻找Shift的开始点
        for (int i = 1; i < allEvent.size(); i++) {

            if (!DriverState.isOnDuty(allEvent.get(i).getDriverState())) {

                accuRestTime += TimeUtil.intervalSecond(allEvent.get(i - 1).getDate(), allEvent.get(i).getDate());
            } else {

                accuRestTime = 0;
            }

            //如果连续休息时间大于等于8小时
            if (accuRestTime >= 8 * 60 * 60) {
                //找到第一个不是休息的事件
                for (int index = i; index >= 0; index--) {

                    if (DriverState.isOnDuty(allEvent.get(index).getDriverState())) {

                        shiftStartTime = allEvent.get(index).getDate();
                        return allEvent.subList(0, index + 1);
                    }
                }
                //如果走到这里，说明到现在都一直是休息状态
                shiftStartTime = null;
                return new ArrayList<>();
            } else {

                if (allEvent.get(i).getDriverState() == DriverState.SLEEPER_BERTH) {

                    long sbTime = TimeUtil.intervalSecond(allEvent.get(i - 1).getDate(), allEvent.get(i).getDate());
                    if (findLastSb) {

                        if (sbTime >= 2 * 60 * 60) {

                            if (sbTime + lastSbTime >= 10 * 60 * 60) {

                                //找到第一个不是休息的事件
                                for (int index = i; index >= 0; index--) {

                                    if (DriverState.isOnDuty(allEvent.get(index).getDriverState())) {

                                        shiftStartTime = allEvent.get(index).getDate();
                                        return allEvent.subList(0, index + 1);
                                    }
                                }

                                //如果走到这里，说明到现在都一直是休息状态，理论上不会走到这里
                                shiftStartTime = null;
                                return new ArrayList<>();
                            } else {

                                findLastSb = true;
                                lastSbTime = sbTime;
                            }
                        } else {

                            findLastSb = false;
                            lastSbTime = 0;
                        }
                    } else {

                        if (sbTime >= 2 * 60 * 60) {

                            findLastSb = true;
                            lastSbTime = sbTime;
                        }
                    }
                }
            }
        }
        //如果14天都没有找到休息够8小时的数据，那么就是全部事件
        int size = allEvent.size();
        for (int i = size - 1; i >= 0; i--) {

            //距离当前时间最久的那个非休息事件的时间，是Shift的开始时间
            if (DriverState.isOnDuty(allEvent.get(i).getDriverState())) {

                shiftStartTime = allEvent.get(i).getDate();
                break;
            }
        }
        return allEvent;
    }
}
