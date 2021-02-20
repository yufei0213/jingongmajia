package com.unitedbustech.eld.hos.calculator.america;

import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.domain.entry.Rule;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.hos.model.HosAdverseDriveEventModel;
import com.unitedbustech.eld.hos.model.HosDataModel;
import com.unitedbustech.eld.hos.model.HosDayModel;
import com.unitedbustech.eld.hos.model.HosDriveEventModel;
import com.unitedbustech.eld.hos.violation.ViolationEvent;
import com.unitedbustech.eld.hos.violation.ViolationModel;
import com.unitedbustech.eld.util.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyu
 * @date 2018/3/6
 * @description 美国客车规则违规计算器
 */
class ViolationCalculator {

    private Rule rule;

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

        List<ViolationModel> results = new ArrayList<>();

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
        //没有本天的模型，或者本天没有驾驶事件并且本天的开始状态也不是驾驶状态
        if (hosDayModel == null || (!hasDriving(hosDayModel.getHosDriveEventModels()) && hosDayModel.getStartDriverState() != DriverState.DRIVING)) {

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

                    endTime = new Date(TimeUtil.getNextDate(hosDayModel.getDate(), 1).getTime() - 1);
                }
            }

            ViolationModel violationModel = calEventByHosDayModel(hosDayModels,
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

                ViolationModel violationModel = calEventByHosDayModel(temp,
                        afterDrivingModelList,
                        hosDriveEventModel,
                        endTime);

                results.add(violationModel);
            }
        }

        return mergeViolation(results);
    }

    /**
     * 根据当天的所有工作事件，判断是否有Driving事件
     *
     * @param hosDriveEventModels 所有事件模型
     * @return 是否有Driving事件
     */
    private boolean hasDriving(List<HosDriveEventModel> hosDriveEventModels) {

        for (HosDriveEventModel hosDriveEventModel : hosDriveEventModels) {

            if (hosDriveEventModel.getDriverState() == DriverState.DRIVING) {

                return true;
            }
        }
        return false;
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
    private ViolationModel calEventByHosDayModel(List<HosDayModel> hosDayModels,
                                                 List<HosDriveEventModel> hosDriveEventModels,
                                                 HosDriveEventModel hosDriveEventModel,
                                                 Date endTime) {

        ViolationModel violationModel = new ViolationModel();

        List<HosDriveEventModel> shiftEvents = new ArrayList<>();
        shiftEvents.addAll(getShiftEvents(hosDayModels, hosDriveEventModel));

        //Cycle时间
        long cycle = cycleTime(hosDayModels, hosDriveEventModel.getDate());

        HosDataModel hosDataModel = calculateData(shiftEvents, hosDriveEventModel.getDate());
        //Shift时间
        long shift = hosDataModel.getShiftSecond();
        //Driving时间
        long driving = hosDataModel.getPastSecond();

        //Driving事件的开始时间
        long startSecond = TimeUtil.intervalSecond(hosDriveEventModel.getDate(), TimeUtil.getDayBegin(hosDriveEventModel.getDate()));
        //计算截至时间
        long endSecond = TimeUtil.intervalSecond(endTime, TimeUtil.getDayBegin(endTime));

        if (startSecond == endSecond) {

            return violationModel;
        }

        //计算Cycle违规的事件
        ViolationEvent cycleModel = calViolationModel(cycle, rule.getDutyTime() * 60, startSecond, endSecond);
        //计算Shift的违规事件
        ViolationEvent shiftModel = calViolationModel(shift, rule.getMaxDutyForDriving() * 60, startSecond, endSecond);

        //计算Driving的违规事件
        long maxDriving = hasAdverse(shiftEvents, hosDriveEventModels, hosDriveEventModel) ? 12 * 60 * 60 : 10 * 60 * 60;
        ViolationEvent drivingModel = calViolationModel(driving, maxDriving, startSecond, endSecond);

        List<ViolationEvent> result = new ArrayList<>();
        if (cycleModel != null) {

            result.add(cycleModel);
        }
        if (shiftModel != null) {

            result.add(shiftModel);
        }
        if (drivingModel != null) {

            result.add(drivingModel);
        }

        violationModel.addEvent(result);
        if (cycleModel != null) {

            violationModel.setCycle(1);
        }
        if (shiftModel != null) {

            violationModel.setShift(1);
        }
        if (drivingModel != null) {

            violationModel.setDriving(1);
        }
        violationModel.resetLimitString();

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
        shiftEndDate = getShiftEndDate(hosDriveEventModels);

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

            if (tempEvents.isEmpty()) {

                return false;
            } else {

                HosDataModel tempDateModel = calculateData(tempEvents, firstAdverse.getDate());
                return tempDateModel.getPastSecond() < 10 * 60 * 60;
            }
        }
        return false;
    }

    /**
     * 获取Shift内全部的驾驶事件
     * <p>
     * 注意：所有事件中，距离当前最新的一个事件是Driving
     *
     * @param hosDayModels       天模型
     * @param hosDriveEventModel Driving事件
     * @return 全部驾驶事件
     */
    private List<HosDriveEventModel> getShiftEvents(List<HosDayModel> hosDayModels, HosDriveEventModel hosDriveEventModel) {

        List<HosDriveEventModel> allEvent = new ArrayList<>();
        for (HosDayModel hosDayModel : hosDayModels) {

            allEvent.addAll(hosDayModel.getHosDriveEventModels());
        }
        Collections.sort(allEvent);
        if (allEvent.isEmpty()) {

            return new ArrayList<>();
        }

        long restTime = 0;
        //判断最新的事件
        if (!DriverState.isOnDuty(allEvent.get(0).getDriverState())) {

            //如果最新的事件不是工作事件，并且持续时间大于等于8小时
            if (hosDriveEventModel.getDate().getTime() - allEvent.get(0).getDate().getTime() >= 8 * 60 * 60 * 1000L) {

                return new ArrayList<>();
            }
            restTime += (hosDriveEventModel.getDate().getTime() - allEvent.get(0).getDate().getTime()) / 1000;
        }
        //循环寻找是否已经休息足够8小时
        for (int i = 1; i < allEvent.size(); i++) {

            if (!DriverState.isOnDuty(allEvent.get(i).getDriverState())) {

                restTime += TimeUtil.intervalSecond(allEvent.get(i - 1).getDate(), allEvent.get(i).getDate());
            } else {

                restTime = 0;
            }
            if (restTime >= 8 * 60 * 60) {
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

    /**
     * 获取Shift结束的时间
     *
     * @param eventModels Driving事件后面的全部事件
     * @return Shift结束时间
     */
    private Date getShiftEndDate(List<HosDriveEventModel> eventModels) {

        long restTime = 0;
        if (eventModels != null && !eventModels.isEmpty()) {

            int size = eventModels.size();

            HosDriveEventModel startOffEvent = null;
            for (int i = size - 1; i > 0; i--) {

                if (!DriverState.isOnDuty(eventModels.get(i).getDriverState())) {

                    restTime += TimeUtil.intervalSecond(eventModels.get(i - 1).getDate(), eventModels.get(i).getDate());
                    if (startOffEvent == null) {

                        startOffEvent = eventModels.get(i);
                    }
                } else {

                    restTime = 0;
                    startOffEvent = null;
                }

                if (restTime >= 8 * 60 * 60) {

                    return startOffEvent.getDate();
                }
            }

            if (!DriverState.isOnDuty(eventModels.get(0).getDriverState())) {

                restTime += TimeUtil.intervalSecond(new Date(), eventModels.get(0).getDate());
                if (startOffEvent == null) {

                    startOffEvent = eventModels.get(0);
                }
                if (restTime >= 8 * 60 * 60) {

                    return startOffEvent.getDate();
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
     * 注意：该方法仅需要计算出Shift和Driving时间即可，算法参照HosCalculator
     *
     * @param hosDriveEventModels 该SHIFT的所有事件
     * @param now                 用now代替now
     * @return 主页数据
     */
    private HosDataModel calculateData(List<HosDriveEventModel> hosDriveEventModels, Date now) {

        HosDataModel hosDataModel = new HosDataModel();

        if (!hosDriveEventModels.isEmpty()) {

            if (DriverState.isOnDuty(hosDriveEventModels.get(0).getDriverState())) {

                hosDataModel.setShiftSecond(TimeUtil.intervalSecond(now,
                        hosDriveEventModels.get(0).getDate()));
            }
            if (hosDriveEventModels.get(0).getDriverState().equals(DriverState.DRIVING)) {

                hosDataModel.setPastSecond(TimeUtil.intervalSecond(now,
                        hosDriveEventModels.get(0).getDate()));
            }
        }
        for (int i = 1; i < hosDriveEventModels.size(); i++) {

            if (DriverState.isOnDuty(hosDriveEventModels.get(i).getDriverState())) {

                hosDataModel.setShiftSecond(hosDataModel.getShiftSecond() + TimeUtil.intervalSecond(hosDriveEventModels.get(i - 1).getDate(),
                        hosDriveEventModels.get(i).getDate()));
            }
            if (hosDriveEventModels.get(i).getDriverState().equals(DriverState.DRIVING)) {

                hosDataModel.setPastSecond(hosDataModel.getPastSecond() + TimeUtil.intervalSecond(hosDriveEventModels.get(i - 1).getDate(),
                        hosDriveEventModels.get(i).getDate()));
            }
        }

        return hosDataModel;
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
        hosDayModels.addAll(dayModels);

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
     * 根据截至时间，计算Cycle时间
     *
     * @param hosDayModels 天模型
     * @param eventTime    截至时间
     * @return Cycle时间，单位秒
     */
    private long cycleTime(List<HosDayModel> hosDayModels, Date eventTime) {

        int days = rule.getDutyDays();
        int diffDays = TimeUtil.differentDays(hosDayModels.get(0).getDate(), eventTime);
        days = days - diffDays;
        days = hosDayModels.size() < days ? hosDayModels.size() : days;

        long result = 0;
        for (int i = 0; i < days; i++) {

            result += hosDayModels.get(i).getOnDutyTime(eventTime);
        }

        return result;
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
     * 将多个违规事件合并
     *
     * @param violationModels 所有的违规列表
     * @return 合并后对外返回的违规模型
     */
    private ViolationModel mergeViolation(List<ViolationModel> violationModels) {

        ViolationModel result = new ViolationModel();
        for (ViolationModel violationModel : violationModels) {

            if (violationModel == null) {

                continue;
            }
            result.setCycle(result.getCycle() + violationModel.getCycle());
            result.setDriving(result.getDriving() + violationModel.getDriving());
            result.setShift(result.getShift() + violationModel.getShift());
            result.getEvents().addAll(violationModel.getEvents());
        }
        result.resetLimitString();
        return result;
    }
}
