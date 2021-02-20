/**
 * @author yufei0213
 * @date 2018/2/24
 * @description 字符集（英文）
 */
var PageConfig = {};

PageConfig.ChangeStatus = {
    url: "change-status.html",
    title: "Change Duty Status"
};

PageConfig.EcmInfo = {
    url: "ecminfo.html",
    title: "ECM Information"
};

PageConfig.Malfunction = {
    url: "malfunction.html",
    title: "Malfunction/Diagnostic"
};

PageConfig.ConnectVehicle = {
    url: "connect-vehicle.html",
    title: "Select Vehicle"
};

PageConfig.SelectVehicle = {
    url: "select-vehicle.html",
    title: "Select Vehicle"
};

PageConfig.DdlProfileEdit = {
    url: "ddl-profile-edit.html",
    title: "Header"
};

PageConfig.DdlStatusEdit = {
    url: "ddl-detail-edit-status.html",
    title: "Edit Status"
};

PageConfig.DdlStatusDetail = {
    url: "ddl-detail-status-detail.html",
    title: "Detail"
};

PageConfig.DdlInsertStatus = {
    url: "ddl-detail-insert-status.html",
    title: "Insert Status"
};

PageConfig.DdlAddRemark = {
    url: "ddl-detail-add-remark.html",
    title: "Add Remark"
};

PageConfig.InspectionNew = {
    url: "inspection-new.html",
    title: "New Inspection"
};

PageConfig.InspectionAddDefects = {
    url: "inspection-new-add-defects.html",
    title: "Add Defects"
};

PageConfig.InspectionConfirm = {
    url: "inspection-new-confirm.html",
    title: "Confirmation"
};

PageConfig.InspectionDetail = {
    url: "inspection-history-detail.html",
    title: "Inspection Detail"
};

PageConfig.InspectionHistory = {
    url: "inspection-history.html",
    title: "DVIR History"
};

PageConfig.ChangePassword = {
    url: "change-password.html",
    title: "Change Password"
};

PageConfig.Language = {
    url: "language.html",
    title: "Language"
};

PageConfig.Notification = {
    url: "notification.html",
    title: "Notification"
};

PageConfig.SMS = {
    url: "notification.html",
    title: "SMS"
};

PageConfig.DriverInfo = {
    url: "driver-info.html",
    title: "Driver Info"
};

PageConfig.DriverInfoEdit = {
    url: "driver-info-edit.html",
    title: "Driver Info"
};

PageConfig.CarrierInfo = {
    url: "carrier-info.html",
    title: "Carrier Info"
};

PageConfig.AddCopilot = {
    url: "select-copilot.html",
    title: "Add Co-driver"
};

PageConfig.SendPdf = {
    url: "send-pdf.html",
    title: "Send PDF"
};

PageConfig.Alerts = {
    url: "alerts.html",
    title: "Alert"
};

PageConfig.AlertsEditDetail = {
    url: "alerts-edit-detail.html",
    title: "Edit Detail"
};

PageConfig.AlertsAssignDetail = {
    url: "alerts-assigned-detail.html",
    title: "Assigned detail"
};

PageConfig.AlertsSign = {
    url: "alerts-not-certified-sign.html",
    title: "Alert Sign"
};

PageConfig.CycleRule = {
    url: "cycle-rule.html",
    title: "Cycle Rules"
};

PageConfig.Ifta = {
    url: "ifta.html",
    title: "IFTA"
};

PageConfig.IftaHistory = {
    url: "ifta-history.html",
    title: "Fueling History"
};

PageConfig.Unidentified = {
    url: "unidentified-driver-log.html",
    title: "Unidentified Driver Log"
};

PageConfig.UnidentifiedDetail = {
    url: "unidentified-driver-log-detail.html",
    title: "Detail"
};

PageConfig.UnidentifiedEngineDetail = {
    url: "unidentified-engine-log-detail.html",
    title: "Detail"
};

PageConfig.Settings = {
    url: "setting.html",
    title: "Settings"
};

PageConfig.About = {
    url: "about.html",
    title: "About"
};

PageConfig.Features = {
    url: "features.html",
    title: "Features"
};

PageConfig.Help = {
    url: "help.html",
    title: "Help"
};

