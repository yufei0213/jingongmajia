package com.unitedbustech.eld.hos.calculator.america;

import com.unitedbustech.eld.App;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.domain.entry.Rule;
import com.unitedbustech.eld.hos.calculator.HosCalculator;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.hos.model.DailyLogsSummary;
import com.unitedbustech.eld.hos.model.HosDataModel;
import com.unitedbustech.eld.hos.model.HosDayModel;
import com.unitedbustech.eld.hos.violation.ViolationModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyu
 * @date 2018/5/29
 * @description 美国客车Hos计算器
 */
public class AmericaCalculator extends HosCalculator {

    public AmericaCalculator(Rule rule) {

        super(rule);
    }

    @Override
    public HosDataModel calculateHosData() {

        //每次都要new出来，防止多线程问题
        return new TimeCalculator(rule).invoke();
    }

    @Override
    public List<DailyLogsSummary> summary() {

        List<DailyLogsSummary> dailyLogsSummaries = new ArrayList<>();
        List<HosDayModel> hosDayModels = ModelCenter.getInstance().getAllHosDayModels();
        for (int i = 0; i < Constants.DDL_DAYS; i++) {

            HosDayModel hosDayModel = hosDayModels.get(i);
            dailyLogsSummaries.add(hosDayModel.summary());
        }
        return dailyLogsSummaries;
    }

    @Override
    public ViolationModel calculateViolation(Date date) {

        //每次都要new出来，防止多线程问题
        return new ViolationCalculator(rule).getHosViolationInfo(date);
    }

    @Override
    public String getTomorrowDrivingRemainTip() {

        int day = rule.getDutyDays() - 1;
        int result = 0;
        List<HosDayModel> hosDayModels = ModelCenter.getInstance().getAllHosDayModels();
        for (int i = 0; i < day; i++) {

            result += hosDayModels.get(i).getOnDutyTime();
        }

        result = rule.getDutyTime() * 60 - result;
        result = result < rule.getMaxDriving() * 60 ? result : rule.getMaxDriving() * 60;
        result = result < 0 ? 0 : result;

        int hours = result / 60 / 60; //小时数
        int minutes = result / 60 - hours * 60; //分钟数

        String hourStr = hours < 10 ? "0" + hours : "" + hours;
        String minuteStr = minutes < 10 ? "0" + minutes : "" + minutes;

        String tips = App.getContext().getString(R.string.dialog_post_trip_off_tip)
                .replace("#hour#", hourStr)
                .replace("#minute#", minuteStr);

        return tips;
    }
}
