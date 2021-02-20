package com.unitedbustech.eld.hos.core;

import com.unitedbustech.eld.common.RuleType;
import com.unitedbustech.eld.domain.entry.Rule;
import com.unitedbustech.eld.hos.calculator.HosCalculator;
import com.unitedbustech.eld.hos.calculator.america.AmericaCalculator;
import com.unitedbustech.eld.hos.calculator.canada.CanadaCalculator;

/**
 * @author zhangyu
 * @date 2018/5/29
 * @description Hos处理器，处理基于Hos的计算。
 */
public class HosHandler {

    /**
     * 唯一实例
     */
    private static HosHandler instance;

    /**
     * 规则
     */
    private Rule rule;

    /**
     * HOS计算器
     */
    private HosCalculator hosCalculator;

    private HosHandler() {
    }

    public static HosHandler getInstance() {

        if (instance == null) {

            instance = new HosHandler();
        }
        return instance;
    }

    /**
     * 默认是客车规则计算器。
     * 如果需要切换规则，切换计算器实现即可
     *
     * @param rule 规则
     */
    public void init(Rule rule) {

        this.rule = rule;

        if (rule.getId() == RuleType.CAR_7D_60H || rule.getId() == RuleType.CAR_8D_70H) {

            hosCalculator = new AmericaCalculator(rule);
        } else {

            hosCalculator = new CanadaCalculator(rule);
        }
    }

    public Rule getRule() {

        return rule;
    }

    /**
     * 获取hos的计算器。
     *
     * @return 获取hos计算器。
     */
    public HosCalculator getCalculator() {

        return hosCalculator;
    }
}
