package com.unitedbustech.eld.jsinterface;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Driver;
import com.unitedbustech.eld.domain.entry.InspectionLog;
import com.unitedbustech.eld.dvir.model.DVIRVo;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.request.RequestCacheService;
import com.unitedbustech.eld.request.RequestType;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.util.TimeUtil;
import com.unitedbustech.eld.view.UIWebView;
import com.unitedbustech.eld.web.BaseJsInterface;
import com.unitedbustech.eld.web.JsInterface;

import java.util.Date;
import java.util.List;

/**
 * @author zhangyu
 * @date 2018/1/20
 * @description 与日志有关的js接口
 */
@JsInterface(name = "dvir")
public class DvirJsInterface extends BaseJsInterface {

    private static final String TAG = "DvirJsInterface";

    private static final String UPLOAD_INSPECTION = "uploadInspection";
    private static final String INSPECTION_LIST = "inspectionList";
    private static final String GET_INSPECTION_LOG = "getInspectionLog";

    public DvirJsInterface(Context context, UIWebView uiWebView) {

        super(context, uiWebView);
    }

    @Override
    @JavascriptInterface
    public void method(String params) {

        JSONObject obj = JsonUtil.parseObject(params);
        String method = JsonUtil.getString(obj, METHOD_KEY);
        String data = JsonUtil.getString(obj, DATA_KEY);

        switch (method) {
            case UPLOAD_INSPECTION:

                uploadInspection(data);
                break;
            case INSPECTION_LIST:

                getInspectionList(data);
                break;
            case GET_INSPECTION_LOG:

                getInspectionLog(data);
                break;
            default:
                break;
        }
    }

    private void uploadInspection(String data) {

        final JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final String vehicleId = JsonUtil.getString(jsonObject, "vehicle_id");
        final String type = JsonUtil.getString(jsonObject, "type");
        final String odometer = JsonUtil.getString(jsonObject, "odometer");
        final String time = JsonUtil.getString(jsonObject, "time");
        final String location = JsonUtil.getString(jsonObject, "location");
        final String latitude = JsonUtil.getString(jsonObject, "latitude");
        final String longitude = JsonUtil.getString(jsonObject, "longitude");
        final String defects = JsonUtil.getString(jsonObject, "defects");
        final String result = JsonUtil.getString(jsonObject, "result");
        final String remark = JsonUtil.getString(jsonObject, "remark");
        final String defect_level = JsonUtil.getString(jsonObject, "defect_level");
        final String driver_sign = JsonUtil.getString(jsonObject, "driver_sign");

        User user = SystemHelper.getUser();

        final HttpRequest HttpRequest = new HttpRequest.Builder()
                .url(Constants.API_REQUEST_INSPECTION_CREATE)
                .addParam("access_token", user.getAccessToken())
                .addParam("vehicle_id", vehicleId)
                .addParam("type", type)
                .addParam("odometer", odometer)
                .addParam("time", time)
                .addParam("location", location)
                .addParam("latitude", latitude)
                .addParam("longitude", longitude)
                .addParam("defects", defects)
                .addParam("result", result)
                .addParam("remark", remark)
                .addParam("defect_level", defect_level)
                .addParam("driver_sign", driver_sign)
                .build();

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                RequestCacheService.getInstance().cachePost(HttpRequest, RequestType.OTHERS);

                Driver driver = DataBaseHelper.getDataBase().driverDao().getDriver(SystemHelper.getUser().getDriverId());
                jsonObject.put("driverName", driver.getName());

                InspectionLog inspectionLog = new InspectionLog();
                inspectionLog.setDriverId(driver.getId());
                inspectionLog.setVehicleId(Integer.parseInt(vehicleId));
                inspectionLog.setTime(Long.parseLong(time));
                inspectionLog.setInspectionJson(jsonObject.toJSONString());
                inspectionLog.setType(Integer.parseInt(type));
                DataBaseHelper.getDataBase().inspectionLogDao().insert(inspectionLog);

                ModelCenter.getInstance().updateRemarkAfterInspection(Integer.parseInt(type));

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                    }
                });
            }
        });
    }

    private void getInspectionList(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);

        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final int type = JsonUtil.getInt(jsonObject, "type");
        final int vehicle_id = JsonUtil.getInt(jsonObject, "vehicle_id");
        final Date end_date = new Date();
        final Date start_date = TimeUtil.getNextDate(end_date, -15);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();

                HttpRequest HttpRequest = new HttpRequest.Builder()
                        .url(Constants.API_REQUEST_INSPECTION_LIST)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("type", String.valueOf(type))
                        .addParam("vehicle_id", String.valueOf(vehicle_id))
                        .addParam("start_time", start_date.getTime() + "")
                        .addParam("end_time", end_date.getTime() + "")
                        .build();

                final HttpResponse response = HttpRequest.get();

                if (response.isSuccess()) {

                    JSONObject data = JsonUtil.parseObject(response.getData());
                    final JSONArray inspections = JsonUtil.getJsonArray(data, "inspections");
                    // 转换时间
                    for (int i = 0; i < inspections.size(); i++) {
                        JSONObject inspectionObj = inspections.getJSONObject(i);

                        String time = TimeUtil.utcToLocal(inspectionObj.getLongValue("time"),
                                SystemHelper.getUser().getTimeZone(),
                                TimeUtil.STANDARD_FORMAT);
                        inspectionObj.put("time", time);
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(inspections) + "');");
                        }
                    });
                } else {

                    final JSONArray inspections = new JSONArray();
                    List<InspectionLog> inspectionLogs;
                    if (type == 0) {//获取司机的

                        inspectionLogs = DataBaseHelper.getDataBase().inspectionLogDao().getByDriver(SystemHelper.getUser().getDriverId());
                    } else {

                        inspectionLogs = DataBaseHelper.getDataBase().inspectionLogDao().getByVehicle(vehicle_id);
                    }
                    for (InspectionLog inspectionLog : inspectionLogs) {

                        inspections.add(new DVIRVo(JsonUtil.parseObject(inspectionLog.getInspectionJson())));
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(inspections) + "');");
                        }
                    });
                }

            }
        });
    }

    private void getInspectionLog(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                int driverId = SystemHelper.getUser().getDriverId();
                int vehicleId = SystemHelper.getUser().getVehicleId();
                long time = new Date().getTime() - 1 * 60 * 60 * 1000;
                final InspectionLog inspectionLog = DataBaseHelper.getDataBase().inspectionLogDao().get(driverId, vehicleId, time);

                if (inspectionLog == null) {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "();");
                        }
                    });
                } else {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(inspectionLog) + "');");
                        }
                    });
                }

            }
        });
    }
}
