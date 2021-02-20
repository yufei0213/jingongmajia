package com.unitedbustech.eld.hos.core;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.vo.DailyLogDataHeadVo;
import com.unitedbustech.eld.dailylog.model.Profile;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Carrier;
import com.unitedbustech.eld.domain.entry.DailyLog;
import com.unitedbustech.eld.domain.entry.ProfileEntity;
import com.unitedbustech.eld.domain.entry.SignCache;
import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.eventcenter.enums.DDLStatusEnum;
import com.unitedbustech.eld.eventcenter.enums.EventItem;
import com.unitedbustech.eld.eventcenter.model.EventModel;
import com.unitedbustech.eld.eventcenter.model.SelfCheckModel;
import com.unitedbustech.eld.hos.model.HosDayModel;
import com.unitedbustech.eld.hos.model.HosDriveEventModel;
import com.unitedbustech.eld.hos.model.HosEventModel;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.request.RequestCacheService;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyu
 * @date 2018/1/16
 * @description 数据转化加载器
 */
public class DataTransformer {

    /**
     * 加载网络数据或者本地数据，用于初始化内存模型
     *
     * @param callback 回调
     */
    public void init(final InitResult callback) {

        List<HosEventModel> hosEventModels = new ArrayList<>();
        List<Profile> profiles = new ArrayList<>();
        List<String> signs = new ArrayList<>();

        //如果不是离线登录
        if (!RequestCacheService.getInstance().isOfflineLogin()) {

            //如果需要重新请求DDL，则Profile也需要重新请求
            if (RequestCacheService.getInstance().isNeedRequestDdl()) {

                Logger.i(Tags.INIT, "load ddl from server...");
                //从服务端请求DDL
                List<HosEventModel> ddl = requestDdl();
                if (ddl == null) {

                    Logger.i(Tags.INIT, "load ddl failed. initFailed.");
                    //请求DDL失败
                    callback.initFailure();
                    return;
                }

                Logger.i(Tags.INIT, "load profile from server...");
                //从服务端请求表头
                List<Profile> profileList = requestProfile();
                if (profileList == null) {

                    Logger.i(Tags.INIT, "load profile failed. initFailed.");
                    //请求Profile失败
                    callback.initFailure();
                    return;
                }

                Logger.i(Tags.INIT, "load sign status from server...");
                List<SignCache> signCaches = requestSignModel();
                if (signCaches == null) {

                    Logger.i(Tags.INIT, "load sign status failed. initFailed.");
                    //请求Profile失败
                    callback.initFailure();
                    return;
                }
            } else {

                Logger.i(Tags.INIT, "load profile from server...");
                //从服务端请求表头
                List<Profile> profileList = requestProfile();
                if (profileList == null) {

                    Logger.i(Tags.INIT, "load profile failed.");
                }
            }
        }

        Logger.i(Tags.INIT, "load ddl from local...");
        //从本地加载DDL
        hosEventModels.addAll(loadLocalDdl());

        Logger.i(Tags.INIT, "load profile from local...");
        //加载表头
        profiles.addAll(loadProfiles());

        Logger.i(Tags.INIT, "load sign from local...");
        //加载签名
        signs.addAll(loadSign());

        Logger.i(Tags.INIT, "convert memory model...");
        List<HosDayModel> hosDayModels = convertHosDayModel(hosEventModels);
        hosDayModels = mergeDdlAndProfile(hosDayModels, profiles, signs);

        Logger.i(Tags.INIT, "init over...");
        //初始化完成
        callback.initData(hosDayModels);

        Logger.i(Tags.INIT, "report cache...");
        //补报数据
        RequestCacheService.getInstance().startReportCache();
    }

    interface InitResult {

        void initData(List<HosDayModel> result);

        void initFailure();
    }

