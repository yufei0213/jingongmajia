package com.unitedbustech.eld.eventbus;

/**
 * @author yufei0213
 * @date 2018/2/24
 * @description DashBoard页面是否显示故障图标
 */
public class DashBoardMalFunctionEvent {

    private boolean isShow;

    public DashBoardMalFunctionEvent() {
    }

    public DashBoardMalFunctionEvent(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
