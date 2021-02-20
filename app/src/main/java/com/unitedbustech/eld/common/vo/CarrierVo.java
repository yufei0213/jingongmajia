package com.unitedbustech.eld.common.vo;

import com.unitedbustech.eld.domain.entry.Carrier;
import com.unitedbustech.eld.domain.entry.Rule;

import java.util.List;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description CarrierVo
 */
public class CarrierVo {

    private int id;

    private String name;

    private String contact;

    private String phone;

    private String email;

    private String timeZone;

    private String timeZoneAlias;

    private String timeZoneName;

    private String usdot;

    private List<Rule> rules;

    private String city;

    private String state;

    private String address;

    public CarrierVo() {
    }

    public CarrierVo(Carrier carrier, List<Rule> rules) {

        this.id = carrier.getId();
        this.name = carrier.getName();
        this.contact = carrier.getContact();
        this.phone = carrier.getPhone();
        this.email = carrier.getEmail();
        this.timeZone = carrier.getTimeZone();
        this.timeZoneAlias = carrier.getTimeZoneAlias();
        this.timeZoneName = carrier.getTimeZoneName();
        this.usdot = carrier.getUsdot();
        this.city = carrier.getCity();
        this.state = carrier.getState();

        this.address = this.city == null ? "" : this.city;
        this.address = this.state == null ? this.address : this.address + "," + this.state;


        this.rules = rules;
    }


    public Carrier getCarrier() {

        Carrier carrier = new Carrier();

        carrier.setId(id);
        carrier.setName(name);
        carrier.setContact(contact);
        carrier.setPhone(phone);
        carrier.setEmail(email);
        carrier.setTimeZone(timeZone);
        carrier.setTimeZoneAlias(timeZoneAlias);
        carrier.setTimeZoneName(timeZoneName);
        carrier.setUsdot(usdot);
        carrier.setCity(city);
        carrier.setState(state);

        return carrier;
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

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
