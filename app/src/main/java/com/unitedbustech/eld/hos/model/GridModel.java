package com.unitedbustech.eld.hos.model;

import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;

/**
 * @author zhangyu
 * @date 2018/1/23
 * @description 用于画图表的类。给js画图表的模型类.
 * 在使用时，图标根据一个本类List绘制图表
 */
public class GridModel {

    /**
     * 开始秒数
     */
    private int startSecond;

    /**
     * 结束秒数
     */
    private int endSecond;

    /**
     * 状态
     */
    private DriverState driverStatus;

    /**
     * 是否是自动记录
     */
    private boolean autoRecord;

    /**
     * 是否是虚拟的
     * 为了绘制Grid，会建立虚拟的点，通常在一天的开始和结束
     */
    private boolean invented;

    public GridModel(int startSecond, int endSecond, DriverState driverStatus) {
        this.startSecond = startSecond;
        this.endSecond = endSecond;
        this.driverStatus = driverStatus;
    }

    public GridModel(int startSecond, int endSecond, DriverState driverStatus, int origin) {
        this(startSecond, endSecond, driverStatus);
        autoRecord = (origin == DDLOriginEnum.AUTO_BY_ELD.getCode() || origin == DDLOriginEnum.UNIDENTIFIED.getCode());
    }

    public GridModel(int startSecond, int endSecond, DriverState driverStatus, int origin, boolean invented) {
        this(startSecond, endSecond, driverStatus);
        autoRecord = (origin == DDLOriginEnum.AUTO_BY_ELD.getCode() || origin == DDLOriginEnum.UNIDENTIFIED.getCode());
        this.invented = invented;
    }

    public GridModel() {
    }

    public int getStartSecond() {
        return startSecond;
    }

    public void setStartSecond(int startSecond) {
        this.startSecond = startSecond;
    }

    public int getEndSecond() {
        return endSecond;
    }

    public void setEndSecond(int endSecond) {
        this.endSecond = endSecond;
    }

    public DriverState getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(DriverState driverStatus) {
        this.driverStatus = driverStatus;
    }

    public boolean isAutoRecord() {
        return autoRecord;
    }

    public void setAutoRecord(boolean autoRecord) {
        this.autoRecord = autoRecord;
    }

    public boolean isInvented() {
        return invented;
    }

    public void setInvented(boolean invented) {
        this.invented = invented;
    }
}
