package com.unitedbustech.eld.domain.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.unitedbustech.eld.domain.entry.InspectionLog;

import java.util.List;

/**
 * @author yufei0213
 * @date 2018/1/18
 * @description CarrierVehicleDao
 */
@Dao
public interface InspectionLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(InspectionLog inspectionLog);

    @Query("select * from inspection_log where driver_id = :driverId and vehicle_id=:vehicleId and time>:time and type = 3 limit 1")
    InspectionLog get(int driverId, int vehicleId, long time);

    @Query("select * from inspection_log where driver_id = :driverId order by `time` desc")
    List<InspectionLog> getByDriver(int driverId);

    @Query("select * from inspection_log where vehicle_id=:vehicleId  order by `time` desc")
    List<InspectionLog> getByVehicle(int vehicleId);
}
