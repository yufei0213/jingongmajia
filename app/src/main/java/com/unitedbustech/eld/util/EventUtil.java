package com.unitedbustech.eld.util;

import com.unitedbustech.eld.App;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.DiagnosticType;
import com.unitedbustech.eld.common.MalfunctionType;
import com.unitedbustech.eld.eventcenter.enums.EventItem;

/**
 * @author zhangyu
 * @date 2018/1/27
 * @description EventUtil
 */
public class EventUtil {

    /**
     * 根据服务端存储的事件type与code，计算出在页面数显示的标题
     *
     * @param type type
     * @param code code
     * @return 解析后的字符串
     */
    public static String getHosTitle(int type, int code, boolean isDot) {

        if (type == 1) {

            switch (code) {
                case 1:
                    return App.getContext().getString(R.string.ddl_off_duty);
                case 2:
                    return App.getContext().getString(R.string.ddl_sleeper_berth);
                case 3:
                    return App.getContext().getString(R.string.ddl_driving);
                case 4:
                    return App.getContext().getString(R.string.ddl_on_duty_not_driving);

            }
        }
        if (type == 3) {

            switch (code) {
                case 0:
                    return App.getContext().getString(R.string.ddl_clear_special);
                case 1:
                    return App.getContext().getString(R.string.ddl_personal_use);
                case 2:
                    return App.getContext().getString(R.string.ddl_yard_move);
            }
        }
        if (type == 5) {

            switch (code) {
                case 1:
                    if (isDot) {

                        //适配国际化
                        return App.getContext().getString(R.string.ddl_login_dot);
                    } else {

                        return App.getContext().getString(R.string.ddl_login);
                    }
                case 2:
                    if (isDot) {

                        //适配国际化
                        return App.getContext().getString(R.string.ddl_log_out_dot);
                    } else {

                        return App.getContext().getString(R.string.ddl_log_out);
                    }
            }
        }
        if (type == 6) {

            switch (code) {

                case 1:
                case 2:
                    if (isDot) {

                        //适配国际化
                        return App.getContext().getString(R.string.ddl_engine_on_dot);
                    } else {

                        return App.getContext().getString(R.string.ddl_engine_on);
                    }

                case 3:
                case 4:
                    if (isDot) {

                        return App.getContext().getString(R.string.ddl_engine_off_dot);
                    } else {

                        return App.getContext().getString(R.string.ddl_engine_off);
                    }
            }
        }
        if (type == 4) {

            if (isDot) {

                return App.getContext().getString(R.string.ddl_sign_dot);
            } else {

                return App.getContext().getString(R.string.ddl_sign);
            }
        }
        if (type == EventItem.ADVERSE_DRIVING.getCode()) {

            if (isDot) {

                return App.getContext().getString(R.string.ddl_adverse_driving_dot);
            } else {

                return App.getContext().getString(R.string.ddl_adverse_driving);
            }
        }
        if (type == EventItem.INTERMEDIATE.getCode()) {
            if (isDot) {

                return App.getContext().getString(R.string.ddl_intermediate_dot);
            } else {

                return App.getContext().getString(R.string.ddl_intermediate);
            }
        }
        if (type == EventItem.RULE_CHANGE.getCode()) {

            if (isDot) {

                switch (code) {
                    case 1:
                        return App.getContext().getString(R.string.ddl_rule_change_usa_60hours_dot);
                    case 2:
                        return App.getContext().getString(R.string.ddl_rule_change_usa_70hours_dot);
                    case 7:
                        return App.getContext().getString(R.string.ddl_rule_change_canada_70hours_dot);
                    case 8:
                        return App.getContext().getString(R.string.ddl_rule_change_canada_120hours_dot);
                }
            } else {

                switch (code) {
                    case 1:
                        return App.getContext().getString(R.string.ddl_rule_change_usa_60hours);
                    case 2:
                        return App.getContext().getString(R.string.ddl_rule_change_usa_70hours);
                    case 7:
                        return App.getContext().getString(R.string.ddl_rule_change_canada_70hours);
                    case 8:
                        return App.getContext().getString(R.string.ddl_rule_change_canada_120hours);
                }
            }
        }
        String result = App.getContext().getString(R.string.ddl_unknown);

        return result;
    }

