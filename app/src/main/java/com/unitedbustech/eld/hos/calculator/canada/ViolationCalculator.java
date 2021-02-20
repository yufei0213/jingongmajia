package com.unitedbustech.eld.hos.calculator.canada;

import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.RuleType;
import com.unitedbustech.eld.domain.entry.Rule;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.hos.model.HosAdverseDriveEventModel;
import com.unitedbustech.eld.hos.model.HosDataModel;
import com.unitedbustech.eld.hos.model.HosDayModel;
import com.unitedbustech.eld.hos.model.HosDriveEventModel;
import com.unitedbustech.eld.hos.violation.CanadaViolationModel;
import com.unitedbustech.eld.hos.violation.ViolationEvent;
import com.unitedbustech.eld.hos.violation.ViolationModel;
import com.unitedbustech.eld.util.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author yufei0213
 * @date 2018/7/16
 * @description ViolationCalculator
 */
class ViolationCalculator {

    /**
     * 规则
     */
    private Rule rule;

    /**
     * 计算违规的那一天的零点
     */
    private Date now;

    ViolationCalculator(Rule rule) {

        this.rule = rule;
    }

    /**
     * 根据日期，获取当天的违规信息
     *
     * @param date 日期
     * @return ViolationModel
     */
    public ViolationModel getHosViolationInfo(Date date) {

        List<CanadaViolationModel> results = new ArrayList<>();

        now = date;

        List<HosDayModel> oraHosDayModels = new ArrayList<>();
        List<HosDayModel> hosDayModels = new ArrayList<>();

        //获取所有天的模型
        List<HosDayModel> tempList = ModelCenter.getInstance().getAllHosDayModels();
        for (HosDayModel dayModel : tempList) {

            oraHosDayModels.add((HosDayModel) dayModel.clone());
        }

        //删除每天的重复事件
        for (HosDayModel dayModel : oraHosDayModels) {

            List<HosDriveEventModel> eventModels = dayModel.getHosDriveEventModels();
            if (eventModels != null && !eventModels.isEmpty()) {

                HosDriveEventModel lastModel = null;
                List<HosDriveEventModel> removeList = new ArrayList<>();
                for (HosDriveEventModel model : eventModels) {

                    if (lastModel != null && lastModel.getDriverState() == model.getDriverState()) {

                        removeList.add(lastModel);
                    }

                    lastModel = model;
                }
                eventModels.removeAll(removeList);
            }
        }

        //获取当天的模型
        HosDayModel hosDayModel = null;
        for (HosDayModel dayModel : oraHosDayModels) {

            if (TimeUtil.isSameDay(date, dayModel.getDate())) {

                hosDayModel = (HosDayModel) dayModel.clone();
                break;
            }
        }
        //没有本天的模型
        if (hosDayModel == null) {

            return new ViolationModel();
        }

        //如果本天的开始状态是Driving
        if (hosDayModel.getStartDriverState() == DriverState.DRIVING) {

            //获取当天之前的所有天的模型
            hosDayModels.clear();
            for (HosDayModel model : oraHosDayModels) {

                if (model.getDate().getTime() < hosDayModel.getDate().getTime()) {

                    hosDayModels.add((HosDayModel) model.clone());
                }
            }

            //新建一个假的driving事件，认为是当天0点的事件
            HosDriveEventModel hosDriveEventModel = new HosDriveEventModel();
            hosDriveEventModel.setDate(hosDayModel.getDate());
            hosDriveEventModel.setDriverState(hosDayModel.getStartDriverState());

            //Driving事件之后的全部事件
            List<HosDriveEventModel> afterDrivingModelList = new ArrayList<>();
            //违规计算的截止时间
            Date endTime = null;
            if (hosDayModel.getHosDriveEventModels().size() > 0) {

                endTime = hosDayModel.getHosDriveEventModels().get(hosDayModel.getHosDriveEventModels().size() - 1).getDate();
                afterDrivingModelList.addAll(hosDayModel.getHosDriveEventModels());
            } else {

                if (TimeUtil.isSameDay(date, new Date())) {

                    endTime = new Date();
                } else {

                    endTime = new Date(TimeUtil.getNextDate(now, 1).getTime() - 1);
                }
            }

            CanadaViolationModel violationModel = calEventByHosDayModel(hosDayModels,
                    afterDrivingModelList,
                    hosDriveEventModel,
                    endTime);

            results.add(violationModel);
        }

        //遍历当天全部事件，计算所有Driving事件的违规
        for (int i = hosDayModel.getHosDriveEventModels().size() - 1; i >= 0; i--) {

            hosDayModels.clear();
            for (HosDayModel model : oraHosDayModels) {

                hosDayModels.add((HosDayModel) model.clone());
            }
            HosDriveEventModel hosDriveEventModel = hosDayModel.getHosDriveEventModels().get(i);
            if (hosDriveEventModel.getDriverState() == DriverState.DRIVING) {

                Date endTime = null;
                if (i == 0) {

                    if (TimeUtil.isSameDay(new Date(), hosDriveEventModel.getDate())) {

                        endTime = new Date();
                    } else {

                        endTime = new Date(TimeUtil.getNextDate(hosDayModel.getDate(), 1).getTime() - 1);
                    }
                } else {

                    endTime = hosDayModel.getHosDriveEventModels().get(i - 1).getDate();
                }

                //将事件追溯到Driving事件的前一个事件
                List<HosDayModel> temp = getHosDayModelsByDrivingEvent(hosDriveEventModel, hosDayModels);
                //Driving事件之后的全部事件
                List<HosDriveEventModel> afterDrivingModelList = getHosDriveEventModelsByDrivingEvent(hosDayModels, hosDriveEventModel);

                CanadaViolationModel violationModel = calEventByHosDayModel(temp,
                        afterDrivingModelList,
                        hosDriveEventModel,
                        endTime);

                results.add(violationModel);
            }
        }

        //计算Day违规
        results.add(calDayOffViolation());
        //计算OFF违规
        results.add(calOffViolation());
        //计算Cycle违规
        if (rule.getId() == RuleType.CANADA_14D_120H) {

            results.add(calCycle24OffViolation());
        }

        return mergeViolation(results);
    }