    /**
     * 根据本地数据库数据，刷新内存模型
     *
     * @return List<HosDayModel>
     */
    public List<HosDayModel> reloadDayModels() {

        List<HosDayModel> hosDayModels = convertHosDayModel(loadLocalDdl());
        List<Profile> profiles = loadProfiles();
        List<String> signs = loadSign();

        mergeDdlAndProfile(hosDayModels, profiles, signs);
        return hosDayModels;
    }

    /**
     * 如果跨天时，重新获取模型
     *
     * @return List<HosDayModel>
     */
    public List<HosDayModel> refreshDayModels() {

        List<HosDayModel> hosDayModels = convertHosDayModel(loadLocalDdl());
        List<Profile> profiles = loadProfiles();
        List<String> signs = loadSign();

        //如果是跨天，将最新一天的表头取出来，复制前一天的表头
        Profile lastProfile = profiles.get(0);
        String lastProfileDate = lastProfile.getDailyLogDataHeadVo().getRecordDate();

        Profile beforeLastProfile = profiles.get(1);

        lastProfile = new Profile(beforeLastProfile);
        DailyLogDataHeadVo dailyLogDataHeadVo = lastProfile.getDailyLogDataHeadVo();
        dailyLogDataHeadVo.setStartEndOdometer("0");
        dailyLogDataHeadVo.setStartEndEngineHours("0.0");
        dailyLogDataHeadVo.setRecordDate(lastProfileDate);

        profiles.remove(0);
        profiles.add(0, lastProfile);

        mergeDdlAndProfile(hosDayModels, profiles, signs);
        return hosDayModels;
    }

