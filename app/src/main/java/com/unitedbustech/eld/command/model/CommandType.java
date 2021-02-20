package com.unitedbustech.eld.command.model;

/**
 * @author yufei0213
 * @date 2018/9/6
 * @description 命令类型
 */
public final class CommandType {

    /**
     * 上传日志
     * <p>
     * 参数：
     * -s：开始日期（格式MMddyy，包含该天）。缺省时，上传最近三天日志
     * -e：结束日期（格式MMddyy，包含该天）。缺省时，上传包含开始日期的后三天日志
     * -v：车辆code。不缺省时，如果当天连接车辆为该车辆时，根据日期上传日志；缺省时，忽略该参数
     * <p>
     * 参数示例：-s 090518 -e 090918 -v 102
     * 当司机连接车辆为102时，上传2018-09-05到2018-09-18总共五天的日志
     */
    public static final int UPLOAD_LOGS = 1;
}
