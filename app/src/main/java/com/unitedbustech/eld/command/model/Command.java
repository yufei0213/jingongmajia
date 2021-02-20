package com.unitedbustech.eld.command.model;

/**
 * @author yufei0213
 * @date 2018/9/6
 * @description 命令模型
 */
public class Command {

    /**
     * 命令ID
     */
    private int id;

    /**
     * 司机ID
     */
    private int driverId;

    /**
     * 命令类别
     */
    private int cmdType;

    /**
     * 命令状态
     * <p>
     * 0（未下发）、1（下发成功）、2（执行成功）和3（执行失败）
     */
    private int status;

    /**
     * 参数
     * <p>
     * 该字段随着命令类型的不同而不同，详细内容见CommandType
     */
    private String args;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getCmdType() {
        return cmdType;
    }

    public void setCmdType(int cmdType) {
        this.cmdType = cmdType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }
}
