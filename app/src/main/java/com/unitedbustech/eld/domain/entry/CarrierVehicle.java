package com.unitedbustech.eld.domain.entry;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

/**
 * @author yufei0213
 * @date 2018/1/18
 * @description CarrierVehicle
 */
@Entity(tableName = "carrier_vehicle", primaryKeys = {"carrier_id", "vehicle_id"})
public class CarrierVehicle {

    @ColumnInfo(name = "carrier_id")
    private int carrierId;

    @ColumnInfo(name = "vehicle_id")
    private int vehicleId;

    public int getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(int carrierId) {
        this.carrierId = carrierId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
}
