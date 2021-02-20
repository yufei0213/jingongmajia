package com.unitedbustech.eld.view;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.VehicleConnectType;
import com.unitedbustech.eld.common.vo.VehicleVo;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Vehicle;
import com.unitedbustech.eld.eventbus.AlertsRefreshViewEvent;
import com.unitedbustech.eld.eventbus.DashBoardMalFunctionEvent;
import com.unitedbustech.eld.eventbus.HosModelChangeEvent;
import com.unitedbustech.eld.eventbus.NetworkChangeEvent;
import com.unitedbustech.eld.eventbus.TeamWorkDashBoardChangeEvent;
import com.unitedbustech.eld.eventbus.VehicleConnectEvent;
import com.unitedbustech.eld.eventbus.VehicleSelectEvent;
import com.unitedbustech.eld.jsinterface.AlertJsInterface;
import com.unitedbustech.eld.jsinterface.DailylogJsInterface;
import com.unitedbustech.eld.jsinterface.DashboardJsInterface;
import com.unitedbustech.eld.jsinterface.DotJsInterface;
import com.unitedbustech.eld.jsinterface.DrivingJsInterface;
import com.unitedbustech.eld.jsinterface.DvirJsInterface;
import com.unitedbustech.eld.jsinterface.IftaJsInterface;
import com.unitedbustech.eld.jsinterface.SDKJsInterface;
import com.unitedbustech.eld.jsinterface.TeamWorkJsInterface;
import com.unitedbustech.eld.jsinterface.UserJsInterface;
import com.unitedbustech.eld.jsinterface.VehicleJsInterface;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.LanguageUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.web.JsInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author yufei0213
 * @date 2017/10/20
 * @description 自定义WebView
 */
public class UIWebView extends WebView {

    private static final String TAG = "UIWebView";

    public static final String EXTRA_PARAMS = "com.unitedbustech.eld.view.UIWebView.params";
    public static final String EXTRA_RESULT = "com.unitedbustech.eld.view.UIWebView.result";

    private static final String HOS_CHANGE = "Global.hosModelChangeMessage";
    private static final String VEHICLE_SELECT_CHANGE = "Global.vehicleSelectChange";
    private static final String VEHICLE_CONNECT_STATE_CHANGE = "Global.vehicleConnectStateChange";
    private static final String TEAM_WORK_CHANGE = "Global.teamWorkDashBoardChange";
    private static final String MALFUNCTION_CHANGE = "Global.malFunctionRefresh";
    private static final String ALERTS_CHANGE = "Global.alertsRefresh";
    private static final String NETWORK_STATE_CHANGE = "Global.networkStateChange";

    private static final int MALFUNCTION_SHOW = 1;
    private static final int MALFUNCTION_HIDE = 0;

    public static final int NETWORK_CONNECTED = 1;
    public static final int NETWORK_DISCONNECTED = 0;

    private boolean hosHasChange;
    private boolean vehicleSelectChange;
    private boolean vehicleConnectStateChange;
    private boolean teamWorkDashBoardChange;
    private boolean dashBoardMalFunctionChange;
    private boolean alertsChange;
    private boolean isNetworkChanged;

    private VehicleConnectEvent vehicleConnectEvent;
    private TeamWorkDashBoardChangeEvent teamWorkDashBoardChangeEvent;
    private DashBoardMalFunctionEvent dashBoardMalFunctionEvent;
    private AlertsRefreshViewEvent alertsRefreshViewEvent;
    private NetworkChangeEvent networkChangeEvent;

    private Context context;

    private boolean isAvailable;

    public UIWebView(Context context) {

        this(context, null);
    }

    public UIWebView(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        this.context = context;

        initWebViewParams();

        this.addJsInterface(SDKJsInterface.class,
                UserJsInterface.class,
                VehicleJsInterface.class,
                DashboardJsInterface.class,
                DailylogJsInterface.class,
                DrivingJsInterface.class,
                DvirJsInterface.class,
                DotJsInterface.class,
                AlertJsInterface.class,
                TeamWorkJsInterface.class,
                IftaJsInterface.class);
    }

