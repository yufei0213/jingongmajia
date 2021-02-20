package com.unitedbustech.eld.domain.entry;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description 司机
 */
@Entity(tableName = "driver")
public class Driver {

    /**
     * 主键
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    /**
     * 名字
     */
    @ColumnInfo(name = "name")
    private String name;

    /**
     * 电话
     */
    @ColumnInfo(name = "phone")
    private String phone;

    /**
     * 邮件
     */
    @ColumnInfo(name = "email")
    private String email;

    /**
     * 驾照类型
     */
    @ColumnInfo(name = "license_type")
    private int licenseType;

    /**
     * 驾照号码
     */
    @ColumnInfo(name = "license_no")
    private String licenseNo;

    /**
     * 驾照所在州
     */
    @ColumnInfo(name = "license_state")
    private String licenseState;

    /**
     * 时区
     */
    @ColumnInfo(name = "time_zone")
    private String timeZone;

    /**
     * 时区简写
     */
    @ColumnInfo(name = "time_zone_alias")
    private String timeZoneAlias;

    /**
     * 时区名字
     */
    @ColumnInfo(name = "time_zone_name")
    private String timeZoneName;

    /**
     * 是否有hos提醒
     */
    @ColumnInfo(name = "hos_alert_on")
    private int hosAlertOn;

    /**
     * hos提醒的分钟数
     */
    @ColumnInfo(name = "advanced_notice")
    private int advancedNotice;

    /**
     * 是否有按天划分的豁免权限
     */
    @ColumnInfo(name = "exempt_days")
    private int exemptDays;

    /**
     * 是否有按距离划分的豁免权限
     */
    @ColumnInfo(name = "exempt_mile")
    private int exemptMile;

    /**
     * 是否有personaluse权限
     */
    @ColumnInfo(name = "personal_use")
    private int personalUse;

    /**
     * 是否有yardmove权限
     */
    @ColumnInfo(name = "yard_move")
    private int yardMove;

    /**
     * 是否有效
     */
    @ColumnInfo(name = "valid")
    private int valid;

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

    public int getHosAlertOn() {
        return hosAlertOn;
    }

    public void setHosAlertOn(int hosAlertOn) {
        this.hosAlertOn = hosAlertOn;
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
}
