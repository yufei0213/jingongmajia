package com.unitedbustech.eld.common;

/**
 * @author yufei0213
 * @date 2018/2/6
 * @description 服务端下发的团队驾驶的消息类型
 */
public final class TeamWorkMsgType {

    /**
     * 主驾驶邀请副驾驶
     */
    public static final int PILOT_INVITE_COPILOT = 0;
    /**
     * 主驾驶取消邀请
     */
    public static final int PILOT_CANCEL_INVITE = 1;
    /**
     * 副驾驶接受邀请
     */
    public static final int COPILOT_ACCEPT_INVITE = 2;
    /**
     * 副驾驶拒绝邀请
     */
    public static final int COPILOT_REFUSE_INVITE = 3;
    /**
     * 主驾驶请求切换角色
     */
    public static final int PILOT_REQUEST_SWITCH = 4;
    /**
     * 主驾取消切换
     */
    public static final int PILOT_CANCEL_SWITCH = 5;
    /**
     * 副驾驶接受角色切换请求
     */
    public static final int COPILOT_ACCEPT_SWITCH = 6;
    /**
     * 副驾驶拒绝角色切换请求
     */
    public static final int COPILOT_REFUSE_SWITCH = 7;
    /**
     * 原主驾驶已经断开车辆连接
     */
    public static final int ORIGIN_PILOT_DISCONNECT_VEHICLE = 10;
    /**
     * 主驾驶移除副驾驶
     */
    public static final int PILOT_REMOVE_COPILOT = 8;
    /**
     * 副驾驶离开
     */
    public static final int COPILOT_EXIT = 9;
}
