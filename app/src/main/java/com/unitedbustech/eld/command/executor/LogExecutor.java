package com.unitedbustech.eld.command.executor;

import android.text.TextUtils;

import com.unitedbustech.eld.command.model.Command;
import com.unitedbustech.eld.command.model.CommandStatus;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Vehicle;
import com.unitedbustech.eld.http.HttpFileRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.FileUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.TimeUtil;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author yufei0213
 * @date 2018/9/7
 * @description 日志命令处理器
 */
public class LogExecutor extends BaseExecutor {

    private static final String START_DATE_KEY = "-s";
    private static final String END_DATE_KEY = "-e";
    private static final String VEHICLE_CODE_KEY = "-v";

    private static final String MMDDYY_REG = "^\\d{6}$";

    @Override
    public void execute(Command command) {

        String args = command.getArgs().trim();

        String startDate = null;
        String endDate = null;
        String vehicleCode = null;

        if (!TextUtils.isEmpty(args)) {

            Logger.i(Tags.COMMAND, "LogExecutor: args=" + args);

            List<String> params = Arrays.asList(args.split("\\s+"));

            int maxIndex = params.size() - 1;

            int startDateKeyIndex = params.indexOf(START_DATE_KEY);
            int endDateKeyIndex = params.indexOf(END_DATE_KEY);
            int vehicleKeyIndex = params.indexOf(VEHICLE_CODE_KEY);

            if (startDateKeyIndex != -1 && startDateKeyIndex < maxIndex) {

                Logger.i(Tags.COMMAND, "LogExecutor: has -s arg, find startDate");
                if (params.get(startDateKeyIndex + 1).matches(MMDDYY_REG)) {

                    startDate = params.get(startDateKeyIndex + 1);
                    Logger.i(Tags.COMMAND, "LogExecutor: find startDate [" + startDate + "]");
                } else {

                    Logger.i(Tags.COMMAND, "LogExecutor: can't find startDate");
                }
            } else {

                Logger.i(Tags.COMMAND, "LogExecutor: no -s arg.");
            }
            if (endDateKeyIndex != -1 && endDateKeyIndex < maxIndex) {

                Logger.i(Tags.COMMAND, "LogExecutor: has -s arg, find endDate");
                if (params.get(endDateKeyIndex + 1).matches(MMDDYY_REG)) {

                    endDate = params.get(endDateKeyIndex + 1);
                    Logger.i(Tags.COMMAND, "LogExecutor: find endDate [" + endDate + "]");
                } else {

                    Logger.i(Tags.COMMAND, "LogExecutor: can't find endDate");
                }
            } else {

                Logger.i(Tags.COMMAND, "LogExecutor: no -e arg.");
            }
            if (vehicleKeyIndex != -1 && vehicleKeyIndex < maxIndex) {

                vehicleCode = params.get(vehicleKeyIndex + 1);
                Logger.i(Tags.COMMAND, "LogExecutor: has -v arg, vehicleCode=" + vehicleCode);
            } else {

                Logger.i(Tags.COMMAND, "LogExecutor: no -v arg.");
            }
        } else {

            Logger.i(Tags.COMMAND, "LogExecutor: args is null");
        }

        if (!TextUtils.isEmpty(vehicleCode)) {

            Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(SystemHelper.getUser().getVehicleId());
            if (vehicle != null && vehicleCode.equals(vehicle.getCode())) {

                Logger.i(Tags.COMMAND, "LogExecutor: update command status [notified]");
                updateCommandStatus(command.getId(), CommandStatus.HAS_NOTIFIED);
                Logger.i(Tags.COMMAND, "LogExecutor: upload logs....");
                uploadLogs(command, startDate, endDate);
            } else {

                if (vehicle == null) {

                    Logger.i(Tags.COMMAND, "LogExecutor: no vehicle selected now, ignore this command");
                } else {

                    Logger.i(Tags.COMMAND, "LogExecutor: current vehicleCode is " + vehicle.getCode() + ", isn't " + vehicleCode + ", ignore this command.");
                }
            }
        } else {

            Logger.i(Tags.COMMAND, "LogExecutor: update command status [notified]");
            updateCommandStatus(command.getId(), CommandStatus.HAS_NOTIFIED);
            Logger.i(Tags.COMMAND, "LogExecutor: upload logs....");
            uploadLogs(command, startDate, endDate);
        }
    }

    /**
     * 根据开始结束日期上报日志
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     */
    private void uploadLogs(Command command, String startDate, String endDate) {

        SimpleDateFormat usFormatter = new SimpleDateFormat("MMddyy", Locale.US);
        usFormatter.setTimeZone(TimeZone.getTimeZone("US/Eastern"));

        Date start = null;
        Date end = null;

        if (!TextUtils.isEmpty(startDate)) {

            try {

                start = usFormatter.parse(startDate);
            } catch (ParseException e) {

                Logger.w(Tags.COMMAND, "LogExecutor: parse startDate error: " + e.toString());
            }
        }
        if (!TextUtils.isEmpty(endDate)) {

            try {

                end = usFormatter.parse(endDate);
            } catch (ParseException e) {

                Logger.w(Tags.COMMAND, "LogExecutor: parse endDate error: " + e.toString());
            }
        }

        List<String> fileList = new ArrayList<>();

        if (start == null) {

            Logger.i(Tags.COMMAND, "LogExecutor: startDate = null");
            for (int i = 0; i < 3; i++) {

                fileList.add(TimeUtil.getPastDate(i, "yyyy-MM-dd", "US/Eastern"));
            }
        } else {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setTimeZone(TimeZone.getTimeZone("US/Eastern"));

            if (end == null) {

                Logger.i(Tags.COMMAND, "LogExecutor: startDate != null, endDate = null");
                for (int i = 0; i < 3; i++) {

                    fileList.add(format.format(TimeUtil.getNextDate(start, i)));
                }
            } else {

                int days = (int) ((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1;
                Logger.i(Tags.COMMAND, "LogExecutor: startDate != null, endDate != null, days = " + days);
                for (int i = 0; i < days; i++) {

                    fileList.add(format.format(TimeUtil.getNextDate(start, i)));
                }
            }
        }

        Logger.i(Tags.COMMAND, "LogExecutor: fileList = " + JsonUtil.toJSONString(fileList));
        if (!fileList.isEmpty()) {

            String zipPath = FileUtil.zipLogs(fileList.toArray(new String[fileList.size()]));

            HttpFileRequest.Builder builder = new HttpFileRequest.Builder();
            builder.url(Constants.API_UPLOAD_LOGS)
                    .addParam("access_token", SystemHelper.getUser().getAccessToken())
                    .addParam("log_file", new File(zipPath));

            HttpFileRequest request = builder.build();
            HttpResponse response = request.upload();

            FileUtil.delFiles(zipPath);

            if (response.isSuccess()) {

                Logger.i(Tags.COMMAND, "LogExecutor: upload logs success, update command status [success]");
                updateCommandStatus(command.getId(), CommandStatus.EXECUTE_SUCCESS);
            } else {

                Logger.w(Tags.COMMAND, "LogExecutor: upload logs failed. code=" + response.getCode() + ", msg=" + response.getMsg() + ", update command status [success]");
                updateCommandStatus(command.getId(), CommandStatus.EXECUTE_FAILED);
            }
        } else {

            Logger.i(Tags.COMMAND, "LogExecutor: no file need upload, update command status [failed]");
            updateCommandStatus(command.getId(), CommandStatus.EXECUTE_FAILED);
        }
    }
}
