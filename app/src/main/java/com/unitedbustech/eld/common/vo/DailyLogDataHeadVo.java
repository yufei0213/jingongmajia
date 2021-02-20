package com.unitedbustech.eld.common.vo;

/**
 * @author mamw
 * @date 2018/1/25
 * @description DailyLogDataHeadVo
 */
public class DailyLogDataHeadVo {

    private String carrier;
    private String coDriverId;
    private String coDriverName;
    private String dataDiagnosticIndicators;
    private String driverId;
    private String driverLicenseNumber;
    private String driverLicenseState;
    private String driverName;
    private String eldId;
    private String eldMalfunctionIndicators;
    private String eldManufacturer;
    private String exemptDriverStatus;
    private String periodStartingTime;
    private String recordDate;
    private String shippingId;
    private String startEndEngineHours;
    private String startEndOdometer;
    private String timeZone;
    private String trailerId;
    private String truckTractorId;
    private String truckTractorVin;
    private String unidentifiedDriverRecords;
    private String usdot;

    public DailyLogDataHeadVo() {
    }

    public DailyLogDataHeadVo(DailyLogDataHeadVo dailyLogDataHeadVo) {

        this.carrier = dailyLogDataHeadVo.getCarrier();
        this.coDriverId = dailyLogDataHeadVo.getCoDriverId();
        this.coDriverName = dailyLogDataHeadVo.getCoDriverName();
        this.dataDiagnosticIndicators = dailyLogDataHeadVo.getDataDiagnosticIndicators();
        this.driverId = dailyLogDataHeadVo.getDriverId();
        this.driverLicenseNumber = dailyLogDataHeadVo.getDriverLicenseNumber();
        this.driverLicenseState = dailyLogDataHeadVo.getDriverLicenseState();
        this.driverName = dailyLogDataHeadVo.getDriverName();
        this.eldId = dailyLogDataHeadVo.getEldId();
        this.eldMalfunctionIndicators = dailyLogDataHeadVo.getEldMalfunctionIndicators();
        this.eldManufacturer = dailyLogDataHeadVo.getEldManufacturer();
        this.exemptDriverStatus = dailyLogDataHeadVo.getExemptDriverStatus();
        this.periodStartingTime = dailyLogDataHeadVo.getPeriodStartingTime();
        this.recordDate = dailyLogDataHeadVo.getRecordDate();
        this.shippingId = dailyLogDataHeadVo.getShippingId();
        this.startEndEngineHours = dailyLogDataHeadVo.getStartEndEngineHours();
        this.startEndOdometer = dailyLogDataHeadVo.getStartEndOdometer();
        this.timeZone = dailyLogDataHeadVo.getTimeZone();
        this.trailerId = dailyLogDataHeadVo.getTrailerId();
        this.trailerId = dailyLogDataHeadVo.getTrailerId();
        this.truckTractorId = dailyLogDataHeadVo.getTruckTractorId();
        this.truckTractorVin = dailyLogDataHeadVo.getTruckTractorVin();
        this.unidentifiedDriverRecords = dailyLogDataHeadVo.getUnidentifiedDriverRecords();
        this.usdot = dailyLogDataHeadVo.getUsdot();
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getCoDriverId() {
        return coDriverId;
    }

    public void setCoDriverId(String coDriverId) {
        this.coDriverId = coDriverId;
    }

    public String getCoDriverName() {
        return coDriverName;
    }

    public void setCoDriverName(String coDriverName) {
        this.coDriverName = coDriverName;
    }

    public String getDataDiagnosticIndicators() {
        return dataDiagnosticIndicators;
    }

    public void setDataDiagnosticIndicators(String dataDiagnosticIndicators) {
        this.dataDiagnosticIndicators = dataDiagnosticIndicators;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public String getDriverLicenseState() {
        return driverLicenseState;
    }

    public void setDriverLicenseState(String driverLicenseState) {
        this.driverLicenseState = driverLicenseState;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getEldId() {
        return eldId;
    }

    public void setEldId(String eldId) {
        this.eldId = eldId;
    }

    public String getEldMalfunctionIndicators() {
        return eldMalfunctionIndicators;
    }

    public void setEldMalfunctionIndicators(String eldMalfunctionIndicators) {
        this.eldMalfunctionIndicators = eldMalfunctionIndicators;
    }

    public String getEldManufacturer() {
        return eldManufacturer;
    }

    public void setEldManufacturer(String eldManufacturer) {
        this.eldManufacturer = eldManufacturer;
    }

    public String getExemptDriverStatus() {
        return exemptDriverStatus;
    }

    public void setExemptDriverStatus(String exemptDriverStatus) {
        this.exemptDriverStatus = exemptDriverStatus;
    }

    public String getPeriodStartingTime() {
        return periodStartingTime;
    }

    public void setPeriodStartingTime(String periodStartingTime) {
        this.periodStartingTime = periodStartingTime;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getShippingId() {
        return shippingId;
    }

    public void setShippingId(String shippingId) {
        this.shippingId = shippingId;
    }

    public String getStartEndEngineHours() {
        return startEndEngineHours;
    }

    public void setStartEndEngineHours(String startEndEngineHours) {
        this.startEndEngineHours = startEndEngineHours;
    }

    public String getStartEndOdometer() {
        return startEndOdometer;
    }

    public void setStartEndOdometer(String startEndOdometer) {
        this.startEndOdometer = startEndOdometer;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(String trailerId) {
        this.trailerId = trailerId;
    }

    public String getTruckTractorId() {
        return truckTractorId;
    }

    public void setTruckTractorId(String truckTractorId) {
        this.truckTractorId = truckTractorId;
    }

    public String getTruckTractorVin() {
        return truckTractorVin;
    }

    public void setTruckTractorVin(String truckTractorVin) {
        this.truckTractorVin = truckTractorVin;
    }

    public String getUnidentifiedDriverRecords() {
        return unidentifiedDriverRecords;
    }

    public void setUnidentifiedDriverRecords(String unidentifiedDriverRecords) {
        this.unidentifiedDriverRecords = unidentifiedDriverRecords;
    }

    public String getUsdot() {
        return usdot;
    }

    public void setUsdot(String usdot) {
        this.usdot = usdot;
    }
}
