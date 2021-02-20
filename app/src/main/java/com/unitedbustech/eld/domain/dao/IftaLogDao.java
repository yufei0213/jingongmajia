package com.unitedbustech.eld.domain.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.unitedbustech.eld.domain.entry.IftaLog;

import java.util.List;

/**
 * @author yufei0213
 * @date 2018/7/13
 * @description IftaLogDao
 */
@Dao
public interface IftaLogDao {

    @Insert
    Long insert(IftaLog iftaLog);

    @Insert
    void insert(List<IftaLog> iftaLogs);

    @Update
    void update(IftaLog iftaLog);

    @Query("select * from ifta_log where server_id = :serverId limit 1")
    IftaLog getByServerId(int serverId);

    @Query("select * from ifta_log where origin_id = :originId limit 1")
    IftaLog getByOriginId(String originId);

    @Query("select * from ifta_log where driver_id = :driverId  order by `create_time` asc")
    List<IftaLog> getByDriver(int driverId);

    @Delete
    void delete(IftaLog iftaLog);

    @Delete
    void delete(List<IftaLog> iftaLogs);

    @Query("delete from ifta_log")
    void deleteAll();
}
