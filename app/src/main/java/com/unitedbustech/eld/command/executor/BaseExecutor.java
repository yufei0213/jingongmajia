package com.unitedbustech.eld.command.executor;

import com.unitedbustech.eld.command.model.Command;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.request.RequestCacheService;
import com.unitedbustech.eld.request.RequestType;
import com.unitedbustech.eld.system.SystemHelper;

/**
 * @author yufei0213
 * @date 2018/9/7
 * @description 命令处理器基类
 */
public abstract class BaseExecutor {

    public abstract void execute(Command command);

    /**
     * 更新命令状态
     *
     * @param commandId     命令ID
     * @param commandStatus 命令状态
     */
    protected void updateCommandStatus(int commandId, int commandStatus) {

        HttpRequest httpRequest = new HttpRequest.Builder()
                .url(Constants.API_COMMAND_STATUS)
                .addParam("access_token", SystemHelper.getUser().getAccessToken())
                .addParam("command_id", Integer.toString(commandId))
                .addParam("status", Integer.toString(commandStatus))
                .build();

        RequestCacheService.getInstance().cachePost(httpRequest, RequestType.OTHERS);
    }
}
