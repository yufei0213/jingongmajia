package com.unitedbustech.eld.eventbus;

/**
 * @author yufei0213
 * @date 2018/2/10
 * @description 发送自检事件
 */
public class SelfCheckEvent {

    /**
     * SelfCheckEventType
     */
    private int type;

    /**
     * 是否要生成GPS模式启动的Remark
     */
    private boolean isCreateGpsRemark;

    private boolean isEnable;

    public SelfCheckEvent() {
    }

    public SelfCheckEvent(int type) {
        this.type = type;
    }

    public SelfCheckEvent(int type, boolean isEnable) {
        this.type = type;
        this.isEnable = isEnable;
    }

    public SelfCheckEvent(int type, boolean isCreateGpsRemark, boolean isEnable) {
        this.type = type;
        this.isCreateGpsRemark = isCreateGpsRemark;
        this.isEnable = isEnable;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isCreateGpsRemark() {
        return isCreateGpsRemark;
    }

    public void setCreateGpsRemark(boolean createGpsRemark) {
        isCreateGpsRemark = createGpsRemark;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }
}
