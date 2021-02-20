package com.unitedbustech.eld.domain.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.unitedbustech.eld.domain.entry.ProfileEntity;

import java.util.List;

/**
 * @author zhangyu
 * @date 2018/1/18
 * @description 请求缓存的dao
 */
@Dao
public interface ProfileDao {

    @Insert
    Long insert(ProfileEntity profileEntity);

    @Insert()
    List<Long> insert(List<ProfileEntity> profileEntities);

    @Update
    void update(ProfileEntity profileEntity);

    @Query("select * from profile")
    List<ProfileEntity> listAll();

    @Query("select * from profile where date = :date limit 1")
    ProfileEntity getByDate(String date);

    @Query("select count(id) from profile")
    int count();

    @Query("delete from profile")
    void deleteAll();

}
