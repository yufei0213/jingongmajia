package com.unitedbustech.eld.domain.entry;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * @author zhangyu
 * @date 2018/1/17
 * @description API请求的缓存类
 */
@Entity(tableName = "request_cache")
public class RequestCache implements Comparable<RequestCache> {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "type")
    private int type;
    //参数的json
    @ColumnInfo(name = "param_json")
    private String paramJson;

    @ColumnInfo(name = "daily_log_id")
    private long dailyLogId;

    @ColumnInfo(name = "create_time")
    private long createTime;

    public RequestCache() {

        this.createTime = new Date().getTime();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParamJson() {
        return paramJson;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDailyLogId() {
        return dailyLogId;
    }

    public void setDailyLogId(long dailyLogId) {
        this.dailyLogId = dailyLogId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public int compareTo(@NonNull RequestCache o) {

        return new Date(this.createTime).compareTo(new Date(o.createTime));
    }
}
