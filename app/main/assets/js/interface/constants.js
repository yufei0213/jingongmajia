/**
 * @author yufei0213
 * @date 2018/01/08
 * @description 配置文件
 */
var Constants = {};

Constants.SDK_INTERFACE = "sdk";
Constants.USER_INTERFACE = "user";
Constants.TEAMWORK_INTERFACE = "teamwork";
Constants.VEHICLE_INTERFACE = "vehicle";
Constants.DASHBOARD_INTERFACE = "dashboard";
Constants.DAILYLOG_INTERFACE = "dailylog";
Constants.DVIR_INTERFACE = "dvir";
Constants.DOT_INTERFACE = "dot";
Constants.ALERT_INTERFACE = "alert";
Constants.DRIVING_INTERFACE = "driving";
Constants.IFTA_INTERFACE = "ifta";

Constants.SDK_INTERFACE_SHOW_LOADING = "showLoading";
Constants.SDK_INTERFACE_HIDE_LOADING = "hideLoading";
Constants.SDK_INTERFACE_SHOW_DOT_LOADING = "showDotLoading";
Constants.SDK_INTERFACE_HIDE_DOT_LOADING = "hideDotLoading";
Constants.SDK_INTERFACE_SHOW_DIALOG = "showDialog";
Constants.SDK_INTERFACE_SHOW_SPANNABLE_DIALOG = "showSpannableStringDialog";
Constants.SDK_INTERFACE_SHOW_VERTICAL_DIALOG = "showVerticalDialog";
Constants.SDK_INTERFACE_SHOW_PROMPT = "showPrompt";
Constants.SDK_INTERFACE_SHOW_DOT_PROMPT = "showDotPrompt";
Constants.SDK_INTERFACE_SHOW_EXPIRE_DAY = "showExpireDay";
Constants.SDK_INTERFACE_SHOW_TIME_PICKER = "showTimePicker";
Constants.SDK_INTERFACE_SHOW_DATE_PICKER = "showDatePicker";
Constants.SDK_INTERFACE_OPENPAGE = "openPage";
Constants.SDK_INTERFACE_OPEN_GRID_PAGE = "openGridPage";
Constants.SDK_INTERFACE_OPEN_MAIN_PAGE = "openMainPage";
Constants.SDK_INTERFACE_OPEN_DAILY_LOG_PAGE = "openDailyLogPage";
Constants.SDK_INTERFACE_GET_GEO_LOCATION = "getGeoLocation";
Constants.SDK_INTERFACE_GET_LOCATION = "getLocation";
Constants.SDK_INTERFACE_GET_REMARK_LIST = "getRemarkList";
Constants.SDK_INTERFACE_GET_DEFECT_LIST = "getDefectList";
Constants.SDK_INTERFACE_GET_LANGUAGE_SETTING = "getLanguageSetting";
Constants.SDK_INTERFACE_SET_LANGUAGE = "setLanguage";
Constants.SDK_INTERFACE_GET_NOTIFICATION_SETTING = "getNotificationSetting";
Constants.SDK_INTERFACE_SET_NOTIFICATION = "setNotificationSetting";
Constants.SDK_INTERFACE_SET_WEBDATA = "setWebData";
Constants.SDK_INTERFACE_CLEAR_WEB_DATA = "clearWebData";
Constants.SDK_INTERFACE_GET_DATE = "getDate";
Constants.SDK_INTERFACE_GET_OFFSET_DATE = "getOffsetDate";
Constants.SDK_INTERFACE_BACK = "back";
Constants.SDK_INTERFACE_BACK_FOR_RESULT = "backForResult";
Constants.SDK_INTERFACE_LOG = "log";
Constants.SDK_INTERFACE_SHOW_MESSAGE = "showMessage";
Constants.SDK_INTERFACE_GET_VERSION_NAME = "getVersionName";
Constants.SDK_INTERFACE_OPEN_LAUNCHER_ANIMATION = "openLauncherAnimation";
Constants.SDK_INTERFACE_CLOSE_LAUNCHER_ANIMATION = "closeLauncherAnimation";
Constants.SDK_INTERFACE_COLLECT_FIREBASE_EVENT = "collectFirebaseEvent";
Constants.SDK_INTERFACE_COLLECT_FIREBASE_SCREEN = "collectFirebaseScreen";
Constants.SDK_INTERFACE_SEND_EMAIL = "sendEmail";
Constants.SDK_INTERFACE_CALL = "call";
Constants.SDK_INTERFACE_GET_TOTAL_HOUR = "getTotalHour";
Constants.SDK_INTERFACE_GET_DATA_COLLECTOR_TYPE = "getDataCollectorType";
Constants.SDK_INTERFACE_GET_DRIVER_NAME = "getDriverName";
Constants.SDK_INTERFACE_UPLOAD_LOGS = "uploadLogs";
Constants.SDK_INTERFACE_CHECK_RESUME = "checkResume";
Constants.SDK_INTERFACE_GET_RULE_LIST = "getRuleList";
Constants.SDK_INTERFACE_SET_RULE = "setRule";
Constants.SDK_INTERFACE_GET_CURRENT_RULE = "getCurrentRuleId";

