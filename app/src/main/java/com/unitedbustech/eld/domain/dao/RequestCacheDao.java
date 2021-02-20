package com.unitedbustech.eld.domain.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.unitedbustech.eld.domain.entry.RequestCache;

import java.util.List;

/**
 * @author zhangyu
 * @date 2018/1/18
 * @description 请求缓存的dao
 */
@Dao
public interface RequestCacheDao {

    @Insert
    long insert(RequestCache requestCache);

    @Insert
    void insert(List<RequestCache> requestCacheList);

    @Query("select * from request_cache")
    List<RequestCache> listAll();

    @Query("select * from request_cache where type = :type")
    List<RequestCache> listByType(int type);

    @Query("select * from request_cache where daily_log_id = :dailyLogId")
    RequestCache getRequestCache(Long dailyLogId);

    @Delete
    void delete(RequestCache requestCache);

    @Delete
    void delete(List<RequestCache> requestCacheList);

    @Query("delete from request_cache where type= :type")
    void deleteByType(int type);

    @Update()
    int updateRequestCache(RequestCache requestCache);
}