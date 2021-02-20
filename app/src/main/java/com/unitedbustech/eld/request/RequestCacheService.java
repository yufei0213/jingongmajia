package com.unitedbustech.eld.request;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.App;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.GpsLog;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Carrier;
import com.unitedbustech.eld.domain.entry.DailyLog;
import com.unitedbustech.eld.domain.entry.Driver;
import com.unitedbustech.eld.domain.entry.RequestCache;
import com.unitedbustech.eld.domain.entry.Rule;
import com.unitedbustech.eld.eventbus.NetworkChangeEvent;
import com.unitedbustech.eld.eventcenter.enums.DDLStatusEnum;
import com.unitedbustech.eld.eventcenter.enums.LatLngSpecialEnum;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.http.HttpFileRequest;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.http.RequestStatus;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.system.UUIDS;
import com.unitedbustech.eld.util.AppUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.LocationUtil;
import com.unitedbustech.eld.util.ThreadUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangyu
 * @date 2018/2/28
 * @description 负责上报数据
 * 1、待上报的数据到达此处后，首先缓存到数据库
 * 2、从本地数据库中读取全部需要上报的数据，这部分数据有可能是之前未能上报成功的数据
 * 3、将从本地数据库中取出来的数据进行整合和分类
 * 4、逐一上报数据
 * 5、如果上报成功，将上报成功的数据从本地缓存中删除
 */
public class RequestCacheService extends BroadcastReceiver {

    private static final String TAG = "RequestCacheService";

    /**
     * 检查数据上报的周期
     */
    private static final long CHECK_NETWORK_DURATION = 5 * 60 * 1000L;

    /**
     * 每次上报的缓存数量
     */
    public static final int QUERY_REQUEST_CACHE_LIMIT = 50;

    private Handler handler;

    /**
     * 是否离线模式
     */
    private boolean isOffline;

    /**
     * 是否是离线模式登录
     */
    private boolean isOfflineLogin;

    /**
     * 是否需要重新请求ddl
     * 如果是离线模式登录，则该值无效
     */
    private boolean isNeedRequestDdl;

    private static RequestCacheService instance = null;

    private RequestCacheService() {
    }

    public static RequestCacheService getInstance() {

        if (instance == null) {

            instance = new RequestCacheService();
        }

        return instance;
    }

