package com.unitedbustech.eld.jsinterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.vo.AlertAssignedVo;
import com.unitedbustech.eld.common.vo.AlertEditVo;
import com.unitedbustech.eld.common.vo.AlertNotCertifiedVo;
import com.unitedbustech.eld.common.vo.UnidentifiedEngineLogVo;
import com.unitedbustech.eld.common.vo.UnidentifiedLogVo;
import com.unitedbustech.eld.dailylog.detail.DailylogDetailActivity;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.DailyLog;
import com.unitedbustech.eld.domain.entry.Vehicle;
import com.unitedbustech.eld.eventbus.AlertsRefreshViewEvent;
import com.unitedbustech.eld.eventbus.GetAlertsEvent;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.eventcenter.enums.DDLStatusEnum;
import com.unitedbustech.eld.eventcenter.enums.EventItem;
import com.unitedbustech.eld.eventcenter.model.DriverStatusModel;
import com.unitedbustech.eld.eventcenter.model.EngineEventModel;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.hos.model.GridModel;
import com.unitedbustech.eld.hos.model.HosDayModel;
import com.unitedbustech.eld.hos.model.HosDriveEventModel;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpRequestCallback;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.DataCacheService;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.EventUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.LanguageUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.util.TimeUtil;
import com.unitedbustech.eld.view.UIWebView;
import com.unitedbustech.eld.web.BaseJsInterface;
import com.unitedbustech.eld.web.JsInterface;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author mamw
 * @date 2018/1/28
 * @description 与Alert相关的js接口
 */
@JsInterface(name = "alert")
public class AlertJsInterface extends BaseJsInterface {

    private static final String TAG = "AlertJsInterface";

    private static final String ALERT_INTERFACE_GET_SUMMARY = "getAlertSummary";
    private static final String ALERT_INTERFACE_GET_NOTCERTIFED_DATA = "getNotCertifiedAlertData";
    private static final String ALERT_INTERFACE_GET_EDIT_DATA = "getEditAlertData";
    private static final String ALERT_INTERFACE_GET_ASSIGNED_DATA = "getAssignedAlertData";
    private static final String ALERT_INTERFACE_UPLOAD_NOTCERTIED_SIGN = "uploadNotCertifiedAlertSign";
    private static final String ALERT_INTERFACE_UPDATE_EDIT_STATUS = "updateEditAlertStatus";
    private static final String ALERT_INTERFACE_UPDATE_ASSIGNED_STATUS = "updateAssignedAlertStatus";
    private static final String ALERT_INTERFACE_GET_UNIDENTIFIED_DRIVER_LOG_DATA = "getUnidentifiedDriverLogData";
    private static final String ALERT_INTERFACE_UPDATE_UNIDENTIFIED_DRIVER_LOG_STATUS = "updateUnidentifiedDriverLogStatus";
    private static final String ALERT_INTERFACE_OPEN_DAILYLOG_DETAIL_PAGE = "openDailyLogDetailPage";

    public AlertJsInterface(Context context, UIWebView uiWebView) {

        super(context, uiWebView);
    }

