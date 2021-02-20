package com.unitedbustech.eld.hos.model;

import com.unitedbustech.eld.common.DriverState;

/**
 * @author zhangyu
 * @date 2018/1/16
 * @description HOS驾驶状态事件使用的事件模型。该模型支持页面显示与时间逻辑的计算。从基础事件转化而来。
 * 只有6种驾驶状态的切换的模型。
 * 该事件模型聚合形成HosDayModel。
 */
public class HosDriveEventModel extends HosEventModel implements Cloneable{

    /**
     * 开始的新状态
     */
    private DriverState driverState;

    /**
     * 是否是自动记录的
     */
    private int origin;

    /**
     * 是否是被隐藏的事件。
     * 隐藏原因：如果该事件的前序事件与该事件的状态一致，则在画图，以及dailylog中，不做显示。
     * 但是在做插入与编辑的事件时，需要对原事件进行编辑。因此使用isHide记录是否是被隐藏的点。
     * 规则：如果在同一天的事件，该事件与之前的事件相同。则被置为隐藏点。
     */
    private boolean isHide;

    public DriverState getDriverState() {
        return driverState;
    }

    public void setDriverState(DriverState driverState) {
        this.driverState = driverState;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    @Override
    public HosDriveEventModel clone(){
        try {
            return (HosDriveEventModel) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
