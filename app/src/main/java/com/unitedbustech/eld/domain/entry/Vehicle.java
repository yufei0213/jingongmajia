package com.unitedbustech.eld.domain.entry;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author yufei0213
 * @date 2018/1/18
 * @description Vehicle
 */
@Entity(tableName = "vehicle")
public class Vehicle {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "engine_hour")
    private double engineHour;

    @ColumnInfo(name = "odometer")
    private double odometer;

    @ColumnInfo(name = "vin")
    private String vin;

    @ColumnInfo(name = "ecmlink_type")
    private String ecmLinkType;

    @ColumnInfo(name = "odo_offset")
    private double odoOffset;

    @ColumnInfo(name = "ecm_sn")
    private String ecmSn;

    @ColumnInfo(name = "fuel_type")
    private String fuelType;

    @ColumnInfo(name = "odo_offset_update_time")
    private String odoOffsetUpdateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getEngineHour() {
        return engineHour;
    }

    public void setEngineHour(double engineHour) {
        this.engineHour = engineHour;
    }

    public double getOdometer() {
        return odometer;
    }

    public void setOdometer(double odometer) {
        this.odometer = odometer;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getEcmLinkType() {
        return ecmLinkType;
    }

    public void setEcmLinkType(String ecmLinkType) {
        this.ecmLinkType = ecmLinkType;
    }

    public double getOdoOffset() {
        return odoOffset;
    }

    public void setOdoOffset(double odoOffset) {
        this.odoOffset = odoOffset;
    }

    public String getEcmSn() {
        return ecmSn;
    }

    public void setEcmSn(String ecmSn) {
        this.ecmSn = ecmSn;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getOdoOffsetUpdateTime() {
        return odoOffsetUpdateTime;
    }

    public void setOdoOffsetUpdateTime(String odoOffsetUpdateTime) {
        this.odoOffsetUpdateTime = odoOffsetUpdateTime;
    }
}