    /**
     * 根据一个Driving的模型，返回违规信息
     *
     * @param hosDayModels        当前需要分析的驾驶事件,将时间追溯到还没有发生该事件的model
     * @param hosDriveEventModels 发生在Driving事件后的所有事件
     * @param hosDriveEventModel  驾驶事件
     * @param endTime             计算的截至时间，事件结束时间或者当前或者当天的23:59:59
     * @return 如果没有违规，返回null
     */
    private CanadaViolationModel calEventByHosDayModel(List<HosDayModel> hosDayModels,
                                                       List<HosDriveEventModel> hosDriveEventModels,
                                                       HosDriveEventModel hosDriveEventModel,
                                                       Date endTime) {

        CanadaViolationModel violationModel = new CanadaViolationModel();

        List<HosDriveEventModel> shiftEvents = new ArrayList<>();
        shiftEvents.addAll(getShiftEvents(hosDayModels, hosDriveEventModels, hosDriveEventModel));

        //累计Driving和ODND的时间
        HosDataModel hosDataModel = calShiftOdndAndDriving(shiftEvents, hosDriveEventModel.getDate());
        //计算当天Driving和ODND时间
        if (hosDayModels != null) {

            for (HosDayModel model : hosDayModels) {

                if (TimeUtil.isSameDay(model.getDate(), endTime)) {

                    calTodayOdndAndDriving(hosDataModel, model, hosDriveEventModel.getDate());
                    break;
                }
            }
        }

        //Driving事件的开始时间
        long startSecond = TimeUtil.intervalSecond(hosDriveEventModel.getDate(), TimeUtil.getDayBegin(hosDriveEventModel.getDate()));
        //计算截至时间
        long endSecond = TimeUtil.intervalSecond(endTime, TimeUtil.getDayBegin(endTime));

        if (startSecond == endSecond) {

            return violationModel;
        }
        //判断是否存在有效的Adverse Driving
        boolean hasAdverse = hasAdverse(shiftEvents, hosDriveEventModels, hosDriveEventModel);
        //计算Shift违规
        calShiftViolation(violationModel, hasAdverse, hosDataModel, endTime, startSecond, endSecond);
        //计算Day违规
        calDayDrivingViolation(violationModel, hasAdverse, hosDataModel, startSecond, endSecond);
        //计算Cycle违规
        calCycleDrivingViolation(violationModel, hosDayModels, hosDriveEventModel, startSecond, endSecond);

        return violationModel;
    }

    /**
     * 检查当前Shift是否有有效的Adverse Driving
     *
     * @param shiftEvents         Driving之前的Shift内全部事件
     * @param hosDriveEventModels Driving之后的Shift内全部事件
     * @param hosDriveEventModel  Driving事件
     * @return 是否有有效的Adverse Driving
     */
    private boolean hasAdverse(List<HosDriveEventModel> shiftEvents,
                               List<HosDriveEventModel> hosDriveEventModels,
                               HosDriveEventModel hosDriveEventModel) {

        Date shiftStartDate = null; //Shift开始时间
        Date shiftEndDate = null; //Shift结束时间
        if (shiftEvents.isEmpty()) {

            shiftStartDate = hosDriveEventModel.getDate();
        } else {

            shiftStartDate = shiftEvents.get(shiftEvents.size() - 1).getDate();
        }
        shiftEndDate = getShiftEndDate(shiftEvents, hosDriveEventModels);

        List<HosAdverseDriveEventModel> adverseDriveEventModels = ModelCenter.getInstance()
                .getAdverseDriving(shiftStartDate, shiftEndDate);

        if (!adverseDriveEventModels.isEmpty()) {

            HosAdverseDriveEventModel firstAdverse = adverseDriveEventModels.get(adverseDriveEventModels.size() - 1);
            List<HosDriveEventModel> tempEvents = new ArrayList<>();
            for (HosDriveEventModel model : shiftEvents) {

                if (model.getDate().getTime() <= firstAdverse.getDate().getTime()) {

                    tempEvents.add(model);
                }
            }
            if (hosDriveEventModel.getDate().getTime() <= firstAdverse.getDate().getTime()) {

                tempEvents.add(hosDriveEventModel);
            }
            for (HosDriveEventModel model : hosDriveEventModels) {

                if (model.getDate().getTime() <= firstAdverse.getDate().getTime()) {

                    tempEvents.add(model);
                }
            }

            if (tempEvents.isEmpty()) {

                return false;
            } else {

                HosDataModel tempDateModel = calShiftOdndAndDriving(tempEvents, firstAdverse.getDate());
                return tempDateModel.getPastSecond() < 13 * 60 * 60;
            }
        }
        return false;
    }

