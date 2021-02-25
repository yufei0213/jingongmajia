package com.interest.calculator.jsinterface;//package com.interest.eld.jsinterface;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.text.TextUtils;
//import android.webkit.JavascriptInterface;
//import android.widget.Toast;
//
//import com.alibaba.fastjson.JSONObject;
//import com.interest.eld.R;
//import com.interest.eld.activity.WebActivity;
//import com.interest.eld.logs.Logger;
//import com.interest.eld.util.AppUtil;
//import com.interest.eld.util.FirebaseRequestUtil;
//import com.interest.eld.util.JsonUtil;
//import com.interest.eld.view.DatePickerDialog;
//import com.interest.eld.view.DotLoadingDialog;
//import com.interest.eld.view.DotPromptDialog;
//import com.interest.eld.view.HorizontalDialog;
//import com.interest.eld.view.LoadingDialog;
//import com.interest.eld.view.PromptDialog;
//import com.interest.eld.view.TimePickerDialog;
//import com.interest.eld.view.UIWebView;
//import com.interest.eld.view.VerticalDialog;
//import com.interest.eld.web.BaseJsInterface;
//import com.interest.eld.web.JsInterface;
//
//import static android.app.Activity.RESULT_CANCELED;
//import static android.app.Activity.RESULT_OK;
//
///**
// * @author yufei0213
// * @date 2017/12/6
// * @description webview 接口基类
// */
//@JsInterface(name = "sdk")
//public class SDKJsInterface extends BaseJsInterface {
//
//    private static final String TAG = "SDKJsInterface";
//
//    private static final int DIALOG_ICON_AMAZING = 1;
//    private static final int DIALOG_ICON_AWKWARD = 2;
//    private static final int DIALOG_ICON_GREATE = 3;
//    private static final int DIALOG_ICON_HELLO = 4;
//    private static final int DIALOG_ICON_LOVE = 5;
//    private static final int DIALOG_ICON_MSG = 6;
//    private static final int DIALOG_ICON_QUESTION = 7;
//    private static final int DIALOG_ICON_SLEEP = 8;
//    private static final int DIALOG_CANCELABLE = 1;
//    private static final int DIALOG_NOCANCELABLE = -1;
//
//    private static final String NOTIFICATION_SWITCH = "notification_switch";
//
//    private static final String SMS_SWITCH = "sms_switch";
//
//    private static final String SHOW_LOADING = "showLoading";
//    private static final String HIDE_LOADING = "hideLoading";
//    private static final String SHOW_DOT_LOADING = "showDotLoading";
//    private static final String HIDE_DOT_LOADING = "hideDotLoading";
//    private static final String SHOW_DIALOG = "showDialog";
//    private static final String SHOW_SPANNABLE_DIALOG = "showSpannableStringDialog";
//    private static final String SHOW_VERTICAL_DIALOG = "showVerticalDialog";
//    private static final String SHOW_PROMPT = "showPrompt";
//    private static final String SHOW_DOT_PROMPT = "showDotPrompt";
//    private static final String SHOW_MESSAGE = "showMessage";
//    private static final String SHOW_TIME_PICKER = "showTimePicker";
//    private static final String SHOW_DATE_PICKER = "showDatePicker";
//    private static final String OPEN_PAGE = "openPage";
//    private static final String OPEN_GRID_PAGE = "openGridPage";
//    private static final String OPEN_MAIN_PAGE = "openMainPage";
//    private static final String OPEN_DAILY_LOG_PAGE = "openDailyLogPage";
//    private static final String GET_GEO_LOCATION = "getGeoLocation";
//    private static final String GET_LOCATION = "getLocation";
//    private static final String GET_REMARK_LIST = "getRemarkList";
//    private static final String GET_DEFECT_LIST = "getDefectList";
//    private static final String GET_LANGUAGE_SETTING = "getLanguageSetting";
//    private static final String SET_LANGUAGE = "setLanguage";
//    private static final String GET_NOTIFICATION_SETTING = "getNotificationSetting";
//    private static final String SET_NOTIFICATION = "setNotificationSetting";
//    private static final String SET_WEB_DATA = "setWebData";
//    private static final String CLEAR_WEB_DATA = "clearWebData";
//    private static final String GET_DATE = "getDate";
//    private static final String GET_OFFSET_DATE = "getOffsetDate";
//    private static final String FINISH = "back";
//    private static final String FINISH_FOR_RESULT = "backForResult";
//    private static final String LOG = "log";
//    private static final String GET_VERSION_NAME = "getVersionName";
//    private static final String OPEN_LAUNCHER_ANIMATION = "openLauncherAnimation";
//    private static final String CLOSE_LAUNCHER_ANIMATION = "closeLauncherAnimation";
//    private static final String COLLECT_FIREBASE_EVENT = "collectFirebaseEvent";
//    private static final String COLLECT_FIREBASE_SCREEN = "collectFirebaseScreen";
//    private static final String SEND_EMAIL = "sendEmail";
//    private static final String CALL = "call";
//    private static final String GET_TOTAL_HOUR = "getTotalHour";
//    private static final String GET_DATA_COLLECTOR_TYPE = "getDataCollectorType";
//    private static final String GET_DRIVER_NAME = "getDriverName";
//    private static final String CHECK_RESUME = "checkResume";
//    private static final String UPLOAD_LOGS = "uploadLogs";
//    private static final String GET_RULE_LIST = "getRuleList";
//    private static final String SET_RULE = "setRule";
//    private static final String GET_CURRENT_RULE = "getCurrentRuleId";
//    private static final String INTERFACE_SHOW_EXPIRE_DAY = "showExpireDay";
//
//    private LoadingDialog loadingDialog;
//    private DotLoadingDialog dotLoadingDialog;
//
//    public SDKJsInterface(Context context, UIWebView uiWebView) {
//
//        super(context, uiWebView);
//    }
//
//    @Override
//    @JavascriptInterface
//    public void method(String params) {
//
//        JSONObject obj = JsonUtil.parseObject(params);
//
//        String method = JsonUtil.getString(obj, METHOD_KEY);
//        String data = JsonUtil.getString(obj, DATA_KEY);
//
//        switch (method) {
//
//            case SHOW_LOADING:
//
//                showLoading();
//                break;
//            case HIDE_LOADING:
//
//                hideLoading();
//                break;
//            case SHOW_DIALOG:
//
//                showDialog(data);
//                break;
//            case SHOW_DOT_LOADING:
//
//                showDotLoading();
//                break;
//            case HIDE_DOT_LOADING:
//
//                hideDotLoading();
//                break;
//            case SHOW_VERTICAL_DIALOG:
//
//                showVerticalDialog(data);
//                break;
//            case SHOW_PROMPT:
//
//                showPrompt(data);
//                break;
//            case SHOW_DOT_PROMPT:
//
//                showDotPrompt(data);
//                break;
//            case SHOW_MESSAGE:
//
//                showMessage(data);
//                break;
//            case SHOW_TIME_PICKER:
//
//                showTimePicker(data);
//                break;
//            case SHOW_DATE_PICKER:
//
//                showDatePicker(data);
//                break;
//            case OPEN_PAGE:
//
//                openPage(data);
//                break;
//            case FINISH:
//
//                finish(data);
//                break;
//            case FINISH_FOR_RESULT:
//
//                finishForResult(data);
//                break;
//            case LOG:
//
//                log(data);
//                break;
//            case GET_VERSION_NAME:
//
//                getVersionName(data);
//                break;
//            case COLLECT_FIREBASE_EVENT:
//
//                collectFirebaseEvent(data);
//                break;
//            case COLLECT_FIREBASE_SCREEN:
//
//                collectFirebaseScreen(data);
//                break;
//            case SEND_EMAIL:
//
//                sendEmail(data);
//                break;
//            case CALL:
//
//                call(data);
//                break;
//            default:
//                break;
//        }
//    }
//
//    /**
//     * 打开子页面
//     *
//     * @param data 页面数据
//     */
//    private void openPage(String data) {
//
//        JSONObject obj = JsonUtil.parseObject(data);
//
//        String fileName = JsonUtil.getString(obj, "url");
//        String title = JsonUtil.getString(obj, "title");
//        String params = JsonUtil.getString(obj, "params");
//        int canBack = JsonUtil.getInt(obj, "canBack");
//
//        Intent intent = WebActivity.newIntent(context, fileName, title, canBack, params);
//        context.startActivity(intent);
//    }
//
//    /**
//     * 显示信息（toast）
//     */
//    private void showMessage(String data) {
//
//        JSONObject jsonObject = JsonUtil.parseObject(data);
//        final String message = JsonUtil.getString(jsonObject, DATA_KEY);
//
//        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//    }
//
//    /**
//     * 关闭当前页面
//     */
//    private void finish(String data) {
//
//        JSONObject obj = JsonUtil.parseObject(data);
//        int index = JsonUtil.getInt(obj, "index");
//    }
//
//    /**
//     * 关闭当前界面，并且传递参数给父界面
//     *
//     * @param data 数据
//     */
//    private void finishForResult(String data) {
//
//        JSONObject obj = JsonUtil.parseObject(data);
//
//        String resultData = JsonUtil.getString(obj, "data");
//
//        Activity activity = (Activity) context;
//        if (!TextUtils.isEmpty(resultData)) {
//
//            Intent intent = new Intent();
//            intent.putExtra(uiWebView.EXTRA_RESULT, resultData);
//
//            activity.setResult(RESULT_OK, intent);
//        } else {
//
//            activity.setResult(RESULT_CANCELED);
//        }
//
//        activity.finish();
//    }
//
//    /**
//     * 输出日志
//     *
//     * @param data 日志数据
//     */
//    private void log(String data) {
//
//        JSONObject obj = JsonUtil.parseObject(data);
//        String tag = JsonUtil.getString(obj, "tag");
//        String info = JsonUtil.getString(obj, "info");
//        int level = JsonUtil.getInt(obj, "level");
//
//        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(info)) {
//
//            if (level > 0) {
//
//                Logger.i(tag, info);
//            } else {
//
//                Logger.d(tag, info);
//            }
//        }
//    }
//
//    /**
//     * 展示loading
//     */
//    private void showLoading() {
//
//        Activity activity = (Activity) context;
//        if (!activity.isFinishing()) {
//
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                    loadingDialog = new LoadingDialog(context);
//                    loadingDialog.show();
//                }
//            });
//        }
//    }
//
//    /**
//     * 隐藏loading
//     */
//    private void hideLoading() {
//
//        ((Activity) context).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                if (loadingDialog != null) {
//
//                    loadingDialog.dismiss();
//                    loadingDialog = null;
//                }
//            }
//        });
//    }
//
//    /**
//     * 展示loading
//     */
//    private void showDotLoading() {
//
//        ((Activity) context).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                dotLoadingDialog = new DotLoadingDialog(context);
//                dotLoadingDialog.show();
//            }
//        });
//    }
//
//    /**
//     * 隐藏loading
//     */
//    private void hideDotLoading() {
//
//        ((Activity) context).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                if (dotLoadingDialog != null) {
//
//                    dotLoadingDialog.dismiss();
//                    dotLoadingDialog = null;
//                }
//            }
//        });
//    }
//
//    /**
//     * 显示弹窗
//     *
//     * @param data 弹窗配置
//     */
//    private void showDialog(String data) {
//
//        JSONObject jsonObject = JsonUtil.parseObject(data);
//        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
//
//        int iconId = JsonUtil.getInt(jsonObject, "icon");
//        String text = JsonUtil.getString(jsonObject, "text");
//        String negativeBtnText = JsonUtil.getString(jsonObject, "negativeBtnText");
//        String neutralBtnText = JsonUtil.getString(jsonObject, "neutralBtnText");
//        String positiveBtnText = JsonUtil.getString(jsonObject, "positiveBtnText");
//        int cancelable = JsonUtil.getInt(jsonObject, "cancelable");
//
//        int iconResId = 0;
//        switch (iconId) {
//
//            case DIALOG_ICON_AMAZING:
//
//                iconResId = R.drawable.ic_emoji_amazing;
//                break;
//            case DIALOG_ICON_AWKWARD:
//
//                iconResId = R.drawable.ic_emoji_awkward;
//                break;
//            case DIALOG_ICON_GREATE:
//
//                iconResId = R.drawable.ic_emoji_greate;
//                break;
//            case DIALOG_ICON_HELLO:
//
//                iconResId = R.drawable.ic_emoji_hi;
//                break;
//            case DIALOG_ICON_LOVE:
//
//                iconResId = R.drawable.ic_emoji_love;
//                break;
//            case DIALOG_ICON_MSG:
//
//                iconResId = R.drawable.ic_emoji_msg;
//                break;
//            case DIALOG_ICON_QUESTION:
//
//                iconResId = R.drawable.ic_emoji_question;
//                break;
//            case DIALOG_ICON_SLEEP:
//
//                iconResId = R.drawable.ic_emoji_sleep;
//                break;
//            default:
//                break;
//        }
//
//        final HorizontalDialog.Builder builder = new HorizontalDialog.Builder(context)
//                .setIcon(iconResId)
//                .setText(text);
//
//        if (!TextUtils.isEmpty(positiveBtnText)) {
//
//            builder.setPositiveBtn(positiveBtnText, new HorizontalDialog.OnClickListener() {
//                @Override
//                public void onClick(final HorizontalDialog dialog, final int which) {
//
//                    ((Activity) context).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            dialog.dismiss();
//                            if (!TextUtils.isEmpty(callback)) {
//
//                                uiWebView.loadUrl("javascript:" + callback + "('" + which + "');");
//                            }
//                        }
//                    });
//                }
//            });
//        } else {
//
//            builder.setNegativeBtn(negativeBtnText, new HorizontalDialog.OnClickListener() {
//                @Override
//                public void onClick(final HorizontalDialog dialog, final int which) {
//
//                    ((Activity) context).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            dialog.dismiss();
//                            if (!TextUtils.isEmpty(callback)) {
//
//                                uiWebView.loadUrl("javascript:" + callback + "('" + which + "');");
//                            }
//                        }
//                    });
//                }
//            });
//            builder.setNeutralBtn(neutralBtnText, new HorizontalDialog.OnClickListener() {
//                @Override
//                public void onClick(final HorizontalDialog dialog, final int which) {
//
//                    ((Activity) context).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            dialog.dismiss();
//                            if (!TextUtils.isEmpty(callback)) {
//
//                                uiWebView.loadUrl("javascript:" + callback + "('" + which + "');");
//                            }
//                        }
//                    });
//                }
//            });
//        }
//
//        builder.setCancelable(cancelable == DIALOG_CANCELABLE);
//
//        ((Activity) context).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                builder.build().show();
//            }
//        });
//    }
//
//    /**
//     * 显示弹窗
//     *
//     * @param data 弹窗配置
//     */
//    private void showVerticalDialog(String data) {
//
//        JSONObject jsonObject = JsonUtil.parseObject(data);
//        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
//
//        int iconId = JsonUtil.getInt(jsonObject, "icon");
//        String text = JsonUtil.getString(jsonObject, "text");
//        String negativeBtnText = JsonUtil.getString(jsonObject, "negativeBtnText");
//        String neutralBtnText = JsonUtil.getString(jsonObject, "neutralBtnText");
//        String positiveBtnText = JsonUtil.getString(jsonObject, "positiveBtnText");
//        int cancelable = JsonUtil.getInt(jsonObject, "cancelable");
//
//        int iconResId = 0;
//        switch (iconId) {
//
//            case DIALOG_ICON_AMAZING:
//
//                iconResId = R.drawable.ic_emoji_amazing;
//                break;
//            case DIALOG_ICON_AWKWARD:
//
//                iconResId = R.drawable.ic_emoji_awkward;
//                break;
//            case DIALOG_ICON_GREATE:
//
//                iconResId = R.drawable.ic_emoji_greate;
//                break;
//            case DIALOG_ICON_HELLO:
//
//                iconResId = R.drawable.ic_emoji_hi;
//                break;
//            case DIALOG_ICON_LOVE:
//
//                iconResId = R.drawable.ic_emoji_love;
//                break;
//            case DIALOG_ICON_MSG:
//
//                iconResId = R.drawable.ic_emoji_msg;
//                break;
//            case DIALOG_ICON_QUESTION:
//
//                iconResId = R.drawable.ic_emoji_question;
//                break;
//            case DIALOG_ICON_SLEEP:
//
//                iconResId = R.drawable.ic_emoji_sleep;
//                break;
//            default:
//                break;
//        }
//
//        final VerticalDialog.Builder builder = new VerticalDialog.Builder(context)
//                .setIcon(iconResId)
//                .setTipText(text);
//
//        if (!TextUtils.isEmpty(positiveBtnText)) {
//
//            builder.setPositiveBtn(positiveBtnText, new VerticalDialog.OnClickListener() {
//                @Override
//                public void onClick(final VerticalDialog dialog, final int which) {
//
//                    ((Activity) context).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            dialog.dismiss();
//                            uiWebView.loadUrl("javascript:" + callback + "('" + which + "');");
//                        }
//                    });
//                }
//            });
//        } else {
//
//            builder.setNegativeBtn(negativeBtnText, new VerticalDialog.OnClickListener() {
//                @Override
//                public void onClick(final VerticalDialog dialog, final int which) {
//
//                    ((Activity) context).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            dialog.dismiss();
//                            uiWebView.loadUrl("javascript:" + callback + "('" + which + "');");
//                        }
//                    });
//                }
//            });
//            builder.setNeutralBtn(neutralBtnText, new VerticalDialog.OnClickListener() {
//                @Override
//                public void onClick(final VerticalDialog dialog, final int which) {
//
//                    ((Activity) context).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            dialog.dismiss();
//                            uiWebView.loadUrl("javascript:" + callback + "('" + which + "');");
//                        }
//                    });
//                }
//            });
//        }
//
//        builder.setCancelable(cancelable == DIALOG_CANCELABLE);
//
//        ((Activity) context).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                builder.build().show();
//            }
//        });
//    }
//
//    /**
//     * 展示成功或失败提示
//     *
//     * @param data 数据
//     */
//    private void showPrompt(String data) {
//
//        JSONObject jsonObject = JsonUtil.parseObject(data);
//        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
//        final int status = JsonUtil.getInt(jsonObject, "type");
//
//        ((Activity) context).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                new PromptDialog.Builder(context)
//                        .type(status)
//                        .listener(new PromptDialog.OnHideListener() {
//                            @Override
//                            public void onHide() {
//
//                                if (!TextUtils.isEmpty(callback)) {
//
//                                    uiWebView.loadUrl("javascript:" + callback + "();");
//                                }
//                            }
//                        })
//                        .build()
//                        .show();
//            }
//        });
//    }
//
//    /**
//     * 展示成功或失败提示
//     *
//     * @param data 数据
//     */
//    private void showDotPrompt(String data) {
//
//        JSONObject jsonObject = JsonUtil.parseObject(data);
//        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
//        final int status = JsonUtil.getInt(jsonObject, "type");
//
//        ((Activity) context).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                new DotPromptDialog.Builder(context)
//                        .type(status)
//                        .listener(new DotPromptDialog.OnHideListener() {
//                            @Override
//                            public void onHide() {
//
//                                if (!TextUtils.isEmpty(callback)) {
//
//                                    uiWebView.loadUrl("javascript:" + callback + "();");
//                                }
//                            }
//                        })
//                        .build()
//                        .show();
//            }
//        });
//    }
//
//    /**
//     * 打开时间选择器
//     *
//     * @param data 初始数据
//     *             返回选择时间的秒数
//     */
//    private void showTimePicker(String data) {
//
//        JSONObject jsonObject = JsonUtil.parseObject(data);
//        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
//        int second = JsonUtil.getInt(jsonObject, "nowSecond");
//        int oneDayHours = JsonUtil.getInt(jsonObject, "oneDayHours");
//
//        new TimePickerDialog.Builder(context)
//                .initSecond(second)
//                .oneDayHours(oneDayHours)
//                .listener(new TimePickerDialog.OnDoneClickListener() {
//                    @Override
//                    public void onDone(final int seconds) {
//
//                        ((Activity) context).runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                uiWebView.loadUrl("javascript:" + callback + "('" + seconds + "');");
//                            }
//                        });
//                    }
//                })
//                .build()
//                .show();
//    }
//
//    /**
//     * 打开时间选择器
//     *
//     * @param data 初始数据
//     */
//    private void showDatePicker(String data) {
//
//        JSONObject jsonObject = JsonUtil.parseObject(data);
//        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
//        String date = JsonUtil.getString(jsonObject, "date");
//
//        new DatePickerDialog.Builder(context)
//                .initDate(date)
//                .listener(new DatePickerDialog.OnDoneClickListener() {
//                    @Override
//                    public void onDone(final String date) {
//
//                        ((Activity) context).runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                uiWebView.loadUrl("javascript:" + callback + "('" + date + "');");
//                            }
//                        });
//                    }
//                })
//                .build()
//                .show();
//    }
//
//    /**
//     * 获取版本
//     */
//    private void getVersionName(String data) {
//
//        JSONObject jsonObject = JsonUtil.parseObject(data);
//        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
//
//        ((Activity) context).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                uiWebView.loadUrl("javascript:" + callback + "('" + AppUtil.getVersionName(context) + "');");
//            }
//        });
//    }
//
//    /**
//     * 收集Firebase事件
//     */
//    private void collectFirebaseEvent(String jsonParams) {
//
//        FirebaseRequestUtil.getInstance((Activity) context).logEventForJsonString(jsonParams);
//    }
//
//    /**
//     * 收集Firebase屏幕
//     */
//    private void collectFirebaseScreen(String jsonParams) {
//
//        FirebaseRequestUtil.getInstance((Activity) context).logScreenForJsonString(jsonParams);
//    }
//
//    /**
//     * 发送邮件
//     */
//    private void sendEmail(String data) {
//
//        JSONObject jsonObject = JsonUtil.parseObject(data);
//        String emailUrl = JsonUtil.getString(jsonObject, "emailUrl");
//
//        try {
//
//            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + emailUrl));
//            context.startActivity(intent);
//        } catch (Exception e) {
//
//            Toast.makeText(context, R.string.not_found_mail, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * 拨打电话
//     */
//    private void call(String data) {
//
//        JSONObject jsonObject = JsonUtil.parseObject(data);
//        String phoneNo = JsonUtil.getString(jsonObject, "phoneNo");
//        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNo));
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//    }
//}
