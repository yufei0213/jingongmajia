package com.unitedbustech.eld.common;

/**
 * @author yufei0213
 * @date 2017/12/8
 * @description 静常量态
 */
public class Constants {


    public static final String VEST_SIGN_URL = "https://d2d0drb98uxrz0.cloudfront.net/admin/client/vestSign.do";

    public static final String VEST_CODE = "CUHX4A73";

    /**
     * 判定车辆移动的速度阈值
     * <p>
     * DOT规定阈值为5mph，为了降低灵敏度，调节为10mph
     * <p>
     * 单位 mph
     */
    public static final float SPEED_THRESHOLD = 10F;

    /**
     * 判断车辆静止的时间阈值
     * <p>
     * DOT规定为3s
     */
    public static final long VEHICLE_STATIC_THRESHOLD = 3 * 1000L;//20

    /**
     * 图片格式
     */
    public static final String PIC_SUFFIX = ".jpg";

    /**
     * 判断车辆打火的转速阈值
     * <p>
     * 单位 r/min
     */
    public static final float RPM_THRESHOLD = 300F;

    /**
     * 处理android7.0以上版本的文件读取问题
     */
    public static final String FILE_PROVIDER = "com.unitedbustech.eld.fileProvider";

    /**
     * SD卡存储路径
     */
    public static final String STORAGE_PATH = "/storage/emulated/0/Interest_Calculator/";

    /**
     * 日志默认TAG
     */
    public static final String LOG_DEFAULT_TAG = "SHIELD";

    /**
     * 日志默认存储路径
     */
    public static final String LOG_DEFAULT_PATH = STORAGE_PATH + "logs/";

    /**
     * 缓存目录
     */
    public static final String TEMP_PATH = STORAGE_PATH + ".temp/";

    /**
     * apk存储目录
     */
    public static final String APK_PATH = STORAGE_PATH + "apk/";

    /**
     * 照片目录
     */
    public static final String CAMERA_PATH = STORAGE_PATH + ".camera/";

    /**
     * 离线模式支持目录
     */
    public static final String OFFLINE_SUPPORT_PATH = STORAGE_PATH + ".offline/";

    /**
     * 静态资源路径
     */
    public static final String ASSETS_PATH = "file:///android_asset/";

    /**
     * 静态页面路径
     */
    public static final String WEB_PATH = ASSETS_PATH + "html/";

    /**
     * 中文静态页面路径
     * ZH：中文简体缩写
     */
    public static final String WEB_PATH_ZH = WEB_PATH + "zh/";

    /**
     * 英文静态页面路径
     */
    public static final String WEB_PATH_EN = WEB_PATH + "en/";

    /**
     * Remark 最小长度
     */
    public static final int REMARK_MIN_LENGTH = 4;

    /**
     * Remark 最大长度
     */
    public static final int REMARK_MAX_LENGTH = 60;

    /**
     * Location 最小长度
     */
    public static final int LOCATION_MIN_LENGTH = 5;

    /**
     * Location 最大长度
     */
    public static final int LOCATION_MAX_LENGTH = 60;

    /**
     * 本地存储的DDL天数
     */
    public static final int DDL_DAYS = 15;

    /**
     * 匹配数字，字母以及部分符号的正则表达式
     */
//    public static final String INPUT_REGEXP = "[^0-9a-zA-Z\\s\\r\\n_,+×÷~=!@#$%^&*.;:'\"?|/(){}<>\\[\\]\\-]";

    /**
     * 匹配中文和表情符
     */
    public static final String INPUT_REGEXP = "[\u4e00-\u9fa5]|[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";

    /**
     * 客服电话
     */
    public static final String SERVICE_PHONENUMBER = "2028006565";

    /**
     * 通知开关存储key
     */
    public static final String NOTIFICATION_SWITCH = "notification_switch";

    /**
     * 短信开关存储key
     */
    public static final String SMS_SWITCH = "sms_switch";

    /**
     * api地址
     */
//    public final static String API_ROOT = "http://eld-dev.riverroad.cn/api/";
//    public final static String API_ROOT = "http://eld-test.riverroad.cn/api/";
    public final static String API_ROOT = "http://eld-demo.riverroad.cn/api/";
//    public final static String API_ROOT = "https://shield.unitedbustech.com/api/";

    /**
     * 接口url
     */
    public final static String API_BASE = API_ROOT;

    /**
     * 登录
     */
    public final static String API_LOGIN = API_BASE + "account/login";