    /**
     * Shift 违规计算
     *
     * @param violationModel    违规模型
     * @param hasAdverseDriving 是否有特殊条件
     * @param hosDataModel      Hos计算结果
     * @param startSecond       Driving开始时间
     * @param endSecond         计算截止时间
     */
    private void calShiftViolation(CanadaViolationModel violationModel,
                                   boolean hasAdverseDriving,
                                   HosDataModel hosDataModel,
                                   Date endTime,
                                   long startSecond,
                                   long endSecond) {

        //计算13小时Shift违规
        ViolationEvent thirteenShiftEvent = calViolationModel(hosDataModel.getPastSecond(),
                ((hasAdverseDriving ? 2 * 60 : 0) + rule.getMaxDriving()) * 60,
                startSecond,
                endSecond);
        //计算14小时Shift违规
        ViolationEvent fourteenShiftEvent = calViolationModel(hosDataModel.getOdndSecond(),
                rule.getMaxDutyForDriving() * 60,
                startSecond,
                endSecond);
        //计算16小时Shift违规
        ViolationEvent sixteenShiftEvent = calViolationModel(hosDataModel.getShiftSecond(),
                16 * 60 * 60,
                startSecond,
                endSecond);

        List<ViolationEvent> result = new ArrayList<>();
        if (thirteenShiftEvent != null) {

            result.add(thirteenShiftEvent);
        }
        if (fourteenShiftEvent != null) {

            result.add(fourteenShiftEvent);
        }
        if (sixteenShiftEvent != null) {

            result.add(sixteenShiftEvent);
        }

        violationModel.addEvent(result);

        if (thirteenShiftEvent != null) {

            violationModel.setShift_13_limit(1);
        }
        if (fourteenShiftEvent != null) {

            violationModel.setShift_14_limit(1);
        }
        if (sixteenShiftEvent != null) {

            violationModel.setShift_16_limit(1);
        }
    }

    /**
     * Day 违规计算
     * <p>
     * 13小时Driving限制和14小时ODND限制
     *
     * @param violationModel 违规模型
     * @param hosDataModel   Hos计算结果
     * @param startSecond    Driving开始时间
     * @param endSecond      计算截止时间
     */
    private void calDayDrivingViolation(CanadaViolationModel violationModel,
                                        boolean hasAdverseDriving,
                                        HosDataModel hosDataModel,
                                        long startSecond,
                                        long endSecond) {

        //计算13小时Day违规
        ViolationEvent thirteenDayEvent = calViolationModel(hosDataModel.getTodayDriveSecond(),
                (hasAdverseDriving ? 2 * 60 * 60 : 0) + 13 * 60 * 60,
                startSecond,
                endSecond);
        //计算14小时Day违规
        ViolationEvent fourteenDayEvent = calViolationModel(hosDataModel.getTodayOdndSecond(),
                14 * 60 * 60,
                startSecond,
                endSecond);

        List<ViolationEvent> result = new ArrayList<>();
        if (thirteenDayEvent != null) {

            result.add(thirteenDayEvent);
        }
        if (fourteenDayEvent != null) {

            result.add(fourteenDayEvent);
        }

        violationModel.addEvent(result);

        if (thirteenDayEvent != null) {

            violationModel.setDay_13_limit(1);
        }
        if (fourteenDayEvent != null) {

            violationModel.setDay_14_limit(1);
        }
    }

