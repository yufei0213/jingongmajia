package com.unitedbustech.eld.jsinterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.activity.ActivityStack;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.TeamWorkState;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.vo.CarrierVo;
import com.unitedbustech.eld.common.vo.DriverVo;
import com.unitedbustech.eld.domain.AppDatabase;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Carrier;
import com.unitedbustech.eld.domain.entry.Driver;
import com.unitedbustech.eld.domain.entry.Rule;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.login.LoginActivity;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.view.UIWebView;
import com.unitedbustech.eld.web.BaseJsInterface;
import com.unitedbustech.eld.web.JsInterface;

import java.util.List;

/**
 * @author yufei0213
 * @date 2018/1/19
 * @description 用户相关接口。通过此接口获取当前用户信息，司机信息以及司机所属公司信息等
 */
@JsInterface(name = "user")
public class UserJsInterface extends BaseJsInterface {

    private static final String TAG = "UserJsInterface";

    private static final String GET_USER = "getUser";
    private static final String GET_DRIVER = "getDriver";
    private static final String GET_USERROLE = "getUserRole";
    private static final String GET_USERFUNC = "getUserFunc";
    private static final String GET_CARRIER = "getCarrier";
    private static final String UPDATE_DRIVER_INFO = "updateDriverInfo";
    private static final String UPDATE_PASSWORD = "updatePassword";
    private static final String GET_DRIVER_NOTCERTIFIED_ALERT_COUNT = "getDriverNotCertifiedAlertCount";
    private static final String LOGOUT_REQUEST = "logoutRequest";
    private static final String LOGOUT_LOCAL = "logoutLocal";

    public UserJsInterface(Context context, UIWebView uiWebView) {

        super(context, uiWebView);
    }

    @Override
    @JavascriptInterface
    public void method(String params) {

        JSONObject obj = JsonUtil.parseObject(params);

        String method = JsonUtil.getString(obj, METHOD_KEY);
        String data = JsonUtil.getString(obj, DATA_KEY);

        switch (method) {

            case GET_USER:

                getUser(data);
                break;
            case GET_DRIVER:

                getDriver(data);
                break;
            case GET_USERROLE:

                getUserRole(data);
                break;
            case GET_USERFUNC:

                getUserFunc(data);
                break;
            case GET_CARRIER:

                getCarrier(data);
                break;
            case UPDATE_DRIVER_INFO:

                updateDriverInfo(data);
                break;
            case UPDATE_PASSWORD:

                updatePassword(data);
                break;
            case GET_DRIVER_NOTCERTIFIED_ALERT_COUNT:

                getDriverNotCertifiedAlertCount(data);
                break;
            case LOGOUT_REQUEST:

                logoutRequest(data);
                break;
            case LOGOUT_LOCAL:

                logoutLocal();
                break;
            default:
                break;
        }
    }

