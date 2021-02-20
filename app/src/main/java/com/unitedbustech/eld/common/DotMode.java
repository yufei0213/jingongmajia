package com.unitedbustech.eld.common;

/**
 * @author yufei0213
 * @date 2018/6/19
 * @description Dot界面显示的模式
 */
public class DotMode {

    public static final int America = 0;

    public static final int Canada_7_60 = 1;

    public static final int Canada_14_120 = 2;

    public static int getModeType(int ruleId) {

        switch (ruleId) {
            case RuleType.CAR_7D_60H:
            case RuleType.CAR_8D_70H:

                return America;
            case RuleType.CANADA_7D_70H:

                return Canada_7_60;
            case RuleType.CANADA_14D_120H:

                return Canada_14_120;

        }
        return America;
    }
}
