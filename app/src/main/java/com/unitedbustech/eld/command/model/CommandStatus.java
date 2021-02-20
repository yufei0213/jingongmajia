package com.unitedbustech.eld.command.model;

/**
 * @author yufei0213
 * @date 2018/9/6
 * @description 命令执行状态
 */
public final class CommandStatus {

    /**
     * App尚未收到通知
     */
    public static final int NOT_NOTIFIED = 0;

    /**
     * App已经收到通知
     */
    public static final int HAS_NOTIFIED = 1;

    /**
     * App执行命令成功
     */
    public static final int EXECUTE_SUCCESS = 2;

    /**
     * App执行命令失败
     */
    public static final int EXECUTE_FAILED = 3;
}