    /**
     * 获取user
     *
     * @param data 回调函数
     */
    private void getUser(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        User user = SystemHelper.getUser();
        final String userStr = JsonUtil.toJsJSONString(user);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + userStr + "');");
            }
        });
    }

    /**
     * 获取用户角色
     * <p>
     * normal
     * pilot
     * copilot
     *
     * @param data 回调函数
     */
    private void getUserRole(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + teamWorkState.getUserRole() + "');");
            }
        });
    }

    /**
     * 获取用户权限
     * <p>
     * normal
     * days_exemption
     * miles_exemption
     * all
     *
     * @param data 回调函数
     */
    private void getUserFunc(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final int userFunction = SystemHelper.getExemptionFunc();

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + userFunction + "');");
            }
        });
    }

    /**
     * 获取司机信息
     *
     * @param data 回调函数
     */
    private void getDriver(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                try {

                    AppDatabase database = DataBaseHelper.getDataBase();

                    int driverId = SystemHelper.getUser().getDriverId();
                    Logger.i(Tags.SYSTEM, "USER.getDriver: driverId = " + driverId);
                    Driver driver = database.driverDao().getDriver(driverId);
                    Logger.i(Tags.SYSTEM, "USER.getDriver: driver = " + driver == null ? "null" : "!null");
                    int ruleId = database.driverRuleDao().getRuleId(driverId);
                    Logger.i(Tags.SYSTEM, "USER.getDriver: ruleId = " + ruleId);
                    Rule rule = database.ruleDao().getRule(ruleId);
                    Logger.i(Tags.SYSTEM, "USER.getDriver: rule = " + rule == null ? "null" : "!null");

                    DriverVo driverVo = new DriverVo(driver, rule);
                    final String driverStr = JsonUtil.toJsJSONString(driverVo);

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + driverStr + "');");
                        }
                    });
                } catch (Exception e) {

                    Logger.e(Tags.SYSTEM, "User.getDriver: " + e.toString());
                }
            }
        });
    }

    /**
     * 获取公司信息
     *
     * @param data 回调函数
     */
    private void getCarrier(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                final AppDatabase database = DataBaseHelper.getDataBase();

                int carrierId = SystemHelper.getUser().getCarriedId();
                Carrier carrier = database.carrierDao().getCarrier(carrierId);
                List<Integer> ruleIds = database.carrierRuleDao().getRuleIdList(carrierId);
                List<Rule> ruleList = database.ruleDao().getRuleList(ruleIds);

                CarrierVo carrierVo = new CarrierVo(carrier, ruleList);
                final String carrierStr = JsonUtil.toJsJSONString(carrierVo);

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + carrierStr + "');");
                    }
                });
            }
        });
    }

    /**
     * 更新driver信息
     *
     * @param data 回调函数
     */
    private void updateDriverInfo(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final String phone = JsonUtil.getString(jsonObject, "phone");
        final String email = JsonUtil.getString(jsonObject, "email");
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                HttpRequest request = new HttpRequest.Builder()
                        .url(Constants.API_UPDATE_DRIVER_INFO)
                        .addParam("access_token", SystemHelper.getUser().getAccessToken())
                        .addParam("phone", phone)
                        .addParam("email", email)
                        .build();

                HttpResponse response = request.post();
                if (response.isSuccess()) {

                    final AppDatabase database = DataBaseHelper.getDataBase();
                    int driverId = SystemHelper.getUser().getDriverId();
                    Driver driver = database.driverDao().getDriver(driverId);
                    driver.setPhone(phone);
                    driver.setEmail(email);
                    database.driverDao().updateDriver(driver);

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
     * 更新密码
     *
     * @param data 回调函数
     */
    private void updatePassword(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final String old_passwd = JsonUtil.getString(jsonObject, "old_passwd");
        final String new_passwd = JsonUtil.getString(jsonObject, "new_passwd");
        final String confirm_passwd = JsonUtil.getString(jsonObject, "confirm_passwd");
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                HttpRequest request = new HttpRequest.Builder()
                        .url(Constants.API_UPDATE_PASSWORD)
                        .addParam("access_token", SystemHelper.getUser().getAccessToken())
                        .addParam("old_passwd", old_passwd)
                        .addParam("new_passwd", new_passwd)
                        .addParam("confirm_passwd", confirm_passwd)
                        .build();

                HttpResponse response = request.post();
                if (response.isSuccess()) {

                    User user = SystemHelper.getUser();
                    user.setPassword(new_passwd);
                    SystemHelper.setUser(user);

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
     * 获取司机Not Certified Alert数量
     *
     * @param data 回调函数
     */
    private void getDriverNotCertifiedAlertCount(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                HttpRequest request = new HttpRequest.Builder()
                        .url(Constants.API_ALERT_GET_SUMMARY)
                        .addParam("access_token", SystemHelper.getUser().getAccessToken())
                        .build();

                final HttpResponse response = request.get();

                if (response.isSuccess()) {

                    JSONObject object = JsonUtil.parseObject(response.getData());
                    final int notCertifiedLogsCnt = JsonUtil.getInt(object, "notCertifiedLogsCnt");
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + notCertifiedLogsCnt + "');");
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
     * 司机请求服务器登出
     *
     * @param data 回调函数
     */
    private void logoutRequest(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                // 上报登出事件
                EventCenter.getInstance().notifyLoginEvent(false);

                HttpRequest request = new HttpRequest.Builder()
                        .url(Constants.API_LOGOUT)
                        .addParam("access_token", SystemHelper.getUser().getAccessToken())
                        .build();

                HttpResponse response = request.post();
                if (response.isSuccess()) {

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
     * 司机本地登出
     */
    private void logoutLocal() {

        SystemHelper.setLogout(false);

        // 跳转到登陆页面
        Intent intent = LoginActivity.newIntent(context);
        context.startActivity(intent);
        ActivityStack.getInstance().finishOthers(LoginActivity.class);
    }
}
