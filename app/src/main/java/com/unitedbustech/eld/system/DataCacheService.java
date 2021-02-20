package com.unitedbustech.eld.system;

import android.app.Activity;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.ActivityStack;
import com.unitedbustech.eld.activity.WebActivity;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.MalfunctionType;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.vo.AlertAssignedVo;
import com.unitedbustech.eld.common.vo.UnidentifiedEngineLogVo;
import com.unitedbustech.eld.datacollector.common.History;
import com.unitedbustech.eld.driving.DrivingActivity;
import com.unitedbustech.eld.eventbus.AlertsRefreshViewEvent;
import com.unitedbustech.eld.eventbus.DashBoardMalFunctionEvent;
import com.unitedbustech.eld.eventbus.GetAlertsEvent;
import com.unitedbustech.eld.eventbus.NetworkChangeEvent;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.eventcenter.enums.EventItem;
import com.unitedbustech.eld.eventcenter.model.DriverStatusModel;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.hos.model.GridModel;
import com.unitedbustech.eld.hos.model.HosDriveEventModel;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.util.EventUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yufei0213
 * @date 2018/4/21
 * @description 数据缓存服务
 */
public class DataCacheService {

    /**
     * 未认领日志
     */
    private List<AlertAssignedVo> unidentifiedLogs;
    /**
     * 未认领引擎日志
     */
    private List<UnidentifiedEngineLogVo> unidentifiedEngineLogs;

    /**
     * 缓存AlertsRefreshViewEvent
     */
    private AlertsRefreshViewEvent alertsRefreshViewEvent;

    private static DataCacheService instance = null;

    private DataCacheService() {
    }

    public static DataCacheService getInstance() {

        if (instance == null) {

            instance = new DataCacheService();
        }

        return instance;
    }

    /**
     * 初始化
     */
    public void init() {

        if (EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().unregister(this);
        }

        EventBus.getDefault().register(this);
    }