    /**
     * 根据所有事件，分析出每天的内存模型
     *
     * @param hosEventModels 事件列表
     * @return List<HosDayModel>
     */
    private List<HosDayModel> convertHosDayModel(List<HosEventModel> hosEventModels) {

        Date now = new Date();
        Date today = TimeUtil.getDayBegin(now);

        List<HosDayModel> result = new ArrayList<>();
        for (int i = 0; i < Constants.DDL_DAYS; i++) {

            Date date = TimeUtil.getPreviousDate(today, i);
            HosDayModel hosDayModel = new HosDayModel();
            hosDayModel.setDate(date);

            List<HosEventModel> removeList = new ArrayList<>();
            for (int m = 0; m < hosEventModels.size(); m++) {

                HosEventModel model = hosEventModels.get(m);
                //如果是本天的事件
                if (date.getTime() <= model.getDate().getTime() &&
                        (model.getDate().getTime() - date.getTime()) < TimeUtil.getTotalHours(date) * 60 * 60 * 1000L) {

                    //添加到移除列表中
                    removeList.add(model);

                    //如果是今天的，过滤未来事件
                    if (i == 0 && model.getDate().getTime() > now.getTime()) {

                        continue;
                    }
                    //如果是驾驶事件
                    if (model instanceof HosDriveEventModel) {

                        hosDayModel.getHosDriveEventModels().add((HosDriveEventModel) model);
                    } else {

                        hosDayModel.getHosEventModels().add(model);
                    }
                }
            }

            //移除已经添加到天模型的事件
            hosEventModels.removeAll(removeList);

            hosDayModel.sortHosDriveEvent();
            hosDayModel.sortHosEvent();

            result.add(hosDayModel);
        }
        DriverState lastState = null;
        int lastOrigin = 0;

        int size = result.size();

        HosDayModel lastDayModel = result.get(size - 1);
        List<HosDriveEventModel> lastDayHosDriveEventList = lastDayModel.getHosDriveEventModels();
        boolean needInitEvent = false;
        if (lastDayHosDriveEventList.isEmpty()) {

            needInitEvent = true;
        } else {

            HosDriveEventModel firstEvent = lastDayHosDriveEventList.get(lastDayHosDriveEventList.size() - 1);
            if (firstEvent.getDate().getTime() != TimeUtil.getDayBegin(lastDayModel.getDate()).getTime()) {

                needInitEvent = true;
            }
        }
        if (needInitEvent && !hosEventModels.isEmpty()) {

            for (HosEventModel hosEventModel : hosEventModels) {

                if (hosEventModel instanceof HosDriveEventModel && hosEventModel.getDate().getTime() < lastDayModel.getDate().getTime()) {

                    HosDayModel hosDayModel = new HosDayModel();
                    hosDayModel.setDate(TimeUtil.getDayBegin(hosEventModel.getDate()));
                    hosDayModel.getHosDriveEventModels().add((HosDriveEventModel) hosEventModel);
                    hosDayModel.sortHosDriveEvent();
                    result.add(hosDayModel);
                    break;
                }
            }
        }

        //设置初始状态
        for (int i = result.size(); i > 0; i--) {

            HosDayModel dayModel = result.get(i - 1);
            List<HosDriveEventModel> hosDriveEventModels = dayModel.getHosDriveEventModels();

            if (lastState == null) {

                if (hosDriveEventModels.isEmpty()) {

                    dayModel.setStartDriverState(DriverState.OFF_DUTY);
                    dayModel.setStartEventOrigin(DDLOriginEnum.EDIT_BY_DRIVER.getCode());
                } else {

                    HosDriveEventModel firstEvent = hosDriveEventModels.get(hosDriveEventModels.size() - 1);
                    if (firstEvent.getDate().getTime() == TimeUtil.getDayBegin(dayModel.getDate()).getTime()) {

                        dayModel.setStartDriverState(firstEvent.getDriverState());
                        dayModel.setStartEventOrigin(firstEvent.getOrigin());
                    } else {

                        dayModel.setStartDriverState(DriverState.OFF_DUTY);
                        dayModel.setStartEventOrigin(DDLOriginEnum.EDIT_BY_DRIVER.getCode());
                    }
                }
            } else {

                if (hosDriveEventModels.isEmpty()) {

                    dayModel.setStartDriverState(lastState);
                    dayModel.setStartEventOrigin(lastOrigin);
                } else {

                    HosDriveEventModel firstEvent = hosDriveEventModels.get(hosDriveEventModels.size() - 1);
                    if (firstEvent.getDate().getTime() == TimeUtil.getDayBegin(dayModel.getDate()).getTime()) {

                        dayModel.setStartDriverState(firstEvent.getDriverState());
                        dayModel.setStartEventOrigin(firstEvent.getOrigin());
                    } else {

                        dayModel.setStartDriverState(lastState);
                        dayModel.setStartEventOrigin(lastOrigin);
                    }
                }
            }
            DriverState preDayLastEventState = null; //前一天最后一个事件的状态
            if (i < size) {

                List<HosDriveEventModel> preDayHosEventModelList = result.get(i).getHosDriveEventModels();
                if (preDayHosEventModelList.isEmpty()) {

                    preDayLastEventState = lastState;
                } else {

                    preDayLastEventState = preDayHosEventModelList.get(0).getDriverState();
                }
            } else {

                preDayLastEventState = DriverState.OFF_DUTY;
            }
            //设置隐藏事件
            dayModel.resetHideCase(preDayLastEventState);
            if (hosDriveEventModels.isEmpty()) {

                lastState = dayModel.getStartDriverState();
                lastOrigin = dayModel.getStartEventOrigin();
            } else {

                lastState = dayModel.getHosDriveEventModels().get(0).getDriverState();
                lastOrigin = dayModel.getHosDriveEventModels().get(0).getOrigin();
            }
        }
        return result;
    }

    /**
     * 将DDL与表头合并
     *
     * @param hosDayModels DDL
     * @param profiles     表头
     * @return List<HosDayModel>
     */
    private List<HosDayModel> mergeDdlAndProfile(List<HosDayModel> hosDayModels, List<Profile> profiles, List<String> signs) {

        for (int i = 0; i < Constants.DDL_DAYS; i++) {

            hosDayModels.get(i).setProfile(profiles.get(i));
            hosDayModels.get(i).setSign(signs.get(i));
        }
        return hosDayModels;
    }

