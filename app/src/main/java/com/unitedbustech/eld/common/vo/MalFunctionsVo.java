package com.unitedbustech.eld.common.vo;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.App;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.DiagnosticType;
import com.unitedbustech.eld.common.MalfunctionType;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.TimeUtil;

/**
 * @author yufei0213
 * @date 2018/2/24
 * @description 故障检测Vo
 */
public class MalFunctionsVo {

    public static final int MALFUNCTION = 1;
    public static final int DIAGNOSTIC = -1;

    private String code;

    private String name;

    private String updateTime;

    public int type;

    public MalFunctionsVo() {
    }

    public MalFunctionsVo(JSONObject jsonObject) {

        this.code = JsonUtil.getString(jsonObject, "abnormalCode");

        if (MalfunctionType.getEnumByCode(this.code) != null) {

            this.type = MALFUNCTION;

            switch (MalfunctionType.getEnumByCode(this.code)) {

                case POWER_COMPLIANCE:

                    this.name = App.getContext().getResources().getString(R.string.mal_power_name);
                    break;
                case ENGINE_SYNCHRONIZATION_COMPLIANCE:

                    this.name = App.getContext().getResources().getString(R.string.mal_engine_sync_name);
                    break;
                case TIMING_COMPLIANCE:

                    this.name = App.getContext().getResources().getString(R.string.mal_time_name);
                    break;
                case POSITIONING_COMPLIANCE:

                    this.name = App.getContext().getResources().getString(R.string.mal_position_name);
                    break;
                case DATA_RECORDING_COMPLIANCE:

                    this.name = App.getContext().getResources().getString(R.string.mal_data_rec_name);
                    break;
                case DATA_TRANSFER_COMPLIANCE:

                    this.name = App.getContext().getResources().getString(R.string.mal_data_trans_mal);
                    break;
                case OTHER_ELD_DETECT:

                    this.name = App.getContext().getResources().getString(R.string.mal_other_name);
                    break;
                default:
                    break;
            }
        }

        if (DiagnosticType.getEnumByCode(this.code) != null) {

            this.type = DIAGNOSTIC;

            switch (DiagnosticType.getEnumByCode(this.code)) {

                case POWER_DATA:

                    this.name = App.getContext().getResources().getString(R.string.diag_power_name);
                    break;
                case ENGINE_SYNCHRONIZATION:

                    this.name = App.getContext().getResources().getString(R.string.diag_engine_sync_name);
                    break;
                case MISSING_REQUIRED_DATA_ELEMENTS_DATA:

                    this.name = App.getContext().getResources().getString(R.string.diag_data_rec_name);
                    break;
                case DATA_TRANSFER_DATA:

                    this.name = App.getContext().getResources().getString(R.string.diag_data_trans_name);
                    break;
                case UNIDENTIFIED_DRIVING_RECORDS_DATA:

                    this.name = App.getContext().getResources().getString(R.string.diag_unidentified_name);
                    break;
                case OTHER_DIAGNOSTIC:

                    this.name = App.getContext().getResources().getString(R.string.diag_other_name);
                    break;
            }
        }

        long updateTime = JsonUtil.getLong(jsonObject, "updateStatusTime");

        User user = SystemHelper.getUser();
        String dateStr = TimeUtil.utcToLocal(updateTime,
                user.getTimeZone(),
                TimeUtil.STANDARD_FORMAT);

        this.updateTime = dateStr + " " + user.getTimeZoneAlias();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
