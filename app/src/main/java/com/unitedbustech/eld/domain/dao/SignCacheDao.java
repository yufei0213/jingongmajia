package com.unitedbustech.eld.domain.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.unitedbustech.eld.domain.entry.SignCache;

import java.util.List;

/**
 * @author zhangyu
 * @date 2018/1/18
 * @description 请求缓存的dao
 */
@Dao
public interface SignCacheDao {

    @Insert
    void insert(List<SignCache> signCaches);

    @Query("select * from sign_cache")
    List<SignCache> listAll();

    @Query("delete from sign_cache")
    void deleteAll();

    @Query("update sign_cache set status = 0 , sign = '' where date = :date and driver_id = :driverId")
    void unSignOneDay(String date, int driverId);

    @Query("update sign_cache set status = 1, sign = :sign where date = :date and driver_id = :driverId")
    void signOneDay(String date, int driverId, String sign);

    @Query("select sign from sign_cache where date = :date and driver_id = :driverId and status = 1")
    String getOneDaySign(String date, int driverId);
}
