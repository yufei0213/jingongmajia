package com.unitedbustech.eld.common;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description 用户模型
 */
public class User {

    private String accountCarrierId;

    private String accountDriverId;

    private String password;

    private String accessToken;

    private int carriedId;

    private int driverId;

    private int vehicleId;

    private String timeZone;

    private String timeZoneAlias;

    private int function;

    public User() {
    }

    public User(int carriedId, int driverId, String timeZone,
                String timeZoneAlias, String accountCarrierId, String accountDriverId,
                String password, String accessToken) {

        this.driverId = driverId;
        this.carriedId = carriedId;
        this.timeZone = timeZone;
        this.timeZoneAlias = timeZoneAlias;

        this.accountCarrierId = accountCarrierId;
        this.accountDriverId = accountDriverId;
        this.password = password;
        this.accessToken = accessToken;
    }

    public String getAccountCarrierId() {
        return accountCarrierId;
    }

    public void setAccountCarrierId(String accountCarrierId) {
        this.accountCarrierId = accountCarrierId;
    }

    public String getAccountDriverId() {
        return accountDriverId;
    }

    public void setAccountDriverId(String accountDriverId) {
        this.accountDriverId = accountDriverId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getCarriedId() {
        return carriedId;
    }

    public void setCarriedId(int carriedId) {
        this.carriedId = carriedId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTimeZoneAlias() {
        return timeZoneAlias;
    }

    public void setTimeZoneAlias(String timeZoneAlias) {
        this.timeZoneAlias = timeZoneAlias;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getFunction() {
        return function;
    }

    public void setFunction(int function) {
        this.function = function;
    }
}
