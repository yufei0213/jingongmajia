package com.unitedbustech.eld.hos.calculator.canada;

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
 * @author liuzhe
 * @date 2018/7/16
 * @description 加拿大Hos计算器
 */
public class CanadaCalculator extends HosCalculator {

    public CanadaCalculator(Rule rule) {

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

        return new TomorrowDrivingCalculator(rule).invoke();
    }
}