    /**
     * 加载本地存储的DDL
     *
     * @return List<HosEventModel>
     */
    private List<HosEventModel> loadLocalDdl() {

        List<HosEventModel> result = new ArrayList<>();

        List<DailyLog> dailyLogs = DataBaseHelper.getDataBase().dailyLogDao().listAll();

        if (dailyLogs != null && !dailyLogs.isEmpty()) {

            for (DailyLog dailyLog : dailyLogs) {

                EventModel eventModel = JsonUtil.parseObject(dailyLog.getJson(), EventModel.class);
                //对自检事件进行特殊处理
                if (eventModel.getType() == EventItem.SELF_CHECK.getCode()) {

                    eventModel = JsonUtil.parseObject(dailyLog.getJson(), SelfCheckModel.class);
                }
                //判断是否是有效的事件，不是有效的直接不处理
                if (eventModel.getStatus() != DDLStatusEnum.ACTIVE.getCode() && EventItem.isDriverEvent(eventModel.getType())) {

                    continue;
                }
                //不处理豁免模式的事件
                if (eventModel.getType() == EventItem.EXEMPTION_MODE.getCode()) {

                    continue;
                }
                //不处理特殊驾驶事件的status clear事件
                if (eventModel.getType() == EventItem.SPECIAL_WORK_STATE.getCode() && eventModel.getCode() == 0) {

                    continue;
                }
                HosEventModel hosEventModel = eventModel.convertLocalHosModel();
                hosEventModel.setLocalId(dailyLog.getId());
                result.add(hosEventModel);
            }

            //按照日期排序
            Collections.sort(result);
        }

        return result;
    }

    /**
     * 加载本地的表头
     *
     * @return List<Profile>
     */
    private List<Profile> loadProfiles() {

        List<Profile> result = new ArrayList<>();
        List<ProfileEntity> profileEntities = DataBaseHelper.getDataBase().profileDao().listAll();
        for (int i = 0; i < profileEntities.size(); i++) {

            result.add(JsonUtil.parseObject(profileEntities.get(i).getProfileJson(), Profile.class));
        }

        User user = SystemHelper.getUser();

        Date today = TimeUtil.getDayBegin(new Date());

        //构建表头需要的日期
        List<Date> targetDateList = new ArrayList<>();
        for (int i = 0; i < Constants.DDL_DAYS; i++) {

            Date previousDate = TimeUtil.getPreviousDate(today, Constants.DDL_DAYS - (i + 1));
            targetDateList.add(previousDate);
        }

        Logger.i(Tags.INIT, "profile dateList: " + JsonUtil.toJSONString(targetDateList));

        //根据表头查找缺失的日期，排除无效的表头
        List<Profile> availableProfileList = new ArrayList<>();
        List<Date> availableProfileDateList = new ArrayList<>();
        for (Date date : targetDateList) {

            for (Profile profile : result) {

                String dateStr = "";
                DailyLogDataHeadVo dailyLogDataHeadVo = profile.getDailyLogDataHeadVo();
                if (dailyLogDataHeadVo != null) {

                    dateStr = dailyLogDataHeadVo.getRecordDate();
                }

                //如果有日期
                if (!TextUtils.isEmpty(dateStr)) {

                    Date profileDate = TimeUtil.strToDate(dateStr, TimeUtil.MM_DD_YY, user.getTimeZone());
                    if (TimeUtil.isSameDay(profileDate, date)) {

                        availableProfileList.add(profile);
                        availableProfileDateList.add(date);
                    }
                }
            }
        }

        Logger.i(Tags.INIT, "available profile size=" + availableProfileList.size());

        //排除无效表头
        result.clear();
        result.addAll(availableProfileList);

        //移除有效日期，得到缺失表头的日期
        targetDateList.removeAll(availableProfileDateList);

        //查询公司信息
        Carrier carrier = DataBaseHelper.getDataBase().carrierDao().getCarrier(user.getCarriedId());

        //构建缺失的表头
        for (Date date : targetDateList) {

            Profile newProfile = new Profile();
            DailyLogDataHeadVo dailyLogDataHeadVo = newProfile.getDailyLogDataHeadVo();

            dailyLogDataHeadVo.setRecordDate(TimeUtil.dateToStr(date, TimeUtil.MM_DD_YY));
            dailyLogDataHeadVo.setCarrier(carrier.getName());

            result.add(newProfile);
        }

        //按照日期排序
        Collections.sort(result);

        return result;
    }

