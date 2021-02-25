/**
 * @author yufei0213
 * @date 2018/2/24
 * @description 字符集（中文）
 */
var PageConfig = {};

PageConfig.ChangeStatus = {
    url: "change-status.html",
    title: "切换状态"
};

PageConfig.EcmInfo = {
    url: "ecminfo.html",
    title: "ECM 信息"
};

PageConfig.Malfunction = {
    url: "malfunction.html",
    title: "故障/诊断"
};

PageConfig.ConnectVehicle = {
    url: "connect-vehicle.html",
    title: "连接车辆"
};

PageConfig.SelectVehicle = {
    url: "select-vehicle.html",
    title: "选择车辆"
};

PageConfig.DdlProfileEdit = {
    url: "ddl-profile-edit.html",
    title: "基本信息"
};

PageConfig.DdlStatusEdit = {
    url: "ddl-detail-edit-status.html",
    title: "编辑日志"
};

PageConfig.DdlStatusDetail = {
    url: "ddl-detail-status-detail.html",
    title: "详情"
};

PageConfig.DdlInsertStatus = {
    url: "ddl-detail-insert-status.html",
    title: "插入状态"
};

PageConfig.DdlAddRemark = {
    url: "ddl-detail-add-remark.html",
    title: "添加备注"
};

PageConfig.InspectionNew = {
    url: "inspection-new.html",
    title: "新建车辆检查"
};

PageConfig.InspectionAddDefects = {
    url: "inspection-new-add-defects.html",
    title: "选择故障"
};

PageConfig.InspectionConfirm = {
    url: "inspection-new-confirm.html",
    title: "确认"
};

PageConfig.InspectionDetail = {
    url: "inspection-history-detail.html",
    title: "检查详情"
};

PageConfig.InspectionHistory = {
    url: "inspection-history.html",
    title: "车检记录"
};

PageConfig.ChangePassword = {
    url: "change-password.html",
    title: "更改密码"
};

PageConfig.Language = {
    url: "language.html",
    title: "语言设置"
};

PageConfig.Notification = {
    url: "notification.html",
    title: "软件通知"
};

PageConfig.SMS = {
    url: "notification.html",
    title: "手机短信"
};

PageConfig.DriverInfo = {
    url: "driver-info.html",
    title: "司机信息"
};

PageConfig.DriverInfoEdit = {
    url: "driver-info-edit.html",
    title: "司机信息"
};

PageConfig.CarrierInfo = {
    url: "carrier-info.html",
    title: "公司信息"
};

PageConfig.AddCopilot = {
    url: "select-copilot.html",
    title: "添加副驾驶"
};

PageConfig.SendPdf = {
    url: "send-pdf.html",
    title: "发送PDF"
};

PageConfig.Alerts = {
    url: "alerts.html",
    title: "通知"
};

PageConfig.AlertsEditDetail = {
    url: "alerts-edit-detail.html",
    title: "编辑详情"
};

PageConfig.AlertsAssignDetail = {
    url: "alerts-assigned-detail.html",
    title: "分配详情"
};

PageConfig.AlertsSign = {
    url: "alerts-not-certified-sign.html",
    title: "通知签名"
};

PageConfig.CycleRule = {
    url: "cycle-rule.html",
    title: "规则切换"
};

PageConfig.Ifta = {
    url: "ifta.html",
    title: "IFTA"
};

PageConfig.IftaHistory = {
    url: "ifta-history.html",
    title: "加油历史"
};

PageConfig.Unidentified = {
    url: "unidentified-driver-log.html",
    title: "未认领日志"
};

PageConfig.UnidentifiedDetail = {
    url: "unidentified-driver-log-detail.html",
    title: "详情"
};

PageConfig.UnidentifiedEngineDetail = {
    url: "unidentified-engine-log-detail.html",
    title: "详情"
};

PageConfig.Settings = {
    url: "setting.html",
    title: "设置"
};

PageConfig.About = {
    url: "about.html",
    title: "关于"
};

PageConfig.Features = {
    url: "features.html",
    title: "功能"
};

PageConfig.Help = {
    url: "help.html",
    title: "帮助"
};

PageConfig.UploadLogs = {
    url: "upload-logs.html",
    title: "上传开发日志"
};