PageConfig.UploadLogs = {
    url: "upload-logs.html",
    title: "Upload Develop Log File"
};

PageConfig.Contact = {
    url: "contact.html",
    title: "Contact Us"
};

PageConfig.SyncDdl = {
    url: "sync-ddl.html",
    title: "Sync Daily Log"
};

PageConfig.ChangeOdometer = {
    url: "ecminfo-edit-odometer.html",
    title: "Change Odometer"
};

PageConfig.ZoomGrid = {
    url: "zoom-grid.html",
    title: "grid"
}

String.preTrip = "Pre-Trip";
String.interim = "Interim";
String.postTrip = "Post-Trip";
String.hello = "Hello";
String.ok = "OK";
String.confirm = "Confirm";
String.cancel = "Cancel";
String.logout = "Logout";
String.reject = "Reject";
String.accept = "Accept";
String.start = "Start";
String.stop = "Stop";
String.timeH = "h ";//加一个空格是因为显示需要
String.timeM = "m";
String.timeAM = "AM";
String.timePM = "PM";
String.empty = "Empty";
String.yes = "Yes";
String.no = "No";

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

String.phoneEmptyValid = "Phone can't be empty.";
String.phoneLengthValid = "Phone must be 10 numbers.";
String.emailEmptyValid = "Email can't be empty.";
String.emailValid = "Email is invalid.";
String.locationMinValid = "Location must be at least #count# characters.";
String.locationMaxValid = "Location must be less than #count# characters.";
String.locationValid = "DOT official doesn't support Chinese and Special characters.";
String.remarkMinValid = "Remark must be at least #count# characters.";
String.remarkMaxValid = "Remark must be less than #count# characters.";
String.remarkValid = "DOT official doesn\'t support Chinese and Special characters.";
String.remarkMinDotValid = "Remark must be at least #count# characters.";
String.remarkMaxDotValid = "Remark must be less than #count# characters.";
String.remarkDotValid = "DOT official doesn\'t support Chinese and Special characters.";
String.notSignatureTip = "The signature should not be empty. Please write your name.";

String.vehicleIdValid = "Vehicle ID is invalid.";
String.vinValid = "VIN is invalid.";
String.coDriverNameValid = "Co-driver Name is invalid.";
String.coDriverIdValid = "Co-driver ID is invalid.";
String.shippingIdValid = "Shipping ID is invalid.";
String.odometerEmptyValid = "Odometer can't be empty.";
String.odometerTypeError = "The odometer must be number.";
String.odometerLengthError = "The odometer can't exceed 7 digits.";
String.engineHourTypeError = "The engine hour must be number.";
String.engineHourLengthError = "The engine hour can't exceed 5 digits.";

String.dashboardNotBreakTip = "Off Duty status doesn’t need to use Start Break.";
String.dashboardNotChangeStatusTip = "Please Stop Break First.";
String.dashboardCopilotNotDisConnectVehicleTip = "You can't disconnect vehicle in the Co-driver Mode.";

String.ddlAddRemarkNoEvent = "There is no event when you choose. Please choose another time.";
String.ddlDetailStatusNotEdit = "The time your select contains the uneditable status, please choose again.";
String.ddlRequesetailure = "Offline mode failed to save the header. Please restore the network and try again.";
String.ddlSignRequesetailure = "Offline mode failed to save the signature. Please restore the network and try again.";
String.ddlGetSignRequesetailure = "Request sign info error, please check your network config.";
String.ddlTodayProfileNotEdit = "In order to ensure the accuracy of the data, in addition to the shipping ID can be modified today,  please modify other information tomorrow.";
String.ddlDSTTimeInvalid = "Time selected invalid due to DST.";

String.drivingModelDriving = "DRIVING";
String.drivingModelYardMove = "ON DUTY";
String.drivingModelPersonalUse = "OFF DUTY";
String.drivingModelVehicleMove = "IN MOTION";
String.drivingModelVehicleStatic = "STATIONARY";

