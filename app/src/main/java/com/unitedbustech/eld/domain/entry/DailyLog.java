package com.unitedbustech.eld.domain.entry;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description 一天的dailylog使用json序列化后的记录表。
 */
@Entity(tableName = "daily_log")
public class DailyLog {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "json")
    private String json;

    @ColumnInfo(name = "type")
    private int type;

    //原始的数据id，这个id是为了找到之后执行删除操作以及定位操作的。
    @ColumnInfo(name = "originId")
    private String originId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJson() {
        return json;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }
}
