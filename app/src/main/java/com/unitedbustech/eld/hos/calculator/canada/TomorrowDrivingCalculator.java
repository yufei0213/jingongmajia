package com.unitedbustech.eld.hos.calculator.canada;

import com.unitedbustech.eld.App;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.RuleType;
import com.unitedbustech.eld.domain.entry.Rule;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.hos.model.HosDayModel;
import com.unitedbustech.eld.hos.model.HosDriveEventModel;
import com.unitedbustech.eld.util.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author yufei0213
 * @date 2018/8/4
 * @description 明日最大驾驶时间计算
 */
class TomorrowDrivingCalculator {

    /**
     * 当前时间
     */
    private Date now;

    /**
     * 规则
     */
    private Rule rule;

    TomorrowDrivingCalculator(Rule rule) {

        this.rule = rule;
    }

    public String invoke() {

        now = new Date();

        long result = 13 * 60 * 60;

        long cycleTime = calCycleTime();
        long cycleLeft = rule.getDutyTime() * 60 - cycleTime;
        result = result > cycleLeft ? cycleLeft : result;

        long off = calDayOff();
        long offOff = calOffOff();
        long cycleOff = calCycleOff(cycleTime);

        off = off < offOff ? offOff : off;
        off = off < cycleOff ? cycleOff : off;

        long left = 24 * 60 * 60 - off;
        result = result > left ? left : result;

        result = result < 0 ? 0 : result;

        long hours = result / 60 / 60; //小时数
        long minutes = result / 60 - hours * 60; //分钟数

        String hourStr = hours < 10 ? "0" + hours : "" + hours;
        String minuteStr = minutes < 10 ? "0" + minutes : "" + minutes;

        String tips = App.getContext().getString(R.string.dialog_post_trip_off_tip)
                .replace("#hour#", hourStr)
                .replace("#minute#", minuteStr);

        return tips;
    }

    /**
     * 计算Day规则下明日需要的休息的时间
     *
     * @return 明日需要休息的时间
     */
    private long calDayOff() {

        long result = 10 * 60 * 60;

        HosDayModel hosDayModel = ModelCenter.getInstance().getHosDayModel(now);

        List<HosDriveEventModel> hosDriveEventModelList = new ArrayList<>();
        hosDriveEventModelList.addAll(hosDayModel.getHosDriveEventModels());

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

        //当天的结束时间
        long todayEndTime = TimeUtil.getDayBegin(TimeUtil.getNextDate(now, 1)).getTime();
        //计算当天剩余的时间
        long todayLeftTime = (todayEndTime - now.getTime()) / 1000;
        //当最后一个事件是OFF时，此字段有值，实际上调用此方法时最后一个事件，一定是OFF
        long lastOffEventTime = 0;
        //当天最大单次休息时长，不包含最后一个事件
        long todayMaxRestTime = 0;
        //当天累计休息时间，每段休息时间都大于30分钟，不包含最后一个事件
        long todayRestTime = 0;
        //当天可能的最大单次连续休息时长
        long todayMaxRest = 0;
        //当天可能的总的最大休息时间
        long todayRestTotal = 0;

        //计算最新的事件
        if (!DriverState.isOnDuty(hosDriveEventModelList.get(0).getDriverState())) {

            lastOffEventTime = (now.getTime() - hosDriveEventModelList.get(0).getDate().getTime()) / 1000;
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

        //最后一个可能的OFF事件可能的最大持续时间
        long lastOffTime = lastOffEventTime + todayLeftTime;

        todayRestTotal = lastOffTime > 30 * 60 ? todayRestTime + lastOffTime : todayRestTime;
        todayMaxRest = todayMaxRestTime >= lastOffTime ? todayMaxRestTime : lastOffTime;

        //如果今天累计休息时间不足10小时，并且当天最大单次连续休息时间大于等于8小时
        if (todayRestTotal < 10 * 60 * 60 && todayMaxRest >= 8 * 60 * 60) {

            result = 20 * 60 * 60 - todayRestTotal;
        }

        return result;
    }

    /**
     * OFF规则下，明日需要的休息时间
     *
     * @return 明日需要休息的时间
     */
    private long calOffOff() {

        long result = 0;

        List<HosDayModel> hosDayModels = new ArrayList<>();

        List<HosDayModel> temp = ModelCenter.getInstance().getAllHosDayModels();
        int day = 14;
        for (int i = 0; i < day; i++) {

            hosDayModels.add(temp.get(i));
        }

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

                result = TimeUtil.intervalSecond(hosDriveEventModelList.get(0).getDate(),
                        TimeUtil.getDayBegin(hosDriveEventModelList.get(0).getDate()));
            } else {

                result = TimeUtil.intervalSecond(now,
                        TimeUtil.getDayBegin(now));
            }
        }

        return result;
    }

    /**
     * 计算Cycle 规则下，明日需要的休息时间
     *
     * @return 明日需要休息的时间
     */
    private long calCycleOff(long cycleTime) {

        long result = 0;

        if (rule.getId() == RuleType.CANADA_14D_120H && cycleTime >= 70 * 60 * 60) {

            List<HosDriveEventModel> hosDriveEventModels = new ArrayList<>();

            List<HosDayModel> hosDayModels = new ArrayList<>();

            List<HosDayModel> temp = ModelCenter.getInstance().getAllHosDayModels();
            int day = rule.getDutyDays() - 1;
            for (int i = 0; i < day; i++) {

                hosDayModels.add(temp.get(i));
            }

            if (hosDayModels == null || hosDayModels.isEmpty()) {

                return result;
            }
            for (HosDayModel hosDayModel : hosDayModels) {

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

            if (!findCycle24Off(hosDriveEventModels)) {

                if (!DriverState.isOnDuty(hosDriveEventModels.get(0).getDriverState())) {

                    result = TimeUtil.intervalSecond(hosDriveEventModels.get(0).getDate(),
                            TimeUtil.getDayBegin(hosDriveEventModels.get(0).getDate()));
                } else {

                    result = TimeUtil.intervalSecond(now,
                            TimeUtil.getDayBegin(now));
                }
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
    private boolean findCycle24Off(List<HosDriveEventModel> hosDriveEventModels) {

        boolean result = false;

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
     * 计算Cycle时间
     *
     * @return Cycle时间
     */
    private long calCycleTime() {

        long result = 0;

        List<HosDayModel> hosDayModels = new ArrayList<>();

        List<HosDayModel> temp = ModelCenter.getInstance().getAllHosDayModels();
        int days = rule.getDutyDays() - 1;
        for (int i = 0; i < days; i++) {

            hosDayModels.add(temp.get(i));
        }

        if (hosDayModels == null || hosDayModels.isEmpty()) {

            return result;
        }
        List<HosDriveEventModel> hosDriveEventModelList = new ArrayList<>();
        for (HosDayModel hosDayModel : hosDayModels) {

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

                    HosDayModel hosDayModel = hosDayModels.get(i);
                    result += hosDayModel.getOnDutyTime();
                }
            } else {

                days = TimeUtil.differentDays(date, now) + 1;
                for (int i = 0; i < days; i++) {

                    HosDayModel hosDayModel = hosDayModels.get(i);
                    result += hosDayModel.getOnDutyTime();
                }
            }
        } else {

            for (int i = 0; i < days; i++) {

                HosDayModel hosDayModel = hosDayModels.get(i);
                result += hosDayModel.getOnDutyTime();
            }
        }

        return result;
    }
}
