package com.unitedbustech.eld.domain.entry;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * @author zhangyu
 * @date 2018/1/17
 * @description 签名请求缓存类
 */
@Entity(tableName = "sign_cache",primaryKeys = {"date", "driver_id"})
public class SignCache {

    @NonNull
    @ColumnInfo(name = "date")
    private String date;

    @NonNull
    @ColumnInfo(name = "driver_id")
    private int driverId;

    @ColumnInfo(name = "status")
    private int status;

    @ColumnInfo(name = "sign")
    private String sign;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }
}
