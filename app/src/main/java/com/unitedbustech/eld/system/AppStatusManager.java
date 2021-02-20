package com.unitedbustech.eld.system;

/**
 * @author yufei0213
 * @date 2018/2/28
 * @description App状态管理类，用于解决App内存被系统回收后的处理
 */
public class AppStatusManager {

    /**
     * 正常状态
     */
    public static final int NORMAL = 0;

    /**
     * 应用在后台被强制杀死
     */
    public static final int FORCE_KILLED = 1;

    private int appStatus;

    private AppStatusManager() {

        appStatus = FORCE_KILLED;
    }

    private static AppStatusManager instance = null;

    public static AppStatusManager getInstance() {

        if (instance == null) {

            instance = new AppStatusManager();
        }

        return instance;
    }

    public int getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(int appStatus) {
        this.appStatus = appStatus;
    }
}
