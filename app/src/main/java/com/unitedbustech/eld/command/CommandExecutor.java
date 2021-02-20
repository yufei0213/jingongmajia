package com.unitedbustech.eld.command;

import com.unitedbustech.eld.command.executor.BaseExecutor;
import com.unitedbustech.eld.command.executor.LogExecutor;
import com.unitedbustech.eld.command.model.Command;
import com.unitedbustech.eld.command.model.CommandType;
import com.unitedbustech.eld.util.JsonUtil;

import java.util.List;

/**
 * @author yufei0213
 * @date 2018/9/7
 * @description 命令处理器
 */
public class CommandExecutor {

    private List<Command> commandList;

    public void execute(String commands) {

        commandList = JsonUtil.parseArray(commands, Command.class);

        for (Command command : commandList) {

            BaseExecutor executor = null;
            switch (command.getCmdType()) {

                case CommandType.UPLOAD_LOGS:

                    executor = new LogExecutor();
                    break;
                default:
                    break;
            }

            if (executor != null) {

                executor.execute(command);
            }
        }
    }
}