    /**
     * 登录
     */
    public final static String API_LOGOUT = API_BASE + "account/logout";

    /**
     * 心跳接口
     */
    public final static String API_HEARTBEAT = API_BASE + "heartbeat/create";

    /**
     * 获取司机详情
     */
    public final static String API_DRIVER_DETAIL = API_BASE + "driver/detail";

    /**
     * 获取司机详情
     */
    public final static String API_CARRIER_DETAIL = API_BASE + "company/detail";

    /**
     * 车辆列表
     */
    public final static String API_VEHICLE_LIST = API_BASE + "/vehicle/list";

    /**
     * 事件上报
     */
    public final static String API_DDL_CREATE = API_BASE + "ddl/create";

    /**
     * 事件获取
     */
    public final static String API_DDL_LIST = API_BASE + "ddl/list";

    /**
     * 获取ddl id
     */
    public final static String API_DDL_COMPARE = API_BASE + "ddl/compare";

    /**
     * 通过id获取指定ddl
     */
    public final static String API_DDL_GET_BY_IDS = API_BASE + "ddl/list-by-ids";

    /**
     * 事件获取
     */
    public final static String API_HOS_LIST = API_BASE + "hos/list";

    /**
     * 获取geo-location
     */
    public final static String API_GET_GEO_LOCATION = API_BASE + "location/geo";

    /**
     * 获取表头
     */
    public final static String API_GET_PROFILE = API_BASE + "ddl/get-header";

    /**
     * 获取签名信息
     */
    public final static String API_GET_SIGN = API_BASE + "ddl/sign/list";

    /**
     * 获取签名信息（流量优化）
     */
    public final static String API_GET_STATUS_SIGN = API_BASE + "ddl/sign/status/list";

    /**
     * 获取单个信息
     */
    public final static String API_GET_SIGN_INFO = API_BASE + "ddl/sign/info";

    /**
     * 上传签名
     */
    public final static String API_UPLOAD_SIGN = API_BASE + "ddl/sign";

    /**
     * 保存 Review Head数据
     */
    public final static String API_SAVE_DOT_REVIEW_HEAD_DATA = API_BASE + "ddl/save-header";

    /**
     * Dot 请求发送邮件
     */
    public final static String API_REQUEST_DOT_SEND_EMAIL = API_BASE + "ddl/dot-email";

    /**
     * Dot 请求WebService
     */
    public final static String API_REQUEST_DOT_WEB_SERVICE = API_BASE + "ddl/dot-webservice";

    /**
     * Inspection 新增
     */
    public final static String API_REQUEST_INSPECTION_CREATE = API_BASE + "inspection/create";

    /**
     * Inspection 列表
     */
    public final static String API_REQUEST_INSPECTION_LIST = API_BASE + "inspection/list";

    /**
     * 增加备注
     */
    public final static String API_ADD_REMARK = API_BASE + "ddl/comment/update";

    /**
     * Alert 汇总信息
     */
    public final static String API_ALERT_GET_SUMMARY = API_BASE + "alert/summary";

    /**
     * 未签名列表
     */
    public final static String API_ALERT_GET_UNSIGNED_LIST = API_BASE + "ddl/unsigned";

    /**
     * 获取未认领日志列表
     */
    public final static String API_ALERT_GET_UNIDENTIFIED_LIST = API_BASE + "ddl/unidentified";

    /**
     * 获取Duty Status修改建议列表
     */
    public final static String API_ALERT_GET_EDIT_DATA = API_BASE + "ddl/proposed-edit";

    /**
     * 获取Assigned Alert数据列表
     */
    public final static String API_ALERT_GET_ASSIGNED_DATA = API_BASE + "ddl/assigned";

    /**
     * 更新Alert Edit状态
     */
    public final static String API_GET_ALERT_UPDATE_EDIT_STATUS = API_BASE + "ddl/proposed-edit/confirm";

    /**
     * 更新Alert Assigned状态
     */
    public final static String API_GET_ALERT_UPDATE_ASSIGNED_STATUS = API_BASE + "ddl/unidentified/confirm";

    /**
     * 更新driver信息
     */
    public final static String API_UPDATE_DRIVER_INFO = API_BASE + "driver/detail/edit";

    /**
     * 更新密码
     */
    public final static String API_UPDATE_PASSWORD = API_BASE + "account/change-passwd";

    /**
     * 上报FireBaseToken
     */
    public final static String API_FIREBASE_TOKEN = API_BASE + "driver/fcm";

