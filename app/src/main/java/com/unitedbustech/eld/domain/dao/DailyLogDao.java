package com.unitedbustech.eld.domain.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.unitedbustech.eld.domain.entry.DailyLog;

import java.util.List;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description DailyLogDao
 */
@Dao
public interface DailyLogDao {

    @Insert()
    List<Long> insertAll(List<DailyLog> dailyLogs);

    @Insert()
    Long insert(DailyLog dailyLog);

    @Query("select * from daily_log")
    List<DailyLog> listAll();

    @Query("select * from daily_log where id = :id")
    DailyLog getDailylogById(Long id);

    @Query("select * from daily_log where originId in (:ids)")
    List<DailyLog> getDailyLogByOriginIds(List<String> ids);

    @Query("select * from daily_log where originId = :id")
    DailyLog getDailylogByoriginId(String id);

    @Query("select count(id) from daily_log")
    int count();

    @Update()
    void updateDailylog(DailyLog dailyLog);

    @Query("delete from daily_log")
    void deleteAll();
}
