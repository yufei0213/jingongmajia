package com.unitedbustech.eld.message;

/**
 * @author yufei0213
 * @date 2018/2/7
 * @description 从服务端接收到的消息类型
 */
public final class MessageType {

    /**
     * 团队驾驶相关
     */
    public static final int TEAM_WORK = 1;

    /**
     * 司机状态检测
     */
    public static final int DRIVER_STATE_DIAGNOSTIC = 2;

    /**
     * 会话检测
     */
    public static final int SESSION_DIAGNOSTIC = 3;

    /**
     * 上传日志
     */
    public static final int UPLOAD_LOGS = 4;
}
