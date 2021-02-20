package com.unitedbustech.eld.system;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.http.HttpFileRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.util.FileUtil;
import com.unitedbustech.eld.util.JsonUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author yufei0213
 * @date 2018/3/17
 * @description UploadLogs
 */
public class UploadLogs implements Runnable {

    private static final String TAG = "UploadLogs";

    private String data;

    public UploadLogs(String data) {

        this.data = data;
    }

    @Override
    public void run() {

        try {

            JSONObject jsonObject = JsonUtil.parseObject(data);
            String startDateStr = JsonUtil.getString(jsonObject, "startTime"); // MM/dd/yyyy
            String endDateStr = JsonUtil.getString(jsonObject, "endTime"); // MM/dd/yyyy

            Logger.i(Tags.SYSTEM, "startDate=" + startDateStr + ", endDate=" + endDateStr);

            SimpleDateFormat usFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            usFormatter.setTimeZone(TimeZone.getTimeZone("US/Eastern"));

            Date startDate = usFormatter.parse(startDateStr);
            Date endDate = usFormatter.parse(endDateStr);

            int days = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;

            String[] fileName = new String[days];

            for (int i = 0; i < days; i++) {

                fileName[i] = getPastDate(startDate, i);
            }

            String zipPath = FileUtil.zipLogs(fileName);

            HttpFileRequest.Builder builder = new HttpFileRequest.Builder();
            builder.url(Constants.API_UPLOAD_LOGS)
                    .addParam("access_token", SystemHelper.getUser().getAccessToken())
                    .addParam("log_file", new File(zipPath));

            HttpFileRequest request = builder.build();
            HttpResponse response = request.upload();

            FileUtil.delFiles(zipPath);

            if (response.isSuccess()) {

                Logger.i(Tags.SYSTEM, "upload logs success");
            } else {

                Logger.w(Tags.SYSTEM, "upload logs failed. code=" + response.getCode() + ", msg=" + response.getMsg());
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 获取未来第几天的日期
     *
     * @param startDate 开始日期
     * @param past      过去几天
     * @return 日期字符串
     */
    private String getPastDate(Date startDate, int past) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date date = calendar.getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("US/Eastern"));

        String result = format.format(date);
        return result;
    }
}
