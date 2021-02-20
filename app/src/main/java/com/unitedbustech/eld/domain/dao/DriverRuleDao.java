package com.unitedbustech.eld.domain.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.unitedbustech.eld.domain.entry.DriverRule;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description DriverRuleDao
 */
@Dao
public interface DriverRuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(DriverRule driverRule);

    @Query("select rule_id from driver_rule where driver_id = :driverId")
    Integer getRuleId(int driverId);

    @Query("select * from driver_rule where driver_id = :driverId")
    DriverRule getDriverRule(int driverId);
}
