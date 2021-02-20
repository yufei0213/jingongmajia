package com.unitedbustech.eld.domain.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.unitedbustech.eld.domain.entry.Driver;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description DriverDao
 */
@Dao
public interface DriverDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Driver driver);

    @Query("select * from driver where id = :driverId limit 1")
    Driver getDriver(int driverId);

    @Update()
    int updateDriver(Driver driver);
}