String.changeStatusPreInspectionTip = "Do you need a Pre-trip inspection?";
String.changeStatusSelectVehicle = "Detected that you didn't select a vehicle, Do you want to select one ?";
String.changeStatusPreInspectionNeutralBtnText = "Start pre-trip inspection";
String.changeStatusPostInspectionTip = "Do you need a Post-trip inspection?";
String.changeStatusPostInspectionNeutralBtnText = "Start post-trip inspection";
String.changeStatusInspectionNegativeBtnText = "NO, not needed";

String.logoutConfirm = "Are you sure you want to Logout?";
String.logoutRequestFailed = "Request failed, please try again.";
String.logoutNotCertifiedTip = "You have not certified logs, please sign and logout.";
String.logoutAnyway = "Logout Anyway";
String.logoutSign = "Sign Now";
String.logoutChange = "Change Now";
String.alertLog = "log";
String.alertLogs = "logs";

String.inspectionSelectVehicle = "Select Vehicle";
String.inspectionVehicleValid = "Vehicle can't be empty.";
String.inspectionOdometerValid = "Odometer can't be empty.";
String.inspectionOdometerTypeValid = "Odometer must use numbers.";
String.inspectionOdometerLengthValid = "The odometer can't exceed 7 digits.";
String.inspectionCopilotNot = "You can't do inspection in the Co-driver Mode.";
String.inspectionGreenTip = "Condition Satisfactory";
String.inspectionOrgTip = "Vehicle is safe to drive";
String.inspectionRedTip = "Vehicle is not safe to drive";
String.inspectionDriveStateTip = "Your current state is #state#. When you do inspection, you must change the state to On Duty Not Driving. Do you want to change immediately?";
String.inspectionDriveStateCheckYes = "Yes, change now";
String.inspectionDriveStateCheckNo = "No, keep the current status";
String.inspectionTimeTip = "You complete your inspection in much less time than the average that a normal inspection would take 15 minutes. Do you want to pay more time to detail your inspection?";
String.inspectionContinue = "Continue Inspection";
String.inspectionStop = "Finish Inspection";

String.notCopilot = "You must select the vehicle before you can add the co-driver.";
String.offlineNotCopilot = "You can't add co-driver in offline mode. Please try again after restoring the network.";
String.moreVersionTip = "Version #code#";
String.moreLogoutStatusTip = "Your current status is #driverStatus# change to Off Duty and logout.";

String.addCopilotRequestListFiledTip = "Data acquisition fails, click refresh.";
String.addCopilotNoCopilotOnlineTip = "You can invite a co-driver only when he is logged in.";
String.addCopilotNoMatchTip = "No matching result";

String.sendPdfValidTip = "The end date can't be earlier than the start date.";
String.sendPdfValueTip = "You can only send logs for 6 months.";

String.unidentifiedAcceptConfirm = "Do you want to accept the log you choose?";
String.unidentifiedRejectConfirm = "Do you want to reject the log you choose?";
String.unidentifiedRejectTip = "Do you want to reject the #count# logs you choose?";
String.unidentifiedAcceptTip = "Do you want to accept the #count# logs you choose?";
String.unidentifiedRemainAccept = "Set the remaining #count# records as accepted?";
String.unidentifiedRemainZeroAccept = "Set the remaining the record as accepted?";
String.unidentifiedRemainReject = "Set the remaining #count# records as rejected?";
String.unidentifiedRemainZeroReject = "Set the remaining the record as rejected?";
String.unidentifiedDetailAcceptConfirm = "Are you sure you want to accept these changes? This will affect your daily log.";

String.requestEditAcceptTip = "Are you sure to accept these changes? This will affect your daily log.";
String.assignDialogTip = "Do you want to {0} the {1} you choose?";

String.changePsdInvalidPsd = "Invalid password, please try again.";
String.changePsdNotSame = "The current password can't be the same as the new password.";
String.changePsdConfirmSame = "Make sure to enter the same password as above";
String.changePsdLengthInvalid = "New password must be more than 4 characters.";
String.changeOdometer = "The mileage from the ECM is #odometer#mi. According to regulations, this will be displayed in the DOT.";
String.correctOdometer = "Will be #correctOdometer# in next 24hour period.";

String.dotSendFailed = "Failed to send, please check your network.";
String.notSupportedAssign = "Logs 15 days ago are not supported for viewing. Please click the checkbox to sign.";
String.daysLeft = " #day# days left";


