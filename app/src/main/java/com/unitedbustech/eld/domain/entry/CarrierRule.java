package com.unitedbustech.eld.domain.entry;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description CarrierRule
 */
@Entity(tableName = "carrier_rule", primaryKeys = {"carrier_id", "rule_id"})
public class CarrierRule {

    @ColumnInfo(name = "carrier_id")
    private int carrierId;

    @ColumnInfo(name = "rule_id")
    private int ruleId;

    public int getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(int carrierId) {
        this.carrierId = carrierId;
    }

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }
}