    @Override
    @JavascriptInterface
    public void method(String params) {

        try {
            JSONObject obj = JsonUtil.parseObject(params);

            String method = JsonUtil.getString(obj, METHOD_KEY);
            String data = JsonUtil.getString(obj, DATA_KEY);

            switch (method) {

                case ALERT_INTERFACE_GET_SUMMARY:

                    getAlertSummaryData(data);
                    break;
                case ALERT_INTERFACE_GET_NOTCERTIFED_DATA:

                    getNotCertifiedAlertData(data);
                    break;
                case ALERT_INTERFACE_GET_EDIT_DATA:

                    getEditAlertData(data);
                    break;
                case ALERT_INTERFACE_GET_ASSIGNED_DATA:

                    getAssignedAlertData(data);
                    break;
                case ALERT_INTERFACE_UPLOAD_NOTCERTIED_SIGN:

                    uploadNotCertifiedAlertSign(data);
                    break;
                case ALERT_INTERFACE_UPDATE_EDIT_STATUS:

                    updateEditAlertStatus(data);
                    break;
                case ALERT_INTERFACE_UPDATE_ASSIGNED_STATUS:

                    updateAssignedAlertStatus(data);
                    break;
                case ALERT_INTERFACE_GET_UNIDENTIFIED_DRIVER_LOG_DATA:

                    getUnidentifiedDriverLogData(data);
                    break;
                case ALERT_INTERFACE_UPDATE_UNIDENTIFIED_DRIVER_LOG_STATUS:

                    updateUnidentifiedDriverLogStatus(data);
                    break;
                case ALERT_INTERFACE_OPEN_DAILYLOG_DETAIL_PAGE:

                    openDailyLogDetailPage(data);
                    break;
                default:
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Alert 概括
     */
    private void getAlertSummaryData(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        AlertsRefreshViewEvent alertsRefreshViewEvent = DataCacheService.getInstance().getAlertsRefreshViewEvent();
        final String result = JsonUtil.toJsJSONString(alertsRefreshViewEvent == null ? new AlertsRefreshViewEvent() : alertsRefreshViewEvent);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + result + "');");
            }
        });
    }

    /**
     * 获取Not Certified Alert数据
     */
    private void getNotCertifiedAlertData(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final Integer tabIndex = JsonUtil.getInt(jsonObject, "tabIndex");

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();

                HttpRequest HttpRequest = new HttpRequest.Builder()
                        .url(Constants.API_ALERT_GET_UNSIGNED_LIST)
                        .addParam("access_token", user.getAccessToken())
                        .build();

                final HttpResponse response = HttpRequest.get();

                if (response.isSuccess()) {

                    final List<AlertNotCertifiedVo> notCertifiedVoList = new ArrayList<>();
                    JSONObject data = JsonUtil.parseObject(response.getData());
                    final JSONArray list = JsonUtil.getJsonArray(data, "list");
                    for (int i = 0; i < list.size(); i++) {
                        JSONObject obj = list.getJSONObject(i);
                        AlertNotCertifiedVo notCertifiedVo = new AlertNotCertifiedVo();
                        notCertifiedVo.setId(obj.getInteger("id"));
                        notCertifiedVo.setTimeStamp(obj.getLongValue("dateTime"));
                        notCertifiedVo.setShowDateStr(TimeUtil.utcToLocal(obj.getLongValue("dateTime"),
                                SystemHelper.getUser().getTimeZone(),
                                LanguageUtil.getInstance().getLanguageType() == LanguageUtil.LANGUAGE_ZH ?
                                        TimeUtil.DAY_MMM_D_ZH : TimeUtil.DAY_MMM_D));
                        notCertifiedVo.setBackDateStr(TimeUtil.utcToLocal(obj.getLongValue("dateTime"),
                                SystemHelper.getUser().getTimeZone(),
                                TimeUtil.MM_DD_YY));
                        notCertifiedVo.setOnDutyTime(TimeUtil.secondToHourMinStr(obj.getLongValue("onDutyTime")));
                        notCertifiedVo.isShowDetail();
                        notCertifiedVoList.add(notCertifiedVo);
                    }

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + tabIndex + "','" + JsonUtil.toJsJSONString(notCertifiedVoList) + "');");
                        }
                    });
                } else {

                    final List<AlertNotCertifiedVo> notCertifiedVoList = new ArrayList<>();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + tabIndex + "','" + JsonUtil.toJsJSONString(notCertifiedVoList) + "');");
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取Edit Alert数据
     */
    private void getEditAlertData(final String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final Integer index = JsonUtil.getInt(jsonObject, "index");
        final Integer tabIndex = JsonUtil.getInt(jsonObject, "tabIndex");

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();
                HttpRequest HttpRequest;
                String date = null;
                List<HosDayModel> hosDayModels = ModelCenter.getInstance().getAllHosDayModels();
                if (index != null && index >= 0 && index < Constants.DDL_DAYS) {

                    HosDayModel hosDayModel = hosDayModels.get(index);
                    if (hosDayModel != null) {

                        date = TimeUtil.utcToLocal(hosDayModel.getDate().getTime(), SystemHelper.getUser().getTimeZone(), TimeUtil.MM_DD_YY);
                    }
                    HttpRequest = new HttpRequest.Builder()
                            .url(Constants.API_ALERT_GET_EDIT_DATA)
                            .addParam("access_token", user.getAccessToken())
                            .addParam("start", date)
                            .addParam("end", date)
                            .build();
                } else {

                    HttpRequest = new HttpRequest.Builder()
                            .url(Constants.API_ALERT_GET_EDIT_DATA)
                            .addParam("access_token", user.getAccessToken())
                            .build();
                }

                final HttpResponse response = HttpRequest.get();

                if (response.isSuccess()) {

                    final List<AlertEditVo> list = new ArrayList<>();
                    JSONArray drafts = JsonUtil.parseObject(response.getData()).getJSONArray("drafts");
                    for (int i = 0; i < drafts.size(); i++) {

                        JSONObject draftObj = drafts.getJSONObject(i);
                        JSONObject dutyStatusLog = draftObj.getJSONObject("dutyStatusLog");
                        DriverStatusModel driverStatusModel = JsonUtil.parseObject(dutyStatusLog.toJSONString(), DriverStatusModel.class);

                        AlertEditVo vo = new AlertEditVo();
                        vo.setDatetime(dutyStatusLog.getLong("datetime"));
                        vo.setLogJson(dutyStatusLog);
                        vo.setId(dutyStatusLog.getString("id"));
                        int vehicleId = dutyStatusLog.getInteger("vehicleId");
                        Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(vehicleId);
                        if (vehicle != null) {

                            vo.setVehicle(vehicle.getCode());
                        }

                        vo.setCreateDateStr(TimeUtil.utcToLocal(dutyStatusLog.getLongValue("datetime"),
                                SystemHelper.getUser().getTimeZone(),
                                LanguageUtil.getInstance().getLanguageType() == LanguageUtil.LANGUAGE_ZH ? TimeUtil.DAY_MMM_D_ZH : TimeUtil.DAY_MMM_D));

                        vo.setCreateTimeStr(TimeUtil.utcToLocal(dutyStatusLog.getLongValue("datetime"),
                                SystemHelper.getUser().getTimeZone(),
                                TimeUtil.HH_MM_AA) + " " + SystemHelper.getUser().getTimeZoneAlias());

                        vo.setEditorId(draftObj.getString("editorId"));
                        vo.setEditor(draftObj.getString("editor"));
                        vo.setEditDateStr(TimeUtil.utcToLocal(draftObj.getLongValue("editDateTime"),
                                SystemHelper.getUser().getTimeZone(),
                                TimeUtil.STANDARD_FORMAT));
                        if (draftObj.getString("editType").equals("1")) {

                            vo.setEditTypeStr(context.getString(R.string.alert_edit_by_fleet_manager));
                        } else {

                            vo.setEditTypeStr(context.getString(R.string.alert_team_work_adjustment_by_driver));
                        }

                        vo.setComment(dutyStatusLog.getString("comment"));

                        vo.setOdometer(dutyStatusLog.getString("totalOdometer"));
                        vo.setLocation(dutyStatusLog.getString("location"));

                        List<HosDriveEventModel> additionModels = new ArrayList<>();
                        additionModels.add((HosDriveEventModel) driverStatusModel.convertLocalHosModel());

                        //如果有原始事件，说明是修改请求，不是插入请求
                        List<Long> excludeLocalIds = new ArrayList<>();
                        String originId = dutyStatusLog.getString("originator");
                        DailyLog dailyLog = DataBaseHelper.getDataBase().dailyLogDao().getDailylogByoriginId(originId);
                        if (dailyLog != null) {

                            excludeLocalIds.add(dailyLog.getId());
                        }

                        int type = Integer.valueOf(dutyStatusLog.getString("type"));
                        int code = Integer.valueOf(dutyStatusLog.getString("code"));
                        if (type == EventItem.SELF_CHECK.getCode()) {

                            String abnormalCode = dutyStatusLog.getString("abnormalCode");
                            vo.setState(EventUtil.getSelfCheckModelTitle(code, abnormalCode, false));
                        } else {

                            vo.setState(EventUtil.getHosTitle(type, code, false));
                        }

                        List<GridModel> gridModelList = ModelCenter.getInstance().getGridData(new Date(dutyStatusLog.getLongValue("datetime")), additionModels, excludeLocalIds);
                        if (gridModelList != null && !gridModelList.isEmpty()) {

                            vo.setGridModelList(gridModelList);
                            GridModel gridModel = new GridModel();
                            gridModel.setStartSecond((int) TimeUtil.intervalSecond(new Date(dutyStatusLog.getLong("datetime")), TimeUtil.getDayBegin(new Date(dutyStatusLog.getLong("datetime")))));
                            gridModel.setDriverStatus(DriverState.getStateByCodeAndType(Integer.valueOf(dutyStatusLog.getString("type")), dutyStatusLog.getIntValue("code")));
                            for (int j = 0; j < gridModelList.size(); j++) {

                                if (gridModelList.get(j).getEndSecond() > gridModel.getStartSecond()) {

                                    gridModel.setEndSecond(gridModelList.get(j).getEndSecond());
                                    break;
                                }
                            }
                            vo.setGridModel(gridModel);
                        }

                        vo.resetGrid();
                        list.add(vo);
                    }

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + tabIndex + "','" + JsonUtil.toJsJSONString(list) + "');");
                        }
                    });
                } else {

                    final List<AlertEditVo> list = new ArrayList<>();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + tabIndex + "','" + JsonUtil.toJsJSONString(list) + "');");
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取Assigned Alert数据
     */
    private void getAssignedAlertData(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final Integer tabIndex = JsonUtil.getInt(jsonObject, "tabIndex");

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();

                HttpRequest HttpRequest = new HttpRequest.Builder()
                        .url(Constants.API_ALERT_GET_ASSIGNED_DATA)
                        .addParam("access_token", user.getAccessToken())
                        .build();

                final HttpResponse response = HttpRequest.get();

                if (response.isSuccess()) {

                    final List<AlertAssignedVo> alertAssignedVos = new ArrayList<>();

                    JSONObject responseData = JsonUtil.parseObject(response.getData());
                    JSONArray logs = responseData.getJSONArray("logs");
                    JSONArray engineLogs = responseData.getJSONArray("engineLogs");

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

                        vo.setAssignedName(logObj.getString("assignedName"));
                        vo.setAssignedId(logObj.getString("assignedId"));

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
                        Date startDate = new Date(startLog.getLongValue("datetime"));
                        Date endDate = new Date(endLog.getLongValue("datetime"));
                        gridModel.setStartSecond((int) TimeUtil.intervalSecond(startDate, TimeUtil.getDayBegin(startDate)));
                        //判断是否跨天
                        if (TimeUtil.compareDaybyDate(startDate.getTime(), endDate.getTime())) {

                            gridModel.setEndSecond(TimeUtil.getTotalHours(startDate) * 60 * 60);
                        } else {

                            gridModel.setEndSecond((int) TimeUtil.intervalSecond(endDate, TimeUtil.getDayBegin(endDate)));
                        }
                        gridModel.setDriverStatus(DriverState.getStateByCodeAndType(Integer.valueOf(startLog.getString("type")), startLog.getIntValue("code")));
                        vo.setGridModel(gridModel);
                        alertAssignedVos.add(vo);
                    }
                    // 引擎事件
                    List<UnidentifiedEngineLogVo> unidentifiedEngineLogVos = new ArrayList<>();
                    if(engineLogs != null && engineLogs.size() > 0) {

                        unidentifiedEngineLogVos = JSON.parseArray(engineLogs.toJSONString(), UnidentifiedEngineLogVo.class);
                    }

                    final UnidentifiedLogVo unidentifiedLogVo = new UnidentifiedLogVo(alertAssignedVos, unidentifiedEngineLogVos);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + tabIndex + "','" + JsonUtil.toJsJSONString(unidentifiedLogVo) + "');");
                        }
                    });
                } else {

                    final List<AlertAssignedVo> list = new ArrayList<>();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + tabIndex + "','" + JsonUtil.toJsJSONString(list) + "');");
                        }
                    });
                }
            }
        });
    }

    /**
     * 上传签名
     */
    private void uploadNotCertifiedAlertSign(final String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final String date = JsonUtil.getString(jsonObject, "date");
        final String content = JsonUtil.getString(jsonObject, "content");

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();

                HttpRequest profileReq = new HttpRequest.Builder()
                        .url(Constants.API_UPLOAD_SIGN)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("date", date)
                        .addParam("driver_sign", content)
                        .addParam("comment", "")
                        .build();
                profileReq.post(new HttpRequestCallback() {
                    @Override
                    public void onRequestFinish(HttpResponse response) {

                        if (response.isSuccess()) {

                            String[] dates = date.split(",");
                            Date[] dateTimes = new Date[dates.length];
                            for (int i = 0; i < dates.length; i++) {

                                String oneDate = dates[i];
                                Date dateTime = TimeUtil.strToDate(oneDate, TimeUtil.MM_DD_YY, SystemHelper.getUser().getTimeZone());
                                dateTimes[i] = dateTime;

                                HosDayModel hosDayModel = ModelCenter.getInstance().getHosDayModel(dateTime);
                                if (hosDayModel != null) {

                                    hosDayModel.setSign(content);
                                    //签名后更新本地库
                                    int driverId = SystemHelper.getUser().getDriverId();
                                    String dateString = TimeUtil.utcToLocal(dateTime.getTime(), SystemHelper.getUser().getTimeZone(), TimeUtil.MM_DD_YY);
                                    DataBaseHelper.getDataBase().signCacheDao().signOneDay(dateString, driverId, content);
                                }
                            }
                            EventCenter.getInstance().signEvent(dateTimes);

                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                                }
                            });

                            EventBus.getDefault().post(new GetAlertsEvent());
                        } else {

                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    /**
     * 更新Edit Alert状态
     */
    private void updateEditAlertStatus(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final String id = JsonUtil.getString(jsonObject, "id");
        final int status = JsonUtil.getInt(jsonObject, "status");
        final String log = JsonUtil.getString(jsonObject, "log");

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();

                HttpRequest HttpRequest = new HttpRequest.Builder()
                        .url(Constants.API_GET_ALERT_UPDATE_EDIT_STATUS)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("id", id)
                        .addParam("status", String.valueOf(status))
                        .build();

                HttpResponse response = HttpRequest.post();

                if (response.isSuccess()) {

                    List<DriverStatusModel> statusModels = new ArrayList<>();
                    if (status == 1) {

                        //更新本地模型
                        DriverStatusModel eventModel = JsonUtil.parseObject(log, DriverStatusModel.class);
                        eventModel.setStatus(DDLStatusEnum.ACTIVE.getCode());
                        statusModels.add(eventModel);
                    }

                    ModelCenter.getInstance().updateModelByAlert(statusModels);

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                        }
                    });

                } else {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                        }
                    });
                }
            }
        });
    }

    /**
     * 更新Assigned Alert状态
     */
    private void updateAssignedAlertStatus(String data) {

        final JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final String ids = JsonUtil.getString(jsonObject, "ids");
        final String objects = JsonUtil.getString(jsonObject, "objects");
        final int result = JsonUtil.getInt(jsonObject, "result"); // 0:accept, 1:reject

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();

                HttpRequest HttpRequest = new HttpRequest.Builder()
                        .url(Constants.API_GET_ALERT_UPDATE_ASSIGNED_STATUS)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("ids", ids)
                        .addParam("result", String.valueOf(result))
                        .build();

                Logger.i(Tags.UNIDENTIFIED, "update assigned status, ids=" + ids + ", result=" + result + "(0:accept, 1:reject)");

                HttpResponse response = HttpRequest.post();

                if (response.isSuccess()) {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                        }
                    });

                    afterAcceptedUnidentified(user, response, objects, result);

                    Logger.i(Tags.UNIDENTIFIED, "update assigned status success");
                } else {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                        }
                    });

                    Logger.i(Tags.UNIDENTIFIED, "update assigned status failed. code=" + response.getCode() + ", msg=" + response.getMsg());
                }
            }
        });
    }

    /**
     * 获取Unidentified Driver Log数据
     */
    private void getUnidentifiedDriverLogData(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final List<AlertAssignedVo> alertAssignedVos = DataCacheService.getInstance().getUnidentifiedLogs();
        final List<UnidentifiedEngineLogVo> unidentifiedEngineLogs = DataCacheService.getInstance().getUnidentifiedEngineLogs();

        final UnidentifiedLogVo unidentifiedLogVo = new UnidentifiedLogVo(alertAssignedVos, unidentifiedEngineLogs);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(unidentifiedLogVo) + "');");
            }
        });
    }

    /**
     * 更新Unidentified Driver Log状态
     */
    private void updateUnidentifiedDriverLogStatus(String data) {

        final JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final String ids = JsonUtil.getString(jsonObject, "ids");
        final String objects = JsonUtil.getString(jsonObject, "objects");
        final int result = JsonUtil.getInt(jsonObject, "result"); // 0:accept, 1:reject

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();

                HttpRequest HttpRequest = new HttpRequest.Builder()
                        .url(Constants.API_GET_ALERT_UPDATE_ASSIGNED_STATUS)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("ids", ids)
                        .addParam("result", String.valueOf(result))
                        .build();

                Logger.i(Tags.UNIDENTIFIED, "update unidentifiedLogs status, ids=" + ids + ", result=" + result + "(0:accept, 1:reject)");

                HttpResponse response = HttpRequest.post();

                if (response.isSuccess()) {

                    JSONArray jsonArray = JsonUtil.parseArray(ids);
                    if (jsonArray != null) {

                        for (int i = 0; i < jsonArray.size(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            if (object != null) {

                                DataCacheService.getInstance().refreshUnidentifiedLogs(JsonUtil.getString(object, "startId"),
                                        JsonUtil.getString(object, "endId"));

                                DataCacheService.getInstance().refreshUndientiedEngineLogs(JsonUtil.getString(object, "startId"));
                            }
                        }
                    }

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                        }
                    });

                    afterAcceptedUnidentified(user, response, objects, result);

                    afterAcceptedUnidentifiedEngine(user, response, objects, result);

                    Logger.i(Tags.UNIDENTIFIED, "update unidentifiedLogs status success");
                } else {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                        }
                    });

                    Logger.i(Tags.UNIDENTIFIED, "update unidentifiedLogs status failed. code=" + response.getCode() + ", msg=" + response.getMsg());
                }
            }
        });
    }

    /**
     * 接受未认领日志后的处理
     *
     * @param user     用户
     * @param response 请求结果
     * @param objects  未认领日志数据
     * @param result   处理方式
     */
    private void afterAcceptedUnidentified(User user, HttpResponse response, String objects, int result) {

        List<DriverStatusModel> driverStatusModels = new ArrayList<>();

        //如果有数据并且是accept
        if (objects != null && result == 0) {

            SimpleDateFormat usFormatter = new SimpleDateFormat("MMddHHmm", Locale.US);
            usFormatter.setTimeZone(TimeZone.getTimeZone(user.getTimeZone()));
            String remark = "Accept: " + usFormatter.format(new Date());

            List<JSONObject> jsonObjects = JsonUtil.parseArray(objects, JSONObject.class);

            String acceptResult = response.getData();
            if (!TextUtils.isEmpty(acceptResult)) {

                Logger.i(Tags.UNIDENTIFIED, "unidentified log acceptResult = [" + acceptResult + "]");

                JSONObject acceptObj = JsonUtil.parseObject(acceptResult);

                JSONArray invalidArray = acceptObj.getJSONObject("data").getJSONArray("invalid");
                JSONArray validArray = acceptObj.getJSONObject("data").getJSONArray("success");

                JSONArray resultObjs = new JSONArray();
                for (JSONObject object : jsonObjects) {

                    JSONObject startObj = object.getJSONObject("startJson");
                    JSONObject endObj = object.getJSONObject("endJson");

                    int size = validArray.size();
                    for (int i = 0; i < size; i++) {

                        JSONObject validIds = validArray.getJSONObject(i);
                        if (startObj != null && startObj.get("id").equals(validIds.get("startId")) && endObj != null && endObj.get("id").equals(validIds.get("endId"))) {

                            JSONObject newStartObj = new JSONObject();
                            JSONObject newEndObj = new JSONObject();

                            for (String key : startObj.keySet()) {

                                newStartObj.put(key, startObj.get(key));
                            }
                            for (String key : endObj.keySet()) {

                                newEndObj.put(key, endObj.get(key));
                            }

                            newStartObj.put("originator", startObj.get("id"));
                            newStartObj.put("id", validIds.get("newStartId"));
                            newStartObj.put("origin", DDLOriginEnum.UNIDENTIFIED.getCode());
                            String startRemark = startObj.getString("comment");
                            startRemark = TextUtils.isEmpty(startRemark) ? remark : startRemark + "; " + remark;
                            if (startRemark.length() > Constants.REMARK_MAX_LENGTH) {

                                startRemark = startRemark.substring(0, Constants.REMARK_MAX_LENGTH);
                            }
                            newStartObj.put("comment", startRemark);

                            newEndObj.put("originator", endObj.get("id"));
                            newEndObj.put("id", validIds.get("newEndId"));
                            newEndObj.put("origin", DDLOriginEnum.UNIDENTIFIED.getCode());
                            String endRemark = endObj.getString("comment");
                            endRemark = TextUtils.isEmpty(endRemark) ? remark : endRemark + "; " + remark;
                            if (endRemark.length() > Constants.REMARK_MAX_LENGTH) {

                                endRemark = endRemark.substring(0, Constants.REMARK_MAX_LENGTH);
                            }
                            newEndObj.put("comment", endRemark);

                            startObj.put("status", DDLStatusEnum.INACTIVE_CHANGED.getCode());
                            endObj.put("status", DDLStatusEnum.INACTIVE_CHANGED.getCode());

                            JSONObject oldObj = new JSONObject();
                            oldObj.put("startJson", startObj);
                            oldObj.put("endJson", endObj);

                            JSONObject resultObj = new JSONObject();
                            resultObj.put("startJson", newStartObj);
                            resultObj.put("endJson", newEndObj);

                            resultObjs.add(oldObj);
                            resultObjs.add(resultObj);

                            validArray.remove(validIds);
                            break;
                        }
                    }
                }

                int size = resultObjs.size();
                for (int i = 0; i < size; i++) {

                    JSONObject object = resultObjs.getJSONObject(i);

                    DriverStatusModel drivingModel = JsonUtil.parseObject(object.getString("startJson"), DriverStatusModel.class);
                    DriverStatusModel odndModel = JsonUtil.parseObject(object.getString("endJson"), DriverStatusModel.class);

                    driverStatusModels.add(drivingModel);
                    driverStatusModels.add(odndModel);
                }
            } else {

                Logger.w(Tags.UNIDENTIFIED, "unidentified log acceptResult = [" + acceptResult + "]");
            }
        }

        ModelCenter.getInstance().updateModelByAlert(driverStatusModels);
    }

    /**
     * 未认领引擎事件认领后处理
     * @param user
     * @param response
     * @param objects
     * @param result
     */
    public void afterAcceptedUnidentifiedEngine(User user, HttpResponse response, String objects, int result) {

        List<EngineEventModel> engineEventModels = new ArrayList<>();

        //如果有数据并且是accept
        if (objects != null && result == 0) {

            SimpleDateFormat usFormatter = new SimpleDateFormat("MMddHHmm", Locale.US);
            usFormatter.setTimeZone(TimeZone.getTimeZone(user.getTimeZone()));
            String remark = "Accept: " + usFormatter.format(new Date());

            List<JSONObject> jsonObjects = JsonUtil.parseArray(objects, JSONObject.class);

            String acceptResult = response.getData();
            if (!TextUtils.isEmpty(acceptResult)) {

                Logger.i(Tags.UNIDENTIFIED, "unidentified log acceptResult = [" + acceptResult + "]");

                JSONObject acceptObj = JsonUtil.parseObject(acceptResult);

                JSONArray validArray = acceptObj.getJSONObject("data").getJSONArray("success");

                JSONArray resultObjs = new JSONArray();
                for (JSONObject object : jsonObjects) {

                    JSONObject obj = object.getJSONObject("data");

                    int size = validArray.size();
                    for (int i = 0; i < size; i++) {

                        JSONObject validIds = validArray.getJSONObject(i);
                        if (obj != null && obj.get("id").equals(validIds.get("startId"))) {

                            JSONObject newStartObj = new JSONObject();
                            for (String key : obj.keySet()) {

                                newStartObj.put(key, obj.get(key));
                            }
                            newStartObj.put("originator", obj.get("id"));
                            newStartObj.put("id", validIds.get("newStartId"));
                            newStartObj.put("origin", DDLOriginEnum.UNIDENTIFIED.getCode());
                            String startRemark = obj.getString("comment");
                            startRemark = TextUtils.isEmpty(startRemark) ? remark : startRemark + "; " + remark;
                            if (startRemark.length() > Constants.REMARK_MAX_LENGTH) {

                                startRemark = startRemark.substring(0, Constants.REMARK_MAX_LENGTH);
                            }
                            newStartObj.put("comment", startRemark);
                            obj.put("status", DDLStatusEnum.INACTIVE_CHANGED.getCode());

                            JSONObject oldObj = new JSONObject();
                            oldObj.put("json", obj);
                            JSONObject resultObj = new JSONObject();
                            resultObj.put("json", newStartObj);

//                            resultObjs.add(oldObj);
                            resultObjs.add(resultObj);

                            validArray.remove(validIds);
                            break;
                        }
                    }
                }

                int size = resultObjs.size();
                for (int i = 0; i < size; i++) {

                    JSONObject object = resultObjs.getJSONObject(i);
                    EngineEventModel model = JsonUtil.parseObject(object.getString("json"), EngineEventModel.class);

                    engineEventModels.add(model);
                }
            } else {

                Logger.w(Tags.UNIDENTIFIED, "unidentified log acceptResult = [" + acceptResult + "]");
            }
        }

        ModelCenter.getInstance().updateEngineModelByAlert(engineEventModels);
    }

    /**
     * notCertified 跳转dailyLogDetailsPage页面
     */
    private void openDailyLogDetailPage(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        long date = JsonUtil.getLong(jsonObject, "date");
        // 转换index
        for (int i = 0; i < Constants.DDL_DAYS; i++) {

            HosDayModel hosDayModel = ModelCenter.getInstance().getAllHosDayModels().get(i);
            if (TimeUtil.isSameDay(new Date(date), hosDayModel.getDate())) {

                jsonObject.put("index", i);
                break;
            }
        }
        Intent intent = DailylogDetailActivity.newIntent(context, jsonObject.toJSONString());
        context.startActivity(intent);
    }
}