    /**
     * 本地加载签名
     *
     * @return 15天签名，倒序排列
     */
    private List<String> loadSign() {

        List<String> result = new ArrayList<>();

        Date today = TimeUtil.getDayBegin(new Date());

        //构建表头需要的日期
        List<String> targetDateList = new ArrayList<>();
        for (int i = 0; i < Constants.DDL_DAYS; i++) {

            Date previousDate = TimeUtil.getPreviousDate(today, Constants.DDL_DAYS - (i + 1));
            targetDateList.add(TimeUtil.dateToStr(previousDate, TimeUtil.MM_DD_YY));
        }

        Logger.i(Tags.INIT, "sign dateList: " + JsonUtil.toJSONString(targetDateList));

        List<SignCache> signCaches = DataBaseHelper.getDataBase().signCacheDao().listAll();
        int driverId = SystemHelper.getUser().getDriverId();
        for (int i = Constants.DDL_DAYS - 1; i >= 0; i--) {

            String dateStr = targetDateList.get(i);
            SignCache targetSign = null;
            for (SignCache signCache : signCaches) {

                if (signCache.getDriverId() == driverId && !TextUtils.isEmpty(signCache.getDate()) && signCache.getDate().equals(dateStr) && signCache.getStatus() == 1) {

                    targetSign = signCache;
                    break;
                }
            }
            if (targetSign != null && !TextUtils.isEmpty(targetSign.getSign())) {

                result.add(targetSign.getSign());
            } else {

                result.add("");
            }
        }

        return result;
    }

    /**
     * 从服务端获取DDL
     *
     * @return List<HosEventModel>
     */
    private List<HosEventModel> requestDdl() {

        List<HosEventModel> result = new ArrayList<>();
        HttpRequest httpRequest = new HttpRequest.Builder()
                .url(Constants.API_DDL_LIST)
                .addParam("access_token", SystemHelper.getUser().getAccessToken())
                .addParam("days", Integer.toString(Constants.DDL_DAYS))
                .build();

        HttpResponse httpResponse = httpRequest.get();
        if (httpResponse.isSuccess()) {

            String data = httpResponse.getData();
            JSONArray jsonArray = JSON.parseObject(data).getJSONArray("logs");

            List<String> oraJsonArray = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {

                String jsonString = jsonArray.get(i).toString();
                EventModel eventModel = JsonUtil.parseObject(jsonString, EventModel.class);
                //对自检事件进行特殊处理
                if (eventModel.getType() == EventItem.SELF_CHECK.getCode()) {

                    eventModel = JsonUtil.parseObject(jsonString, SelfCheckModel.class);
                }
                //判断是否是request事件，如果是，则不处理
                if (eventModel.getStatus() == DDLStatusEnum.INACTIVE_CHANGE_REQUESTED.getCode() ||
                        eventModel.getStatus() == DDLStatusEnum.INACTIVE_CHANGE_REJECTED.getCode()) {

                    continue;
                }
                oraJsonArray.add(jsonString);
                result.add(eventModel.convertLocalHosModel());
            }

            //入库，必须先入库再排序，否则会影响内存模型与本地数据库的映射关系
            recordToDatabase(oraJsonArray, result);

            //按照日期排序
            Collections.sort(result);
        } else {

            result = null;
        }

        return result;
    }

