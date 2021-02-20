package com.unitedbustech.eld.domain.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.unitedbustech.eld.domain.entry.Vehicle;

import java.util.List;

/**
 * @author yufei0213
 * @date 2018/1/18
 * @description VehicleDao
 */
@Dao
public interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Vehicle vehicle);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Vehicle> vehicleList);

    @Update
    void update(Vehicle vehicle);

    @Query("delete from vehicle")
    void deleteAll();

    @Query("select * from vehicle where id in (:ids)")
    List<Vehicle> getVehicleList(List<Integer> ids);

    @Query("select * from vehicle where id = :vehicleId limit 1")
    Vehicle getVehicle(int vehicleId);
}
