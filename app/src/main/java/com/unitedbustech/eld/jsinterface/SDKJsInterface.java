package com.unitedbustech.eld.jsinterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.text.SpannableString;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.ActivityStack;
import com.unitedbustech.eld.activity.BaseActivity;
import com.unitedbustech.eld.activity.WebActivity;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.DataCollectorType;
import com.unitedbustech.eld.common.SelfCheckEventType;
import com.unitedbustech.eld.common.TeamWorkState;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.dailylog.detail.DailylogDetailActivity;
import com.unitedbustech.eld.datacollector.DataCollectorHandler;
import com.unitedbustech.eld.datacollector.common.CollectorType;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Driver;
import com.unitedbustech.eld.domain.entry.DriverRule;
import com.unitedbustech.eld.domain.entry.Rule;
import com.unitedbustech.eld.eventbus.SelfCheckEvent;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.grid.GridActivity;
import com.unitedbustech.eld.hos.core.HosHandler;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.hos.model.HosDayModel;
import com.unitedbustech.eld.http.HttpFileRequest;
import com.unitedbustech.eld.http.HttpFileUploadCallback;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.launcher.LauncherActivity;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.request.RequestCacheService;
import com.unitedbustech.eld.request.RequestType;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.AppUtil;
import com.unitedbustech.eld.util.FileUtil;
import com.unitedbustech.eld.util.FirebaseRequestUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.LanguageUtil;
import com.unitedbustech.eld.util.LocalDataStorageUtil;
import com.unitedbustech.eld.util.SpannableStringUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.util.TimeUtil;
import com.unitedbustech.eld.util.ZipProgress;
import com.unitedbustech.eld.view.DatePickerDialog;
import com.unitedbustech.eld.view.DotLoadingDialog;
import com.unitedbustech.eld.view.DotPromptDialog;
import com.unitedbustech.eld.view.ExpireDayDialog;
import com.unitedbustech.eld.view.HorizontalDialog;
import com.unitedbustech.eld.view.HorizontalSpannableStringDialog;
import com.unitedbustech.eld.view.LoadingDialog;
import com.unitedbustech.eld.view.PromptDialog;
import com.unitedbustech.eld.view.TabMenu;
import com.unitedbustech.eld.view.TimePickerDialog;
import com.unitedbustech.eld.view.UIWebView;
import com.unitedbustech.eld.view.VerticalDialog;
import com.unitedbustech.eld.web.BaseJsInterface;
import com.unitedbustech.eld.web.JsInterface;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * @author yufei0213
 * @date 2017/12/6
 * @description webview 接口基类
 */
@JsInterface(name = "sdk")
public class SDKJsInterface extends BaseJsInterface {

    private static final String TAG = "SDKJsInterface";

    private static final int DIALOG_ICON_AMAZING = 1;
    private static final int DIALOG_ICON_AWKWARD = 2;
    private static final int DIALOG_ICON_GREATE = 3;
    private static final int DIALOG_ICON_HELLO = 4;
    private static final int DIALOG_ICON_LOVE = 5;
    private static final int DIALOG_ICON_MSG = 6;
    private static final int DIALOG_ICON_QUESTION = 7;
    private static final int DIALOG_ICON_SLEEP = 8;
    private static final int DIALOG_CANCELABLE = 1;
    private static final int DIALOG_NOCANCELABLE = -1;

    private static final String NOTIFICATION_SWITCH = "notification_switch";

    private static final String SMS_SWITCH = "sms_switch";

