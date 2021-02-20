package com.unitedbustech.eld.common.vo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Driver;
import com.unitedbustech.eld.domain.entry.IftaLog;
import com.unitedbustech.eld.domain.entry.Vehicle;

import java.util.Arrays;
import java.util.List;

/**
 * @author yufei0213
 * @date 2018/6/28
 * @description 油税详情
 */
public class IftaDetailVo implements Parcelable {

    /**
     * 主键
     */
    private int id;

    /**
     * 本地数据库id
     */
    private String appRecordId;

    /**
     * 司机名字
     */
    private String driver;

    /**
     * 车辆ID
     */
    private int busId;

    /**
     * 车辆CODE
     */
    private String busCode;

    /**
     * 燃油类型
     */
    private String fuelType;

    /**
     * 加油时间，格式 MM/dd/yyyy
     */
    private String fuelTime;

    /**
     * 加油时所在的州，缩写
     */
    private String state;

    /**
     * 单价
     */
    private double unitPrice;

    /**
     * 油量
     */
    private double taxPaidGallon;

    /**
     * 总价
     */
    private double price;

    /**
     * 收据下载链接
     */
    private List<String> fileLinkList;

    /**
     * 收据本地存储地址
     */
    private List<String> localPicPathList;

    public IftaDetailVo(IftaLog iftaLog) {

        this.id = iftaLog.getServerId();
        this.appRecordId = iftaLog.getOriginId();
        Driver driver = DataBaseHelper.getDataBase().driverDao().getDriver(iftaLog.getDriverId());
        if (driver != null) {

            this.driver = driver.getName();
        }

        this.busId = iftaLog.getVehicleId();
        Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(this.busId);
        if (vehicle != null) {

            this.busCode = vehicle.getCode();
        }

        this.fuelType = iftaLog.getFuelType();
        this.fuelTime = iftaLog.getDate();
        this.state = iftaLog.getState();
        this.unitPrice = iftaLog.getUnitPrice();
        this.taxPaidGallon = iftaLog.getGallon();
        this.price = iftaLog.getTotalPrice();

        String localPics = iftaLog.getReceiptPicPaths();
        if (!TextUtils.isEmpty(localPics)) {

            String picArray[] = localPics.split(";");
            this.localPicPathList = Arrays.asList(picArray);
        }

        String receiptUrls = iftaLog.getReceiptPicUrls();
        if (!TextUtils.isEmpty(receiptUrls)) {

            String picArray[] = receiptUrls.split(";");
            this.fileLinkList = Arrays.asList(picArray);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppRecordId() {
        return appRecordId;
    }

    public void setAppRecordId(String appRecordId) {
        this.appRecordId = appRecordId;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public String getBusCode() {
        return busCode;
    }

    public void setBusCode(String busCode) {
        this.busCode = busCode;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getFuelTime() {
        return fuelTime;
    }

    public void setFuelTime(String fuelTime) {
        this.fuelTime = fuelTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTaxPaidGallon() {
        return taxPaidGallon;
    }

    public void setTaxPaidGallon(double taxPaidGallon) {
        this.taxPaidGallon = taxPaidGallon;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getFileLinkList() {
        return fileLinkList;
    }

    public void setFileLinkList(List<String> fileLinkList) {
        this.fileLinkList = fileLinkList;
    }

    public List<String> getLocalPicPathList() {
        return localPicPathList;
    }

    public void setLocalPicPathList(List<String> localPicPathList) {
        this.localPicPathList = localPicPathList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.appRecordId);
        dest.writeString(this.driver);
        dest.writeInt(this.busId);
        dest.writeString(this.busCode);
        dest.writeString(this.fuelType);
        dest.writeString(this.fuelTime);
        dest.writeString(this.state);
        dest.writeDouble(this.unitPrice);
        dest.writeDouble(this.taxPaidGallon);
        dest.writeDouble(this.price);
        dest.writeStringList(this.fileLinkList);
        dest.writeStringList(this.localPicPathList);
    }

    public IftaDetailVo() {
    }

    protected IftaDetailVo(Parcel in) {
        this.id = in.readInt();
        this.appRecordId = in.readString();
        this.driver = in.readString();
        this.busId = in.readInt();
        this.busCode = in.readString();
        this.fuelType = in.readString();
        this.fuelTime = in.readString();
        this.state = in.readString();
        this.unitPrice = in.readDouble();
        this.taxPaidGallon = in.readDouble();
        this.price = in.readDouble();
        this.fileLinkList = in.createStringArrayList();
        this.localPicPathList = in.createStringArrayList();
    }

    public static final Creator<IftaDetailVo> CREATOR = new Creator<IftaDetailVo>() {
        @Override
        public IftaDetailVo createFromParcel(Parcel source) {
            return new IftaDetailVo(source);
        }

        @Override
        public IftaDetailVo[] newArray(int size) {
            return new IftaDetailVo[size];
        }
    };
}