    /**
     * 销毁
     */
    public void destroy() {

        try {

            EventBus.getDefault().unregister(this);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 刷新缓存
     *
     * @param startId startId
     * @param endId   endId
     */
    public void refreshUnidentifiedLogs(String startId, String endId) {

        if (unidentifiedLogs == null) {

            return;
        }
        List<AlertAssignedVo> deleteList = new ArrayList<>();
        for (AlertAssignedVo alertAssignedVo : unidentifiedLogs) {

            if (alertAssignedVo.getStartId().equals(startId) && alertAssignedVo.getEndId().equals(endId)) {

                deleteList.add(alertAssignedVo);
            }
        }

        unidentifiedLogs.removeAll(deleteList);
    }

    /**
     * 刷新未认领引擎事件缓存
     *
     * @param startId startId
     */
    public void refreshUndientiedEngineLogs(String startId) {

        if (unidentifiedEngineLogs == null) {

            return;
        }
        List<UnidentifiedEngineLogVo> deleteList = new ArrayList<>();
        for (UnidentifiedEngineLogVo unidentifiedEngineLogVo : unidentifiedEngineLogs) {

            if(unidentifiedEngineLogVo.getId().equals(startId)) {

                deleteList.add(unidentifiedEngineLogVo);
            }
        }

        unidentifiedEngineLogs.removeAll(deleteList);
    }

    /**
     * 获取缓存的未认领日志
     *
     * @return AlertAssignedVo
     */
    public List<AlertAssignedVo> getUnidentifiedLogs() {

        return unidentifiedLogs == null ? new ArrayList<AlertAssignedVo>() : unidentifiedLogs;
    }

    public List<UnidentifiedEngineLogVo> getUnidentifiedEngineLogs() {
        return null == unidentifiedEngineLogs ? new ArrayList<UnidentifiedEngineLogVo>() : unidentifiedEngineLogs;
    }

    /**
     * 获取AlertsRefreshViewEvent
     *
     * @return AlertsRefreshViewEvent
     */
    public AlertsRefreshViewEvent getAlertsRefreshViewEvent() {

        return alertsRefreshViewEvent;
    }

    /**
     * 处理未认领日志
     *
     * @param histories 历史数据
     * @param startTime 历史记录截止时间
     */
    public void handleUnidentifiedLogs(final List<History> histories, final long startTime, final int deviceType) {

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                if (histories != null && !histories.isEmpty()) {

                    List<History> deleteList = new ArrayList<>();
                    for (History history : histories) {

                        if (history.getEventTime() > startTime) {

                            deleteList.add(history);
                        }
                    }

                    //删除多余的历史数据
                    histories.removeAll(deleteList);

                    Logger.i(Tags.UNIDENTIFIED, "get history list: " + JsonUtil.toJSONString(histories));

                    Logger.i(Tags.UNIDENTIFIED, "handleUnidentifiedLogs merge and upload...");
                    EventCenter.getInstance().handleUnidentifiedLogs(histories, deviceType);
                } else {

                    Logger.i(Tags.UNIDENTIFIED, "here is no handleUnidentifiedLogs");
                }

                getUnidentifiedLogFromOrigin();
            }
        });
    }

    public void getUnidentifiedLogFromOrigin(){

        User user = SystemHelper.getUser();
        HttpRequest httpRequest = new HttpRequest.Builder()
                .url(Constants.API_ALERT_GET_UNIDENTIFIED_LIST)
                .addParam("access_token", user.getAccessToken())
                .addParam("vehicle_id", Integer.toString(user.getVehicleId()))
                .build();

        Logger.i(Tags.UNIDENTIFIED, "get unidentifiedLogs from server...");
        HttpResponse httpResponse = httpRequest.get();
        if (httpResponse.isSuccess()) {

            JSONObject obj = JsonUtil.parseObject(httpResponse.getData());
            JSONArray logs = JsonUtil.getJsonArray(obj, "logs");
            JSONArray engineLogs = JsonUtil.getJsonArray(obj, "engineLogs");

            Logger.i(Tags.UNIDENTIFIED, "get unidentified from server success, logs size=" + (logs == null ? "null" : logs.size())+"; engineLogs size = " +(null == engineLogs ? "null" : engineLogs.size()));

            if (logs != null && logs.size() > 0) {

                unidentifiedLogs = createAlertAssignedVo(logs);
            } else {

                unidentifiedLogs = new ArrayList<>();
            }

            if(null != engineLogs && engineLogs.size()>0){

                unidentifiedEngineLogs = JSON.parseArray(engineLogs.toJSONString(), UnidentifiedEngineLogVo.class);
            } else {

                unidentifiedEngineLogs = new ArrayList<>();
            }
        } else {

            Logger.w(Tags.UNIDENTIFIED, "get unidentifiedLogs from server failed: code=" + httpResponse.getCode() + ", msg=" + httpResponse.getMsg());
        }
    }

    /**
     * 打开未认领日志处理界面
     */
    @Deprecated
    public void openUnidentifiedLogsPage() {

        Activity activity = ActivityStack.getInstance().getCurrentActivity();
        if (!(activity instanceof DrivingActivity)) {

            Logger.i(Tags.UNIDENTIFIED, "open unidentifiedLogPage");

            String fileName = "unidentified-driver-log.html";
            String title = activity.getResources().getString(R.string.unidentified_page_title);

            Intent intent = WebActivity.newIntent(activity, fileName, title);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } else {

            Logger.w(Tags.UNIDENTIFIED, "current page is Driving Page, can't open unidentifiedLogPage");
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onNetworkChangeEvent(NetworkChangeEvent networkChangeEvent) {

        if (networkChangeEvent.isConnected()) {

            onAlertsRefreshEvent(null);
        } else {

            this.alertsRefreshViewEvent = new AlertsRefreshViewEvent();
            EventBus.getDefault().post(this.alertsRefreshViewEvent);
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onAlertsRefreshEvent(GetAlertsEvent getAlertsEvent) {

        User user = SystemHelper.getUser();
        HttpRequest getMalFunctionRequest = new HttpRequest.Builder()
                .url(Constants.API_ALERT_GET_SUMMARY)
                .addParam("access_token", user.getAccessToken())
                .addParam("vehicle_id", Integer.toString(user.getVehicleId()))
                .build();

        HttpResponse httpResponse = getMalFunctionRequest.get();

        if (httpResponse.isSuccess()) {

            JSONObject summaryObj = JsonUtil.parseObject(httpResponse.getData());
            if (summaryObj != null) {

                this.alertsRefreshViewEvent = new AlertsRefreshViewEvent(summaryObj);
                EventBus.getDefault().post(this.alertsRefreshViewEvent);

                int malfunctionCnt = JsonUtil.getInt(summaryObj, "malfunctionCnt");
                int diagnosticCnt = JsonUtil.getInt(summaryObj, "diagnosticCnt");

                //加一层判断，处理接口请求慢的问题
                if (SystemHelper.hasVehicle()) {

                    EventBus.getDefault().post(new DashBoardMalFunctionEvent(malfunctionCnt > 0 || diagnosticCnt > 0));

                    SystemHelper.setMalFunction(malfunctionCnt == 0);
                    SystemHelper.setDiagnostic(diagnosticCnt == 0);

                    //如果当前有故障，检查当前是否有GPS故障
                    if (malfunctionCnt > 0) {

                        HttpRequest getPositionMalFunctionRequest = new HttpRequest.Builder()
                                .url(Constants.API_GET_MALFUNCTION_LIST)
                                .addParam("access_token", user.getAccessToken())
                                .addParam("vehicle_id", Integer.toString(user.getVehicleId()))
                                .addParam("code", MalfunctionType.POSITIONING_COMPLIANCE.getCode())
                                .build();
                        HttpResponse getPositionMalFunctionResponse = getPositionMalFunctionRequest.get();
                        if (getPositionMalFunctionResponse.isSuccess()) {

                            JSONObject dataObj = JsonUtil.parseObject(getPositionMalFunctionResponse.getData());
                            JSONArray malfunctionArray = JsonUtil.getJsonArray(dataObj, "malfunctions");

                            //加一层判断，处理接口请求慢的问题
                            if (SystemHelper.hasVehicle()) {

                                SystemHelper.setPositionMalFunction(malfunctionArray.size() == 0);
                            } else {

                                SystemHelper.setPositionMalFunction(true);
                            }
                        }
                    }
                } else {

                    EventBus.getDefault().post(new DashBoardMalFunctionEvent(false));

                    SystemHelper.setMalFunction(true);
                    SystemHelper.setDiagnostic(true);
                }
            } else {

                EventBus.getDefault().post(new DashBoardMalFunctionEvent(false));

                SystemHelper.setMalFunction(true);
                SystemHelper.setDiagnostic(true);
            }
        }
    }

    /**
     * 创建Vo
     *
     * @param logs 数据数据
     */
    private List<AlertAssignedVo> createAlertAssignedVo(JSONArray logs) {

        List<AlertAssignedVo> list = new ArrayList<>();

        String pattern = "%.2f";
        for (int i = 0; i < logs.size(); i++) {

            JSONObject logObj = logs.getJSONObject(i);
            JSONObject endLog = logObj.getJSONObject("endLog");
            JSONObject startLog = logObj.getJSONObject("startLog");

            DriverStatusModel driveModel = JsonUtil.parseObject(startLog.toJSONString(), DriverStatusModel.class);
            DriverStatusModel odndModel = JsonUtil.parseObject(endLog.toJSONString(), DriverStatusModel.class);

            List<HosDriveEventModel> additionModels = new ArrayList<>();
            additionModels.add((HosDriveEventModel) driveModel.convertLocalHosModel());
            additionModels.add((HosDriveEventModel) odndModel.convertLocalHosModel());

            AlertAssignedVo vo = new AlertAssignedVo();
            vo.setDatetime(startLog.getLong("datetime"));
            vo.setStartJson(startLog);
            vo.setEndJson(endLog);
            vo.setStartId(startLog.getString("id"));
            vo.setEndId(endLog.getString("id"));

            vo.setVehicleNo(logObj.getString("vehicleCode"));

            vo.setIntervalTime(TimeUtil.secondToHourMinStr(Math.round((endLog.getLongValue("datetime") - startLog.getLongValue("datetime")) / 1000)));
            vo.setIntervalTimeStr(TimeUtil.utcToLocal(startLog.getLongValue("datetime"),
                    SystemHelper.getUser().getTimeZone(),
                    TimeUtil.STANDARD_FORMAT) + " - " + TimeUtil.utcToLocal(endLog.getLongValue("datetime"),
                    SystemHelper.getUser().getTimeZone(),
                    TimeUtil.STANDARD_FORMAT));

            vo.setStartLocation(startLog.getString("location"));
            vo.setEndLocation(endLog.getString("location"));
            int type = Integer.valueOf(startLog.getString("type"));
            int code = Integer.valueOf(startLog.getString("code"));
            if (type == EventItem.SELF_CHECK.getCode()) {
                String abnormalCode = startLog.getString("abnormalCode");
                vo.setState(EventUtil.getSelfCheckModelTitle(code, abnormalCode, false));
            } else {
                vo.setState(EventUtil.getHosTitle(type, code, false));
            }
            vo.setStartTime(TimeUtil.utcToLocal(startLog.getLongValue("datetime"),
                    SystemHelper.getUser().getTimeZone(),
                    TimeUtil.STANDARD_FORMAT) + " " + SystemHelper.getUser().getTimeZoneAlias());
            vo.setEndTime(TimeUtil.utcToLocal(endLog.getLongValue("datetime"),
                    SystemHelper.getUser().getTimeZone(),
                    TimeUtil.STANDARD_FORMAT) + " " + SystemHelper.getUser().getTimeZoneAlias());

            double startOdometer = startLog.getDouble("totalOdometer");
            double endOdometer = endLog.getDouble("totalOdometer");
            double distance = endOdometer - startOdometer;
            vo.setStart_end_odometer(String.format(pattern, startOdometer) +
                    "-" +
                    String.format(pattern, endOdometer) +
                    "(" +
                    String.format(pattern, distance) +
                    ")");

            vo.setIntervalOdometer(String.format(pattern, distance));

            double startEngineHour = startLog.getDouble("totalEngineHours");
            double endEngineHour = endLog.getDouble("totalEngineHours");
            double duration = endEngineHour - startEngineHour;
            vo.setEngineTime(String.format(pattern, startEngineHour) +
                    "-" +
                    String.format(pattern, endEngineHour) +
                    "("
                    +
                    String.format(pattern, duration) +
                    ")");

            vo.setComment(endLog.getString("comment"));
            vo.setGridModelList(ModelCenter.getInstance().getGridData(new Date(startLog.getLongValue("datetime")), additionModels, null));

            GridModel gridModel = new GridModel();
            //判断是否跨天
            if (TimeUtil.compareDaybyDate(startLog.getLongValue("datetime"), endLog.getLongValue("datetime"))) {

                gridModel.setStartSecond((int) TimeUtil.intervalSecond(new Date(startLog.getLongValue("datetime")), TimeUtil.getDayBegin(new Date(startLog.getLongValue("datetime")))));
                gridModel.setEndSecond(TimeUtil.getTotalHours(new Date(startLog.getLongValue("datetime"))) * 60 * 60);
            } else {

                gridModel.setStartSecond(TimeUtil.hourMinSecondStrToSecond(startLog.getString("time")));
                gridModel.setEndSecond(TimeUtil.hourMinSecondStrToSecond(endLog.getString("time")));
            }
            gridModel.setDriverStatus(DriverState.getStateByCodeAndType(Integer.valueOf(startLog.getString("type")), startLog.getIntValue("code")));
            vo.setGridModel(gridModel);

            list.add(vo);
        }

        return list;
    }
}