    /**
     * 获取诊断数据的字符串
     *
     * @param code         code
     * @param abnormalCode 异常描述
     * @return 解析后的字符串
     */
    public static String getSelfCheckModelTitle(int code, String abnormalCode, boolean isDot) {

        if (code < 3) {

            if (MalfunctionType.getEnumByCode(abnormalCode) != null && !isDot) {

                switch (MalfunctionType.getEnumByCode(abnormalCode)) {

                    case POWER_COMPLIANCE:
                        if (code == 1) {
                            return App.getContext().getString(R.string.ddl_diag_log_p);
                        } else {
                            return App.getContext().getString(R.string.ddl_diag_log_p_c);
                        }
                    case ENGINE_SYNCHRONIZATION_COMPLIANCE:
                        if (code == 1) {
                            return App.getContext().getString(R.string.ddl_diag_log_e);
                        } else {
                            return App.getContext().getString(R.string.ddl_diag_log_e_c);
                        }
                    case TIMING_COMPLIANCE:
                        if (code == 1) {
                            return App.getContext().getString(R.string.ddl_diag_log_t);
                        } else {
                            return App.getContext().getString(R.string.ddl_diag_log_t_c);
                        }
                    case POSITIONING_COMPLIANCE:
                        if (code == 1) {
                            return App.getContext().getString(R.string.ddl_diag_log_l);
                        } else {
                            return App.getContext().getString(R.string.ddl_diag_log_l_c);
                        }
                    case DATA_RECORDING_COMPLIANCE:
                        if (code == 1) {
                            return App.getContext().getString(R.string.ddl_diag_log_r);
                        } else {
                            return App.getContext().getString(R.string.ddl_diag_log_r_c);
                        }
                    case DATA_TRANSFER_COMPLIANCE:
                        if (code == 1) {
                            return App.getContext().getString(R.string.ddl_diag_log_s);
                        } else {
                            return App.getContext().getString(R.string.ddl_diag_log_s_c);
                        }
                    case OTHER_ELD_DETECT:
                        if (code == 1) {
                            return App.getContext().getString(R.string.ddl_diag_log_o);
                        } else {
                            return App.getContext().getString(R.string.ddl_diag_log_o_c);
                        }
                }
            }
            if (MalfunctionType.getEnumByCode(abnormalCode) != null && isDot) {
                switch (MalfunctionType.getEnumByCode(abnormalCode)) {

                    case POWER_COMPLIANCE:
                        if (code == 1) {
                            return App.getContext().getString(R.string.dot_diag_log_p);
                        } else {
                            return App.getContext().getString(R.string.dot_diag_log_p_c);
                        }
                    case ENGINE_SYNCHRONIZATION_COMPLIANCE:
                        if (code == 1) {
                            return App.getContext().getString(R.string.dot_diag_log_e);
                        } else {
                            return App.getContext().getString(R.string.dot_diag_log_e_c);
                        }
                    case TIMING_COMPLIANCE:
                        if (code == 1) {
                            return App.getContext().getString(R.string.dot_diag_log_t);
                        } else {
                            return App.getContext().getString(R.string.dot_diag_log_t_c);
                        }
                    case POSITIONING_COMPLIANCE:
                        if (code == 1) {
                            return App.getContext().getString(R.string.dot_diag_log_l);
                        } else {
                            return App.getContext().getString(R.string.dot_diag_log_l_c);
                        }
                    case DATA_RECORDING_COMPLIANCE:
                        if (code == 1) {
                            return App.getContext().getString(R.string.dot_diag_log_r);
                        } else {
                            return App.getContext().getString(R.string.dot_diag_log_r_c);
                        }
                    case DATA_TRANSFER_COMPLIANCE:
                        if (code == 1) {
                            return App.getContext().getString(R.string.dot_diag_log_s);
                        } else {
                            return App.getContext().getString(R.string.dot_diag_log_s_c);
                        }
                    case OTHER_ELD_DETECT:
                        if (code == 1) {
                            return App.getContext().getString(R.string.dot_diag_log_o);
                        } else {
                            return App.getContext().getString(R.string.dot_diag_log_o_c);
                        }
                }
            }

        } else {

            if (DiagnosticType.getEnumByCode(abnormalCode) != null && !isDot) {

                switch (DiagnosticType.getEnumByCode(abnormalCode)) {

                    case POWER_DATA:
                        if (code == 3) {
                            return App.getContext().getString(R.string.ddl_diag_log_1);
                        } else {
                            return App.getContext().getString(R.string.ddl_diag_log_1_c);
                        }
                    case ENGINE_SYNCHRONIZATION:
                        if (code == 3) {
                            return App.getContext().getString(R.string.ddl_diag_log_2);
                        } else {
                            return App.getContext().getString(R.string.ddl_diag_log_2_c);
                        }
                    case MISSING_REQUIRED_DATA_ELEMENTS_DATA:
                        if (code == 3) {
                            return App.getContext().getString(R.string.ddl_diag_log_3);
                        } else {
                            return App.getContext().getString(R.string.ddl_diag_log_3_c);
                        }
                    case DATA_TRANSFER_DATA:
                        if (code == 3) {
                            return App.getContext().getString(R.string.ddl_diag_log_4);
                        } else {
                            return App.getContext().getString(R.string.ddl_diag_log_4_c);
                        }
                    case UNIDENTIFIED_DRIVING_RECORDS_DATA:
                        if (code == 3) {
                            return App.getContext().getString(R.string.ddl_diag_log_5);
                        } else {
                            return App.getContext().getString(R.string.ddl_diag_log_5_c);
                        }
                    case OTHER_DIAGNOSTIC:
                        if (code == 3) {
                            return App.getContext().getString(R.string.ddl_diag_log_6);
                        } else {
                            return App.getContext().getString(R.string.ddl_diag_log_6_c);
                        }
                }
            }
            if (DiagnosticType.getEnumByCode(abnormalCode) != null && isDot) {

                switch (DiagnosticType.getEnumByCode(abnormalCode)) {

                    case POWER_DATA:
                        if (code == 3) {
                            return App.getContext().getString(R.string.dot_diag_log_1);
                        } else {
                            return App.getContext().getString(R.string.dot_diag_log_1_c);
                        }
                    case ENGINE_SYNCHRONIZATION:
                        if (code == 3) {
                            return App.getContext().getString(R.string.dot_diag_log_2);
                        } else {
                            return App.getContext().getString(R.string.dot_diag_log_2_c);
                        }
                    case MISSING_REQUIRED_DATA_ELEMENTS_DATA:
                        if (code == 3) {
                            return App.getContext().getString(R.string.dot_diag_log_3);
                        } else {
                            return App.getContext().getString(R.string.dot_diag_log_3_c);
                        }
                    case DATA_TRANSFER_DATA:
                        if (code == 3) {
                            return App.getContext().getString(R.string.dot_diag_log_4);
                        } else {
                            return App.getContext().getString(R.string.dot_diag_log_4_c);
                        }
                    case UNIDENTIFIED_DRIVING_RECORDS_DATA:
                        if (code == 3) {
                            return App.getContext().getString(R.string.dot_diag_log_5);
                        } else {
                            return App.getContext().getString(R.string.dot_diag_log_5_c);
                        }
                    case OTHER_DIAGNOSTIC:
                        if (code == 3) {
                            return App.getContext().getString(R.string.dot_diag_log_6);
                        } else {
                            return App.getContext().getString(R.string.dot_diag_log_6_c);
                        }
                }
            }
        }

        return App.getContext().getString(R.string.ddl_unknown);
    }
}
