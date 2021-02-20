package com.unitedbustech.eld.domain.entry;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author yufei0213
 * @date 2018/1/18
 * @description RecentVehicle
 */
@Entity(tableName = "recent_vehicle")
public class RecentVehicle {

    @PrimaryKey
    @ColumnInfo(name = "vehicle_id")
    private int vehicleId;

    @ColumnInfo(name = "carrier_id")
    private int carrierId;

    @ColumnInfo(name = "connected_time")
    private long connectedTime;

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(int carrierId) {
        this.carrierId = carrierId;
    }

    public long getConnectedTime() {
        return connectedTime;
    }

    public void setConnectedTime(long connectedTime) {
        this.connectedTime = connectedTime;
    }
}
