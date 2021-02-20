package com.unitedbustech.eld.login;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.RuleType;
import com.unitedbustech.eld.common.TeamWorkState;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.UserFunction;
import com.unitedbustech.eld.common.vo.CarrierVo;
import com.unitedbustech.eld.common.vo.DriverVo;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.CarrierRule;
import com.unitedbustech.eld.domain.entry.CarrierVehicle;
import com.unitedbustech.eld.domain.entry.DriverRule;
import com.unitedbustech.eld.domain.entry.Rule;
import com.unitedbustech.eld.domain.entry.Vehicle;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.http.RequestStatus;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.request.RequestCacheService;
import com.unitedbustech.eld.request.RequestType;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.system.UUIDS;
import com.unitedbustech.eld.util.AppUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.LocalDataStorageUtil;
import com.unitedbustech.eld.util.ThreadUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description LoginPresenter
 */
public class LoginPresenter implements LoginContract.Presenter {

    private String accessToken;
    private String carrierId;
    private String driverId;
    private String password;

    private HttpRequest loginRequest;
    private HttpRequest baseInfoRequest;

    private Context context;
    private LoginContract.View view;

    public LoginPresenter(Context context, LoginContract.View view) {

        this.context = context;
        this.view = view;

        if (this.view != null) {

            this.view.setPresenter(this);
        }
    }

    @Override
    public void onDestroy() {

        this.cancelLogin();

        this.view = null;
    }

    @Override
    public void loadUser() {

        User user = SystemHelper.getUser();
        if (user != null) {

            view.initInputValue(user);
        }
    }