    /**
     * 将数据记录至数据库
     *
     * @param jsonString     原始数据
     * @param hosEventModels 事件模型
     */
    private void recordToDatabase(List<String> jsonString, List<HosEventModel> hosEventModels) {

        DataBaseHelper.getDataBase().dailyLogDao().deleteAll();

        List<DailyLog> dailyLogs = new ArrayList<>();
        for (int i = 0; i < jsonString.size(); i++) {

            DailyLog dailyLog = new DailyLog();
            dailyLog.setJson(jsonString.get(i));
            EventModel eventModel = JsonUtil.parseObject(jsonString.get(i), EventModel.class);
            dailyLog.setOriginId(eventModel.getId());
            dailyLogs.add(dailyLog);
        }

        List<Long> ids = DataBaseHelper.getDataBase().dailyLogDao().insertAll(dailyLogs);

        for (int i = 0; i < ids.size(); i++) {

            hosEventModels.get(i).setLocalId(ids.get(i));
        }
    }

    /**
     * 从服务端获取表头信息
     * 返回表头为15天
     *
     * @return List<Profile>
     */
    private List<Profile> requestProfile() {

        List<Profile> result = new ArrayList<>();

        HttpRequest httpRequest = new HttpRequest.Builder()
                .url(Constants.API_GET_PROFILE)
                .addParam("access_token", SystemHelper.getUser().getAccessToken())
                .build();

        HttpResponse httpResponse = httpRequest.get();
        if (httpResponse.isSuccess()) {

            JSONObject jsonObject = JsonUtil.parseObject(httpResponse.getData());

            JSONArray jsonArray = null;
            if (jsonObject != null) {

                jsonArray = jsonObject.getJSONArray("header_list");
            }
            if (jsonArray != null && !jsonArray.isEmpty()) {

                List<ProfileEntity> databaseModel = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {

                    JSONObject profileObject = jsonArray.getJSONObject(i);
                    Profile profile = Profile.getProfileByServerJson(profileObject);
                    result.add(profile);

                    ProfileEntity profileEntity = new ProfileEntity();
                    profileEntity.setProfileJson(JsonUtil.toJSONString(profile));
                    profileEntity.setDate(profileObject.getString("recordDate"));
                    databaseModel.add(profileEntity);
                }

                DataBaseHelper.getDataBase().profileDao().deleteAll();
                DataBaseHelper.getDataBase().profileDao().insert(databaseModel);
            }
        } else {

            result = null;
        }

        return result;
    }

    /**
     * 从服务端获取签名信息
     * 默认15天
     *
     * @return 15天的签名
     */
    private List<SignCache> requestSignModel() {

        List<SignCache> result = new ArrayList<>();

        HttpRequest httpRequest = new HttpRequest.Builder()
                .url(Constants.API_GET_SIGN)
                .addParam("access_token", SystemHelper.getUser().getAccessToken())
                .build();

        HttpResponse httpResponse = httpRequest.get();
        if (httpResponse.isSuccess()) {

            JSONObject jsonObject = JsonUtil.parseObject(httpResponse.getData());
            if (jsonObject != null) {

                JSONArray signatures = jsonObject.getJSONArray("list");
                if (signatures != null && !signatures.isEmpty()) {

                    int driverId = SystemHelper.getUser().getDriverId();
                    for (int i = 0; i < signatures.size(); i++) {

                        String signStr = signatures.getJSONObject(i).getString("content");

                        SignCache sign = new SignCache();
                        sign.setSign(signStr);
                        sign.setDate(signatures.getJSONObject(i).getString("date"));
                        sign.setStatus(signatures.getJSONObject(i).getInteger("status"));
                        sign.setDriverId(driverId);
                        result.add(sign);
                    }

                    DataBaseHelper.getDataBase().signCacheDao().deleteAll();
                    DataBaseHelper.getDataBase().signCacheDao().insert(result);
                }
            }
        } else {

            result = null;
        }

        return result;
    }
}
