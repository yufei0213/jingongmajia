package com.unitedbustech.eld.hos.calculator.america;

import com.unitedbustech.eld.common.DriverState;
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

    TimeCalculator(Rule rule) {

        allHosDayModel = new ArrayList<>();
        allEvent = new ArrayList<>();

        this.rule = rule;
    }

    public HosDataModel invoke() {

        HosDataModel hosDataModel = new HosDataModel(this.rule);

        allHosDayModel.clear();
        allHosDayModel.addAll(ModelCenter.getInstance().getAllHosDayModels());
        allEvent.clear();
        for (HosDayModel hosDayModel : allHosDayModel) {

            for (HosDriveEventModel hosDriveEventModel : hosDayModel.getHosDriveEventModels()) {

                allEvent.add(hosDriveEventModel);
            }
        }
        Collections.sort(allEvent);

        now = new Date();

        if (allEvent.isEmpty()) {

            //如果没有事件，默认当前状态为OFF
            hosDataModel.setCurrentState(DriverState.OFF_DUTY);
        } else {

            //列表中第一个事件，代表当前司机的状态
            hosDataModel.setCurrentState(allEvent.get(0).getDriverState());
        }

        boolean hasAdverse = false;
        //Shift周期内，全部驾驶相关的事件
        List<HosDriveEventModel> shiftHosDriveEventList = getShiftHosDriveModels(allEvent);

        if (!shiftHosDriveEventList.isEmpty()) {

            //判断是否有有效的Adverse
            hasAdverse = hasAdverse(shiftHosDriveEventList);
            //计算Shift周期内的ODND和Driving
            calShiftOdndAndDriving(hosDataModel, shiftHosDriveEventList, now);
        }

        //计算当前Break时间
        hosDataModel.setBreakSecond(calculateBreak());
        //计算Cycle时间
        hosDataModel.setCycleSecond(calculateCycle());

        //计算剩余驾驶时间
        hosDataModel.setDriveSecond(calculateLeftDrive(hosDataModel, hasAdverse));

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

                result = calculateLeftDrive(hosDataModel, false) > 0;
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

        //如果当前状态是工作状态，计算本次连续工作时长
        if (DriverState.isOnDuty(shiftHosDriveEventList.get(0).getDriverState())) {

            //计算Shift时长
            hosDataModel.setShiftSecond(TimeUtil.intervalSecond(endTime, shiftHosDriveEventList.get(0).getDate()));
        }
        //如果当前状态是Driving，计算本次连续Driving时间
        if (shiftHosDriveEventList.get(0).getDriverState().equals(DriverState.DRIVING)) {

            hosDataModel.setPastSecond(TimeUtil.intervalSecond(endTime, shiftHosDriveEventList.get(0).getDate()));
        }

        //累计工作时长和Driving时长
        for (int i = 1; i < shiftHosDriveEventList.size(); i++) {

            if (DriverState.isOnDuty(shiftHosDriveEventList.get(i).getDriverState())) {

                //计算Shift时长
                hosDataModel.setShiftSecond(hosDataModel.getShiftSecond() +
                        TimeUtil.intervalSecond(shiftHosDriveEventList.get(i - 1).getDate(), shiftHosDriveEventList.get(i).getDate()));
            }
            if (shiftHosDriveEventList.get(i).getDriverState().equals(DriverState.DRIVING)) {

                //计算Driving时长
                hosDataModel.setPastSecond(hosDataModel.getPastSecond() +
                        TimeUtil.intervalSecond(shiftHosDriveEventList.get(i - 1).getDate(), shiftHosDriveEventList.get(i).getDate()));
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
        for (int i = 0; i < days; i++) {

            result += allHosDayModel.get(i).getOnDutyTime();
        }
        return result;
    }

    /**
     * 计算剩余的可驾驶时间
     * <p>
     * 单位：秒
     *
     * @param hosDataModel Hos计算结果
     * @param hasAdverse   Shift周期内是否有Adverse Driving
     * @return 剩余的可驾驶时间
     */
    private long calculateLeftDrive(HosDataModel hosDataModel, boolean hasAdverse) {

        //10小时要求剩余时间(加上不利条件驾驶)
        long tenLimitLeft = ((hasAdverse ? 2 * 60 : 0) + rule.getMaxDriving()) * 60 - hosDataModel.getPastSecond();
        //15小时要求剩余时间
        long fifteenLimitLeft = rule.getMaxDutyForDriving() * 60 - hosDataModel.getShiftSecond();
        //cycle要求的剩余时间
        long cycleLimitLeft = rule.getDutyTime() * 60 - hosDataModel.getCycleSecond();

        long result = tenLimitLeft;
        if (result > fifteenLimitLeft) {

            result = fifteenLimitLeft;
        }
        if (result > cycleLimitLeft) {

            result = cycleLimitLeft;
        }

        return result < 0 ? 0 : result;
    }

    /**
     * 获取全部驾驶相关的事件
     *
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

            return new ArrayList<>();
        }

        Date now = new Date();
        long restTime = 0;

        //判断最新的事件
        if (!DriverState.isOnDuty(allEvent.get(0).getDriverState())) {

            //如果最新的事件不是工作事件，并且持续时间大于等于8小时
            if (now.getTime() - allEvent.get(0).getDate().getTime() >= rule.getMinOffDutyForDriving() * 60 * 1000L) {

                return new ArrayList<>();
            }
            restTime += (now.getTime() - allEvent.get(0).getDate().getTime()) / 1000;
        }

        //循环寻找是否已经休息足够8小时
        for (int i = 1; i < allEvent.size(); i++) {

            if (!DriverState.isOnDuty(allEvent.get(i).getDriverState())) {

                restTime += TimeUtil.intervalSecond(allEvent.get(i - 1).getDate(), allEvent.get(i).getDate());
            } else {

                restTime = 0;
            }

            if (restTime >= rule.getMinOffDutyForDriving() * 60) {
                //找到第一个不是休息的事件
                for (int index = i; index >= 0; index--) {

                    if (DriverState.isOnDuty(allEvent.get(index).getDriverState())) {

                        return allEvent.subList(0, index + 1);
                    }
                }
                //如果走到这里，说明到现在都一直是休息状态
                return new ArrayList<>();
            }
        }
        //如果14天都没有找到休息够8小时的数据，那么就是全部事件
        return allEvent;
    }
}