    /**
     * 获取可用副驾驶列表
     */
    public final static String API_GET_COPILOT_LIST = API_BASE + "co-driver/list-codriver";

    /**
     * 主驾驶发起副驾驶邀请
     */
    public final static String API_PILOT_INVITE_COPILOT = API_BASE + "co-driver/invite";

    /**
     * 副驾驶回应主驾驶的邀请
     */
    public final static String API_COPILOT_RESPONSE_PILOT_INVITE = API_BASE + "co-driver/invite/confirm";

    /**
     * 主驾驶取消邀请副驾驶
     */
    public final static String API_PILOT_CANCEL_INVITE_COPILOT = API_BASE + "co-driver/invite/cancel";

    /**
     * 主驾驶发起角色切换请求
     */
    public final static String API_PILOT_REQUEST_SWITCH = API_BASE + "co-driver/switch";

    /**
     * 副驾驶回应主驾驶的切换请求
     */
    public final static String API_COPILOT_RESPONSE_REQUEST_SWITCH = API_BASE + "co-driver/switch/confirm";

    /**
     * 主驾驶取消角色切换请求
     */
    public final static String API_PILOT_CANCEL_SWITCH_REQUEST = API_BASE + "co-driver/switch/cancel";

    /**
     * 角色切换后原主家断开车辆连接
     */
    public final static String API_ORIGIN_PILOT_DISCONNNECT_VEHICLE = API_BASE + "co-driver/switch/vehicle/disconnect";

    /**
     * 主驾驶移除副驾驶
     */
    public final static String API_PILOT_REMOVE_COPILOT = API_BASE + "co-driver/remove";

    /**
     * 副驾驶离开团队
     */
    public final static String API_COPILOT_EXIT = API_BASE + "co-driver/exit";

    /**
     * 获取当前用户团队驾驶状态
     */
    public final static String API_TEAMWORK_STATE = API_BASE + "co-driver/status";

    /**
     * 获取服务器UTC时间戳
     */
    public final static String API_GET_SERVICE_TIME = API_BASE + "malfunction/getUTC";

    /**
     * 获取异常和诊断列表
     */
    public final static String API_GET_MALFUNCTION_LIST = API_BASE + "malfunction/list";

    /**
     * 上报日志
     */
    public final static String API_UPLOAD_LOGS = API_BASE + "data/log/upload";

    /**
     * 检查版本号提示更新
     */
    public final static String API_CHECK_VERSIONCODE = API_BASE + "version/latest";

    /**
     * 登录时基础配置信息接口
     */
    public final static String API_BASE_INFO = API_BASE + "account/login/base-info";

    /**
     * 更新车辆对应ECM ODO偏移
     */
    public final static String API_CHANGE_ODOMETER = API_BASE + "vehicle/odo/offset";

    /**
     * HEAD
     * 新增油税记录
     */
    public final static String API_FUEL_CREATE = API_BASE + "fuel-record/create";

    /**
     * 更新油税记录
     */
    public final static String API_FUEL_UPDATE = API_BASE + "fuel-record/update";

    /**
     * 删除油税记录
     */
    public final static String API_FUEL_DELETE = API_BASE + "fuel-record/delete";

    /**
     * 获取加油类型
     */
    public final static String API_FUEL_LIST = API_BASE + "fuel-record/list";

    /**
     * 上报GPS轨迹
     */
    public final static String API_GPS_LOG = API_BASE + "gpslog/upload";

    /**
     * 上传司机最新的规则以及是否团队驾驶
     */
    public final static String API_CHANGE_RULE = API_BASE + "driver/change-rule";

    /**
     * 更新命令状态接口
     */
    public final static String API_COMMAND_STATUS = API_BASE + "cmd/status/update";

    /**
     * 返回父界面时传值的key，这里的key与js里面的值对应
     */
    public final static String FUEL_HISTORY_DELETE = "fuel_history_delete_key";
    public final static String FUEL_HISTORY_UPDATE = "fuel_history_update_key";

    /**
     * 获取未认领日志列表
     */
    public final static String API_GET_COMPANY_INFO = API_BASE + "company/expiration-info";

    /**
     * 未认领日志丢弃部分上报
     */
    public final static String API_UPLOAD_ECM = API_BASE + "ecm/create";

    /**
     * 通知，短息开关
     */
    public final static String API_PUSH_SWITCH = API_BASE + "driver/push-switch";

}