    /**
     * Day 违规计算
     * <p>
     * 10小时OFF限制和20小时限制
     */
    private CanadaViolationModel calDayOffViolation() {

        CanadaViolationModel violationModel = new CanadaViolationModel();

        Date preDate = TimeUtil.getPreviousDate(now, 1);

        HosDayModel preHosDayModel = ModelCenter.getInstance().getHosDayModel(preDate); //前一天的HosDayModel
        HosDayModel hosDayModel = ModelCenter.getInstance().getHosDayModel(now); //当天的HosDayModel

        List<HosDriveEventModel> hosDriveEventModelList = new ArrayList<>();
        List<HosDriveEventModel> preHosDriveEventModelList = new ArrayList<>();

        hosDriveEventModelList.addAll(hosDayModel.getHosDriveEventModels());
        if (preHosDayModel != null) {

            preHosDriveEventModelList.addAll(preHosDayModel.getHosDriveEventModels());
        }

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

        if (preHosDayModel != null) {

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
        }

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

            lastOffEventTime = TimeUtil.intervalSecond(temp, hosDriveEventModelList.get(0).getDate());
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

        if (!preHosDriveEventModelList.isEmpty()) {

            //计算最新的事件
            if (!DriverState.isOnDuty(preHosDriveEventModelList.get(0).getDriverState())) {

                long time = TimeUtil.intervalSecond(new Date(yesterdayEndTime), preHosDriveEventModelList.get(0).getDate());
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
        }

        //最后一个可能的OFF事件可能的最大持续时间
        long lastOffTime = lastOffEventTime + todayLeftTime;

        todayRestTotal = lastOffTime > 30 * 60 ? todayRestTime + lastOffTime : todayRestTime;
        todayMaxRest = todayMaxRestTime >= lastOffTime ? todayMaxRestTime : lastOffTime;

        //如果当天累计休息时间加上今天的剩余时间不足10小时，并且当天最大单次连续休息时间不足8小时，则违反10小时规则
        if (todayRestTotal < 10 * 60 * 60 && todayMaxRest < 8 * 60 * 60) {

            violationModel.setDay_10_limit(1);
        }

        //如果前一天最大单次连续休息大于等于8小时，并且累计休息时间不足10小时，则开启20小时规则检测
        if (yesterdayMaxRestTime >= 8 * 60 * 60 && yesterdayRestTime < 10 * 60 * 60) {

            //要求的当天最小单次连续休息时间
            long todayMinRestTime = 10 * 60 * 60 - yesterdayMaxRestTime + 8 * 60 * 60;

            //如果当天最大单次连续休息时间不足，则违反20小时限制
            if (todayMaxRest < todayMinRestTime) {

                violationModel.setDay_20_limit(1);
            } else {

                //如果两天的总的休息时间不足20小时，则违反20小时限制
                if (yesterdayRestTime + todayRestTotal < 20 * 60 * 60) {

                    violationModel.setDay_20_limit(1);
                }
            }
        }

        return violationModel;
    }

    /**
     * Cycle 违规计算
     *
     * @param violationModel 违规模型
     * @param hosDayModels   HOS模型
     * @param startSecond    Driving开始时间
     * @param endSecond      计算截止时间
     */
    private void calCycleDrivingViolation(CanadaViolationModel violationModel,
                                          List<HosDayModel> hosDayModels,
                                          HosDriveEventModel hosDriveEventModel,
                                          long startSecond,
                                          long endSecond) {

        if (hosDayModels == null || hosDayModels.isEmpty()) {

            return;
        }

        Date endTime = hosDriveEventModel.getDate();

        List<HosDriveEventModel> hosDriveEventModels = new ArrayList<>();
        //首先找到Cycle内全部的驾驶事件
        for (HosDayModel hosDayModel : hosDayModels) {

            if (hosDayModel.getHosDriveEventModels() != null && !hosDayModel.getHosDriveEventModels().isEmpty()) {

                hosDriveEventModels.addAll(hosDayModel.getHosDriveEventModels());
            }
        }

        if (hosDriveEventModels.isEmpty()) {

            return;
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

        long totalCycleTime = rule.getDutyTime() * 60;
        long cycleTime = calCycleTime(hosDayModels, endTime, hosDriveEventModels);

        List<ViolationEvent> result = new ArrayList<>();
        ViolationEvent cycleEvent = calViolationModel(cycleTime,
                totalCycleTime,
                startSecond,
                endSecond);

        if (cycleEvent != null) {

            result.add(cycleEvent);
        }

        violationModel.addEvent(result);

        if (cycleEvent != null) {

            if (rule.getId() == RuleType.CANADA_7D_70H) {

                violationModel.setCycle_70_limit(1);
            } else if (rule.getId() == RuleType.CANADA_14D_120H) {

                violationModel.setCycle_120_limit(1);
            }
        }
    }

    /**
     * Cycle 违规计算
     *
     * @return CanadaViolationModel
     */
    private CanadaViolationModel calCycle24OffViolation() {

        CanadaViolationModel canadaViolationModel = new CanadaViolationModel();

        List<HosDayModel> oraHosDayModels = new ArrayList<>();
        List<HosDayModel> hosDayModels = new ArrayList<>();
        List<HosDriveEventModel> hosDriveEventModels = new ArrayList<>();

        //获取所有天的模型
        oraHosDayModels.addAll(ModelCenter.getInstance().getAllHosDayModels());
        for (HosDayModel model : oraHosDayModels) {

            if (model.getDate().getTime() <= now.getTime()) {

                hosDayModels.add(model);
                if (model.getHosDriveEventModels() != null &&
                        !model.getHosDriveEventModels().isEmpty()) {

                    hosDriveEventModels.addAll(model.getHosDriveEventModels());
                }
            }
        }
        if (hosDriveEventModels.isEmpty()) {

            return canadaViolationModel;
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

        Date endTime = new Date();
        if (!TimeUtil.isSameDay(new Date(), now)) {

            endTime = new Date(TimeUtil.getDayBegin(TimeUtil.getNextDate(now, 1)).getTime());
        }

        if (hosDriveEventModels.isEmpty()) {

            return canadaViolationModel;
        }

        long odndTime = 0; //累计工作时长
        long restTime = 0; //某个连续休息时长
        if (DriverState.isOnDuty(hosDriveEventModels.get(0).getDriverState())) {

            odndTime += TimeUtil.intervalSecond(endTime, hosDriveEventModels.get(0).getDate());
        } else {

            restTime += TimeUtil.intervalSecond(endTime, hosDriveEventModels.get(0).getDate());
            if (restTime >= 24 * 60 * 60) {

                return canadaViolationModel;
            }
        }
        for (int i = 1; i < hosDriveEventModels.size(); i++) {

            HosDriveEventModel curModel = hosDriveEventModels.get(i);
            HosDriveEventModel nextModel = hosDriveEventModels.get(i - 1);
            if (DriverState.isOnDuty(curModel.getDriverState())) {

                odndTime += TimeUtil.intervalSecond(nextModel.getDate(), curModel.getDate());
                restTime = 0;
            } else {

                restTime += TimeUtil.intervalSecond(nextModel.getDate(), curModel.getDate());
                if (restTime >= 24 * 60 * 60) {

                    break;
                }
            }
        }

        if (odndTime > 70 * 60 * 60) {

            canadaViolationModel.setCycle_24_limit(1);
        }

        return canadaViolationModel;
    }

    /**
     * 计算当前Cycle 时长
     *
     * @param hosDayModels        HOS模型
     * @param endTime             截止时间
     * @param hosDriveEventModels 事件列表
     * @return Cycle 时长
     */
    private long calCycleTime(List<HosDayModel> hosDayModels, Date endTime, List<HosDriveEventModel> hosDriveEventModels) {

        long cycleTime = 0;

        if (hosDriveEventModels.isEmpty()) {

            return cycleTime;
        }

        long restTime = 0;
        //判断最新的事件
        if (!DriverState.isOnDuty(hosDriveEventModels.get(0).getDriverState())) {

            //如果最新的事件不是工作事件，并且持续时间大于等于36小时或者72小时
            if (endTime.getTime() - hosDriveEventModels.get(0).getDate().getTime() >= rule.getResetCycleDutyTime() * 60 * 1000L) {

                return cycleTime;
            }
            restTime += (endTime.getTime() - hosDriveEventModels.get(0).getDate().getTime()) / 1000;
        }
        int index = 0;
        boolean findReset = false;
        //循环寻找是否已经休息足够36小时或者72小时
        for (int i = 1; i < hosDriveEventModels.size(); i++) {

            if (!DriverState.isOnDuty(hosDriveEventModels.get(i).getDriverState())) {

                restTime += TimeUtil.intervalSecond(hosDriveEventModels.get(i - 1).getDate(), hosDriveEventModels.get(i).getDate());
            } else {

                index = i;
                restTime = 0;
            }

            if (restTime >= rule.getResetCycleDutyTime() * 60) {

                findReset = true;
                break;
            }
        }

        Date date = TimeUtil.getDayBegin(hosDriveEventModels.get(index).getDate());
        Date defaultStartDate = TimeUtil.getDayBegin(TimeUtil.getPreviousDate(endTime, rule.getDutyDays() - 1));

        if (findReset) {

            //如果找到的重置时间，在Cycle的周期之外或者与Cycle的周期相同
            if (date.getTime() <= defaultStartDate.getTime()) {

                int days = rule.getDutyDays();
                int diffDays = TimeUtil.differentDays(hosDayModels.get(0).getDate(), endTime);
                days = days - diffDays;
                days = hosDayModels.size() < days ? hosDayModels.size() : days;
                for (int i = 0; i < days; i++) {

                    HosDayModel hosDayModel = hosDayModels.get(i);
                    cycleTime += hosDayModel.getOnDutyTime(endTime);
                }
            } else {

                int days = TimeUtil.differentDays(date, endTime) + 1;
                int diffDays = TimeUtil.differentDays(hosDayModels.get(0).getDate(), endTime);
                days = days - diffDays;
                days = hosDayModels.size() < days ? hosDayModels.size() : days;
                for (int i = 0; i < days; i++) {

                    HosDayModel hosDayModel = hosDayModels.get(i);
                    cycleTime += hosDayModel.getOnDutyTime(endTime);
                }
            }
        } else {

            int days = rule.getDutyDays();
            int diffDays = TimeUtil.differentDays(hosDayModels.get(0).getDate(), endTime);
            days = days - diffDays;
            days = hosDayModels.size() < days ? hosDayModels.size() : days;
            for (int i = 0; i < days; i++) {

                HosDayModel hosDayModel = hosDayModels.get(i);
                cycleTime += hosDayModel.getOnDutyTime(endTime);
            }
        }

        return cycleTime;
    }

    /**
     * OFF 违规
     */
    private CanadaViolationModel calOffViolation() {

        CanadaViolationModel violationModel = new CanadaViolationModel();
        //如果不是今天，则认为不违规
        if (!TimeUtil.isSameDay(new Date(), now)) {

            return violationModel;
        }

        List<HosDayModel> hosDayModels = new ArrayList<>();
        hosDayModels.addAll(ModelCenter.getInstance().getAllHosDayModels());
        if (hosDayModels == null || hosDayModels.isEmpty()) {

            return violationModel;
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

            violationModel.setOff_24_limit(1);
        }

        return violationModel;
    }

    /**
     * 根据当前已经经过的时间和超限的总时间以及开始时间和结束时间进行计算
     *
     * @param now       当前已经经过了多长时间。例如，已经cycle30分钟，该值为30*60 = 1800（秒）
     * @param total     限制是多少，例如cycle限制是60小时，total为60*60*60（秒）
     * @param startTime 开始的秒数
     * @param endTime   结束的秒数
     * @return ViolationEvent
     */
    private ViolationEvent calViolationModel(long now, long total, long startTime, long endTime) {

        ViolationEvent result = new ViolationEvent();
        long timeToOver = total - now;
        long eventTime = endTime - startTime;
        if (timeToOver < 0) {

            result.setStart(startTime);
            result.setEnd(endTime);
            return result;
        }
        if (eventTime > timeToOver) {

            result.setStart(startTime + timeToOver);
            result.setEnd(endTime);
            return result;
        }
        return null;
    }

    /**
     * 获取Shift内全部的驾驶事件
     * <p>
     * 注意：所有事件中，距离当前最新的一个事件是Driving
     *
     * @param hosDayModels        天模型
     * @param hosDriveEventModels Driving事件之后的事件
     * @param hosDriveEventModel  Driving事件
     * @return 全部驾驶事件
     */
    private List<HosDriveEventModel> getShiftEvents(List<HosDayModel> hosDayModels,
                                                    List<HosDriveEventModel> hosDriveEventModels,
                                                    HosDriveEventModel hosDriveEventModel) {

        List<HosDriveEventModel> allEvent = new ArrayList<>();
        for (HosDayModel hosDayModel : hosDayModels) {

            allEvent.addAll(hosDayModel.getHosDriveEventModels());
        }
        if (allEvent.isEmpty()) {

            return new ArrayList<>();
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

        //累计休息时间
        long accuRestTime = 0;
        //上一个SB事件的持续时间，lastSbTime >= 2
        long lastSbTime = 0;
        //是否找到上一个符合条件的SB
        boolean findLastSb = false;

        if (hosDriveEventModels != null && !hosDriveEventModels.isEmpty()) {

            //删除全部重复事件
            HosDriveEventModel lastModel1 = null;
            List<HosDriveEventModel> removeList1 = new ArrayList<>();
            for (HosDriveEventModel model : hosDriveEventModels) {

                if (lastModel1 != null && lastModel1.getDriverState() == model.getDriverState()) {

                    removeList1.add(lastModel1);
                }

                lastModel1 = model;
            }
            hosDriveEventModels.removeAll(removeList1);

            int size = hosDriveEventModels.size();

            long restTime = 0; //Driving事件后面的连续休息时间，非SB事件
            //先判断第一个事件
            if (size > 0) {

                HosDriveEventModel lastEvent = hosDriveEventModels.get(size - 1);
                if (lastEvent.getDriverState() == DriverState.SLEEPER_BERTH) {

                    long sbTime = 0;
                    if (size > 1) {

                        sbTime = TimeUtil.intervalSecond(hosDriveEventModels.get(size - 2).getDate(), lastEvent.getDate());
                    } else {

                        sbTime = TimeUtil.intervalSecond(new Date(), lastEvent.getDate());
                    }

                    if (sbTime >= 2 * 60 * 60) {

                        lastSbTime = sbTime;
                        findLastSb = true;
                    }
                } else {

                    if (!DriverState.isOnDuty(lastEvent.getDriverState())) {

                        if (size > 1) {

                            restTime = TimeUtil.intervalSecond(hosDriveEventModels.get(size - 2).getDate(), lastEvent.getDate());
                        } else {

                            restTime = TimeUtil.intervalSecond(new Date(), lastEvent.getDate());
                        }
                    }
                }
            }
            //如果第一个事件不满足条件，向后查找满足条件的事件
            if (!findLastSb && size > 1) {

                for (int i = size - 2; i >= 0; i--) {

                    HosDriveEventModel lastEvent = hosDriveEventModels.get(i);
                    if (lastEvent.getDriverState() == DriverState.SLEEPER_BERTH) {

                        long sbTime = 0;
                        if (i >= 1) {

                            sbTime = TimeUtil.intervalSecond(hosDriveEventModels.get(i - 1).getDate(), lastEvent.getDate());
                        } else {

                            sbTime = TimeUtil.intervalSecond(new Date(), lastEvent.getDate());
                        }

                        if (sbTime >= 2 * 60 * 60) {

                            lastSbTime = sbTime;
                            findLastSb = true;
                        }

                        break;
                    } else {

                        if (!DriverState.isOnDuty(lastEvent.getDriverState())) {

                            if (i >= 1) {

                                restTime += TimeUtil.intervalSecond(hosDriveEventModels.get(i - 1).getDate(), lastEvent.getDate());
                            } else {

                                restTime += TimeUtil.intervalSecond(new Date(), lastEvent.getDate());
                            }
                            if (restTime >= 8 * 60 * 60) {

                                break;
                            }
                        } else {

                            restTime = 0;
                        }
                    }
                }
            }
        }

        //判断最新的事件
        if (!DriverState.isOnDuty(allEvent.get(0).getDriverState())) {

            accuRestTime += TimeUtil.intervalSecond(hosDriveEventModel.getDate(), allEvent.get(0).getDate());

            //如果最新的事件不是工作事件，并且持续时间大于等于8小时
            if (accuRestTime >= 8 * 60 * 60) {

                return new ArrayList<>();
            }
            //如果最新的事件是SB，并且持续时间大于等于2小时
            if (allEvent.get(0).getDriverState() == DriverState.SLEEPER_BERTH && accuRestTime >= 2 * 60 * 60) {

                if (findLastSb) {

                    if (accuRestTime + lastSbTime >= 10 * 60 * 60) {

                        return new ArrayList<>();
                    } else {

                        lastSbTime = accuRestTime;
                        findLastSb = true;
                    }
                } else {

                    lastSbTime = accuRestTime;
                    findLastSb = true;
                }
            }
        }
        //循环寻找Shift的开始点
        for (int i = 1; i < allEvent.size(); i++) {

            if (!DriverState.isOnDuty(allEvent.get(i).getDriverState())) {

                accuRestTime += TimeUtil.intervalSecond(allEvent.get(i - 1).getDate(), allEvent.get(i).getDate());
            } else {

                accuRestTime = 0;
            }

            //如果连续休息时间大于8小时
            if (accuRestTime >= 8 * 60 * 60) {
                //找到第一个不是休息的事件
                for (int index = i; index >= 0; index--) {

                    if (DriverState.isOnDuty(allEvent.get(index).getDriverState())) {

                        return allEvent.subList(0, index + 1);
                    }
                }
                //如果走到这里，说明到现在都一直是休息状态
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

                                        return allEvent.subList(0, index + 1);
                                    }
                                }

                                //如果走到这里，说明到现在都一直是休息状态，理论上不会走到这里
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
        //如果没有找到休息够的数据，那么就是全部事件
        return allEvent;
    }

    /**
     * 获取Shift结束时间
     *
     * @param shiftEvents Driving事件之前的Shift内全部事件
     * @param eventModels Driving事件之后的全部事件
     * @return Shift结束时间
     */
    private Date getShiftEndDate(List<HosDriveEventModel> shiftEvents, List<HosDriveEventModel> eventModels) {

        //累计休息时间
        long accuRestTime = 0;
        //上一个SB事件的持续时间，lastSbTime >= 2
        long lastSbTime = 0;
        //是否找到上一个符合条件的SB
        boolean findLastSb = false;

        HosDriveEventModel startOffEvent = null;
        Date firstSbEndDate = null;

        //Shift开始事件的时间之后的全部事件，用于查找Shift结束时间
        List<HosDriveEventModel> allEvent = new ArrayList<>();
        allEvent.addAll(shiftEvents);
        allEvent.addAll(eventModels);
        Collections.sort(allEvent);

        if (allEvent != null && !allEvent.isEmpty()) {

            int size = allEvent.size();
            for (int i = size - 1; i > 0; i--) {

                if (!DriverState.isOnDuty(allEvent.get(i).getDriverState())) {

                    accuRestTime += TimeUtil.intervalSecond(allEvent.get(i - 1).getDate(), allEvent.get(i).getDate());
                    if (startOffEvent == null) {

                        startOffEvent = allEvent.get(i);
                    }
                } else {

                    accuRestTime = 0;
                    startOffEvent = null;
                }

                if (accuRestTime >= 8 * 60 * 60) {

                    return startOffEvent.getDate();
                } else {

                    if (allEvent.get(i).getDriverState() == DriverState.SLEEPER_BERTH) {

                        long sbTime = TimeUtil.intervalSecond(allEvent.get(i - 1).getDate(), allEvent.get(i).getDate());
                        if (findLastSb) {

                            if (sbTime >= 2 * 60 * 60) {

                                if (sbTime + lastSbTime >= 10 * 60 * 60) {

                                    return firstSbEndDate;
                                } else {

                                    findLastSb = true;
                                    lastSbTime = sbTime;
                                    firstSbEndDate = allEvent.get(i - 1).getDate();
                                }
                            } else {

                                findLastSb = false;
                                lastSbTime = 0;
                            }
                        } else {

                            if (sbTime >= 2 * 60 * 60) {

                                findLastSb = true;
                                lastSbTime = sbTime;
                                firstSbEndDate = allEvent.get(i - 1).getDate();
                            }
                        }
                    }
                }
            }

            if (!DriverState.isOnDuty(allEvent.get(0).getDriverState())) {

                accuRestTime += TimeUtil.intervalSecond(new Date(), allEvent.get(0).getDate());
                if (startOffEvent == null) {

                    startOffEvent = allEvent.get(0);
                }
                if (accuRestTime >= 8 * 60 * 60) {

                    return startOffEvent.getDate();
                } else {

                    if (findLastSb && allEvent.get(0).getDriverState() == DriverState.SLEEPER_BERTH) {

                        long sbTime = TimeUtil.intervalSecond(new Date(), allEvent.get(0).getDate());
                        if (sbTime + lastSbTime >= 10 * 60 * 60) {

                            return firstSbEndDate;
                        }
                    }
                }
            } else {

                return new Date();
            }
        }

        return new Date();
    }

    /**
     * 根据该SHIFT的所有事件，计算时间
     * <p>
     * 注意：该方法需要计算出Shift、累计ODND、当天累计ODND、累计Driving和当天累计Driving，算法参照HosCalculator
     *
     * @param hosDriveEventModels 该SHIFT的所有事件
     * @param endTime             截止时间
     * @return 主页数据
     */
    private HosDataModel calShiftOdndAndDriving(List<HosDriveEventModel> hosDriveEventModels, Date endTime) {

        HosDataModel hosDataModel = new HosDataModel();

        //按照时间倒序排列
        Collections.sort(hosDriveEventModels);

        if (!hosDriveEventModels.isEmpty()) {

            //计算Shift时长
            hosDataModel.setShiftSecond(TimeUtil.intervalSecond(endTime,
                    hosDriveEventModels.get(hosDriveEventModels.size() - 1).getDate()));

            //如果当前状态是工作状态，计算本次连续工作时长
            if (DriverState.isOnDuty(hosDriveEventModels.get(0).getDriverState())) {

                hosDataModel.setOdndSecond(TimeUtil.intervalSecond(endTime,
                        hosDriveEventModels.get(0).getDate()));
            }
            //如果当前状态是Driving，计算本次连续Driving时间
            if (hosDriveEventModels.get(0).getDriverState().equals(DriverState.DRIVING)) {

                hosDataModel.setPastSecond(TimeUtil.intervalSecond(endTime,
                        hosDriveEventModels.get(0).getDate()));
            }
        }
        for (int i = 1; i < hosDriveEventModels.size(); i++) {

            if (DriverState.isOnDuty(hosDriveEventModels.get(i).getDriverState())) {

                hosDataModel.setOdndSecond(hosDataModel.getOdndSecond() +
                        TimeUtil.intervalSecond(hosDriveEventModels.get(i - 1).getDate(), hosDriveEventModels.get(i).getDate()));
            }
            if (hosDriveEventModels.get(i).getDriverState().equals(DriverState.DRIVING)) {

                hosDataModel.setPastSecond(hosDataModel.getPastSecond() +
                        TimeUtil.intervalSecond(hosDriveEventModels.get(i - 1).getDate(), hosDriveEventModels.get(i).getDate()));
            }
        }

        return hosDataModel;
    }

    /**
     * 计算当天到Driving事件前的累计ODND和累计Driving时间
     *
     * @param hosDataModel 当天的HosDayModel，事件追溯到Driving事件
     * @param hosDayModel  HOS计算结果
     * @param endTime      截止时间
     */
    private void calTodayOdndAndDriving(HosDataModel hosDataModel, HosDayModel hosDayModel, Date endTime) {

        if (hosDayModel == null) {

            return;
        }

        List<HosDriveEventModel> hosDriveEventModels = hosDayModel.getHosDriveEventModels();

        //新建零点事件
        HosDriveEventModel hosDriveEventModel = new HosDriveEventModel();
        hosDriveEventModel.setDate(TimeUtil.getDayBegin(hosDayModel.getDate()));
        hosDriveEventModel.setDriverState(hosDayModel.getStartDriverState());

        hosDriveEventModels.add(hosDriveEventModel);

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

        //如果当前状态是工作状态，计算本次连续工作时长
        if (DriverState.isOnDuty(hosDriveEventModels.get(0).getDriverState())) {

            //计算当天累计的工作时长
            hosDataModel.setTodayOdndSecond(TimeUtil.intervalSecond(endTime, hosDriveEventModels.get(0).getDate()));

        }
        //如果当前状态是Driving，计算本次连续Driving时间
        if (hosDriveEventModels.get(0).getDriverState().equals(DriverState.DRIVING)) {

            //计算今天累计的Drive时长
            hosDataModel.setTodayDriveSecond(TimeUtil.intervalSecond(endTime, hosDriveEventModels.get(0).getDate()));
        }
        for (int i = 1; i < hosDriveEventModels.size(); i++) {

            if (DriverState.isOnDuty(hosDriveEventModels.get(i).getDriverState())) {

                //计算今天累计的工作时长
                hosDataModel.setTodayOdndSecond(hosDataModel.getTodayOdndSecond() +
                        TimeUtil.intervalSecond(hosDriveEventModels.get(i - 1).getDate(), hosDriveEventModels.get(i).getDate()));
            }
            if (hosDriveEventModels.get(i).getDriverState().equals(DriverState.DRIVING)) {

                //计算今天累计的Drive时长
                hosDataModel.setTodayDriveSecond(hosDataModel.getTodayDriveSecond() +
                        TimeUtil.intervalSecond(hosDriveEventModels.get(i - 1).getDate(), hosDriveEventModels.get(i).getDate()));
            }
        }
    }

    /**
     * 找到某个时间之前的全部事件的天模型
     *
     * @param hosDriveEventModel 事件
     * @param dayModels          天模型
     * @return 天模型列表
     */
    private List<HosDayModel> getHosDayModelsByDrivingEvent(HosDriveEventModel hosDriveEventModel, List<HosDayModel> dayModels) {

        List<HosDayModel> result = new ArrayList<>();
        List<HosDayModel> hosDayModels = new ArrayList<>();

        for (HosDayModel model : dayModels) {

            hosDayModels.add((HosDayModel) model.clone());
        }

        for (HosDayModel hosDayModel : hosDayModels) {

            if (hosDayModel.getDate().getTime() > hosDriveEventModel.getDate().getTime()) {

                continue;
            } else {

                result.add(hosDayModel);
            }
        }
        if (hosDayModels.isEmpty()) {

            return result;
        }
        List<HosDriveEventModel> removeList = new ArrayList<>();
        for (HosDriveEventModel driveEventModel : result.get(0).getHosDriveEventModels()) {

            if (driveEventModel.getDate().getTime() >= hosDriveEventModel.getDate().getTime()) {

                removeList.add(driveEventModel);
            }
        }
        result.get(0).getHosDriveEventModels().removeAll(removeList);
        return result;
    }

    /**
     * 获取指定事件之后的全部事件
     *
     * @param hosDayModels       Hos模型
     * @param hosDriveEventModel 指定事件
     * @return List<HosDriveEventModel>
     */
    private List<HosDriveEventModel> getHosDriveEventModelsByDrivingEvent(List<HosDayModel> hosDayModels, HosDriveEventModel hosDriveEventModel) {

        List<HosDriveEventModel> afterDrivingModelList = new ArrayList<>();
        for (HosDayModel hosDayModel : hosDayModels) {

            HosDayModel model = (HosDayModel) hosDayModel.clone();
            List<HosDriveEventModel> list = model.getHosDriveEventModels();
            if (list != null && !list.isEmpty()) {

                for (HosDriveEventModel driveEventModel : list) {

                    if (driveEventModel.getDate().getTime() > hosDriveEventModel.getDate().getTime()) {

                        afterDrivingModelList.add(driveEventModel);
                    }
                }
            }
        }

        Collections.sort(afterDrivingModelList);
        return afterDrivingModelList;
    }

    /**
     * 将多个违规事件合并
     *
     * @param violationModels 所有的违规列表
     * @return 合并后对外返回的违规模型
     */
    private ViolationModel mergeViolation(List<CanadaViolationModel> violationModels) {

        CanadaViolationModel result = new CanadaViolationModel();
        for (CanadaViolationModel violationModel : violationModels) {

            if (violationModel == null) {

                continue;
            }

            result.setShift_16_limit(result.getShift_16_limit() + violationModel.getShift_16_limit());
            result.setShift_13_limit(result.getShift_13_limit() + violationModel.getShift_13_limit());
            result.setShift_14_limit(result.getShift_14_limit() + violationModel.getShift_14_limit());

            result.setDay_10_limit(result.getDay_10_limit() + violationModel.getDay_10_limit());
            result.setDay_20_limit(result.getDay_20_limit() + violationModel.getDay_20_limit());
            result.setDay_13_limit(result.getDay_13_limit() + violationModel.getDay_13_limit());
            result.setDay_14_limit(result.getDay_14_limit() + violationModel.getDay_14_limit());

            result.setCycle_70_limit(result.getCycle_70_limit() + violationModel.getCycle_70_limit());
            result.setCycle_120_limit(result.getCycle_120_limit() + violationModel.getCycle_120_limit());
            result.setCycle_24_limit(result.getCycle_24_limit() + violationModel.getCycle_24_limit());

            result.setOff_24_limit(result.getOff_24_limit() + violationModel.getOff_24_limit());

            result.getEvents().addAll(violationModel.getEvents());
        }
        if (result.getShift_16_limit() + result.getShift_14_limit() + result.getShift_13_limit() > 0) {

            result.setShift(1);
        }
        if (result.getDay_14_limit() + result.getDay_13_limit() + result.getDay_10_limit() + result.getDay_20_limit() > 0) {

            result.setDriving(1);
        }
        if (result.getCycle_70_limit() + result.getCycle_120_limit() + result.getCycle_24_limit() > 0) {

            result.setCycle(1);
        }
        if (result.getOff_24_limit() > 0) {

            result.setOff(1);
        }
        result.resetLimitString();

        return result;
    }
}