Constants.USER_INTERFACE_GET_USER = "getUser";
Constants.USER_INTERFACE_GET_DRIVER = "getDriver";
Constants.USER_INTERFACE_GET_USERROLE = "getUserRole";
Constants.USER_INTERFACE_GET_USERFUNC = "getUserFunc";
Constants.USER_INTERFACE_GET_CARRIER = "getCarrier";
Constants.USER_INTERFACE_UPDATE_DRIVER = "updateDriverInfo";
Constants.USER_INTERFACE_UPDATE_PASSWORD = "updatePassword";
Constants.USER_INTERFACE_LOGOUT_REQUEST = "logoutRequest";
Constants.USER_INTERFACE_LOGOUT_LOCAL = "logoutLocal";
Constants.USER_INTERFACE_GET_DRIVER_NOTCERTIFIED_ALERT_COUNT = "getDriverNotCertifiedAlertCount";

Constants.TEAMWORK_INTERFACE_GET_COPILOT_LIST = "getCopilotList";
Constants.TEAMWORK_INTERFACE_INVITE_COPILOT = "pilotInviteCopilot";
Constants.TEAMWORK_INTERFACE_PILOT_CANCEL_INVITE_COPILOT = "pilotCancelInviteCopilot";
Constants.TEAMWORK_INTERFACE_PILOT_REQUEST_SWITCH = "pilotRequestSwitch";
Constants.TEAMWORK_INTERFACE_PILOT_CANCEL_SWITCH_REQUEST = "pilotCancelSwitchRequest";
Constants.TEAMWORK_INTERFACE_PILOT_REMOVE_COPILOT = "pilotRemoveCopilot";
Constants.TEAMWORK_INTERFACE_COPILOT_EXIT = "copilotExit";

Constants.VEHICLE_INTERFACE_GET_VEHICLE_LIST = "getVehicleList";
Constants.VEHICLE_INTERFACE_GET_CURRENT_VEHICLE = "getCurrentVehicle";
Constants.VEHICLE_INTERFACE_CONNECT_VEHICLE = "connectVehicle";
Constants.VEHICLE_INTERFACE_DISCONNECT_VEHICLE = "disconnectVehicle";
Constants.VEHICLE_INTERFACE_DISCONNECT_VEHICLE_AUTO = "disconnectVehicleAuto";
Constants.VEHICLE_INTERFACE_GET_VEHICLE_MALFUCNTION_LIST = "getVehicleMalfunctionList";
Constants.VEHICLE_INTERFACE_CHANGE_ODOMETER = "changeOdometer";

Constants.DASHBOARD_INTERFACE_GET_DASHBOARD_TIME = "getDashboardTime";
Constants.DASHBOARD_INTERFACE_GET_OFF_LINE = "getOffline";
Constants.DASHBOARD_INTERFACE_GET_DRIVER_STATE = "getDriverState";
Constants.DASHBOARD_INTERFACE_OPEN_START_BREAK_DIALOG = "openStartBreakDialog";
Constants.DASHBOARD_INTERFACE_CLOSE_START_BREAK_DIALOG = "closeStartBreakDialog";
Constants.DASHBOARD_INTERFACE_OPEN_ADVERSE_DRIVING_DIALOG = "openAdverseDrivingDialog";
Constants.DASHBOARD_INTERFACE_CLOSE_ADVERSE_DRIVING_DIALOG = "closeAdverseDrivingDialog";
Constants.DASHBOARD_INTERFACE_GET_ECM_INFO = "getEcmInfo";
Constants.DASHBOARD_INTERFACE_IS_BREAK = "isBreak";
Constants.DASHBOARD_INTERFACE_SHIPPING_DIALOG = "openShippingDialog";
Constants.DASHBOARD_INTERFACE_SAVE_SHIPPING = "saveShipping";
Constants.DASHBOARD_INTERFACE_GET_EXPIRE_DAY = "getExpireDay";

