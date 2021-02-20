package com.unitedbustech.eld.jsinterface;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.ActivityStack;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.dailylog.model.Profile;
import com.unitedbustech.eld.datacollector.DataCollectorHandler;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.ProfileEntity;
import com.unitedbustech.eld.driving.DrivingActivity;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.eventcenter.core.StateAdditionInfo;
import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.hos.core.HosHandler;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.hos.model.HosDataModel;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.request.RequestCacheService;
import com.unitedbustech.eld.request.RequestType;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.util.TimeUtil;
import com.unitedbustech.eld.view.AdverseDialog;
import com.unitedbustech.eld.view.BreakDialog;
import com.unitedbustech.eld.view.TrailerDialog;
import com.unitedbustech.eld.view.UIWebView;
import com.unitedbustech.eld.web.BaseJsInterface;
import com.unitedbustech.eld.web.JsInterface;

import java.util.Date;

/**
 * @author zhangyu
 * @date 2018/1/20
 * @description 主页数据的js接口
 */
@JsInterface(name = "dashboard")
public class DashboardJsInterface extends BaseJsInterface {

    private static final String TAG = "DashboardJsInterface";

    private static final String GET_DASHBOARD_TIME = "getDashboardTime";
    private static final String GET_OFF_LINE = "getOffline";
    private static final String GET_DRIVER_STATE = "getDriverState";
    private static final String OPEN_START_BREAK_DIALOG = "openStartBreakDialog";
    private static final String CLOSE_START_BREAK_DIALOG = "closeStartBreakDialog";
    private static final String OPEN_ADVERSE_DRIVING_DIALOG = "openAdverseDrivingDialog";
    private static final String CLOSE_ADVERSE_DRIVING_DIALOG = "closeAdverseDrivingDialog";
    private static final String GET_ECM_INFO = "getEcmInfo";
    private static final String IS_BREAK = "isBreak";
    private static final String OPEN_SHIPPING_DIALOG = "openShippingDialog";
    private static final String SAVE_SHIPPING = "saveShipping";
    private static final String DASHBOARD_INTERFACE_GET_EXPIRE_DAY = "getExpireDay";

    private BreakDialog breakDialog;
    private AdverseDialog adverseDialog;
    private TrailerDialog trailerDialog;

    public DashboardJsInterface(Context context, UIWebView uiWebView) {

        super(context, uiWebView);
    }

    @Override
    @JavascriptInterface
    public void method(String params) {

        JSONObject obj = JsonUtil.parseObject(params);

        String method = JsonUtil.getString(obj, METHOD_KEY);
        String data = JsonUtil.getString(obj, DATA_KEY);

        switch (method) {

            case GET_DASHBOARD_TIME:

                getDashboardData(data);
                break;
            case GET_OFF_LINE:

                getOffline(data);
                break;
            case GET_DRIVER_STATE:

                getDriverState(data);
                break;
            case OPEN_START_BREAK_DIALOG:

                openStartBreakDialog(data);
                break;
            case CLOSE_START_BREAK_DIALOG:

                closeStartBreakDialog(data);
                break;
            case OPEN_ADVERSE_DRIVING_DIALOG:

                openAdverseDrivingDialog(data);
                break;
            case CLOSE_ADVERSE_DRIVING_DIALOG:

                closeAdverseDrivingDialog(data);
                break;
            case GET_ECM_INFO:

                getEcmInfo(data);
                break;
            case IS_BREAK:

                isBreak(data);
                break;
            case OPEN_SHIPPING_DIALOG:

                openShippingDialog(data);
                break;
            case SAVE_SHIPPING:

                saveShipping(data);
                break;
            case DASHBOARD_INTERFACE_GET_EXPIRE_DAY:

                getExpireDay(data);
                break;

            default:
                break;
        }
    }

