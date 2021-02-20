package com.unitedbustech.eld.domain.entry;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.unitedbustech.eld.common.vo.IftaDetailVo;
import com.unitedbustech.eld.ifta.common.IftaParamsKey;
import com.unitedbustech.eld.system.SystemHelper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author yufei0213
 * @date 2018/7/13
 * @description 加油记录
 */
@Entity(tableName = "ifta_log")
public class IftaLog {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    /**
     * 服务端id
     */
    @ColumnInfo(name = "server_id")
    private int serverId;

    /**
     * 唯一标识id
     */
    @ColumnInfo(name = "origin_id")
    private String originId;

    /**
     * 司机id
     */
    @ColumnInfo(name = "driver_id")
    private int driverId;

    /**
     * 车辆id
     */
    @ColumnInfo(name = "vehicle_id")
    private int vehicleId;

    /**
     * 日期
     */
    @ColumnInfo(name = "date")
    private String date;

    /**
     * 州缩写
     */
    @ColumnInfo(name = "state")
    private String state;

    /**
     * 燃油类型
     */
    @ColumnInfo(name = "fuel_type")
    private String fuelType;

    /**
     * 单价
     */
    @ColumnInfo(name = "unit_price")
    private double unitPrice;

    /**
     * 数量
     */
    @ColumnInfo(name = "gallon")
    private double gallon;

    /**
     * 总价
     */
    @ColumnInfo(name = "total_price")
    private double totalPrice;

    /**
     * 收据图片的本地路径，多个图片之间用分号分割
     */
    @ColumnInfo(name = "receipt_pics")
    private String receiptPicPaths;

    /**
     * 收据图片的下载地址，多个图片之间用分号分割
     */
    @ColumnInfo(name = "receipt_urls")
    private String receiptPicUrls;

    /**
     * 创建时间
     */
    @ColumnInfo(name = "create_time")
    private long createTime;

    public IftaLog() {
    }

    public IftaLog(IftaDetailVo iftaDetailVo) {

        this.serverId = iftaDetailVo.getId();
        this.originId = iftaDetailVo.getAppRecordId();
        this.driverId = SystemHelper.getUser().getDriverId();
        this.vehicleId = iftaDetailVo.getBusId();
        this.date = iftaDetailVo.getFuelTime();
        this.state = iftaDetailVo.getState();
        this.fuelType = iftaDetailVo.getFuelType();
        this.unitPrice = iftaDetailVo.getUnitPrice();
        this.gallon = iftaDetailVo.getTaxPaidGallon();
        this.totalPrice = iftaDetailVo.getPrice();

        List<String> fileLinkList = iftaDetailVo.getFileLinkList();
        List<String> localPicPathList = iftaDetailVo.getLocalPicPathList();

        if (fileLinkList != null) {

            String picUrls = "";
            for (int i = 0; i < fileLinkList.size(); i++) {

                picUrls = picUrls + fileLinkList.get(i) + ((i < fileLinkList.size() - 1) ? ";" : "");
            }

            this.receiptPicUrls = picUrls;
        }

        if (localPicPathList != null) {

            String localPath = "";
            for (int i = 0; i < localPicPathList.size(); i++) {

                localPath = localPath + localPicPathList.get(i) + ((i < localPicPathList.size() - 1) ? ";" : "");
            }

            this.receiptPicPaths = localPath;
        }
    }

    public IftaLog(Map<String, String> params, List<String> targetPicPath) {

        this.setOriginId(UUID.randomUUID().toString().replaceAll("-", ""));

        if (params != null) {

            this.setDriverId(Integer.parseInt(params.get(IftaParamsKey.DRIVER_ID)));
            this.setVehicleId(Integer.parseInt(params.get(IftaParamsKey.VEHICLE_ID)));
            this.setDate(params.get(IftaParamsKey.DATE));
            this.setState(params.get(IftaParamsKey.STATE));
            this.setFuelType(params.get(IftaParamsKey.FUEL_TYPE));
            this.setUnitPrice(Double.parseDouble(params.get(IftaParamsKey.UNIT_PRICE)));
            this.setGallon(Double.parseDouble(params.get(IftaParamsKey.GALLONS)));
            this.setTotalPrice(Double.parseDouble(params.get(IftaParamsKey.TOTAL_PRICE)));
        }

        if (targetPicPath != null) {

            String localPath = "";
            for (int i = 0; i < targetPicPath.size(); i++) {

                localPath = localPath + targetPicPath.get(i) + ((i < targetPicPath.size() - 1) ? ";" : "");
            }

            this.setReceiptPicPaths(localPath);
        }

        this.setCreateTime(new Date().getTime());
    }

    public void update(int serverId, Map<String, String> params, List<String> targetPicPath) {

        this.serverId = serverId;
        if (params != null) {

            this.setDriverId(Integer.parseInt(params.get(IftaParamsKey.DRIVER_ID)));
            this.setVehicleId(Integer.parseInt(params.get(IftaParamsKey.VEHICLE_ID)));
            this.setDate(params.get(IftaParamsKey.DATE));
            this.setState(params.get(IftaParamsKey.STATE));
            this.setFuelType(params.get(IftaParamsKey.FUEL_TYPE));
            this.setUnitPrice(Double.parseDouble(params.get(IftaParamsKey.UNIT_PRICE)));
            this.setGallon(Double.parseDouble(params.get(IftaParamsKey.GALLONS)));
            this.setTotalPrice(Double.parseDouble(params.get(IftaParamsKey.TOTAL_PRICE)));
        }

        if (targetPicPath != null) {

            String localPath = "";
            for (int i = 0; i < targetPicPath.size(); i++) {

                localPath = localPath + targetPicPath.get(i) + ((i < targetPicPath.size() - 1) ? ";" : "");
            }

            this.setReceiptPicPaths(localPath);
            this.setReceiptPicUrls(""); //如果更新了本地的图片路径，那么图片下载路径也发生了变化，但是此时并不清楚新的下载路径，所以设置为空
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getGallon() {
        return gallon;
    }

    public void setGallon(double gallon) {
        this.gallon = gallon;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getReceiptPicPaths() {
        return receiptPicPaths;
    }

    public void setReceiptPicPaths(String receiptPicPaths) {
        this.receiptPicPaths = receiptPicPaths;
    }

    public String getReceiptPicUrls() {
        return receiptPicUrls;
    }

    public void setReceiptPicUrls(String receiptPicUrls) {
        this.receiptPicUrls = receiptPicUrls;
    }
}
