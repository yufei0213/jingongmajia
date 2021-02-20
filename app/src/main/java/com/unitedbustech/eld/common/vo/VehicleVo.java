package com.unitedbustech.eld.common.vo;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.domain.entry.RecentVehicle;
import com.unitedbustech.eld.domain.entry.Vehicle;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.TimeUtil;

/**
 * @author yufei0213
 * @date 2018/1/18
 * @description VehicleVo
 */
public class VehicleVo implements Comparable<VehicleVo> {

    private int id;

    private String code;

    private double engineHour;

    private double odometer;

    private String vin;

    private String ecmLinkType;

    private String connectedTime;

    private String ecmSn;

    private String fuelType;

    public VehicleVo() {
    }

    public VehicleVo(Vehicle vehicle) {

        this.id = vehicle.getId();
        this.code = vehicle.getCode();
        this.engineHour = vehicle.getEngineHour();
        this.odometer = vehicle.getOdometer();
        this.vin = vehicle.getVin();
        this.ecmLinkType = vehicle.getEcmLinkType();
        this.ecmSn = vehicle.getEcmSn();
        this.fuelType = vehicle.getFuelType();
    }

    public VehicleVo(Vehicle vehicle, VehicleDataModel vehicleDataModel) {

        this.id = vehicle.getId();
        this.code = vehicle.getCode();

        if (((int) vehicleDataModel.getTotalEngineHours() * 1000) != 0) {

            this.engineHour = vehicleDataModel.getTotalEngineHours();
        } else {

            this.engineHour = vehicle.getEngineHour();
        }
        if (((int) vehicleDataModel.getTotalOdometer() * 1000) != 0) {

            this.odometer = vehicleDataModel.getTotalOdometer();
        } else {

            this.odometer = vehicle.getOdometer();
        }
        if (TextUtils.isEmpty(vehicle.getVin())) {

            this.vin = vehicleDataModel.getVin();
        }

        this.ecmLinkType = vehicle.getEcmLinkType();
        this.fuelType = vehicle.getFuelType();
    }

    public VehicleVo(Vehicle vehicle, RecentVehicle recentVehicle) {

        this.id = vehicle.getId();
        this.code = vehicle.getCode();
        this.engineHour = vehicle.getEngineHour();
        this.odometer = vehicle.getOdometer();
        this.vin = vehicle.getVin();
        this.ecmLinkType = vehicle.getEcmLinkType();
        this.fuelType = vehicle.getFuelType();

        User user = SystemHelper.getUser();
        String dateStr = TimeUtil.utcToLocal(recentVehicle.getConnectedTime(),
                user.getTimeZone(),
                TimeUtil.STANDARD_FORMAT);

        this.connectedTime = dateStr + " " + user.getTimeZoneAlias();
    }

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

    public String getConnectedTime() {
        return connectedTime;
    }

    public void setConnectedTime(String connectedTime) {
        this.connectedTime = connectedTime;
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

    @Override
    public int compareTo(@NonNull VehicleVo o) {

        return this.code.toLowerCase().compareTo(o.getCode().toLowerCase());
    }
}
