package com.unitedbustech.eld.hos.calculator;

import com.unitedbustech.eld.domain.entry.Rule;
import com.unitedbustech.eld.hos.model.DailyLogsSummary;
import com.unitedbustech.eld.hos.model.HosDataModel;
import com.unitedbustech.eld.hos.violation.ViolationModel;

import java.util.Date;
import java.util.List;

/**
 * @author zhangyu
 * @date 2018/5/29
 * @description Hos计算器。
 */
public abstract class HosCalculator {

    protected Rule rule;

    public HosCalculator(Rule rule) {

        this.rule = rule;
    }

    /**
     * 计算当前的Hos数据。
     *
     * @return 返回值为当前的Hos的统计数据
     */
    public abstract HosDataModel calculateHosData();

    /**
     * 计算统计数据。统计Dailylog的基本信息。
     *
     * @return 统计信息
     */
    public abstract List<DailyLogsSummary> summary();

    /**
     * 根据指定日期计算违规信息。
     *
     * @param date 计算的日期。
     * @return 计算结果
     */
    public abstract ViolationModel calculateViolation(Date date);

    /**
     * 计算明天的驾驶提示信息。
     *
     * @return 提示信息。
     */
    public abstract String getTomorrowDrivingRemainTip();
}