    @Override
    public void login(final String carriedId, final String driverId, final String password, final boolean isForce) {

        this.view.showLoading();

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                LoginPresenter.this.carrierId = carriedId;
                LoginPresenter.this.driverId = driverId;
                LoginPresenter.this.password = password;

                loginRequest = new HttpRequest.Builder()
                        .url(Constants.API_LOGIN)
                        .addParam("carrier_name", carriedId)
                        .addParam("name", driverId)
                        .addParam("passwd", password)
                        .addParam("dev_id", UUIDS.getUUID())
                        .addParam("ignored", isForce ? "1" : "0")
                        .addParam("app_version", AppUtil.getVersionName(context))
                        .addParam("app_type", "1")
                        .addParam("dev_type", "1")
                        .addParam("interface_version", "1.3.2") // 此处随便传一个值即可，服务端取到值就会判断是否需要续费。对于老版本App，不进行是否续费的判断，可以直接登录
                        .build();

                Logger.i(Tags.INIT, "login: call login api...");
                HttpResponse loginResponse = loginRequest.post();
                if (loginResponse.getCode() != RequestStatus.SUCCESS.getCode()) {

                    //如果该司机，在其他设备上登陆过
                    if (loginResponse.getCode() == RequestStatus.LOGIN_ON_DRIVING.getCode() ||
                            loginResponse.getCode() == RequestStatus.LOGIN_NOT_DRIVING.getCode()) {

                        Logger.i(Tags.INIT, "login: login on other device. code=" + loginResponse.getCode());
                        //账号已经在其他设备登录
                        view.hideLoading();
                        view.accountOnline(loginResponse.getCode(), loginResponse.getMsg());
                    } else if (loginResponse.getCode() == RequestStatus.ACCOUNT_INVALID.getCode()) {

                        Logger.i(Tags.INIT, "use truck login, carrierId=" + carriedId + ", driverId=" + driverId);
                        //使用卡车账号登录客车App
                        view.hideLoading();
                        view.accountInvalid(loginResponse.getCode(), loginResponse.getMsg());
                    } else if (loginResponse.getCode() == RequestStatus.ACCOUNT_NORENEWALS.getCode()) {

                        Logger.i(Tags.INIT, "use no renewals, carrierId=" + carriedId + ", driverId=" + driverId);
                        //未续费禁止登陆
                        view.hideLoading();
                        view.accountInvalid(loginResponse.getCode(), context.getResources().getString(R.string.login_on_norenewals));
                    } else {

                        //如果不是用户信息校验失败
                        if (loginResponse.getCode() != RequestStatus.INVALID_CARRIER_ID.getCode() &&
                                loginResponse.getCode() != RequestStatus.INVALID_DRIVER_ID.getCode() &&
                                loginResponse.getCode() != RequestStatus.INVALID_PASSWORD.getCode()) {

                            Logger.i(Tags.INIT, "login: call login api failed. validOffline and localData.");
                            //校验本次登录的用户与上次登录的用户是否是同一个用户，检查本地数据是否完整
                            if (RequestCacheService.getInstance().validOffline(carrierId, driverId, password) &&
                                    RequestCacheService.getInstance().isLocalDataAvailable()) {

                                Logger.i(Tags.INIT, "login: offline login.");
                                //开启离线模式
                                RequestCacheService.getInstance().offlineLogin();

                                view.hideLoading();
                                view.loginSuccess();
                            } else {

                                Logger.i(Tags.INIT, "login: can't offline login, login failed. code=" + loginResponse.getCode() + ", msg=" + loginResponse.getMsg());

                                //登录失败
                                view.hideLoading();
                                view.loadFailed(loginResponse.getCode(), loginResponse.getMsg());
                            }
                        } else {

                            Logger.i(Tags.INIT, "login: login failed. code=" + loginResponse.getCode() + ", msg=" + loginResponse.getMsg());

                            //登录失败
                            view.hideLoading();
                            view.loadFailed(loginResponse.getCode(), loginResponse.getMsg());
                        }
                    }
                } else {

                    JSONObject accessTokenObj = JsonUtil.parseObject(loginResponse.getData());
                    accessToken = JsonUtil.getString(accessTokenObj, "accessToken");

                    String lastDevId = JsonUtil.getString(accessTokenObj, "lastDevId");

                    Logger.i(Tags.INIT, "login: call login api success. token=" + accessToken);

                    //登录接口请求成功，判断本次登录是否还需要重新请求ddl
                    if (RequestCacheService.getInstance().isLastDevice(lastDevId) &&
                            RequestCacheService.getInstance().validOffline(carrierId, driverId, password) &&
                            RequestCacheService.getInstance().isLocalDataAvailable()) {

                        Logger.i(Tags.INIT, "login: don't need request ddl.");
                        RequestCacheService.getInstance().setNeedRequestDdl(false);
                    } else {

                        Logger.i(Tags.INIT, "login: need request ddl.");
                        RequestCacheService.getInstance().setNeedRequestDdl(true);
                    }

                    Logger.i(Tags.INIT, "login: online login.");

                    view.loginSuccess();
                }
            }
        });
    }

    @Override
    public void loadData() {

        if (RequestCacheService.getInstance().isOfflineLogin()) {

            view.hideLoading();
            view.loadSuccess();
        } else {

            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    //请求driver信息
                    baseInfoRequest = new HttpRequest.Builder()
                            .url(Constants.API_BASE_INFO)
                            .addParam("access_token", accessToken)
                            .build();

                    Logger.i(Tags.INIT, "login: request base info.");
                    HttpResponse baseInfoResponse = baseInfoRequest.get();
                    //如果获取基本信息失败，检查本地数据是否是完整的，如果是完整的，则离线登录
                    if (baseInfoResponse.getCode() != RequestStatus.SUCCESS.getCode()) {

                        if (RequestCacheService.getInstance().isLocalDataAvailable()) {

                            Logger.i(Tags.INIT, "login: get baseInfo failed, offline login, code=" + baseInfoResponse.getCode() + ", msg=" + baseInfoResponse.getMsg());
                            RequestCacheService.getInstance().offlineLogin();
                            view.hideLoading();
                            view.loadSuccess();
                        } else {

                            Logger.i(Tags.INIT, "login: get baseInfo failed, code=" + baseInfoResponse.getCode() + ", msg=" + baseInfoResponse.getMsg());
                            view.hideLoading();
                            view.loadFailed(baseInfoResponse.getCode(), baseInfoResponse.getMsg());
                        }
                    } else {

                        //缓存driver数据
                        JSONObject baseInfoObj = JsonUtil.parseObject(baseInfoResponse.getData());
                        String driverStr = JsonUtil.getString(baseInfoObj, "driver");
                        DriverVo driverVo = JsonUtil.parseObject(driverStr, DriverVo.class);

                        String driveRuleStr = JsonUtil.getString(baseInfoObj, "driverRule");
                        DriverRule driverRule = JsonUtil.parseObject(driveRuleStr, DriverRule.class);
                        if (driverRule != null && !TextUtils.isEmpty(driveRuleStr)) {

                            driverRule.setTeamDrive(JsonUtil.getBoolean(JsonUtil.parseObject(driveRuleStr), "teamDriving") ? 1 : 0);
                        }
                        if (driverRule == null) {

                            driverRule = new DriverRule();
                            driverRule.setRuleId(driverVo.getRule().getId());
                            driverRule.setDriverId(driverVo.getId());
                            driverRule.setTeamDrive(JsonUtil.getBoolean(JsonUtil.parseObject(driveRuleStr), "teamDriving") ? 1 : 0);
                        }

                        DataBaseHelper.getDataBase().driverDao().insert(driverVo.getDriver());

                        //缓存通知开发，短信开关信息
                        JSONObject pushJson = JsonUtil.getJsonObject(baseInfoObj, "pushSwitch");
                        if(pushJson != null) {

                            LocalDataStorageUtil.putInt(Constants.NOTIFICATION_SWITCH, pushJson.getBoolean("pushNotificationOn") ? 1 : 0);
                            LocalDataStorageUtil.putInt(Constants.SMS_SWITCH, pushJson.getBoolean("pushSMSOn") ? 1 : 0);
                        }

                        //缓存carrier数据
                        String carrierStr = JsonUtil.getString(baseInfoObj, "company");
                        CarrierVo carrierVo = JsonUtil.parseObject(carrierStr, CarrierVo.class);

                        DataBaseHelper.getDataBase().carrierDao().insert(carrierVo.getCarrier());

                        List<Rule> ruleList = carrierVo.getRules();
                        DataBaseHelper.getDataBase().ruleDao().insert(ruleList);

                        List<CarrierRule> carrierRuleList = new ArrayList<>();
                        for (Rule rule : ruleList) {

                            CarrierRule carrierRule = new CarrierRule();
                            carrierRule.setCarrierId(carrierVo.getId());
                            carrierRule.setRuleId(rule.getId());

                            carrierRuleList.add(carrierRule);
                        }

                        DataBaseHelper.getDataBase().carrierRuleDao().insert(carrierRuleList);

                        User user = null;
                        if (RequestCacheService.getInstance().isNeedRequestDdl()) {

                            user = new User(carrierVo.getId(),
                                    driverVo.getId(),
                                    driverVo.getTimeZone(),
                                    driverVo.getTimeZoneAlias(),
                                    carrierId,
                                    driverId,
                                    password,
                                    accessToken);
                        } else {

                            user = SystemHelper.getUser();
                            user.setTimeZone(driverVo.getTimeZone());
                            user.setTimeZoneAlias(driverVo.getTimeZoneAlias());
                            user.setAccessToken(accessToken);
                        }

                        //记录当前用户属于豁免模式的何种类型
                        if (driverVo.getExemptDays() == 1 && driverVo.getExemptMile() == 1) {

                            user.setFunction(UserFunction.ALL);
                        } else {

                            if (driverVo.getExemptDays() == 1) {

                                user.setFunction(UserFunction.DAYS_EXEMPTION);
                            }

                            if (driverVo.getExemptMile() == 1) {

                                user.setFunction(UserFunction.MILES_EXEMPTION);
                            }

                            if (driverVo.getExemptMile() == 0 && driverVo.getExemptDays() == 0) {

                                user.setFunction(UserFunction.NORMAL);
                            }
                        }

                        //根据服务器返回的ruleID取出本地存储相对应的rule存到数据库中
                        String ruleStr = AppUtil.readAssetsFile(context, "data/rules.txt");
                        //豁免模式下不存在加拿大规则,如果是加拿大规则修改成第一个客车规则，上传服务器并且本地生成事件
                        if (user.getFunction() != UserFunction.NORMAL) {

                            if (driverRule.getRuleId() == RuleType.CANADA_7D_70H || driverRule.getRuleId() == RuleType.CANADA_14D_120H) {

                                driverRule.setRuleId(RuleType.CAR_7D_60H);
                                TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();
                                HttpRequest httpRequest = new HttpRequest.Builder()
                                        .url(Constants.API_CHANGE_RULE)
                                        .addParam("access_token", user.getAccessToken())
                                        .addParam("rule_id", String.valueOf(driverRule.getRuleId()))
                                        .addParam("team_driving", String.valueOf(teamWorkState == null ? 0 : 1))
                                        .addParam("datetime", String.valueOf(new Date().getTime()))
                                        .build();
                                RequestCacheService.getInstance().cachePost(httpRequest, RequestType.RULE);

                                //生成事件
                                EventCenter.getInstance().newRuleEvent(driverRule.getRuleId());
                            }
                        }
                        if (!TextUtils.isEmpty(ruleStr) && driverRule != null) {

                            JSONArray jsonArray = JsonUtil.parseArray(ruleStr);
                            if (jsonArray != null && jsonArray.size() > 0) {

                                for (int i = 0; i < jsonArray.size(); i++) {

                                    Rule rule = JsonUtil.parseObject(jsonArray.getString(i), Rule.class);
                                    if (driverRule.getRuleId() == rule.getId()) {

                                        driverVo.setRule(rule);
                                        break;
                                    }
                                }
                            }
                        }

                        DataBaseHelper.getDataBase().ruleDao().insert(driverVo.getRule());

                        DataBaseHelper.getDataBase().driverRuleDao().insert(driverRule);
                        //登录成功，缓存user
                        SystemHelper.setUser(user);

                        //缓存车辆信息
                        JSONArray vehicleArray = JsonUtil.getJsonArray(baseInfoObj, "vehicles");

                        List<Vehicle> vehicleList = JsonUtil.parseArray(JsonUtil.toJSONString(vehicleArray), Vehicle.class);

                        DataBaseHelper.getDataBase().vehicleDao().deleteAll();
                        DataBaseHelper.getDataBase().vehicleDao().insert(vehicleList);

                        List<CarrierVehicle> carrierVehicleList = new ArrayList<>();
                        for (Vehicle vehicle : vehicleList) {

                            CarrierVehicle carrierVehicle = new CarrierVehicle();
                            carrierVehicle.setCarrierId(user.getCarriedId());
                            carrierVehicle.setVehicleId(vehicle.getId());

                            carrierVehicleList.add(carrierVehicle);
                        }

                        DataBaseHelper.getDataBase().carrierVehicleDao().insert(carrierVehicleList);

                        Logger.i(Tags.INIT, "login: get baseInfo success.");
                        view.hideLoading();
                        view.loadSuccess();
                    }
                }
            });
        }
    }

    @Override
    public void cancelLogin() {

        if (loginRequest != null) {

            loginRequest.cancel();
        }

        if (baseInfoRequest != null) {

            baseInfoRequest.cancel();
        }
    }
}
