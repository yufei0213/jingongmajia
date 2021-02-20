package com.unitedbustech.eld.datacollector.device.bluelink.analysis;

/**
 * @author yufei0213
 * @date 2018/1/6
 * @description 分析数据的结果对应的KEY
 */
public class ReadDataKey {

    //combined group key
    public static final String rpm = "Engine Speed";
    public static final String speed = "Vehicle Speed";
    public static final String odometer = "Total Distance";
    public static final String totalFuelUsed = "Total Fuel Used";
    public static final String primaryFuelLevel = "Primary Fuel Level";
    public static final String secondaryFuelLevel = "secondaryFuelLevel";
    public static final String engineLoad = "Engine Load";
    public static final String engineOil = "Engine Oil";
    public static final String engineHours = "Engine Hours";
    public static final String vehicleState = "Vehicle State";
    public static final String engineOnTime = "Engine On Time";
    public static final String latitude = "Latitude";
    public static final String longitude = "Longitude";

    //stateString data
    public static final String state = "State";
    public static final String reportTime = "Report Time";
    //evspeed
    public static final String intakeManifold1pressure = "Intake Manifold 1 pressure";

    //fuel
    public static final String engineAverageFuelEconomy = "Engine Average Fuel economy";
    public static final String totalAmountOfFuelUsedSenderSourceAddress = "Sender Source Address";
    public static final String senderBus = "Sender Bus";
    public static final String instantaneousFuelRate = "instantaneous fuel rate";
    public static final String instantaneousFuelEconomy = "instantaneous fuel economy";

    //Battery
    public static final String vehicleBatteryPotential = "vehicle battery potential";
    public static final String netBatteryCurrent = "Net Battery Current";
    public static final String engineCoolantTemperature = " Engine Coolant Temperature";
    public static final String engineCoolantLevel = " Engine Coolant Level";
    public static final String engineOiltemperature = "Engine Oil temperature";
    public static final String engineOillevel = "Engine Oil level";
    public static final String transmissionOilTemperature = "Transmission Oil temperature";
    public static final String transmissionOillevel = "Transmission Oil level";
    public static final String blueLinkExternalPowerLevel = "BlueLink External power level";
    //VIN
    public static final String vin = "VIN Code";

    //新事项
    public static final String newHistory = "New History";

    //历史数据
    public static final String hasHistory = "has record";
    public static final String historyId = "record id";
    public static final String historyType = "history type";
    public static final String historyTime = "Event time";
    public static final String historyOdometer = "Odometer";
    public static final String historyEngineHour = "engine total hours";
    public static final String historyLat = "Location’s latitude";
    public static final String historyLng = "Location’s longitude";
    public static final String historyFlag = "Record flags";
    public static final String historySpeed = "Speed";
}