PageConfig.Contact = {
    url: "contact.html",
    title: "联系我们"
};

PageConfig.SyncDdl = {
    url: "sync-ddl.html",
    title: "同步行车日志"
};

PageConfig.ChangeOdometer = {
    url: "ecminfo-edit-odometer.html",
    title: "里程修正"
};

PageConfig.ZoomGrid = {
    url: "zoom-grid.html",
    title: "图表"
}

String.preTrip = "行程前";
String.interim = "行程中";
String.postTrip = "行程后";
String.hello = "Hello";
String.ok = "确定";
String.confirm = "确认";
String.cancel = "取消";
String.logout = "退出登录";
String.reject = "拒绝";
String.accept = "接受";
String.start = "开始";
String.stop = "结束";
String.timeH = "小时";
String.timeM = "分钟";
String.timeAM = "上午";
String.timePM = "下午";
String.empty = "空";
String.yes = "是";
String.no = "否";

String.driverStateOdnd = "On Duty Not Driving";
String.driverStateOdndShort = "ODND";
String.driverStateOff = "Off Duty";
String.driverStateOffShort = "OFF";
String.driverStateSb = "Sleeper Berth";
String.driverStateSbShort = "SB";
String.driverStateD = "Driving";
String.driverStateDShort = "D";
String.driverYardMove = "Yard Move";
String.driverYardMoveShort = "ODND";
String.driverStatePersonal = "Personal Use";
String.driverStatePersonalShort = "PC";

String.phoneEmptyValid = "电话号码不能为空。";
String.phoneLengthValid = "电话号码必须为10位。";
String.emailEmptyValid = "邮箱不能为空。";
String.emailValid = "邮箱错误。";
String.locationMinValid = "地址至少为 #count# 个字符。";
String.locationMaxValid = "地址最多为 #count# 个字符。";
String.locationValid = "DOT官方不支持中文和特殊字符。";
String.remarkMinValid = "备注至少为 #count# 个字符。";
String.remarkMaxValid = "备注最多为 #count# 个字符。";
String.remarkValid = "DOT官方不支持中文和特殊字符。";
String.remarkMinDotValid = "Remark must be at least #count# characters.";
String.remarkMaxDotValid = "Remark must be less than #count# characters.";
String.remarkDotValid = "DOT official doesn\'t support Chinese and Special characters.";
String.notSignatureTip = "签名不能为空。";

String.vehicleIdValid = "车号错误。";
String.vinValid = "VIN错误。";
String.coDriverNameValid = "副驾驶名字错误。";
String.coDriverIdValid = "副驾驶ID错误";
String.shippingIdValid = "Shipping ID错误。";
String.odometerEmptyValid = "里程数不能为空。";
String.odometerTypeError = "里程数必须为数字。";
String.odometerLengthError = "里程数不得超过7位数。";
String.engineHourTypeError = "引擎事件必须为数字。";
String.engineHourLengthError = "引擎事件不得超过五位数。";

String.dashboardNotBreakTip = "Off Duty状态无需使用开始休息。";
String.dashboardNotChangeStatusTip = "请先执行停止休息操作。";
String.dashboardCopilotNotDisConnectVehicleTip = "团队驾驶状态下无法断开车辆。";

String.ddlAddRemarkNoEvent = "你选择时间没有事件，请选择其他时间。";
String.ddlDetailStatusNotEdit = "你选择时间内包含不可编辑事件，请你重新选择。";
String.ddlRequesetailure = "离线模式无法保存表头信息，请恢复网络后再试。";
String.ddlSignRequesetailure = "离线模式无法保存签名信息，请恢复网络后再试。";
String.ddlGetSignRequesetailure = "获取签名失败，请检查网络状态。";
String.ddlTodayProfileNotEdit = "为了保证数据的准确性，除shipping ID可在今天修改外，其他信息请在明天修改。";
String.ddlDSTTimeInvalid = "你选择的是无效的DST时间，请重新选择。";

String.drivingModelDriving = "DRIVING";
String.drivingModelYardMove = "ON DUTY";
String.drivingModelPersonalUse = "OFF DUTY";
String.drivingModelVehicleMove = "行驶中";
String.drivingModelVehicleStatic = "静止";