Constants.DAILYLOG_INTERFACE_GET_GRID_DATA_BY_INDEX = "getGridDataByIndex";
Constants.DAILYLOG_INTERFACE_GET_TODAY_GRID = "getTodayGrid";
Constants.DAILYLOG_INTERFACE_GET_DAILY_LOG_SUMMARY = "getDailyLogSummary";
Constants.DAILYLOG_INTERFACE_CHANGE_STATE = "changeStatus";
Constants.DAILYLOG_INTERFACE_GET_DAILY_LOG_DETAIL_BY_INDEX = "getDailyLogDetailByIndex";
Constants.DAILYLOG_INTERFACE_GET_PROFILE = "getProfile";
Constants.DAILYLOG_INTERFACE_UPDATE_PROFILE = "updateProfile";
Constants.DAILYLOG_INTERFACE_GET_SIGN = "getSign";
Constants.DAILYLOG_INTERFACE_UPLOAD_SIGN = "uploadSign";
Constants.DAILYLOG_INTERFACE_GET_LOG_DETAIL = "getLogDetail";
Constants.DAILYLOG_INTERFACE_MODIFY_EVENT = "modifyEvent";
Constants.DAILYLOG_INTERFACE_NEW_EVENT = "newEvent";
Constants.DAILYLOG_INTERFACE_ADD_REMARK = "addRemark";
Constants.DAILYLOG_INTERFACE_GET_STATUS_BY_TIME = "getStatusByTime";
Constants.DAILYLOG_INTERFACE_GET_OUT_OF_LINE_EVENT = "getOutOfLineEvents";
Constants.DAILYLOG_INTERFACE_GET_PAST_DAY_EVENT_SECOND = "getPastDayEventSecond";
Constants.DAILYLOG_INTERFACE_GET_PAST_DAY_END_STATE = "getPastDayEndState";
Constants.DAILYLOG_INTERFACE_GET_TOMORROW_DRIVING_REMAIN_TIP = "getTomorrowDrivingRemainTip";
Constants.DAILYLOG_INTERFACE_SYNC_DDL = "syncDdl";
Constants.DAILYLOG_INTERFACE_HAS_UNIDENTIFIED = "hasUnidentified";
Constants.DAILYLOG_INTERFACE_OPEN_UNIDENTIFIED = "openUnidentified";

Constants.DVIR_INTERFACE_UPLOAD_INSPECTION = "uploadInspection";
Constants.DVIR_INTERFACE_INSPECTION_LIST = "inspectionList";
Constants.DVIR_INTERFACE_GET_INSPECTION_LOG = "getInspectionLog";

Constants.DOT_INTERFACE_OPEN_DETAIL_PAGE = "openDetailPage";
Constants.DOT_INTERFACE_GET_REVIEW_DATA_LIST = "getReviewDataList";
Constants.DOT_INTERFACE_REQUEST_SEND_EMAIL = "requestSendEmail";
Constants.DOT_INTERFACE_REQUEST_SEND_PDF = "requestSendPdf";
Constants.DOT_INTERFACE_REQUEST_WEB_SERVICE = "requestWebService";
Constants.DOT_INTERFACE_SELECT_REVIEW_TAB_ITEM = "selectReviewTabItem";

Constants.ALERT_INTERFACE_GET_NOTCERTIFED_DATA = "getNotCertifiedAlertData";
Constants.ALERT_INTERFACE_GET_EDIT_DATA = "getEditAlertData";
Constants.ALERT_INTERFACE_GET_ASSIGNED_DATA = "getAssignedAlertData";
Constants.ALERT_INTERFACE_UPLOAD_NOTCERTIED_SIGN = "uploadNotCertifiedAlertSign";
Constants.ALERT_INTERFACE_UPDATE_EDIT_STATUS = "updateEditAlertStatus";
Constants.ALERT_INTERFACE_UPDATE_ASSIGNED_STATUS = "updateAssignedAlertStatus";
Constants.ALERT_INTERFACE_GET_SUMMARY = "getAlertSummary";
Constants.ALERT_INTERFACE_GET_UNIDENTIFIED_DRIVER_LOG_DATA = "getUnidentifiedDriverLogData";
Constants.ALERT_INTERFACE_UPDATE_UNIDENTIFIED_DRIVER_LOG_STATUS = "updateUnidentifiedDriverLogStatus";
Constants.ALERT_INTERFACE_OPEN_DAILYLOG_DETAIL_PAGE = "openDailyLogDetailPage";