    /**
     * 初始化方法。需要做的事:
     * 1，启动定时器，定时进行查询补报。
     * 2，注册监听，监听网络情况变化
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    checkNetwork();
                }
            });
            if (handler != null) {

                handler.postDelayed(this, CHECK_NETWORK_DURATION);
            }
        }
    };

    /**
     * 上传数据并开启轮询
     */
    public void startReportCache() {

        checkNetwork();

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, CHECK_NETWORK_DURATION);
    }

    /**
     * 初始化
     */
    public void init() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        App.getContext().registerReceiver(this, intentFilter);
    }

    /**
     * 销毁
     */
    public void destroy() {

        try {

            App.getContext().unregisterReceiver(this);

            if (handler != null) {

                handler.removeCallbacks(runnable);
                handler = null;
            }
        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            instance = null;
        }
    }

    /**
     * receiver方法。
     *
     * @param context Context
     * @param intent  Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        //获得ConnectivityManager对象
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //获取ConnectivityManager对象对应的NetworkInfo对象
        //获取WIFI连接的信息
        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //获取移动数据连接的信息
        NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiNetworkInfo.isConnected() || dataNetworkInfo.isConnected()) {

            handleNetworkConnected();
        } else {

            handleNetworkDisconnected();
        }
    }

    /**
     * 登录的时候如果是离线登录则调用此方法
     * 如果调用了此方法，则意味着本地数据时完整的
     */
    public void offlineLogin() {

        isOfflineLogin = true;
    }

    /**
     * 是否是离线模式登录
     *
     * @return 是否是离线模式登录
     */
    public boolean isOfflineLogin() {

        return isOfflineLogin;
    }

    /**
     * 当前是否是离线
     *
     * @return 是否是离线
     */
    public boolean isOffline() {

        return isOffline;
    }

    /**
     * 本次登录是否需要重新请求DDL
     *
     * @param needRequestDdl 是否需要重新请求DDL
     */
    public void setNeedRequestDdl(boolean needRequestDdl) {
        isNeedRequestDdl = needRequestDdl;
    }

    /**
     * 是否需要重新请求DDL
     *
     * @return 结果
     */
    public boolean isNeedRequestDdl() {
        return isNeedRequestDdl;
    }

    /**
     * 判断当前登录用户是否能够离线登录
     * <p>
     * 能够离线登录要求：
     * 本次登录信息一致与上次登录信息一致
     *
     * @param accountCarrierId 公司id
     * @param accountDriverId  司机id
     * @param password         密码
     * @return 是否能离线登录
     */
    public boolean validOffline(String accountCarrierId, String accountDriverId, String password) {

        boolean result = false;

        if (!TextUtils.isEmpty(accountCarrierId) && !TextUtils.isEmpty(accountDriverId) && !TextUtils.isEmpty(password)) {

            User user = SystemHelper.getUser();
            if (user != null) {

                String lastAccountCarrierId = user.getAccountCarrierId();
                String lastAccountDriverId = user.getAccountDriverId();
                String lastPassword = user.getPassword();

                if (!TextUtils.isEmpty(lastAccountCarrierId) && !TextUtils.isEmpty(lastAccountDriverId) && !TextUtils.isEmpty(lastPassword)) {

                    accountCarrierId = accountCarrierId.toLowerCase();
                    accountDriverId = accountDriverId.toLowerCase();
                    password = password.toLowerCase();

                    lastAccountCarrierId = lastAccountCarrierId.toLowerCase();
                    lastAccountDriverId = lastAccountDriverId.toLowerCase();
                    lastPassword = lastPassword.toLowerCase();

                    if (accountCarrierId.equals(lastAccountCarrierId) && accountDriverId.equals(lastAccountDriverId) && password.equals(lastPassword)) {

                        result = true;
                    } else {

                        Logger.w(Tags.INIT, "validOffline: user info inValid.");
                    }
                } else {

                    Logger.w(Tags.INIT, "validOffline: user.accountCarrierId || user.accountDriverId || user.password == null.");
                }
            } else {

                Logger.w(Tags.INIT, "validOffline: user == null.");
            }
        } else {

            Logger.w(Tags.INIT, "validOffline: accountCarrierId || accountDriverId || password == null.");
        }

        return result;
    }

    /**
     * 判断是否是上一台设备
     *
     * @param lastDeviceId 上一台设备的id
     * @return 判断结果
     */
    public boolean isLastDevice(String lastDeviceId) {

        boolean result = false;

        if (UUIDS.getUUID().equals(lastDeviceId)) {

            result = true;
        }

        return result;
    }

    /**
     * 校验本地数据的完整性
     * <p>
     * 返回true需要满足一下条件：
     * 1、有User信息
     * 2、有完整的司机信息
     * 3、有完整的公司信息
     * 4、有完整的司机规则
     * 5、有完整的DDL
     * 6、有完整的Profile
     * 7、DDL与Profile能够匹配
     *
     * @return 本地数据是否完整
     */
    public boolean isLocalDataAvailable() {

        boolean result = false;
        User user = SystemHelper.getUser();
        if (user != null) {

            //1、首先校验司机、公司、司机规则是否完善
            int driverId = user.getDriverId();
            int carrierId = user.getCarriedId();

            Driver driver = DataBaseHelper.getDataBase().driverDao().getDriver(driverId);
            Carrier carrier = DataBaseHelper.getDataBase().carrierDao().getCarrier(carrierId);

            int ruleId = DataBaseHelper.getDataBase().driverRuleDao().getRuleId(driverId);
            Rule rule = DataBaseHelper.getDataBase().ruleDao().getRule(ruleId);
            if (driver != null && carrier != null && rule != null) {

                //2、检验DDL是否完善
                int dailLogCount = DataBaseHelper.getDataBase().dailyLogDao().count();
                if (dailLogCount != 0) {

                    result = true;
                } else {

                    Logger.w(Tags.INIT, "isLocalDataAvailable: dailLog is not completed.");
                }
            } else {

                Logger.w(Tags.INIT, "isLocalDataAvailable: driver || carrier || rule is not completed. driverId=" + driverId + ", carrierId=" + carrierId);
            }
        } else {

            Logger.w(Tags.INIT, "isLocalDataAvailable: user == null.");
        }

        return result;
    }

    /**
     * 同步的进行可缓存的Post请求.
     * 如果当前有网络连接，则发送一次请求。
     * 如果没有网络连接，返回一个具有具有错误信息的response，
     * 同时将请求的数据进行将持久化，等待补报。
     *
     * @param httpRequest 网络请求
     * @param requestType 离线存储的事件类型
     * @param dailyLogId  dailyLog 本地数据库Id
     */
    public void cachePost(HttpRequest httpRequest, RequestType requestType, Long dailyLogId) {

        DataBaseHelper.getDataBase().requestCacheDao().insert(convertEntityByRequest(httpRequest, requestType, dailyLogId));
        checkNetwork();
    }

    /**
     * 同步的进行可缓存的Post请求.
     * 如果当前有网络连接，则发送一次请求。
     * 如果没有网络连接，返回一个具有具有错误信息的response，
     * 同时将请求的数据进行将持久化，等待补报。
     *
     * @param httpRequest 网络请求
     * @param requestType 离线存储的事件类型
     */
    public void cachePost(HttpRequest httpRequest, RequestType requestType) {

        cachePost(httpRequest, requestType, 0L);
    }

    /**
     * 同步进行可缓存的Post请求
     * 将多个请求合并成一个
     *
     * @param httpRequestList 请求列表
     * @param requestType     离线存储的事件类型
     * @param dailyLogIds     dailyLog 本地数据库Id列表
     */
    public void cachePost(List<HttpRequest> httpRequestList, RequestType requestType, List<Long> dailyLogIds) {

        List<RequestCache> requestCacheList = new ArrayList<>();
        for (int i = 0; i < httpRequestList.size(); i++) {

            HttpRequest httpRequest = httpRequestList.get(i);
            Long dailyId = dailyLogIds.get(i);

            RequestCache requestCache = convertEntityByRequest(httpRequest, requestType, dailyId);
            requestCacheList.add(requestCache);
        }

        DataBaseHelper.getDataBase().requestCacheDao().insert(requestCacheList);
        checkNetwork();
    }

    /**
     * 上报请求，上报内容包含文件
     *
     * @param httpFileRequest 请求体
     * @param requestType     类型
     */
    public void cacheUpload(HttpFileRequest httpFileRequest, RequestType requestType) {

        RequestCache requestCache = new RequestCache();
        requestCache.setUrl(httpFileRequest.getUrl());
        requestCache.setType(requestType.getCode());
        Map<String, Object> params = httpFileRequest.getParams();

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {

            if (entry.getValue() instanceof String) {

                result.put(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof File) {

                result.put(entry.getKey() + "-file", ((File) entry.getValue()).getPath());
            }
        }
        params.remove("access_token");
        requestCache.setParamJson(JsonUtil.toJSONString(result));

        DataBaseHelper.getDataBase().requestCacheDao().insert(requestCache);

        checkNetwork();
    }

    /**
     * 与服务端同步数据
     *
     * @param serverIds    需要从服务端下载的DDL id集合
     * @param localIds     需要上报给服务端的DDL id 集合
     * @param syncCallback 回调函数
     */
    public void syncWithServer(List<String> serverIds, List<String> localIds, SyncCallback syncCallback) {

        synchronized (TAG) {

            List<DailyLog> serverDailyLogs = new ArrayList<>();

            //从服务器下载本地缺失的DDL
            if (serverIds != null && !serverIds.isEmpty()) {

                Logger.i(Tags.SYNC, "request ddl from server, ddl ids=" + JsonUtil.toJSONString(serverIds.toString()));
                HttpRequest httpRequest = new HttpRequest.Builder()
                        .url(Constants.API_DDL_GET_BY_IDS)
                        .addParam("access_token", SystemHelper.getUser().getAccessToken())
                        .addParam("ids", JsonUtil.toJSONString(serverIds))
                        .build();

                HttpResponse httpResponse = httpRequest.post();
                if (httpResponse.isSuccess()) {

                    String data = httpResponse.getData();
                    Logger.i(Tags.SYNC, "request ddl from server success. data=" + data);
                    JSONObject dataObj = JsonUtil.parseObject(data);

                    JSONArray logsArray = JsonUtil.getJsonArray(dataObj, "logs");
                    for (int i = 0; i < logsArray.size(); i++) {

                        JSONObject logObj = logsArray.getJSONObject(i);

                        DailyLog dailyLog = new DailyLog();
                        dailyLog.setJson(logObj.toJSONString());
                        dailyLog.setOriginId(JsonUtil.getString(logObj, "id"));

                        serverDailyLogs.add(dailyLog);
                    }
                } else {

                    Logger.w(Tags.SYNC, "request ddl from server failed. code=" + httpResponse.getCode() + ", msg=" + httpResponse.getMsg());
                    syncCallback.onFailed();
                    return;
                }
            } else {

                Logger.i(Tags.SYNC, "don't need request ddl from server.");
            }

            //上报服务器缺失的DDL
            if (localIds != null && !localIds.isEmpty()) {

                Logger.i(Tags.SYNC, "need upload ddl to server, ids=" + JsonUtil.toJSONString(localIds));
                //最终要上报的DDL数组
                JSONArray array = new JSONArray();

                //查找本地尚未上报成功的DDL
                List<RequestCache> ddlRequestCaches = new ArrayList<>();
                for (RequestCache cache : DataBaseHelper.getDataBase().requestCacheDao().listAll()) {

                    if (cache.getType() == RequestType.DAILYLOG.getCode()) {

                        ddlRequestCaches.add(cache);
                    }
                }

                //按照创建时间排序
                Collections.sort(ddlRequestCaches);
                //本地尚未上报的DDL数组
                JSONArray cacheArray = new JSONArray();
                //找出本地尚未上报成功的DDL的数据id，并添加数据到待上报数组中
                List<String> cacheIds = new ArrayList<>();
                for (RequestCache cache : ddlRequestCaches) {

                    JSONObject jsonObject = JsonUtil.parseObject(cache.getParamJson());
                    if (jsonObject == null) {
                        continue;
                    }
                    JSONArray dataArray = JsonUtil.getJsonArray(jsonObject, "data");
                    if (dataArray == null || dataArray.isEmpty()) {
                        continue;
                    }
                    for (int i = 0; i < dataArray.size(); i++) {

                        cacheArray.add(dataArray.getJSONObject(i));
                        cacheIds.add(JsonUtil.getString(dataArray.getJSONObject(i), "id"));
                    }
                }

                Logger.i(Tags.SYNC, "request cache ddl ids=" + JsonUtil.toJSONString(cacheIds));

                //删除重复的id
                for (String cacheId : cacheIds) {

                    for (String ddlId : localIds) {

                        if (ddlId.equals(cacheId)) {

                            localIds.remove(ddlId);
                            break;
                        }
                    }
                }

                //找出服务端缺失的DDL，将数据添加到待上报数组中
                JSONArray defectsArray = new JSONArray();
                List<DailyLog> dailyLogs = DataBaseHelper.getDataBase().dailyLogDao().getDailyLogByOriginIds(localIds);
                if (dailyLogs != null && !dailyLogs.isEmpty()) {

                    for (DailyLog log : dailyLogs) {

                        JSONObject obj = JsonUtil.parseObject(log.getJson());
                        //应服务端要求，删除以下两个字段
                        obj.remove("createTime");
                        obj.remove("updateTime");
                        //状态置为1
                        obj.put("status", DDLStatusEnum.ACTIVE.getCode());
                        defectsArray.add(obj);
                    }
                }

                if (!defectsArray.isEmpty()) {

                    array.addAll(sortByOriginator(defectsArray));
                }

                if (!cacheArray.isEmpty()) {

                    array.addAll(cacheArray);
                }

                //判断是否有待上报数据
                if (array.isEmpty()) {

                    Logger.i(Tags.SYNC, "array is empty, don't need upload ddl to server.");
                    if (!serverDailyLogs.isEmpty()) {

                        Logger.i(Tags.SYNC, "store ddl which request from server.");
                        DataBaseHelper.getDataBase().dailyLogDao().insertAll(serverDailyLogs);
                    }

                    syncCallback.onSuccess();
                } else {

                    Logger.i(Tags.SYNC, "need upload ddl, " + array.toJSONString());
                    HttpRequest httpRequest = new HttpRequest.Builder()
                            .url(Constants.API_DDL_CREATE)
                            .addParam("data", array.toJSONString())
                            .addParam("access_token", SystemHelper.getUser().getAccessToken())
                            .build();

                    HttpResponse httpResponse = httpRequest.post();
                    if (httpResponse.isSuccess()) {

                        Logger.i(Tags.SYNC, "upload ddl success.");
                        if (!serverDailyLogs.isEmpty()) {

                            Logger.i(Tags.SYNC, "store ddl which request from server.");
                            DataBaseHelper.getDataBase().dailyLogDao().insertAll(serverDailyLogs);
                        }
                        if (!ddlRequestCaches.isEmpty()) {

                            Logger.i(Tags.SYNC, "delete ddl upload request cache.");
                            DataBaseHelper.getDataBase().requestCacheDao().delete(ddlRequestCaches);
                        }
                        syncCallback.onSuccess();
                    } else {

                        Logger.i(Tags.SYNC, "upload ddl failed. code" + httpResponse.getCode() + ", msg=" + httpResponse.getMsg());
                        syncCallback.onFailed();
                    }
                }
            } else {

                Logger.i(Tags.SYNC, "localIds is empty, don't need upload ddl to server.");
                if (!serverDailyLogs.isEmpty()) {

                    Logger.i(Tags.SYNC, "store ddl which request from server.");
                    DataBaseHelper.getDataBase().dailyLogDao().insertAll(serverDailyLogs);
                }

                syncCallback.onSuccess();
            }
        }
    }

    /**
     * 按照原始事件顺序排序
     *
     * @param jsonArray 时间数组
     * @return 事件数组
     */
    private JSONArray sortByOriginator(JSONArray jsonArray) {

        JSONArray result = new JSONArray();
        //首先查找相互有关联的事件
        for (int i = 0; i < jsonArray.size(); i++) {

            JSONObject obj = jsonArray.getJSONObject(i);
            String originator = obj.getString("originator");
            if (!TextUtils.isEmpty(originator)) {

                JSONArray temp = findOriginator(originator, jsonArray);
                for (int y = temp.size() - 1; y >= 0; y--) {

                    boolean hasAdded = false;
                    for (int x = 0; x < result.size(); x++) {

                        if (temp.getJSONObject(y).getString("id").equals(result.getJSONObject(x).getString("id"))) {

                            hasAdded = true;
                            break;
                        }
                    }
                    if (!hasAdded) {

                        result.add(temp.get(y));
                    }
                }
                result.add(obj);
            }
        }
        //然后将独立的事件添加到结果中
        for (int i = 0; i < jsonArray.size(); i++) {

            JSONObject obj = jsonArray.getJSONObject(i);
            boolean hasAdded = false;
            for (int y = 0; y < result.size(); y++) {

                if (obj.getString("id").equals(result.getJSONObject(y).getString("id"))) {

                    hasAdded = true;
                    break;
                }
            }

            if (!hasAdded) {

                result.add(obj);
            }
        }

        return result;
    }

    /**
     * 查找某个事件的全部原始事件
     *
     * @param originator 事件第一个原始事件的id
     * @param jsonArray  事件数组
     * @return 原始时间数组
     */
    private JSONArray findOriginator(String originator, JSONArray jsonArray) {

        JSONArray result = new JSONArray();

        int size = jsonArray.size();
        for (int i = 0; i < size; i++) {

            JSONObject obj = jsonArray.getJSONObject(i);
            String id = obj.getString("id");
            if (originator.equals(id)) {

                if (!TextUtils.isEmpty(obj.getString("originator"))) {

                    JSONArray tempResult = findOriginator(obj.getString("originator"), jsonArray);
                    if (!tempResult.isEmpty()) {

                        int tempSize = tempResult.size();
                        for (int y = tempSize - 1; y >= 0; y--) {

                            result.add(tempResult.get(y));
                        }
                    }
                } else {

                    result.add(obj);
                }
                break;
            }
        }

        return result;
    }

    /**
     * 根据原始请求，转化为缓存
     *
     * @param httpRequest 原始请求
     * @param requestType 存储的类型
     * @return RequestCache
     */
    private RequestCache convertEntityByRequest(HttpRequest httpRequest, RequestType requestType, Long dailyLogId) {

        RequestCache requestCache = new RequestCache();
        requestCache.setUrl(httpRequest.getUrl());
        requestCache.setType(requestType.getCode());
        Map<String, String> params = httpRequest.getParams();
        params.remove("access_token");
        requestCache.setParamJson(JsonUtil.toJSONString(params));
        if (dailyLogId != 0L) {

            requestCache.setDailyLogId(dailyLogId);
        }
        return requestCache;
    }

    /**
     * 检查网络状态。
     * 尝试补报数据，补报成功则将其在数据库中删除。
     */
    private void checkNetwork() {

        synchronized (TAG) {

            List<RequestCache> requestCaches = DataBaseHelper.getDataBase().requestCacheDao().listAll();
            if (requestCaches == null || requestCaches.isEmpty()) {

                Logger.d(Tags.OFFLINE, "nothing to report ,return ");
            } else {

                Logger.i(Tags.OFFLINE, "has " + requestCaches.size() + " report need to report,start to report");

                reportCache(requestCaches);
                checkLocation(requestCaches);
            }
        }
    }

    /**
     * 上报缓存数据
     *
     * @param requestCaches 数据缓存
     */
    private void reportCache(List<RequestCache> requestCaches) {

        Collections.sort(requestCaches); //按照创建时间正序排列
        List<RequestModel> requests = convertRequestsByRequestCache(requestCaches);

        Logger.d(Tags.OFFLINE, "convert request complete, request size = " + requests.size());
        boolean isNeedReportAgain = false;
        for (RequestModel httpRequest : requests) {

            if (httpRequest.requestType == RequestType.IFTA) {

                Logger.i(Tags.OFFLINE, "start upload ifta.");
                HttpResponse httpResponse = httpRequest.getHttpFileRequest().upload();
                if (httpResponse.isSuccess()) {

                    Logger.i(Tags.OFFLINE, "upload ifta success.");
                    DataBaseHelper.getDataBase().requestCacheDao().delete(httpRequest.getRequestCaches());
                } else if (httpResponse.getCode() == RequestStatus.AUTHENTICATION_FAIL.getCode()) {

                    Logger.w(Tags.OFFLINE, "Authentication fail");
                    isNeedReportAgain = true;
                } else if (httpResponse.getCode() == RequestStatus.IFTA_DELETED.getCode()) {

                    Logger.w(Tags.OFFLINE, "ifta record has been deleted, create one again.");

                    cacheUploadIfIftaDeleted(httpRequest);
                } else {

                    Logger.w(Tags.OFFLINE, "upload ifta failed, code=" + httpResponse.getCode() + ", msg=" + httpResponse.getMsg());
                }
            } else {

                HttpResponse response = httpRequest.getHttpRequest().post();

                if (httpRequest.requestType == RequestType.UNIDENTIFIED
                        || httpRequest.requestType == RequestType.DAILYLOG) {

                    Logger.i(Tags.OFFLINE, "start report dailyLog.");
                } else if (httpRequest.requestType == RequestType.GPS) {

                    Logger.i(Tags.OFFLINE, "start report gps log.");
                } else if(httpRequest.requestType == RequestType.ORIGINALHISROEY) {

                    Logger.i(Tags.OFFLINE, "start report original histroy log.");
                }

                //上报成功，从本地删除该事件。
                if (response.isSuccess()) {

                    if (httpRequest.requestType == RequestType.UNIDENTIFIED
                            || httpRequest.requestType == RequestType.DAILYLOG) {

                        printEventIds(httpRequest, true);
                    } else if (httpRequest.requestType == RequestType.GPS) {

                        Logger.i(Tags.GPS_LOG, "report gps log success");
                    } else if(httpRequest.requestType == RequestType.ORIGINALHISROEY) {

                        Logger.i(Tags.OFFLINE, "original history log success");
                    }

                    Logger.i(Tags.OFFLINE, "delete request caches.");
                    DataBaseHelper.getDataBase().requestCacheDao().delete(httpRequest.getRequestCaches());
                } else if (response.getCode() == RequestStatus.AUTHENTICATION_FAIL.getCode()) {

                    Logger.i(Tags.OFFLINE, "authentication failed, need request accessToken.");
                    isNeedReportAgain = true;
                } else {

                    if (httpRequest.requestType == RequestType.UNIDENTIFIED
                            || httpRequest.requestType == RequestType.DAILYLOG) {

                        Logger.w(Tags.OFFLINE, "report dailyLog failed, code=" + response.getCode() + ", msg=" + response.getMsg());
                        printEventIds(httpRequest, false);
                    } else if (httpRequest.requestType == RequestType.GPS) {

                        Logger.w(Tags.OFFLINE, "report gps log failed, code=" + response.getCode() + ", msg=" + response.getMsg());
                    } else if (httpRequest.requestType == RequestType.ORIGINALHISROEY) {

                        Logger.w(Tags.OFFLINE, "report original history log failed, code=" + response.getCode() + ", msg=" + response.getMsg());
                    }
                }
            }
        }

        if (isNeedReportAgain) {

            String accessToken = getAccessToken();
            if (TextUtils.isEmpty(accessToken)) {

                Logger.w(Tags.OFFLINE, "getAccessToken() == null. return;");
                //如果获取不到accessToken了，直接退出循环
                return;
            }
            User user = SystemHelper.getUser();
            user.setAccessToken(accessToken);
            SystemHelper.setUser(user);

            //获取未上报成功的缓存，重新上报
            List<RequestCache> caches = DataBaseHelper.getDataBase().requestCacheDao().listAll();
            if (caches != null && !caches.isEmpty()) {

                //递归调用
                reportCache(caches);
            }
        } else {

            Logger.i(Tags.OFFLINE, "reportCache completed.");
        }
    }

    /**
     * 如果Ifta更新请求中，记录已经被删除，则重新创建
     *
     * @param httpRequest 请求
     */
    private void cacheUploadIfIftaDeleted(RequestModel httpRequest) {

        DataBaseHelper.getDataBase().requestCacheDao().delete(httpRequest.getRequestCaches());

        HttpFileRequest request = httpRequest.getHttpFileRequest();
        request.setUrl(Constants.API_FUEL_CREATE);

        RequestCache requestCache = new RequestCache();
        requestCache.setUrl(request.getUrl());
        requestCache.setType(RequestType.IFTA.getCode());
        Map<String, Object> params = request.getParams();

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {

            if (entry.getValue() instanceof String) {

                result.put(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof File) {

                result.put(entry.getKey() + "-file", ((File) entry.getValue()).getPath());
            }
        }
        params.remove("access_token");
        requestCache.setParamJson(JsonUtil.toJSONString(result));

        DataBaseHelper.getDataBase().requestCacheDao().insert(requestCache);
    }

    /**
     * 将所有的缓存转化成一个个的真实请求。
     *
     * @param requestCaches 原始请求
     * @return 需要进行补报的请求
     */
    private List<RequestModel> convertRequestsByRequestCache(List<RequestCache> requestCaches) {

        List<RequestModel> result = new ArrayList<>();
        List<RequestCache> dailylogCacheRequests = new ArrayList<>();
        List<RequestCache> gpsCacheRequests = new ArrayList<>();
        List<RequestCache> cacheRequests = new ArrayList<>();
        List<RequestCache> uploadRequests = new ArrayList<>();
        List<RequestCache> ruleChangeRequests = new ArrayList<>();
        List<RequestCache> historyCacheRequests = new ArrayList<>();
        for (RequestCache requestCache : requestCaches) {

            if (requestCache.getType() == RequestType.OTHERS.getCode()) {

                cacheRequests.add(requestCache);
            } else if (requestCache.getType() == RequestType.GPS.getCode()) {

                gpsCacheRequests.add(requestCache);
            } else if (requestCache.getType() == RequestType.IFTA.getCode()) {

                uploadRequests.add(requestCache);
            } else if (requestCache.getType() == RequestType.RULE.getCode()) {

                ruleChangeRequests.add(requestCache);
            } else if(requestCache.getType() == RequestType.ORIGINALHISROEY.getCode()) {

                historyCacheRequests.add(requestCache);
            } else {

                dailylogCacheRequests.add(requestCache);
            }
        }
        List<RequestModel> oraRequests = convertRequestsByDailylogRequests(dailylogCacheRequests);
        List<RequestModel> gpsRequests = convertRequestsByGpsRequests(gpsCacheRequests);
        List<RequestModel> otherRequests = convertRequestByEntity(cacheRequests);
        List<RequestModel> iftaRequests = convertRequestByUploadRequests(uploadRequests);
        List<RequestModel> ruleRequests = convertRequestByRuleRequest(ruleChangeRequests);
        List<RequestModel> historyRequests = convertRequestByHistoryRequest(historyCacheRequests);

        result.addAll(oraRequests);
        result.addAll(gpsRequests);
        result.addAll(otherRequests);
        result.addAll(iftaRequests);
        result.addAll(ruleRequests);
        result.addAll(historyRequests);
        return result;
    }

    /**
     * 将请求中的upload请求转换为原始请求
     *
     * @param requestCache List<RequestCache>
     * @return List<RequestModel>
     */
    private List<RequestModel> convertRequestByUploadRequests(List<RequestCache> requestCache) {

        List<RequestModel> result = new ArrayList<>();
        for (RequestCache cache : requestCache) {

            HttpFileRequest.Builder builder = new HttpFileRequest.Builder();
            Map<String, String> params = JsonUtil.parseObject(cache.getParamJson(), Map.class);
            for (String key : params.keySet()) {

                if (key.endsWith("-file")) {

                    String tempKey = key.substring(0, key.length() - 5);
                    builder = builder.addParam(tempKey, new File(params.get(key)));
                } else {

                    builder = builder.addParam(key, params.get(key));
                }
            }
            builder = builder.addParam("access_token", SystemHelper.getUser().getAccessToken());
            builder = builder.url(cache.getUrl());
            builder = builder.ignore(RequestStatus.AUTHENTICATION_FAIL.getCode());

            result.add(new RequestModel(builder.build(),
                    RequestType.IFTA,
                    cache));
        }
        return result;
    }

    /**
     * 根据缓存，转化为原始请求
     *
     * @param requestCache List<RequestCache>
     * @return List<RequestModel>
     */
    private List<RequestModel> convertRequestByEntity(List<RequestCache> requestCache) {

        List<RequestModel> result = new ArrayList<>();
        Collections.sort(requestCache); //按照创建时间排序
        for (RequestCache cache : requestCache) {

            HttpRequest.Builder builder = new HttpRequest.Builder();
            Map<String, String> params = JsonUtil.parseObject(cache.getParamJson(), Map.class);
            for (String key : params.keySet()) {

                builder = builder.addParam(key, params.get(key));
            }
            builder = builder.addParam("access_token", SystemHelper.getUser().getAccessToken());
            builder = builder.url(cache.getUrl());
            builder = builder.ignore(RequestStatus.AUTHENTICATION_FAIL.getCode());

            result.add(new RequestModel(builder.build(),
                    RequestType.OTHERS,
                    cache));
        }

        return result;
    }

    /**
     * 将所有缓存的请求中，GPS上报的请求进行整合
     *
     * @param requestCaches List<RequestCache>
     * @return List<RequestModel>
     */
    private List<RequestModel> convertRequestsByGpsRequests(List<RequestCache> requestCaches) {

        List<RequestModel> result = new ArrayList<>();

        List<GpsLog> dataArray = new ArrayList<>();

        int cacheCount = 0;
        List<RequestCache> tempCaches = new ArrayList<>();
        for (RequestCache requestCache : requestCaches) {

            JSONObject jsonObject = JsonUtil.parseObject(requestCache.getParamJson());
            if (jsonObject == null) {
                continue;
            }
            JSONArray dataObject = JsonUtil.getJsonArray(jsonObject, "data");
            if (dataObject == null || dataObject.isEmpty()) {
                continue;
            }
            for (int i = 0; i < dataObject.size(); i++) {

                //缓存取出数据，将更新时间置为当前时间
                GpsLog gpsLog = JsonUtil.parseObject(dataObject.getString(i), GpsLog.class);
                gpsLog.setUpdateTime(new Date().getTime());
                dataArray.add(gpsLog);
            }

            tempCaches.add(requestCache);

            cacheCount += 1;
            if (cacheCount >= QUERY_REQUEST_CACHE_LIMIT) {

                if (!dataArray.isEmpty()) {

                    Collections.sort(dataArray);

                    HttpRequest.Builder builder = new HttpRequest.Builder().url(Constants.API_GPS_LOG);
                    builder.addParam("data", JsonUtil.toJSONString(dataArray));
                    builder = builder.addParam("access_token", SystemHelper.getUser().getAccessToken());
                    builder.ignore(RequestStatus.AUTHENTICATION_FAIL.getCode());

                    RequestModel request = new RequestModel(builder.build(),
                            RequestType.GPS,
                            tempCaches);
                    result.add(request);
                }
                cacheCount = 0;
                tempCaches = new ArrayList<>();
                dataArray = new ArrayList<>();
            }
        }

        if (!dataArray.isEmpty()) {

            Collections.sort(dataArray);

            HttpRequest.Builder builder = new HttpRequest.Builder().url(Constants.API_GPS_LOG);
            builder.addParam("data", JsonUtil.toJSONString(dataArray));
            builder = builder.addParam("access_token", SystemHelper.getUser().getAccessToken());
            builder.ignore(RequestStatus.AUTHENTICATION_FAIL.getCode());

            RequestModel request = new RequestModel(builder.build(),
                    RequestType.GPS,
                    tempCaches);
            result.add(request);
        }

        return result;
    }

    /**
     * 将已经缓存的所有请求中，接受daililog以及未认领日志的请求进行整合
     *
     * @param requestCaches List<RequestCache>
     * @return List<RequestModel>
     */
    private List<RequestModel> convertRequestsByDailylogRequests(List<RequestCache> requestCaches) {

        List<RequestModel> result = new ArrayList<>();

        JSONArray dataArray = new JSONArray();
        int cacheCount = 0;
        List<RequestCache> tempCaches = new ArrayList<>();
        for (RequestCache requestCache : requestCaches) {

            JSONObject jsonObject = JsonUtil.parseObject(requestCache.getParamJson());
            if (jsonObject == null) {
                continue;
            }
            JSONArray dataObject = JsonUtil.getJsonArray(jsonObject, "data");
            if (dataObject == null || dataObject.isEmpty()) {
                continue;
            }
            for (int i = 0; i < dataObject.size(); i++) {

                dataArray.add(dataObject.get(i));
            }
            tempCaches.add(requestCache);
            cacheCount += 1;
            if (cacheCount >= QUERY_REQUEST_CACHE_LIMIT) {

                if (!dataArray.isEmpty()) {

                    HttpRequest.Builder builder = new HttpRequest.Builder().url(Constants.API_DDL_CREATE);
                    builder.addParam("data", dataArray.toJSONString());
                    builder = builder.addParam("access_token", SystemHelper.getUser().getAccessToken());
                    builder.ignore(RequestStatus.AUTHENTICATION_FAIL.getCode());

                    RequestModel request = new RequestModel(builder.build(),
                            RequestType.DAILYLOG,
                            tempCaches);
                    result.add(request);
                }
                cacheCount = 0;
                tempCaches = new ArrayList<>();
                dataArray = new JSONArray();
            }
        }

        if (!dataArray.isEmpty()) {

            HttpRequest.Builder builder = new HttpRequest.Builder().url(Constants.API_DDL_CREATE);
            builder.addParam("data", dataArray.toJSONString());
            builder = builder.addParam("access_token", SystemHelper.getUser().getAccessToken());
            builder.ignore(RequestStatus.AUTHENTICATION_FAIL.getCode());

            RequestModel request = new RequestModel(builder.build(),
                    RequestType.DAILYLOG,
                    tempCaches);
            result.add(request);
        }
        return result;
    }

    /**
     * 根据缓存，转化为history请求
     *
     * @param requestCache List<RequestCache>
     * @return List<RequestModel>
     */
    private List<RequestModel> convertRequestByHistoryRequest(List<RequestCache> requestCache) {

        List<RequestModel> result = new ArrayList<>();
        for (RequestCache cache : requestCache) {

            HttpRequest.Builder builder = new HttpRequest.Builder();
            Map<String, String> params = JsonUtil.parseObject(cache.getParamJson(), Map.class);
            for (String key : params.keySet()) {

                builder = builder.addParam(key, params.get(key));
            }
            builder = builder.addParam("access_token", SystemHelper.getUser().getAccessToken());
            builder = builder.url(cache.getUrl());
            builder = builder.ignore(RequestStatus.AUTHENTICATION_FAIL.getCode());

            result.add(new RequestModel(builder.build(),
                    RequestType.ORIGINALHISROEY,
                    cache));
        }

        return result;
    }

    /**
     * 根据缓存，转化为原始请求
     *
     * @param requestCache List<RequestCache>
     * @return List<RequestModel>
     */
    private List<RequestModel> convertRequestByRuleRequest(List<RequestCache> requestCache) {

        List<RequestModel> result = new ArrayList<>();
        for (RequestCache cache : requestCache) {

            HttpRequest.Builder builder = new HttpRequest.Builder();
            Map<String, String> params = JsonUtil.parseObject(cache.getParamJson(), Map.class);
            for (String key : params.keySet()) {

                builder = builder.addParam(key, params.get(key));
            }
            builder = builder.addParam("access_token", SystemHelper.getUser().getAccessToken());
            builder = builder.url(cache.getUrl());
            builder = builder.ignore(RequestStatus.AUTHENTICATION_FAIL.getCode());

            result.add(new RequestModel(builder.build(),
                    RequestType.RULE,
                    cache));
        }

        return result;
    }

    /**
     * 上报后，输出上报的事件的id
     *
     * @param httpRequest RequestModel
     */
    private void printEventIds(RequestModel httpRequest, boolean isSuccess) {

        try {

            List<RequestCache> requestCacheSuccessList = httpRequest.getRequestCaches();

            JSONArray dataArray = new JSONArray();
            for (RequestCache requestCache : requestCacheSuccessList) {

                JSONObject jsonObject = JsonUtil.parseObject(requestCache.getParamJson());
                if (jsonObject == null) {

                    continue;
                }
                JSONArray dataObject = JsonUtil.getJsonArray(jsonObject, "data");
                if (dataObject == null || dataObject.isEmpty()) {

                    continue;
                }
                for (int i = 0; i < dataObject.size(); i++) {

                    JSONObject data = dataObject.getJSONObject(i);
                    String eventId = data.getString("id");
                    dataArray.add(eventId);
                }
            }

            if (isSuccess) {

                Logger.i(Tags.OFFLINE, "report dailyLog success event ids=" + dataArray.toString());
            } else {

                Logger.i(Tags.OFFLINE, "report dailyLog failed event ids=" + dataArray.toString());
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 从服务端获取AccessToken
     * 因为如果有离线较长时间未上报的数据，再次上报时有一定可能AccessToken已过期。
     * 因此，补报过程中，如果发生AccessToken过期的问题，则重新执行一次登录。
     *
     * @return Token
     */
    private String getAccessToken() {

        User user = SystemHelper.getUser();

        HttpRequest loginRequest = new HttpRequest.Builder()
                .url(Constants.API_LOGIN)
                .addParam("carrier_name", user.getAccountCarrierId())
                .addParam("name", user.getAccountDriverId())
                .addParam("passwd", user.getPassword())
                .addParam("dev_id", UUIDS.getUUID())
                .addParam("app_version", AppUtil.getVersionName(App.getContext()))
                .addParam("app_type", "1")
                .addParam("dev_type", "1")
                .build();

        HttpResponse loginResponse = loginRequest.post();
        if (loginResponse.isSuccess()) {

            JSONObject accessTokenObj = JsonUtil.parseObject(loginResponse.getData());
            String accessToken = JsonUtil.getString(accessTokenObj, "accessToken");
            return accessToken;
        } else {

            //登录信息错误，退出
            if (loginResponse.getCode() == RequestStatus.INVALID_CARRIER_ID.getCode() ||
                    loginResponse.getCode() == RequestStatus.INVALID_DRIVER_ID.getCode() ||
                    loginResponse.getCode() == RequestStatus.INVALID_PASSWORD.getCode()) {

                SystemHelper.loginInfoError();
            }

            if (loginResponse.getCode() == RequestStatus.LOGIN_ON_DRIVING.getCode() ||
                    loginResponse.getCode() == RequestStatus.LOGIN_NOT_DRIVING.getCode()) {

                //处理删除dailyllog的缓存
                Logger.w(Tags.OFFLINE, "user is login now! delete dailylog");
                DataBaseHelper.getDataBase().requestCacheDao().deleteByType(RequestType.DAILYLOG.code);
                SystemHelper.loginByOtherDuringOffline();
            }
            return null;
        }
    }

    /**
     * 检查地址并赋值
     *
     * @param requestCaches 数据缓存
     */
    private void checkLocation(List<RequestCache> requestCaches) {

        for (RequestCache requestCache : requestCaches) {

            if (requestCache != null && requestCache.getDailyLogId() != 0L) {

                try {

                    JSONObject requestEventModel = JsonUtil.parseObject(requestCache.getParamJson());
                    JSONArray eventModelArray = JsonUtil.getJsonArray(requestEventModel, "data");
                    String latitude = null;
                    String longitude = null;
                    if (eventModelArray != null && eventModelArray.size() > 0) {

                        latitude = JsonUtil.parseObject(eventModelArray.getString(0)).getString("latitude");
                        longitude = JsonUtil.parseObject(eventModelArray.getString(0)).getString("longitude");
                    }
                    //手动填入经纬度过滤掉
                    if (!TextUtils.isEmpty(latitude) &&
                            !latitude.toUpperCase().equals(LatLngSpecialEnum.M.getCode()) &&
                            !latitude.toUpperCase().equals(LatLngSpecialEnum.E.getCode()) &&
                            !latitude.toUpperCase().equals(LatLngSpecialEnum.X.getCode()) &&
                            !TextUtils.isEmpty(longitude) &&
                            !longitude.toUpperCase().equals(LatLngSpecialEnum.M.getCode()) &&
                            !longitude.toUpperCase().equals(LatLngSpecialEnum.E.getCode()) &&
                            !longitude.toUpperCase().equals(LatLngSpecialEnum.X.getCode())) {

                        String location = LocationUtil.getGeolocationOffline(latitude, longitude);

                        if (!TextUtils.isEmpty(location)) {

                            DailyLog dailyLog = DataBaseHelper.getDataBase().dailyLogDao().getDailylogById(requestCache.getDailyLogId());
                            if (dailyLog != null) {

                                JSONObject dailyEventModelJson = JsonUtil.parseObject(dailyLog.getJson());
                                String eventModelString = JsonUtil.updateStringValue(dailyEventModelJson, "location", location);
                                if (!TextUtils.isEmpty(eventModelString)) {

                                    dailyLog.setJson(eventModelString);
                                    DataBaseHelper.getDataBase().dailyLogDao().updateDailylog(dailyLog);
                                }
                            }
                            if (eventModelArray != null && eventModelArray.size() > 0) {

                                if (JsonUtil.parseObject(eventModelArray.getString(0)).containsKey("datetime")) {

                                    ModelCenter.getInstance().updateLocationInMemory(JsonUtil.parseObject(eventModelArray.getString(0)).getLong("datetime"), requestCache.getDailyLogId(), location);
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 处理网络变为可用的状态
     */
    private void handleNetworkConnected() {

        Logger.i(Tags.OFFLINE, "network connected.");

        isOffline = false;
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                checkNetwork();
                EventBus.getDefault().post(new NetworkChangeEvent(true));
            }
        });
    }

    /**
     * 处理网络不可用的状态
     */
    private void handleNetworkDisconnected() {

        Logger.w(Tags.OFFLINE, "network disconnected.");

        isOffline = true;
        EventBus.getDefault().post(new NetworkChangeEvent(false));
    }
}