String.changeStatusPreInspectionTip = "你需要进行行程前检查吗？";
String.changeStatusSelectVehicle = "检测到你没有选择车辆，是否选择?";
String.changeStatusPreInspectionNeutralBtnText = "开始行程前检查";
String.changeStatusPostInspectionTip = "你需要进行行程后检查吗？";
String.changeStatusPostInspectionNeutralBtnText = "开始行程后检查";
String.changeStatusInspectionNegativeBtnText = "不需要";

String.logoutConfirm = "你确定退出登录吗？";
String.logoutRequestFailed = "请求失败，请你重试。";
String.logoutNotCertifiedTip = "你有未签名日志，请你签名后退出登录。";
String.logoutAnyway = "强制退出";
String.logoutSign = "签名";
String.logoutChange = "切换状态";
String.alertLog = "个日志";
String.alertLogs = "#count#个日志";

String.inspectionSelectVehicle = "选择车辆";
String.inspectionVehicleValid = "车辆不能为空。";
String.inspectionOdometerValid = "里程数不能为空。";
String.inspectionOdometerTypeValid = "里程数必须是数字。";
String.inspectionOdometerLengthValid = "里程数不能超过七位数。";
String.inspectionCopilotNot = "团队驾驶状态下无法执行检查。";
String.inspectionGreenTip = "车辆情况良好";
String.inspectionOrgTip = "故障不影响车辆驾驶";
String.inspectionRedTip = "故障影响车辆驾驶";
String.inspectionDriveStateTip = "您当前的状态为#state#，在做车辆检查时，必须将状态切换为On Duty Not Driving，是否立即切换？";
String.inspectionDriveStateCheckYes = "是，马上切换";
String.inspectionDriveStateCheckNo = "不，保持当前状态";
String.inspectionTimeTip = "您完成车检的时间少于平均时间(15分钟), 您想要继续车检吗?";
String.inspectionContinue = "继续车检";
String.inspectionStop = "直接完成车检";

String.notCopilot = "添加副驾驶前，请先选择车辆。";
String.offlineNotCopilot = "离线模式不能添加副驾驶，请恢复网络后再试。";
String.moreVersionTip = "版本号 #code#";
String.moreLogoutStatusTip = "你当前的状态为 #driverStatus# 切换到 Off Duty 并登出。";

String.addCopilotRequestListFiledTip = "数据采集失败，点击刷新。";
String.addCopilotNoCopilotOnlineTip = "你只能邀请已经登录的用户为副驾驶。";
String.addCopilotNoMatchTip = "未找到结果。";

String.sendPdfValidTip = "结束日期不能早于开始日期。";
String.sendPdfValueTip = "你只能发送6个月的日志。";

String.unidentifiedAcceptConfirm = "你确定要接受这些日志吗？";
String.unidentifiedRejectConfirm = "你确定要拒绝这些日志吗？";
String.unidentifiedRejectTip = "你确定要拒绝这 #count# 个日志？";
String.unidentifiedAcceptTip = "你确定要接受这 #count# 个日志？";
String.unidentifiedRemainAccept = "是否接受剩余的 #count# 个日志？";
String.unidentifiedRemainZeroAccept = "是否接受剩余的这一条日志？";
String.unidentifiedRemainReject = "是否拒绝剩余的 #count# 个日志？";
String.unidentifiedRemainZeroReject = "是否拒绝剩余的这一条日志？";
String.unidentifiedDetailAcceptConfirm = "你确定要接受这些日志吗？这将会影响工作日志。";

String.requestEditAcceptTip = "你确定要接受这些日志吗？这将会影响工作日志。";
String.assignDialogTip = "你确定要{0}你选择的这{1}吗？";

String.changePsdInvalidPsd = "旧密码错误，请重试。";
String.changePsdNotSame = "新密码不能与当前密码一样。";
String.changePsdConfirmSame = "确认密码与新密码不同。";
String.changePsdLengthInvalid = "新密码必须大于4个字符";
String.changeOdometer = "ECM的里程为#odometer#英里。按照规定，这将显示在DOT上。";
String.correctOdometer = "将在24小时之后生效成#correctOdometer#";

String.dotSendFailed = "发送失败，请检查你的网络设置。";
String.notSupportedAssign = "15天前的日志暂不支持查看，请点击选择框进行签名。";
String.daysLeft = "剩余 #day#天";