    private static final String SHOW_LOADING = "showLoading";
    private static final String HIDE_LOADING = "hideLoading";
    private static final String SHOW_DOT_LOADING = "showDotLoading";
    private static final String HIDE_DOT_LOADING = "hideDotLoading";
    private static final String SHOW_DIALOG = "showDialog";
    private static final String SHOW_SPANNABLE_DIALOG = "showSpannableStringDialog";
    private static final String SHOW_VERTICAL_DIALOG = "showVerticalDialog";
    private static final String SHOW_PROMPT = "showPrompt";
    private static final String SHOW_DOT_PROMPT = "showDotPrompt";
    private static final String SHOW_MESSAGE = "showMessage";
    private static final String SHOW_TIME_PICKER = "showTimePicker";
    private static final String SHOW_DATE_PICKER = "showDatePicker";
    private static final String OPEN_PAGE = "openPage";
    private static final String OPEN_GRID_PAGE = "openGridPage";
    private static final String OPEN_MAIN_PAGE = "openMainPage";
    private static final String OPEN_DAILY_LOG_PAGE = "openDailyLogPage";
    private static final String GET_GEO_LOCATION = "getGeoLocation";
    private static final String GET_LOCATION = "getLocation";
    private static final String GET_REMARK_LIST = "getRemarkList";
    private static final String GET_DEFECT_LIST = "getDefectList";
    private static final String GET_LANGUAGE_SETTING = "getLanguageSetting";
    private static final String SET_LANGUAGE = "setLanguage";
    private static final String GET_NOTIFICATION_SETTING = "getNotificationSetting";
    private static final String SET_NOTIFICATION = "setNotificationSetting";
    private static final String SET_WEB_DATA = "setWebData";
    private static final String CLEAR_WEB_DATA = "clearWebData";
    private static final String GET_DATE = "getDate";
    private static final String GET_OFFSET_DATE = "getOffsetDate";
    private static final String FINISH = "back";
    private static final String FINISH_FOR_RESULT = "backForResult";
    private static final String LOG = "log";
    private static final String GET_VERSION_NAME = "getVersionName";
    private static final String OPEN_LAUNCHER_ANIMATION = "openLauncherAnimation";
    private static final String CLOSE_LAUNCHER_ANIMATION = "closeLauncherAnimation";
    private static final String COLLECT_FIREBASE_EVENT = "collectFirebaseEvent";
    private static final String COLLECT_FIREBASE_SCREEN = "collectFirebaseScreen";
    private static final String SEND_EMAIL = "sendEmail";
    private static final String CALL = "call";
    private static final String GET_TOTAL_HOUR = "getTotalHour";
    private static final String GET_DATA_COLLECTOR_TYPE = "getDataCollectorType";
    private static final String GET_DRIVER_NAME = "getDriverName";
    private static final String CHECK_RESUME = "checkResume";
    private static final String UPLOAD_LOGS = "uploadLogs";
    private static final String GET_RULE_LIST = "getRuleList";
    private static final String SET_RULE = "setRule";
    private static final String GET_CURRENT_RULE = "getCurrentRuleId";
    private static final String INTERFACE_SHOW_EXPIRE_DAY = "showExpireDay";

    private LoadingDialog loadingDialog;
    private DotLoadingDialog dotLoadingDialog;

    public SDKJsInterface(Context context, UIWebView uiWebView) {

        super(context, uiWebView);
    }

    @Override
    @JavascriptInterface
    public void method(String params) {

        JSONObject obj = JsonUtil.parseObject(params);

        String method = JsonUtil.getString(obj, METHOD_KEY);
        String data = JsonUtil.getString(obj, DATA_KEY);

        switch (method) {

            case SHOW_LOADING:

                showLoading();
                break;
            case HIDE_LOADING:

                hideLoading();
                break;
            case SHOW_DIALOG:

                showDialog(data);
                break;
            case SHOW_SPANNABLE_DIALOG:

                showSpannableStringDialog(data);
                break;
            case SHOW_DOT_LOADING:

                showDotLoading();
                break;
            case HIDE_DOT_LOADING:

                hideDotLoading();
                break;
            case SHOW_VERTICAL_DIALOG:

                showVerticalDialog(data);
                break;
            case SHOW_PROMPT:

                showPrompt(data);
                break;
            case SHOW_DOT_PROMPT:

                showDotPrompt(data);
                break;
            case SHOW_MESSAGE:

                showMessage(data);
                break;
            case SHOW_TIME_PICKER:

                showTimePicker(data);
                break;
            case SHOW_DATE_PICKER:

                showDatePicker(data);
                break;
            case OPEN_PAGE:

                openPage(data);
                break;
            case OPEN_GRID_PAGE:

                openGridPage(data);
                break;
            case OPEN_MAIN_PAGE:

                openMainActivity();
                break;
            case OPEN_DAILY_LOG_PAGE:

                openDailylogActivity(data);
                break;
            case GET_GEO_LOCATION:

                getGeoLocation(data);
                break;
            case GET_LOCATION:

                getLocation(data);
                break;
            case GET_REMARK_LIST:

                getRemarkList(data);
                break;
            case GET_DEFECT_LIST:

                getDefectList(data);
                break;
            case GET_LANGUAGE_SETTING:

                getLanguageSetting(data);
                break;
            case SET_LANGUAGE:

                setLanguage(data);
                break;
            case GET_NOTIFICATION_SETTING:

                getNotificationSetting(data);
                break;
            case SET_NOTIFICATION:

                setNotificationSetting(data);
                break;
            case SET_WEB_DATA:

                setWebData(data);
                break;
            case CLEAR_WEB_DATA:

                clearWebData(data);
                break;
            case GET_DATE:

                getDate(data);
                break;
            case GET_OFFSET_DATE:

                getOffsetDate(data);
                break;
            case FINISH:

                finish(data);
                break;
            case FINISH_FOR_RESULT:

                finishForResult(data);
                break;
            case LOG:

                log(data);
                break;
            case GET_VERSION_NAME:

                getVersionName(data);
                break;
            case OPEN_LAUNCHER_ANIMATION:

                openLauncherAnimation();
                break;
            case CLOSE_LAUNCHER_ANIMATION:

                closeLauncherAnimation();
                break;
            case COLLECT_FIREBASE_EVENT:

                collectFirebaseEvent(data);
                break;
            case COLLECT_FIREBASE_SCREEN:

                collectFirebaseScreen(data);
                break;
            case SEND_EMAIL:

                sendEmail(data);
                break;
            case CALL:

                call(data);
                break;
            case GET_TOTAL_HOUR:

                getTotalHour(data);
                break;
            case GET_DATA_COLLECTOR_TYPE:

                getDataCollectorType(data);
                break;
            case GET_DRIVER_NAME:

                getDriverName(data);
                break;
            case CHECK_RESUME:

                checkResume();
                break;
            case UPLOAD_LOGS:

                uploadLogs(data);
                break;
            case GET_RULE_LIST:

                getRuleList(data);
                break;
            case SET_RULE:

                setRule(data);
                break;
            case GET_CURRENT_RULE:

                getCurrentRuleId(data);
                break;
            case INTERFACE_SHOW_EXPIRE_DAY:

                showExpireDayDialog(data);
                break;

            default:
                break;
        }
    }