    /**
     * 获取主页数据
     *
     * @param data 回调函数
     */
    private void getDashboardData(final String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                final HosDataModel hosDataModel = HosHandler.getInstance().getCalculator().calculateHosData();

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(hosDataModel) + "');");
                    }
                });
            }
        });
    }

    /**
     * 获取主页数据
     *
     * @param data 回调函数
     */
    private void getOffline(final String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + (RequestCacheService.getInstance().isOffline() ? UIWebView.NETWORK_DISCONNECTED : UIWebView.NETWORK_CONNECTED) + "');");
            }
        });
    }

    /**
     * 获取司机状态
     *
     * @param data 回调函数
     */
    private void getDriverState(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                final DriverState driverState = ModelCenter.getInstance().getCurrentDriverState();

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + driverState.getCode() + "');");
                    }
                });

            }
        });
    }

    /**
     * 打开startbreak界面
     *
     * @param data 回调函数
     */
    private void openStartBreakDialog(final String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final JSONObject object = JsonUtil.getJsonObject(jsonObject, DATA_KEY);

        final boolean isOn = object.getBoolean("isOn");

        breakDialog = new BreakDialog.Builder(context)
                .setNeutralBtn(new BreakDialog.OnClickListener() {
                    @Override
                    public void onClick(BreakDialog dialog, int which) {

                        if (!dialog.checkStatus()) {
                            return;
                        }

                        ModelCenter.getInstance().setBreak(!isOn);

                        StateAdditionInfo.Builder builder = new StateAdditionInfo.Builder();
                        builder.origin(DDLOriginEnum.EDIT_BY_DRIVER);
                        builder.location(dialog.getLocation(), dialog.getLatitude(), dialog.getLongitude());
                        builder.remark(dialog.getRemark());

                        if (!isOn) {
                            EventCenter.getInstance().changeDriverState(DriverState.OFF_DUTY, builder.build());
                        } else {
                            EventCenter.getInstance().changeDriverState(DriverState.ON_DUTY_NOT_DRIVING, builder.build());
                        }
                        ThreadUtil.getInstance().execute(new Runnable() {
                            @Override
                            public void run() {

                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        uiWebView.loadUrl("javascript:" + callback + "('');");
                                    }
                                });
                            }
                        });
                        dialog.cancel();

                        breakDialog = null;
                    }
                })
                .setNegativeBtn(new BreakDialog.OnClickListener() {
                    @Override
                    public void onClick(BreakDialog dialog, int which) {
                        dialog.cancel();
                        breakDialog = null;
                    }
                })
                .setTitle(isOn ? context.getString(R.string.stop_break_title) : context.getString(R.string.start_break_title))
                .setRemark(isOn ? context.getString(R.string.stop_break_remark_tip) : context.getString(R.string.start_break_remark_tip))
                .build();

        breakDialog.show();
    }

    /**
     * 关闭startbreak界面
     *
     * @param data 回调函数
     */
    private void closeStartBreakDialog(String data) {

        if (breakDialog != null) {

            breakDialog.cancel();
        }
    }

    /**
     * 打开AdverseDriving界面
     *
     * @param data 回调函数
     */
    private void openAdverseDrivingDialog(final String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        adverseDialog = new AdverseDialog.Builder(context)
                .setNeutralBtn(new AdverseDialog.OnClickListener() {
                    @Override
                    public void onClick(AdverseDialog dialog, int which) {

                        if (!dialog.checkStatus()) {
                            return;
                        }

                        StateAdditionInfo.Builder builder = new StateAdditionInfo.Builder();
                        builder.origin(DDLOriginEnum.EDIT_BY_DRIVER);
                        builder.location(dialog.getLocation(), dialog.getLatitude(), dialog.getLongitude());
                        builder.remark(dialog.getRemark());

                        EventCenter.getInstance().newAdverseDriving(builder.build());
                        ThreadUtil.getInstance().execute(new Runnable() {
                            @Override
                            public void run() {

                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        uiWebView.loadUrl("javascript:" + callback + "('');");
                                    }
                                });
                            }
                        });
                        dialog.cancel();
                        adverseDialog = null;
                    }
                })
                .setNegativeBtn(new AdverseDialog.OnClickListener() {
                    @Override
                    public void onClick(AdverseDialog dialog, int which) {
                        dialog.cancel();
                        adverseDialog = null;
                    }
                })
                .setTitle(context.getString(R.string.adverse_driving_title))
                .build();

        adverseDialog.show();
    }

    /**
     * 关闭AdverseDriving界面
     *
     * @param data 回调函数
     */
    private void closeAdverseDrivingDialog(String data) {

        if (adverseDialog != null) {

            adverseDialog.cancel();
        }
    }

    /**
     * 获取ECM信息
     *
     * @param data 回调函数
     */
    private void getEcmInfo(final String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final VehicleDataModel dataModel = DataCollectorHandler.getInstance().getDataModel();
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(dataModel) + "');");
            }
        });
    }

    /**
     * 当前是否是在Break
     *
     * @param data 回调函数
     */
    private void isBreak(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final boolean isBreak = ModelCenter.getInstance().getBreak();
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int result = isBreak ? 1 : 0;
                uiWebView.loadUrl("javascript:" + callback + "('" + result + "');");
            }
        });
    }

    /**
     * shippingId 弹窗
     *
     * @param data 回调函数
     */
    private void openShippingDialog(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        Activity activity = ActivityStack.getInstance().getCurrentActivity();

        if (activity != null && !(activity instanceof DrivingActivity)) {

            trailerDialog = new TrailerDialog.Builder(activity)
                    .setScrollMaxHeight(300)
                    .setNegativeBtnStr(context.getResources().getString(R.string.skip))
                    .setNeutralBtnStr(context.getResources().getString(R.string.save))
                    .setNeutralListener(new TrailerDialog.OnClickListener() {
                        @Override
                        public void onClick(TrailerDialog dialog, int which) {

                            if (dialog.checkShippingValid()) {

                                final String shippingId = trailerDialog.getShippingStr();
                                dialog.dismiss();

                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "', '" + shippingId + "');");
                                    }
                                });
                            }
                        }
                    })
                    .setNegativeListener(new TrailerDialog.OnClickListener() {
                        @Override
                        public void onClick(TrailerDialog dialog, int which) {

                            dialog.dismiss();
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                                }
                            });
                        }
                    })
                    .build();
            trailerDialog.show();
        }
    }

    /**
     * 保存 shipping
     *
     * @param data 数据和回调函数
     */
    private void saveShipping(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final JSONObject object = JsonUtil.getJsonObject(jsonObject, DATA_KEY);
        final String shippingId = JsonUtil.getString(object, "shipping");

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                Profile profile = ModelCenter.getInstance().getAllHosDayModels().get(0).getProfile();
                Date date = ModelCenter.getInstance().getAllHosDayModels().get(0).getDate();
                if (!TextUtils.isEmpty(profile.getShippingId())) {
                    profile.setShippingId(profile.getShippingId() + "," + shippingId);
                } else {
                    profile.setShippingId(shippingId);
                }

                String dateStr = TimeUtil.utcToLocal(ModelCenter.getInstance().getAllHosDayModels().get(0).getDate().getTime(),
                        SystemHelper.getUser().getTimeZone(),
                        TimeUtil.MM_DD_YY);
                ProfileEntity profileEntity = DataBaseHelper.getDataBase().profileDao().getByDate(dateStr);

                if (profileEntity == null) {

                    profileEntity = new ProfileEntity();
                    profileEntity.setProfileJson(JsonUtil.toJSONString(profile));
                    profileEntity.setDate(dateStr);

                    DataBaseHelper.getDataBase().profileDao().insert(profileEntity);
                } else {

                    profileEntity.setProfileJson(JsonUtil.toJSONString(profile));
                    DataBaseHelper.getDataBase().profileDao().update(profileEntity);
                }

                JSONObject profileObj = Profile.getServerJsonByProfile(profile, date);
                HttpRequest.Builder builder = new HttpRequest.Builder()
                        .addParam("access_token", SystemHelper.getUser().getAccessToken())
                        .url(Constants.API_SAVE_DOT_REVIEW_HEAD_DATA);
                for (String key : profileObj.keySet()) {

                    builder = builder.addParam(key, profileObj.getString(key));
                }

                RequestCacheService.getInstance().cachePost(builder.build(), RequestType.OTHERS);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_COMPLETE + "');");
                    }
                });
            }
        });
    }

    /**
     * 获取公司过期时间
     */
    private void getExpireDay(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                User user = SystemHelper.getUser();
                HttpRequest httpRequest = new HttpRequest.Builder()
                        .url(Constants.API_GET_COMPANY_INFO)
                        .addParam("access_token", user.getAccessToken())
                        .build();
                HttpResponse response = httpRequest.get();
                if (response.isSuccess()) {

                    final JSONObject obj = JsonUtil.parseObject(response.getData());

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            String result = JsonUtil.getString(obj, "remainingDays");

                            if (result != null && result.length() > 0) {

                                uiWebView.loadUrl("javascript:" + callback + "('" + result + "');");
                            }
                        }
                    });

                }
            }
        });

    }

}
