package com.unitedbustech.eld.domain.entry;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author yufei0213
 * @date 2018/1/18
 * @description inspection存储，做缓存以及判断posttrip
 */
@Entity(tableName = "inspection_log")
public class InspectionLog {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "driver_id")
    private int driverId;

    @ColumnInfo(name = "vehicle_id")
    private int vehicleId;

    @ColumnInfo(name = "time")
    private long time;

    @ColumnInfo(name = "type")
    private int type;

    /**
     * 检查项的json
     */
    @ColumnInfo(name = "json")
    private String inspectionJson;

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getInspectionJson() {
        return inspectionJson;
    }

    public void setInspectionJson(String inspectionJson) {
        this.inspectionJson = inspectionJson;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
