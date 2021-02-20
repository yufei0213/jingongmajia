package com.unitedbustech.eld.common.vo;

import java.util.List;

public class UnidentifiedLogVo {

    private List<AlertAssignedVo> logs;
    private List<UnidentifiedEngineLogVo> engineLogs;

    public UnidentifiedLogVo(){}

    public UnidentifiedLogVo(List<AlertAssignedVo> alertAssignedVos, List<UnidentifiedEngineLogVo> unidentifiedEngineLogVos){

        this.logs = alertAssignedVos;
        this.engineLogs = unidentifiedEngineLogVos;
    }

    public List<AlertAssignedVo> getLogs() {
        return logs;
    }

    public void setLogs(List<AlertAssignedVo> logs) {
        this.logs = logs;
    }

    public List<UnidentifiedEngineLogVo> getEngineLogs() {
        return engineLogs;
    }

    public void setEngineLogs(List<UnidentifiedEngineLogVo> engineLogs) {
        this.engineLogs = engineLogs;
    }
}