Constants.DRIVING_INTERFACE_GET_DRIVER_STATUS = "getDriverStatus";
Constants.DRIVING_INTERFACE_SHOW_DIALOG = "showDialog";
Constants.DRIVING_INTERFACE_SET_VEHICLE_LISTENER = "setVehicleStatusListener";
Constants.DRIVING_INTERFACE_CANCEL_VEHICLE_LISTENER = "cancelVehicleStatusListener";

Constants.IFTA_INTERFACE_OPEN_CREATE_PAGE = "openCreatePage";
Constants.IFTA_INTERFACE_OPEN_UPDATE_PAGE = "openUpdatePage";
Constants.IFTA_INTERFACE_GET_FUEL_TYPE = "getFuelType";
Constants.IFTA_INTERFACE_GET_STATE_LIST = "getStateList";
Constants.IFTA_INTERFACE_GET_FUEL_HISTORY_LIST = "getFuelHistoryList";


// 子页面回传值给父页面的key
Constants.CHANGE_STATUE_KEY = "change_statue_key";
Constants.SELECT_VEHICLE_KEY = "select_vehicle_key";
Constants.ADD_DEFECTS_KEY = "add_defects_key";
Constants.HANDLE_UNIDENTIFIED_KEY = "handle_unidentified_key";
Constants.INSPECTION_SELECT_VEHICLE_KEY = "inspection_select_vehicle_key";
Constants.EDIT_DAILY_LOG = "edit_daily_log";
Constants.UPDATE_DRIVER_KEY = "update_driver_key";
Constants.UPDATE_PASSWORD_KEY = "update_password_key";
Constants.UPDATE_PROFILE_KEY = "update_profile_key";
Constants.CHANGE_ODOMETER_KEY = "change_meter_key";
Constants.FUEL_HISTORY_DELETE = "fuel_history_delete_key";
Constants.FUEL_HISTORY_UPDATE = "fuel_history_update_key";

/**
 * js调用java接口是否成功
 * @type {number}
 */
Constants.CALLBACK_SUCCESS = 1;
Constants.CALLBACK_FAILURE = -1;
Constants.CALLBACK_COMPLETE = 2;

/**
 * 新打开的webview页面是否能否返回
 * @type {number}
 */
Constants.WEB_CANBACK = 1;
Constants.WEB_CANNOTBACK = -1;

/**
 * 是否隐藏导航条
 */
Constants.HIDE_NAVIGATION_BAR = 1;
Constants.SHOW_NAVIGATION_BAR = 0;

/**
 * 是否需要给父界面传递参数，该父界面是原生界面
 * @type {number}
 */
Constants.WEB_BACK_FOR_RESULT = 1;
Constants.WEB_BACK_NO_RESULT = -1;

/**
 * 用户角色
 * @type {{}}
 */
var USERROLE = {};
USERROLE.NORMAL = 0; //非团队驾驶
USERROLE.PILOT = 1; //主驾驶
USERROLE.COPILOT = 2; //副驾驶

/**
 * 用户功能
 * @type {{}}
 */
var USERFUNCTION = {};
USERFUNCTION.NORMAL = 0; //正常
USERFUNCTION.MILES_EXEMPTION = 1; //按里程的豁免模式
USERFUNCTION.DAYS_EXEMPTION = 2; //按天的豁免模式
USERFUNCTION.ALL = 3; //具有两种豁免模式

/**
 * 司机状态枚举
 * @type {{}}
 */
var DRIVERSTATUS = {};
DRIVERSTATUS.OFFDUTY = 1;
DRIVERSTATUS.SLEEPER = 2;
DRIVERSTATUS.DRIVING = 3;
DRIVERSTATUS.ONDUTY = 4;
DRIVERSTATUS.YARDMOVE = 5;
DRIVERSTATUS.PERSONALUSE = 6;

/**
 * 数据采集层类型枚举
 * @type {{}}
 */