    /**
     * 注册js接口
     *
     * @param classList class列表
     */
    public void addJsInterface(@NonNull Class<?>... classList) {

        for (Class entry : classList) {

            JsInterface jsInterface = (JsInterface) entry.getAnnotation(JsInterface.class);
            if (jsInterface == null) {

                continue;
            }

            String name = jsInterface.name();
            if (TextUtils.isEmpty(name)) {

                name = entry.getSimpleName();
            }

            try {

                Constructor constructor = entry.getConstructor(Context.class, this.getClass());
                Object object = constructor.newInstance(this.context, this);

                Method method = this.getClass().getMethod("addJavascriptInterface", Object.class, String.class);
                method.invoke(this, object, name);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    /**
     * 订阅通知
     */
    public void subscribeMsg() {

        if (EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().unregister(this);
        }

        EventBus.getDefault().register(this);
    }

    /**
     * 解除订阅通知
     */
    public void unSubscribeMsg() {

        EventBus.getDefault().unregister(this);
    }

    /**
     * 左上角按钮被点击
     */
    public void leftBtnClick() {

        this.loadUrl("javascript:Global.onLeftBtnClick();");
    }

    /**
     * 重新加载
     */
    public void reload() {

        this.isAvailable = true;

        String data = SystemHelper.getWebData();
        if (TextUtils.isEmpty(data)) {

            this.loadUrl("javascript:Global.onReload();");
        } else {

            data = data.replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"");
            this.loadUrl("javascript:Global.onReload('" + data + "');");
        }

        notifyChange();
    }

    /**
     * 页面暂停
     */
    public void pause() {

        this.isAvailable = false;
        this.loadUrl("javascript:Global.onPause();");
    }

    /**
     * 销毁
     */
    public void destroy() {

        this.isAvailable = false;
        this.loadUrl("javascript:Global.onDestroy();");
    }

    /**
     * 使用loadUrl方法。
     * 只写具体的html文件名。
     * 解决国际化的问题。
     *
     * @param fileName 文件名
     */
    public void loadFile(String fileName) {

        String url;
        if (LanguageUtil.getInstance().getLanguageType() == LanguageUtil.LANGUAGE_ZH) {

            url = Constants.WEB_PATH_ZH + fileName;
        } else {

            url = Constants.WEB_PATH_EN + fileName;
        }

        this.loadUrl(url);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHosChangeEvent(HosModelChangeEvent hosModelChangeEvent) {

        if (isAvailable) {

            notifyJsHosChange();
        } else {

            hosHasChange = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVehicleSelectEvent(VehicleSelectEvent vehicleSelectEvent) {

        if (isAvailable) {

            notifyVehicleSelectChange();
        } else {

            vehicleSelectChange = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVehicleConnectStateEvent(VehicleConnectEvent vehicleConnectEvent) {

        if (!SystemHelper.hasVehicle()) {

            return;
        }
        this.vehicleConnectEvent = vehicleConnectEvent;
        if (isAvailable) {

            notifyVehicleConnectStateChange();
        } else {

            vehicleConnectStateChange = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTeamWorkDashBoardChangeEvent(TeamWorkDashBoardChangeEvent teamWorkDashBoardChangeEvent) {

        this.teamWorkDashBoardChangeEvent = teamWorkDashBoardChangeEvent;
        if (isAvailable) {

            notifyTeamWorkChange();
        } else {

            teamWorkDashBoardChange = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDashBoardMalFunctionEvent(DashBoardMalFunctionEvent dashBoardMalFunctionEvent) {

        this.dashBoardMalFunctionEvent = dashBoardMalFunctionEvent;
        if (isAvailable) {

            notifyMalFunctionChange();
        } else {

            dashBoardMalFunctionChange = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAlertsRefreshViewEvent(AlertsRefreshViewEvent alertsRefreshViewEvent) {

        this.alertsRefreshViewEvent = alertsRefreshViewEvent;
        if (isAvailable) {

            notifyAlertsChange();
        } else {

            alertsChange = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkStateChange(NetworkChangeEvent networkChangeEvent) {

        this.networkChangeEvent = networkChangeEvent;
        if (isAvailable) {

            notifyNetworkStateChange();
        } else {

            isNetworkChanged = true;
        }
    }

    private void notifyChange() {

        if (hosHasChange) {

            notifyJsHosChange();
        }
        if (vehicleSelectChange) {

            notifyVehicleSelectChange();
        }
        if (vehicleConnectStateChange) {

            notifyVehicleConnectStateChange();
        }
        if (teamWorkDashBoardChange) {

            notifyTeamWorkChange();
        }
        if (dashBoardMalFunctionChange) {

            notifyMalFunctionChange();
        }
        if (alertsChange) {

            notifyAlertsChange();
        }
        if (isNetworkChanged) {

            notifyNetworkStateChange();
        }
    }

    private void notifyJsHosChange() {

        loadUrl("javascript:" + HOS_CHANGE + "();");
        hosHasChange = false;
    }

    private void notifyVehicleSelectChange() {

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                int vehicleId = SystemHelper.getUser().getVehicleId();
                if (vehicleId != 0) {

                    Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(vehicleId);
                    VehicleVo vehicleVo = new VehicleVo(vehicle);
                    final String vehicleStr = JsonUtil.toJsJSONString(vehicleVo);

                    UIWebView.this.post(new Runnable() {
                        @Override
                        public void run() {

                            loadUrl("javascript:" + VEHICLE_SELECT_CHANGE + "('" + vehicleStr + "');");
                        }
                    });
                } else {
                    UIWebView.this.vehicleConnectEvent = new VehicleConnectEvent(VehicleConnectType.CLEAR);
                    UIWebView.this.post(new Runnable() {
                        @Override
                        public void run() {

                            loadUrl("javascript:" + VEHICLE_SELECT_CHANGE + "();");
                        }
                    });
                }
            }
        });

        vehicleSelectChange = false;
    }

    private void notifyVehicleConnectStateChange() {

        loadUrl("javascript:" + VEHICLE_CONNECT_STATE_CHANGE + "('" + vehicleConnectEvent.getType() + "');");
        vehicleConnectStateChange = false;
    }

    private void notifyTeamWorkChange() {

        loadUrl("javascript:" + TEAM_WORK_CHANGE + "('" + JsonUtil.toJsJSONString(teamWorkDashBoardChangeEvent) + "');");
        teamWorkDashBoardChange = false;
    }

    private void notifyMalFunctionChange() {

        if (dashBoardMalFunctionEvent.isShow()) {

            loadUrl("javascript:" + MALFUNCTION_CHANGE + "('" + MALFUNCTION_SHOW + "');");
        } else {

            loadUrl("javascript:" + MALFUNCTION_CHANGE + "('" + MALFUNCTION_HIDE + "');");
        }

        dashBoardMalFunctionChange = false;
    }

    private void notifyAlertsChange() {

        loadUrl("javascript:" + ALERTS_CHANGE + "('" + JsonUtil.toJsJSONString(alertsRefreshViewEvent) + "');");
        alertsChange = false;
    }

    private void notifyNetworkStateChange() {

        if (networkChangeEvent.isConnected()) {

            loadUrl("javascript:" + NETWORK_STATE_CHANGE + "('" + NETWORK_CONNECTED + "');");
        } else {

            loadUrl("javascript:" + NETWORK_STATE_CHANGE + "('" + NETWORK_DISCONNECTED + "');");
        }

        isNetworkChanged = false;
    }

    /**
     * 交给外界主动调用离线。
     * 为了初始化时的调用
     */
    public void notifyOffline() {

        loadUrl("javascript:" + NETWORK_STATE_CHANGE + "('" + NETWORK_DISCONNECTED + "');");
    }

    /**
     * 初始化webview
     */
    protected void initWebViewParams() {

        getSettings().setTextZoom(100);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDomStorageEnabled(true);

        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);

        setWebViewClient(webViewclient);
        setWebChromeClient(webChromeClient);

        boolean multiTouch = ((Activity) context).getIntent().getBooleanExtra("multi_touch", false);
        if (!multiTouch) {

            setOnTouchListener(touchListener);
        }

        setOnLongClickListener(longClickListener);

        try {

            if (Build.VERSION.SDK_INT >= 16) {

                Class<?> clazz = this.getSettings().getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);

                if (method != null) {

                    method.invoke(this.getSettings(), true);
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private WebViewClient webViewclient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {

            isAvailable = true;

            String param = ((Activity) context).getIntent().getStringExtra(EXTRA_PARAMS);
            if (!TextUtils.isEmpty(param)) {

                param = param.replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"");
                UIWebView.this.loadUrl("javascript:Global.init('" + param + "');");
            } else {

                UIWebView.this.loadUrl("javascript:Global.init();");
            }
        }
    };

    private WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

            return super.onJsAlert(view, url, message, result);
        }
    };

    private OnLongClickListener longClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            return true;
        }
    };

    private OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {

            int action = arg1.getAction();

            switch (action) {

                case MotionEvent.ACTION_POINTER_2_DOWN:
                case MotionEvent.ACTION_POINTER_3_DOWN:

                    return true;

                default:
                    break;
            }

            return false;
        }
    };
}
