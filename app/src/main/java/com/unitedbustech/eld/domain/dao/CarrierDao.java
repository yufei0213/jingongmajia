package com.unitedbustech.eld.domain.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.unitedbustech.eld.domain.entry.Carrier;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description CarrierDao
 */
@Dao
public interface CarrierDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Carrier carrier);

    @Query("select * from carrier where id = :carrierId limit 1")
    Carrier getCarrier(int carrierId);
}