    /**
     * 显示弹窗
     *
     * @param data 弹窗配置
     */
    private void showExpireDayDialog(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final String text = JsonUtil.getString(jsonObject, "days");
        final ExpireDayDialog.Builder builder = new ExpireDayDialog.Builder(context);
        builder.setContent(text);
        builder.setNeutralListener(new ExpireDayDialog.OnClickListener() {
            @Override
            public void onClick(ExpireDayDialog dialog, int which) {
                dialog.cancel();
            }
        });

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.build().show();
            }
        });
    }

    /**
     * 获取当前时间字符串
     *
     * @param data 回调函数
     */
    private void getDate(final String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        User user = SystemHelper.getUser();

        final long time = new Date().getTime();
        final String result = TimeUtil.utcToLocal(time, user.getTimeZone(), TimeUtil.STANDARD_FORMAT);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + result + "', '" + time + "');");
            }
        });
    }

    /**
     * 获取日期
     *
     * @param data 回调函数
     */
    private void getOffsetDate(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        int offset = JsonUtil.getInt(jsonObject, "offset");

        User user = SystemHelper.getUser();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + offset);

        final long time = calendar.getTimeInMillis();
        final String result = TimeUtil.utcToLocal(time, user.getTimeZone(), TimeUtil.US_FORMAT);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + result + "');");
            }
        });
    }

    /**
     * 打开子页面
     *
     * @param data 页面数据
     */
    private void openPage(String data) {

        JSONObject obj = JsonUtil.parseObject(data);

        String fileName = JsonUtil.getString(obj, "url");
        String title = JsonUtil.getString(obj, "title");
        String params = JsonUtil.getString(obj, "params");
        int canBack = JsonUtil.getInt(obj, "canBack");

        Intent intent = WebActivity.newIntent(context, fileName, title, canBack, params);
        context.startActivity(intent);
    }

    /**
     * 打开Grid界面
     *
     * @param data
     */
    private void openGridPage(String data) {

        JSONObject obj = JsonUtil.parseObject(data);
        String params = JsonUtil.getString(obj, "params");

        Intent intent = GridActivity.newIntent(context, params);
        context.startActivity(intent);
    }

    /**
     * 打开主页面
     */
    private void openMainActivity() {

        ((BaseActivity) context).openMainPage(TabMenu.DASHBORAD);
    }

    /**
     * 显示信息（toast）
     */
    private void showMessage(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String message = JsonUtil.getString(jsonObject, DATA_KEY);

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 打开子页面
     *
     * @param data 页面数据
     */
    private void openDailylogActivity(String data) {

        Intent intent = DailylogDetailActivity.newIntent(context, data);
        context.startActivity(intent);
    }

    /**
     * 获取编码后的地址
     *
     * @param data 回调函数
     */
    private void getGeoLocation(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final Location location = LocationHandler.getInstance().getCurrentLocation();
        if (location == null) {

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                }
            });
        } else {

            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    User user = SystemHelper.getUser();
                    HttpRequest HttpRequest = new HttpRequest.Builder()
                            .url(Constants.API_GET_GEO_LOCATION)
                            .addParam("access_token", user.getAccessToken())
                            .addParam("latitude", Double.toString(location.getLatitude()))
                            .addParam("longitude", Double.toString(location.getLongitude()))
                            .build();

                    HttpResponse httpResponse = HttpRequest.get();
                    if (httpResponse.isSuccess()) {

                        String data = httpResponse.getData();

                        JSONObject object = JsonUtil.parseObject(data);

                        final String geoLocation = JsonUtil.getString(object, "geoLocation");

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (TextUtils.isEmpty(geoLocation)) {

                                    uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                                } else {

                                    uiWebView.loadUrl("javascript:" + callback + "('" +
                                            CALLBACK_SUCCESS + "', '" +
                                            location.getLatitude() + "', '" +
                                            location.getLongitude() + "', '" +
                                            geoLocation + "');");
                                }
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
    }

    /**
     * 获取编码后的地址
     *
     * @param data 回调函数
     */
    private void getLocation(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final Location location = LocationHandler.getInstance().getCurrentLocation();

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (location != null) {

                    uiWebView.loadUrl("javascript:" + callback + "('" + location.getLatitude() + "', '" + location.getLongitude() + "');");
                } else {

                    uiWebView.loadUrl("javascript:" + callback + "();");
                }
            }
        });
    }

    /**
     * 获取全部remark 提示
     *
     * @param data 回调函数
     */
    private void getRemarkList(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                final String content = AppUtil.readAssetsFile(context, "data/remark.txt");

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + content + "');");
                    }
                });
            }
        });
    }

    /**
     * 获取全部defects
     *
     * @param data 回调函数
     */
    private void getDefectList(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                final String content = AppUtil.readAssetsFile(context, "data/defects.txt");

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + content + "');");
                    }
                });
            }
        });
    }

    /**
     * 设置短息，通知开关
     * @param data
     */
    private void setNotificationSetting(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final int type = JsonUtil.getInt(jsonObject, "type");
        final int switchStatus = JsonUtil.getInt(jsonObject, DATA_KEY);
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();
                HttpRequest.Builder builder = new HttpRequest.Builder();
                builder.addParam("access_token", user.getAccessToken());
                if(type == 1) {//手机通知类型

                    builder.addParam("pushNotificationOn", switchStatus + "");
                    builder.addParam("pushSmsOn", LocalDataStorageUtil.getInt(SMS_SWITCH) + "");
                } else {//手机短信
                    builder.addParam("pushNotificationOn", LocalDataStorageUtil.getInt(NOTIFICATION_SWITCH) + "");
                    builder.addParam("pushSmsOn", switchStatus + "");
                }
                HttpRequest httpRequest = builder.url(Constants.API_PUSH_SWITCH).build();
                HttpResponse httpResponse = httpRequest.post();
                if(httpResponse.isSuccess()) {
                    if(type == 1)
                        LocalDataStorageUtil.putInt(NOTIFICATION_SWITCH, switchStatus);
                    else
                        LocalDataStorageUtil.putInt(SMS_SWITCH, switchStatus);
                }
            }
        });
    }

    /**
     * 获取短信，通知的设置
     */
    private void getNotificationSetting(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final int type = JsonUtil.getInt(jsonObject, "type");

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                int switchStatus = 0;
                if(type == 1)
                    switchStatus = LocalDataStorageUtil.getInt(NOTIFICATION_SWITCH);
                else
                    switchStatus = LocalDataStorageUtil.getInt(SMS_SWITCH);
                final int status = switchStatus;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + status + "');");
                    }
                });
            }
        });
    }

    /**
     * 获取语言设置
     *
     * @param data 回调函数
     */
    private void getLanguageSetting(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                final int languageTyle = LanguageUtil.getInstance().getLanguageType();

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + languageTyle + "');");
                    }
                });
            }
        });
    }

    /**
     * 设置语言
     *
     * @param data 回调函数
     */
    private void setLanguage(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final int languageType = JsonUtil.getInt(jsonObject, DATA_KEY);

        //更新本地语言设置
        LanguageUtil.getInstance().updateLanguage(context, languageType);
    }

    /**
     * 存储webview传递过来的数值
     *
     * @param data 数据
     */
    private void setWebData(String data) {

        SystemHelper.setWebData(data);
    }

    /**
     * 清除webview数据
     *
     * @param data 数据主键
     */
    private void clearWebData(String data) {

        SystemHelper.clearWebData();
    }

    /**
     * 关闭当前页面
     */
    private void finish(String data) {

        JSONObject obj = JsonUtil.parseObject(data);
        int index = JsonUtil.getInt(obj, "index");
        ActivityStack.getInstance().finishActivityToIndex(index);
    }

    /**
     * 关闭当前界面，并且传递参数给父界面
     *
     * @param data 数据
     */
    private void finishForResult(String data) {

        JSONObject obj = JsonUtil.parseObject(data);

        String resultData = JsonUtil.getString(obj, "data");

        Activity activity = (Activity) context;
        if (!TextUtils.isEmpty(resultData)) {

            Intent intent = new Intent();
            intent.putExtra(uiWebView.EXTRA_RESULT, resultData);

            activity.setResult(RESULT_OK, intent);
        } else {

            activity.setResult(RESULT_CANCELED);
        }

        activity.finish();
    }

    /**
     * 输出日志
     *
     * @param data 日志数据
     */
    private void log(String data) {

        JSONObject obj = JsonUtil.parseObject(data);
        String tag = JsonUtil.getString(obj, "tag");
        String info = JsonUtil.getString(obj, "info");
        int level = JsonUtil.getInt(obj, "level");

        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(info)) {

            if (level > 0) {

                Logger.i(tag, info);
            } else {

                Logger.d(tag, info);
            }
        }
    }

    /**
     * 展示loading
     */
    private void showLoading() {

        Activity activity = (Activity) context;
        if (!activity.isFinishing()) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    loadingDialog = new LoadingDialog(context);
                    loadingDialog.show();
                }
            });
        }
    }

    /**
     * 隐藏loading
     */
    private void hideLoading() {

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (loadingDialog != null) {

                    loadingDialog.dismiss();
                    loadingDialog = null;
                }
            }
        });
    }

    /**
     * 展示loading
     */
    private void showDotLoading() {

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                dotLoadingDialog = new DotLoadingDialog(context);
                dotLoadingDialog.show();
            }
        });
    }

    /**
     * 隐藏loading
     */
    private void hideDotLoading() {

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (dotLoadingDialog != null) {

                    dotLoadingDialog.dismiss();
                    dotLoadingDialog = null;
                }
            }
        });
    }

    /**
     * 显示弹窗
     *
     * @param data 弹窗配置
     */
    private void showDialog(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        int iconId = JsonUtil.getInt(jsonObject, "icon");
        String text = JsonUtil.getString(jsonObject, "text");
        String negativeBtnText = JsonUtil.getString(jsonObject, "negativeBtnText");
        String neutralBtnText = JsonUtil.getString(jsonObject, "neutralBtnText");
        String positiveBtnText = JsonUtil.getString(jsonObject, "positiveBtnText");
        int cancelable = JsonUtil.getInt(jsonObject, "cancelable");

        int iconResId = 0;
        switch (iconId) {

            case DIALOG_ICON_AMAZING:

                iconResId = R.drawable.ic_emoji_amazing;
                break;
            case DIALOG_ICON_AWKWARD:

                iconResId = R.drawable.ic_emoji_awkward;
                break;
            case DIALOG_ICON_GREATE:

                iconResId = R.drawable.ic_emoji_greate;
                break;
            case DIALOG_ICON_HELLO:

                iconResId = R.drawable.ic_emoji_hi;
                break;
            case DIALOG_ICON_LOVE:

                iconResId = R.drawable.ic_emoji_love;
                break;
            case DIALOG_ICON_MSG:

                iconResId = R.drawable.ic_emoji_msg;
                break;
            case DIALOG_ICON_QUESTION:

                iconResId = R.drawable.ic_emoji_question;
                break;
            case DIALOG_ICON_SLEEP:

                iconResId = R.drawable.ic_emoji_sleep;
                break;
            default:
                break;
        }

        final HorizontalDialog.Builder builder = new HorizontalDialog.Builder(context)
                .setIcon(iconResId)
                .setText(text);

        if (!TextUtils.isEmpty(positiveBtnText)) {

            builder.setPositiveBtn(positiveBtnText, new HorizontalDialog.OnClickListener() {
                @Override
                public void onClick(final HorizontalDialog dialog, final int which) {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialog.dismiss();
                            if (!TextUtils.isEmpty(callback)) {

                                uiWebView.loadUrl("javascript:" + callback + "('" + which + "');");
                            }
                        }
                    });
                }
            });
        } else {

            builder.setNegativeBtn(negativeBtnText, new HorizontalDialog.OnClickListener() {
                @Override
                public void onClick(final HorizontalDialog dialog, final int which) {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialog.dismiss();
                            if (!TextUtils.isEmpty(callback)) {

                                uiWebView.loadUrl("javascript:" + callback + "('" + which + "');");
                            }
                        }
                    });
                }
            });
            builder.setNeutralBtn(neutralBtnText, new HorizontalDialog.OnClickListener() {
                @Override
                public void onClick(final HorizontalDialog dialog, final int which) {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialog.dismiss();
                            if (!TextUtils.isEmpty(callback)) {

                                uiWebView.loadUrl("javascript:" + callback + "('" + which + "');");
                            }
                        }
                    });
                }
            });
        }

        builder.setCancelable(cancelable == DIALOG_CANCELABLE);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                builder.build().show();
            }
        });
    }

    /**
     * 显示弹窗
     *
     * @param data 弹窗配置
     */
    private void showSpannableStringDialog(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        int iconId = JsonUtil.getInt(jsonObject, "icon");
        String text = JsonUtil.getString(jsonObject, "text");
        String negativeBtnText = JsonUtil.getString(jsonObject, "negativeBtnText");
        String neutralBtnText = JsonUtil.getString(jsonObject, "neutralBtnText");
        String positiveBtnText = JsonUtil.getString(jsonObject, "positiveBtnText");
        int cancelable = JsonUtil.getInt(jsonObject, "cancelable");

        int iconResId = 0;
        switch (iconId) {

            case DIALOG_ICON_AMAZING:

                iconResId = R.drawable.ic_emoji_amazing;
                break;
            case DIALOG_ICON_AWKWARD:

                iconResId = R.drawable.ic_emoji_awkward;
                break;
            case DIALOG_ICON_GREATE:

                iconResId = R.drawable.ic_emoji_greate;
                break;
            case DIALOG_ICON_HELLO:

                iconResId = R.drawable.ic_emoji_hi;
                break;
            case DIALOG_ICON_LOVE:

                iconResId = R.drawable.ic_emoji_love;
                break;
            case DIALOG_ICON_MSG:

                iconResId = R.drawable.ic_emoji_msg;
                break;
            case DIALOG_ICON_QUESTION:

                iconResId = R.drawable.ic_emoji_question;
                break;
            case DIALOG_ICON_SLEEP:

                iconResId = R.drawable.ic_emoji_sleep;
                break;
            default:
                break;
        }

        final HorizontalSpannableStringDialog.Builder builder = new HorizontalSpannableStringDialog.Builder(context)
                .setIcon(iconResId);

        SpannableString spannableString = SpannableStringUtil.changeStringForegroundColor(text, "[0-9]", R.color.theme, text.length() - 1);
        builder.setText(spannableString);

        if (!TextUtils.isEmpty(positiveBtnText)) {

            builder.setPositiveBtn(positiveBtnText, new HorizontalSpannableStringDialog.OnClickListener() {
                @Override
                public void onClick(final HorizontalSpannableStringDialog dialog, final int which) {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialog.dismiss();
                            if (!TextUtils.isEmpty(callback)) {

                                uiWebView.loadUrl("javascript:" + callback + "('" + which + "');");
                            }
                        }
                    });
                }
            });
        } else {

            builder.setNegativeBtn(negativeBtnText, new HorizontalSpannableStringDialog.OnClickListener() {
                @Override
                public void onClick(final HorizontalSpannableStringDialog dialog, final int which) {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialog.dismiss();
                            if (!TextUtils.isEmpty(callback)) {

                                uiWebView.loadUrl("javascript:" + callback + "('" + which + "');");
                            }
                        }
                    });
                }
            });
            builder.setNeutralBtn(neutralBtnText, new HorizontalSpannableStringDialog.OnClickListener() {
                @Override
                public void onClick(final HorizontalSpannableStringDialog dialog, final int which) {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialog.dismiss();
                            if (!TextUtils.isEmpty(callback)) {

                                uiWebView.loadUrl("javascript:" + callback + "('" + which + "');");
                            }
                        }
                    });
                }
            });
        }

        builder.setCancelable(cancelable == DIALOG_CANCELABLE);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                builder.build().show();
            }
        });
    }

    /**
     * 显示弹窗
     *
     * @param data 弹窗配置
     */
    private void showVerticalDialog(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        int iconId = JsonUtil.getInt(jsonObject, "icon");
        String text = JsonUtil.getString(jsonObject, "text");
        String negativeBtnText = JsonUtil.getString(jsonObject, "negativeBtnText");
        String neutralBtnText = JsonUtil.getString(jsonObject, "neutralBtnText");
        String positiveBtnText = JsonUtil.getString(jsonObject, "positiveBtnText");
        int cancelable = JsonUtil.getInt(jsonObject, "cancelable");

        int iconResId = 0;
        switch (iconId) {

            case DIALOG_ICON_AMAZING:

                iconResId = R.drawable.ic_emoji_amazing;
                break;
            case DIALOG_ICON_AWKWARD:

                iconResId = R.drawable.ic_emoji_awkward;
                break;
            case DIALOG_ICON_GREATE:

                iconResId = R.drawable.ic_emoji_greate;
                break;
            case DIALOG_ICON_HELLO:

                iconResId = R.drawable.ic_emoji_hi;
                break;
            case DIALOG_ICON_LOVE:

                iconResId = R.drawable.ic_emoji_love;
                break;
            case DIALOG_ICON_MSG:

                iconResId = R.drawable.ic_emoji_msg;
                break;
            case DIALOG_ICON_QUESTION:

                iconResId = R.drawable.ic_emoji_question;
                break;
            case DIALOG_ICON_SLEEP:

                iconResId = R.drawable.ic_emoji_sleep;
                break;
            default:
                break;
        }

        final VerticalDialog.Builder builder = new VerticalDialog.Builder(context)
                .setIcon(iconResId)
                .setTipText(text);

        if (!TextUtils.isEmpty(positiveBtnText)) {

            builder.setPositiveBtn(positiveBtnText, new VerticalDialog.OnClickListener() {
                @Override
                public void onClick(final VerticalDialog dialog, final int which) {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialog.dismiss();
                            uiWebView.loadUrl("javascript:" + callback + "('" + which + "');");
                        }
                    });
                }
            });
        } else {

            builder.setNegativeBtn(negativeBtnText, new VerticalDialog.OnClickListener() {
                @Override
                public void onClick(final VerticalDialog dialog, final int which) {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialog.dismiss();
                            uiWebView.loadUrl("javascript:" + callback + "('" + which + "');");
                        }
                    });
                }
            });
            builder.setNeutralBtn(neutralBtnText, new VerticalDialog.OnClickListener() {
                @Override
                public void onClick(final VerticalDialog dialog, final int which) {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialog.dismiss();
                            uiWebView.loadUrl("javascript:" + callback + "('" + which + "');");
                        }
                    });
                }
            });
        }

        builder.setCancelable(cancelable == DIALOG_CANCELABLE);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                builder.build().show();
            }
        });
    }

    /**
     * 展示成功或失败提示
     *
     * @param data 数据
     */
    private void showPrompt(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final int status = JsonUtil.getInt(jsonObject, "type");

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                new PromptDialog.Builder(context)
                        .type(status)
                        .listener(new PromptDialog.OnHideListener() {
                            @Override
                            public void onHide() {

                                if (!TextUtils.isEmpty(callback)) {

                                    uiWebView.loadUrl("javascript:" + callback + "();");
                                }
                            }
                        })
                        .build()
                        .show();
            }
        });
    }

    /**
     * 展示成功或失败提示
     *
     * @param data 数据
     */
    private void showDotPrompt(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final int status = JsonUtil.getInt(jsonObject, "type");

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                new DotPromptDialog.Builder(context)
                        .type(status)
                        .listener(new DotPromptDialog.OnHideListener() {
                            @Override
                            public void onHide() {

                                if (!TextUtils.isEmpty(callback)) {

                                    uiWebView.loadUrl("javascript:" + callback + "();");
                                }
                            }
                        })
                        .build()
                        .show();
            }
        });
    }

    /**
     * 打开时间选择器
     *
     * @param data 初始数据
     *             返回选择时间的秒数
     */
    private void showTimePicker(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        int second = JsonUtil.getInt(jsonObject, "nowSecond");
        int oneDayHours = JsonUtil.getInt(jsonObject, "oneDayHours");

        new TimePickerDialog.Builder(context)
                .initSecond(second)
                .oneDayHours(oneDayHours)
                .listener(new TimePickerDialog.OnDoneClickListener() {
                    @Override
                    public void onDone(final int seconds) {

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                uiWebView.loadUrl("javascript:" + callback + "('" + seconds + "');");
                            }
                        });
                    }
                })
                .build()
                .show();
    }

    /**
     * 打开时间选择器
     *
     * @param data 初始数据
     */
    private void showDatePicker(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        String date = JsonUtil.getString(jsonObject, "date");

        new DatePickerDialog.Builder(context)
                .initDate(date)
                .listener(new DatePickerDialog.OnDoneClickListener() {
                    @Override
                    public void onDone(final String date) {

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                uiWebView.loadUrl("javascript:" + callback + "('" + date + "');");
                            }
                        });
                    }
                })
                .build()
                .show();
    }

    /**
     * 获取版本
     */
    private void getVersionName(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + AppUtil.getVersionName(context) + "');");
            }
        });
    }

    /**
     * 打开启动动画
     */
    private void openLauncherAnimation() {

        final Activity activity = (Activity) context;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (activity instanceof LauncherActivity) {

                    ((LauncherActivity) activity).openAnimation();
                }
            }
        });
    }

    /**
     * 关闭启动动画
     */
    private void closeLauncherAnimation() {

        final Activity activity = (Activity) context;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (activity instanceof LauncherActivity) {

                    ((LauncherActivity) activity).closeAnimation();
                }
            }
        });
    }

    /**
     * 收集Firebase事件
     */
    private void collectFirebaseEvent(String jsonParams) {

        FirebaseRequestUtil.getInstance((Activity) context).logEventForJsonString(jsonParams);
    }

    /**
     * 收集Firebase屏幕
     */
    private void collectFirebaseScreen(String jsonParams) {

        FirebaseRequestUtil.getInstance((Activity) context).logScreenForJsonString(jsonParams);
    }

    /**
     * 发送邮件
     */
    private void sendEmail(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        String emailUrl = JsonUtil.getString(jsonObject, "emailUrl");

        try {

            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + emailUrl));
            context.startActivity(intent);
        } catch (Exception e) {

            Toast.makeText(context, R.string.not_found_mail, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 拨打电话
     */
    private void call(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        String phoneNo = JsonUtil.getString(jsonObject, "phoneNo");
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNo));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 获取一天的总时间
     */
    private void getTotalHour(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        Long dateTime = JsonUtil.getLong(jsonObject, "date");
        Integer index = JsonUtil.getInt(jsonObject, "index");
        Date date = new Date();
        if (index != 0) {//0代表今天
            List<HosDayModel> hosDayModels = ModelCenter.getInstance().getAllHosDayModels();
            if (index < 0 || index >= Constants.DDL_DAYS) {
                return;
            }
            date = hosDayModels.get(index).getDate();
        }
        if (dateTime != 0) {

            date = ModelCenter.getInstance().getHosDayModel(new Date(dateTime)).getDate();
        }
        final int totalHour = TimeUtil.getTotalHours(date);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + totalHour + "');");
            }
        });
    }

    /**
     * 获取数据采集层类型
     *
     * @param data 回调函数
     */
    private void getDataCollectorType(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final CollectorType collectorType = DataCollectorHandler.getInstance().getCurrentCollectorType();

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (collectorType == null) {

                    uiWebView.loadUrl("javascript:" + callback + "('" + DataCollectorType.NONE + "');");
                } else if (collectorType == CollectorType.DEVICE) {

                    uiWebView.loadUrl("javascript:" + callback + "('" + DataCollectorType.DEVICE + "');");
                } else if (collectorType == CollectorType.GPS) {

                    uiWebView.loadUrl("javascript:" + callback + "('" + DataCollectorType.GPS + "');");
                }
            }
        });
    }

    /**
     * 获取司机名称
     */
    private void getDriverName(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        int driverId = SystemHelper.getUser().getDriverId();
        Logger.i(Tags.SYSTEM, "SDK.getDriver: driverId=" + driverId);
        final Driver driver = DataBaseHelper.getDataBase().driverDao().getDriver(driverId);
        Logger.i(Tags.SYSTEM, "SDK.getDriver: driver = " + driver == null ? "null" : "!null");

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + driver.getName() + "');");
            }
        });
    }

    /**
     * 检查是否需要恢复团队驾驶
     */
    private void checkResume() {

        EventBus.getDefault().post(new SelfCheckEvent(SelfCheckEventType.RESUME));
    }

    /**
     * 上传日志
     *
     * @param data 数据
     */
    private void uploadLogs(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                int days = 3;
                String[] fileName = new String[days];
                for (int i = 0; i < days; i++) {

                    fileName[i] = TimeUtil.getPastDate(i, "yyyy-MM-dd", "US/Eastern");
                }

                FileUtil.zipLogs(new ZipProgress() {
                    @Override
                    public void onZipStart() {

                    }

                    @Override
                    public void onZipProgress(final int progress) {

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                int result = progress / 2;

                                uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "','" + result + "');");
                            }
                        });
                    }

                    @Override
                    public void onZipComplete(final String zipPath) {

                        if (TextUtils.isEmpty(zipPath)) {

                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                                }
                            });

                            return;
                        }

                        HttpFileRequest.Builder builder = new HttpFileRequest.Builder();
                        builder.url(Constants.API_UPLOAD_LOGS)
                                .addParam("access_token", SystemHelper.getUser().getAccessToken())
                                .addParam("log_file", new File(zipPath))
                                .uploadListener(new HttpFileUploadCallback() {
                                    @Override
                                    public void onUploadSuccess() {

                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_COMPLETE + "');");
                                            }
                                        });

                                        FileUtil.delFiles(zipPath);
                                        Logger.i(Tags.SYSTEM, "active upload logs success");
                                    }

                                    @Override
                                    public void onUploading(final int progress) {

                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                int result = 50 + progress / 2;

                                                uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "','" + result + "');");
                                            }
                                        });
                                    }

                                    @Override
                                    public void onUploadFailed() {

                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                                            }
                                        });

                                        Logger.w(Tags.SYSTEM, "active upload logs failed.");
                                    }
                                })
                                .build()
                                .uploadAsync();
                    }
                }, fileName);
            }
        });
    }

    /**
     * 获取rule列表
     *
     * @param data
     */
    private void getRuleList(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                final String content = AppUtil.readAssetsFile(context, "data/rules.txt");
                final int ruleId = DataBaseHelper.getDataBase().driverRuleDao().getRuleId(SystemHelper.getUser().getDriverId());
                final int userFunction = SystemHelper.getExemptionFunc();

                ((com.unitedbustech.eld.activity.Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + content + "','" + ruleId + "','" + userFunction + "');");
                    }
                });
            }
        });
    }

    /**
     * 切换规则,修改数据库，并上传服务器
     *
     * @param data 数据
     */
    private void setRule(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        data = JsonUtil.getString(jsonObject, "rule");

        final Rule rule = JsonUtil.parseObject(data, Rule.class);
        Logger.i(Tags.RULE, "select ruleId is " + rule.getId());
        if (rule != null) {

            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    int driverId = SystemHelper.getUser().getDriverId();
                    DriverRule driverRule = DataBaseHelper.getDataBase().driverRuleDao().getDriverRule(driverId);
                    //修改之后的ruleID与现在的ruleID不一样时进行改库操作
                    if (driverRule.getRuleId() != rule.getId()) {

                        //修改数据库
                        driverRule.setRuleId(rule.getId());
                        DataBaseHelper.getDataBase().driverRuleDao().insert(driverRule);
                        DataBaseHelper.getDataBase().ruleDao().insert(rule);
                        Logger.i(Tags.RULE, "rule insert db success");

                        User user = SystemHelper.getUser();
                        TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();
                        HttpRequest httpRequest = new HttpRequest.Builder()
                                .url(Constants.API_CHANGE_RULE)
                                .addParam("access_token", user.getAccessToken())
                                .addParam("rule_id", String.valueOf(rule.getId()))
                                .addParam("team_driving", String.valueOf(teamWorkState == null ? 0 : 1))
                                .addParam("datetime", String.valueOf(new Date().getTime()))
                                .build();

                        HosHandler.getInstance().init(rule);
                        EventCenter.getInstance().newRuleEvent(rule.getId());
                        RequestCacheService.getInstance().cachePost(httpRequest, RequestType.RULE);
                    }
                }
            });
        }
    }

    /**
     * 获取现在的规则
     */
    private void getCurrentRuleId(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final int ruleId = HosHandler.getInstance().getRule().getId();
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + ruleId + "');");
            }
        });
    }
}