var DATACOLLECTORTYPE = {};
DATACOLLECTORTYPE.NONE = -1;
DATACOLLECTORTYPE.DEVICE = 1;
DATACOLLECTORTYPE.GPS = 2;

/**
 * 车辆状态枚举
 * @type {{}}
 */
var VEHICLESTATUS = {};
VEHICLESTATUS.UNKNOWN = 0; //未知
VEHICLESTATUS.MOVING = 1; //车辆正在运动
VEHICLESTATUS.STATIC = -1; //车辆静止

/**
 * 车辆是否连接
 * @type {{}}
 */
var VEHICLECONNECTSTATE = {};
VEHICLECONNECTSTATE.CLEAR = -1;
VEHICLECONNECTSTATE.CONNECTED = 1; //车辆已经连接
VEHICLECONNECTSTATE.DISCONNECTED = 2; //车辆没有连接

/**
 * GPS模式的状态
 * @type {{}}
 */
var GPSMODELSTATE = {};
GPSMODELSTATE.ENABLE = 1;
GPSMODELSTATE.UNENABLE = -1;

/**
 * DashBoard界面上是否显示故障图标
 * @type {{}}
 */
var MALFUNCTION = {};
MALFUNCTION.SHOW = 1; //显示
MALFUNCTION.HIDE = 0; //隐藏

/**
 * DashBoard界面上是否显示离线
 * @type {{}}
 */
var NETWORK = {};
NETWORK.CONNECTED = 1; //显示
NETWORK.DISCONNECTED = 0; //隐藏

/**
 * inspection类型
 * @type {{}}
 */
var INSPECTION = {};
INSPECTION.PRETRIP = 1;
INSPECTION.INTERIM = 2;
INSPECTION.POSTTRIP = 3;

var DOTMODE = {};
DOTMODE.AMERICA = 0;
DOTMODE.CANADA = 1;

/**
 * 团队驾驶消息类型
 * @type {{}}
 */
var TeamWorkDashBoardEventType = {};
TeamWorkDashBoardEventType.BECOME_NORMAL = -1; //恢复正常
TeamWorkDashBoardEventType.BECOME_PILOT = 0; //成为主驾驶
TeamWorkDashBoardEventType.COPILOT_ACCEPT_INVITE = 1; //副驾驶接受主驾驶的邀请
TeamWorkDashBoardEventType.COPILOT_REFUSE_INVITE = 2; //副驾驶拒绝主驾驶的邀请
TeamWorkDashBoardEventType.COPILOT_ACCEPT_SWITCH = 3; //副驾驶接受主驾驶的角色切换请求
TeamWorkDashBoardEventType.COPILOT_REFUSE_SWITCH = 4; //副驾驶拒绝主驾驶的角色切换请求
TeamWorkDashBoardEventType.COPILOT_EXIT = 5; //副驾驶离开团队
TeamWorkDashBoardEventType.BECOME_COPILOT = 6; //成为副驾驶
TeamWorkDashBoardEventType.PILOT_REMOVE_COPILOT = 7; //主驾驶移除副驾驶

/**
 * 弹窗配置类
 * @constructor
 */
var DialogConfig = {};
DialogConfig.Icon_Amazing = 1;
DialogConfig.Icon_Awkward = 2;
DialogConfig.Icon_Greate = 3;
DialogConfig.Icon_Hello = 4;
DialogConfig.Icon_Love = 5;
DialogConfig.Icon_Msg = 6;
DialogConfig.Icon_Question = 7;
DialogConfig.Icon_Sleep = 8;
DialogConfig.Text = "";
DialogConfig.NegativeBtnText = "";
DialogConfig.NeutralBtnText = "";
DialogConfig.PositiveBtnText = "";
DialogConfig.Cancelable = 1;
DialogConfig.NoCancelable = -1;

Constants.BUTTON_POSITIVE = -1;
Constants.BUTTON_NEGATIVE = -2;
Constants.BUTTON_NEUTRAL = -3;

/**
 * 特殊条件下的经纬度字段
 * @type {{}}
 */
var LatLngSpecial = {};
LatLngSpecial.M = "M";
LatLngSpecial.X = "X";
LatLngSpecial.E = "E";

/**
 * 备注类型
 * @type {{}}
 */
