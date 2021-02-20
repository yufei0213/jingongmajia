package com.unitedbustech.eld.dailylog.model;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.common.vo.DailyLogDataHeadVo;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.TimeUtil;

import java.util.Date;

/**
 * @author zhangyu
 * @date 2018/1/26
 * @description 表头模型
 */
public class Profile implements Comparable<Profile> {

    /**
     * 车辆id
     */
    private String vehicleId;

    /**
     * 唯一识别码
     */
    private String vin;

    /**
     * 副驾驶ID
     */
    private String coDriverId;

    /**
     * 副驾驶名称
     */
    private String coDriverName;

    /**
     * 公司名
     */
    private String carrier;

    /**
     * 运单号
     */
    private String shippingId;

    /**
     * 开始结束里程数
     */
    private String startEndOdometer;

    /**
     * 开始结束引擎时间
     */
    private String startEndEngineHour;

    /**
     * 服务端vo
     */
    private DailyLogDataHeadVo dailyLogDataHeadVo;

    /**
     * 根据服务器的json创建新对象。
     *
     * @param jsonObject JSONObject
     * @return Profile
     */
    public static Profile getProfileByServerJson(JSONObject jsonObject) {

        Profile profile = new Profile();
        profile.setVehicleId(jsonObject.getString("truckTractorId"));
        profile.setVin(jsonObject.getString("truckTractorVin"));
        profile.setCoDriverName(jsonObject.getString("coDriverName"));
        profile.setCoDriverId(jsonObject.getString("coDriverId"));
        profile.setCarrier(jsonObject.getString("carrier"));
        profile.setStartEndEngineHour(jsonObject.getString("startEndEngineHours"));
        profile.setStartEndOdometer(jsonObject.getString("startEndOdometer"));
        profile.setShippingId(jsonObject.getString("shippingId"));

        profile.dailyLogDataHeadVo = JsonUtil.parseObject(jsonObject.toJSONString(), DailyLogDataHeadVo.class);
        return profile;
    }

    /**
     * 根据对象创建可以上传至服务端的json对象。
     *
     * @return
     */
    public static JSONObject getServerJsonByProfile(Profile profile, Date date) {

        JSONObject result = new JSONObject();
        result.put("co_driver_id", profile.getCoDriverId());
        result.put("co_driver_name", profile.getCoDriverName());
        result.put("start_end_odometer", profile.getStartEndOdometer());
        result.put("truck_tractor_id", profile.getVehicleId());
        result.put("truck_tractor_vin", profile.getVin());
        result.put("start_end_engine_hours", profile.getStartEndEngineHour());
        result.put("date", TimeUtil.utcToLocal(date.getTime(), SystemHelper.getUser().getTimeZone(), TimeUtil.MM_DD_YY));
        result.put("shipping_id", profile.getShippingId());

        return result;
    }

    public Profile() {

        this.dailyLogDataHeadVo = new DailyLogDataHeadVo();
    }

    public Profile(Profile profile) {

        this.vehicleId = profile.vehicleId;
        this.vin = profile.vin;
        this.coDriverId = profile.coDriverId;
        this.coDriverName = profile.coDriverName;
        this.shippingId = profile.shippingId;
        this.carrier = profile.carrier;
        this.startEndOdometer = profile.startEndOdometer;
        this.startEndEngineHour = profile.startEndEngineHour;

        this.dailyLogDataHeadVo = new DailyLogDataHeadVo(profile.getDailyLogDataHeadVo());
    }

    /**
     * 在更新表头的时候，同时更新表头以及本地内存的dot表头
     *
     * @param profile Profile
     */
    public void updateProfile(Profile profile) {

        this.vehicleId = profile.vehicleId;
        this.vin = profile.vin;
        this.coDriverId = profile.coDriverId;
        this.coDriverName = profile.coDriverName;
        this.shippingId = profile.shippingId;
        this.startEndOdometer = profile.startEndOdometer;
        this.startEndEngineHour = profile.startEndEngineHour;

        this.dailyLogDataHeadVo.setTrailerId(vehicleId);
        this.dailyLogDataHeadVo.setTruckTractorVin(vin);
        this.dailyLogDataHeadVo.setCoDriverName(coDriverName);
        this.dailyLogDataHeadVo.setCoDriverId(coDriverId);
        this.dailyLogDataHeadVo.setShippingId(shippingId);
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
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

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getShippingId() {
        return shippingId;
    }

    public void setShippingId(String shippingId) {
        this.shippingId = shippingId;
    }

    public String getStartEndOdometer() {
        return startEndOdometer;
    }

    public void setStartEndOdometer(String startEndOdometer) {
        this.startEndOdometer = startEndOdometer;
    }

    public String getStartEndEngineHour() {
        return startEndEngineHour;
    }

    public void setStartEndEngineHour(String startEndEngineHour) {
        this.startEndEngineHour = startEndEngineHour;
    }

    public DailyLogDataHeadVo getDailyLogDataHeadVo() {
        return dailyLogDataHeadVo;
    }

    public void setDailyLogDataHeadVo(DailyLogDataHeadVo dailyLogDataHeadVo) {
        this.dailyLogDataHeadVo = dailyLogDataHeadVo;
    }

    @Override
    public int compareTo(@NonNull Profile o) {

        DailyLogDataHeadVo dailyLogDataHeadVo = o.getDailyLogDataHeadVo();

        String dateStr1 = dailyLogDataHeadVo.getRecordDate();
        String dateStr2 = this.dailyLogDataHeadVo.getRecordDate();

        String timeZone = SystemHelper.getUser().getTimeZone();

        Date date1 = TimeUtil.strToDate(dateStr1, TimeUtil.MM_DD_YY, timeZone);
        Date date2 = TimeUtil.strToDate(dateStr2, TimeUtil.MM_DD_YY, timeZone);

        return date1.compareTo(date2);
    }
}
