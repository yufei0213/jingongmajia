package com.unitedbustech.eld.domain.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.unitedbustech.eld.domain.entry.RecentVehicle;

import java.util.List;

/**
 * @author yufei0213
 * @date 2018/1/18
 * @description RecentVehicleDao
 */
@Dao
public interface RecentVehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(RecentVehicle recentVehicle);

    @Query("select * from recent_vehicle where vehicle_id = :vehicleId limit 1")
    RecentVehicle get(int vehicleId);

    @Query("select vehicle_id from recent_vehicle where carrier_id = :carrierId order by connected_time desc limit 3")
    List<Integer> getRecentVehicleList(int carrierId);
}
