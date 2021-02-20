package com.unitedbustech.eld.common;

/**
 * @author yufei0213
 * @date 2018/2/6
 * @description 下发给DashBoard的消息类型
 */
public final class TeamWorkDashBoardEventType {

    /**
     * 恢复正常
     */
    public static final int BECOME_NORMAL = -1;

    /**
     * 发起副驾驶邀请
     */
    public static final int BECOME_PILOT = 0;
    /**
     * 副驾驶接受邀请
     */
    public static final int COPILOT_ACCEPT_INVITE = 1;
    /**
     * 副驾驶拒绝邀请
     */
    public static final int COPILOT_REFUSE_INVITE = 2;
    /**
     * 副驾驶接受角色切换请求
     */
    public static final int COPILOT_ACCEPT_SWITCH = 3;
    /**
     * 副驾驶拒绝角色切换请求
     */
    public static final int COPILOT_REFUSE_SWITCH = 4;
    /**
     * 副驾驶离开
     */
    public static final int COPILOT_EXIT = 5;
    /**
     * 成为副驾驶
     */
    public static final int BECOME_COPILOT = 6;
    /**
     * 主驾驶移除副驾驶
     */
    public static final int PILOT_REMOVE_COPILOT = 7;
}