var REMARKTYPE = {};
REMARKTYPE.ODND = 1;
REMARKTYPE.OFF = 2;
REMARKTYPE.DRIVING = 3;
REMARKTYPE.SB = 4;
REMARKTYPE.ADVERSE = 5;

/**
 * 开始显示预警状态的阈值
 * @type {number}
 */
Constants.START_WARING_MIN = 10;

/**
 * Odometer的最大长度。
 * @type {number}
 */
Constants.ODOMETER_MAX_LENGTHS = 10;

/**
 * remark 最大长度
 * @type {number}
 */
Constants.remarkMaxLength = 60;

/**
 * remark 最小长度
 * @type {number}
 */
Constants.remarkMinLength = 4;

/**
 * location 最大长度
 * @type {number}
 */
Constants.locationMaxLength = 60;

/**
 * 里程数最大值
 * @type {number}
 */
Constants.odometerMax = 10000000;

/**
 * 引擎时间最大值
 * @type {number}
 */
Constants.engineHourMax = 100000;

/**
 * location 最小长度
 * @type {number}
 */
Constants.locationMinLength = 5;

/**
 * 语言类型，英文
 * @type {number}
 */
Constants.langeuageEN = 1;

/**
 * 语言类型，中文
 * @type {number}
 */
Constants.langeuageZH = 2;

/**
 * 语言描述，英文
 * @type {number}
 */
Constants.langeuageENText = "English";

/**
 * 语言描述，中文
 * @type {number}
 */
Constants.langeuageZHText = "中文";

/**
 * 通知类型
 * @type {number}
 */
Constants.notificationSwitch = 1;

/**
 * sms类型
 * @type {number}
 */
Constants.SMSSwitch = 2;
/**
 * 开关关闭
 * @type {number}
 */
Constants.notificationClose = 0;

/**
 * 开关打开
 * @type {number}
 */
Constants.notificationOpen = 1;

// /**
//  * 过滤表情和中文等，只包含英文，数字，空格和部分标点
//  * @type {string}
//  */
// Constants.inputRegexp = /[^0-9a-zA-Z\s\r\n_,+×÷~=!@#$%^&*.;:'"?|/(){}<>\[\]\-]/;

/**
 * 匹配中文和表情符
 * @type {RegExp}
 */
Constants.inputRegexp = /[\u4e00-\u9fa5]|\ud83c[\udc00-\udfff]|\ud83d[\udc00-\udfff]|[\u2600-\u27ff]/;

/**
 * 输出日志的Tag
 * @constructor
 */
var Tags = function () {
};

Tags.ECM = "ecm";
Tags.DRIVINGPAGE = "driving_page";

/**
 * Firebase参数枚举
 * @type {{}}
 */
var FIREBASE = {};
FIREBASE.EVENT_CATEGORY = "eventCategory";
FIREBASE.EVENT_ACTION = "eventAction";
FIREBASE.EVENT_LABEL = "eventLabel";
FIREBASE.EVENT_VALUE = "eventValue";
FIREBASE.EVENT_CATEGORY_LOGIN = "login";
FIREBASE.EVENT_CATEGORY_VCONNECT = "vconnect";
FIREBASE.EVENT_CATEGORY_LOGOUT = "logout";
FIREBASE.EVENT_CATEGORY_INSPECTION = "inspection";
FIREBASE.EVENT_CATEGORY_DOT = "dot";
FIREBASE.EVENT_ACTION_CLICK = "click";
FIREBASE.EVENT_ACTION_ALERT = "alert";

/**
 * 规则时间
 * @type {{}}
 */
var RULETIME = {};
RULETIME.AMERICA1 = "60 hr/7 D";
RULETIME.AMERICA2 = "70 hr/8 D";
RULETIME.CANADA1 = "70 hr/7 D";
RULETIME.CANADA2 = "120 hr/14 D";

var RULEID = {};
RULEID.CAR_7D_60H = 1;
RULEID.CAR_8D_70H = 2;
RULEID.TRUCK_7D_60H = 3;
RULEID.TRUCK_8D_70H = 4;
RULEID.TRUCK_RESER_34H_7D_60H = 5;
RULEID.TRUCK_RESER_34H_8D_70H = 6;
RULEID.CANADA_7D_70H = 7;
RULEID.CANADA_14D_120H = 8;

/**
 * 车检过程最短的时间
 * @type {number}
 */
Constants.inspectionMinTime = 900000;



