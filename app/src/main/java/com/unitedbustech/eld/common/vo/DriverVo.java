package com.unitedbustech.eld.common.vo;

import android.support.annotation.NonNull;

import com.unitedbustech.eld.domain.entry.Driver;
import com.unitedbustech.eld.domain.entry.Rule;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description DriverVo
 */
public class DriverVo implements Comparable<DriverVo> {

    private int id;

    private String name;

    private String phone;

    private String email;

    private int licenseType;

    private String licenseNo;

    private String licenseState;

    private String timeZone;

    private String timeZoneAlias;

    private String timeZoneName;

    private int hosAlerts;

    private int advancedNotice;

    private int exemptDays;

    private int exemptMile;

    private int personalUse;

    private int yardMove;

    private int valid;

    private Rule rule;

    public DriverVo() {
    }

    public DriverVo(Driver driver, Rule rule) {

        this.id = driver.getId();
        this.name = driver.getName();
        this.phone = driver.getPhone();
        this.email = driver.getEmail();
        this.licenseType = driver.getLicenseType();
        this.licenseNo = driver.getLicenseNo();
        this.licenseState = driver.getLicenseState();
        this.timeZone = driver.getTimeZone();
        this.timeZoneAlias = driver.getTimeZoneAlias();
        this.timeZoneName = driver.getTimeZoneName();
        this.hosAlerts = driver.getHosAlertOn();
        this.advancedNotice = driver.getAdvancedNotice();
        this.exemptDays = driver.getExemptDays();
        this.exemptMile = driver.getExemptMile();
        this.personalUse = driver.getPersonalUse();
        this.yardMove = driver.getYardMove();
        this.valid = driver.getValid();

        this.rule = rule;
    }

    public Driver getDriver() {

        Driver driver = new Driver();

        driver.setId(id);
        driver.setName(name);
        driver.setPhone(phone);
        driver.setEmail(email);
        driver.setLicenseType(licenseType);
        driver.setLicenseNo(licenseNo);
        driver.setLicenseState(licenseState);
        driver.setTimeZone(timeZone);
        driver.setTimeZoneAlias(timeZoneAlias);
        driver.setTimeZoneName(timeZoneName);
        driver.setHosAlertOn(hosAlerts);
        driver.setAdvancedNotice(advancedNotice);
        driver.setExemptDays(exemptDays);
        driver.setExemptMile(exemptMile);
        driver.setPersonalUse(personalUse);
        driver.setYardMove(yardMove);
        driver.setValid(valid);

        return driver;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(int licenseType) {
        this.licenseType = licenseType;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getLicenseState() {
        return licenseState;
    }

    public void setLicenseState(String licenseState) {
        this.licenseState = licenseState;
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

    public String getTimeZoneName() {
        return timeZoneName;
    }

    public void setTimeZoneName(String timeZoneName) {
        this.timeZoneName = timeZoneName;
    }

    public int getHosAlerts() {
        return hosAlerts;
    }

    public void setHosAlerts(int hosAlerts) {
        this.hosAlerts = hosAlerts;
    }

    public int getAdvancedNotice() {
        return advancedNotice;
    }

    public void setAdvancedNotice(int advancedNotice) {
        this.advancedNotice = advancedNotice;
    }

    public int getExemptDays() {
        return exemptDays;
    }

    public void setExemptDays(int exemptDays) {
        this.exemptDays = exemptDays;
    }

    public int getExemptMile() {
        return exemptMile;
    }

    public void setExemptMile(int exemptMile) {
        this.exemptMile = exemptMile;
    }

    public int getPersonalUse() {
        return personalUse;
    }

    public void setPersonalUse(int personalUse) {
        this.personalUse = personalUse;
    }

    public int getYardMove() {
        return yardMove;
    }

    public void setYardMove(int yardMove) {
        this.yardMove = yardMove;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    @Override
    public int compareTo(@NonNull DriverVo o) {

        return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
    }
}
