package com.unitedbustech.eld.domain.entry;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description 公司
 */
@Entity(tableName = "carrier")
public class Carrier {

    /**
     * 主键
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    /**
     * 联系人姓名
     */
    @ColumnInfo(name = "contact")
    private String contact;

    /**
     * 联系电话
     */
    @ColumnInfo(name = "phone")
    private String phone;

    /**
     * 联系邮件
     */
    @ColumnInfo(name = "email")
    private String email;

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
     * 时区名
     */
    @ColumnInfo(name = "time_zone_name")
    private String timeZoneName;

    /**
     * dot号码
     */
    @ColumnInfo(name = "us_dot")
    private String usdot;

    /**
     * 城市
     */
    @ColumnInfo(name = "city")
    private String city;

    /**
     * 州
     */
    @ColumnInfo(name = "state")
    private String state;

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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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

    public String getUsdot() {
        return usdot;
    }

    public void setUsdot(String usdot) {
        this.usdot = usdot;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
