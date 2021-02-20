package com.unitedbustech.eld.domain.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.unitedbustech.eld.domain.entry.CarrierVehicle;

import java.util.List;

/**
 * @author yufei0213
 * @date 2018/1/18
 * @description CarrierVehicleDao
 */
@Dao
public interface CarrierVehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CarrierVehicle carrierVehicle);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<CarrierVehicle> carrierVehicleList);

    @Query("select vehicle_id from carrier_vehicle where carrier_id = :carrierId")
    List<Integer> getVehicleIdList(int carrierId);
}
