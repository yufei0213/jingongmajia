package com.unitedbustech.eld.domain.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.unitedbustech.eld.domain.entry.CarrierRule;

import java.util.List;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description CarrierRuleDao
 */
@Dao
public interface CarrierRuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CarrierRule carrierRule);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<CarrierRule> carrierRuleList);

    @Query("select rule_id from carrier_rule where carrier_id = :carrierId")
    List<Integer> getRuleIdList(int carrierId);
}
