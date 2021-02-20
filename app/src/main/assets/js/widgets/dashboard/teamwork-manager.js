/**
 * @author yufei0213
 * @date 2018/2/8
 * @description 团队驾驶管理类
 */

/**
 * 团队驾驶管理
 * @constructor
 */
var TeamWorkManager = function () {

    this.init();
};

/**
 * 初始化视图
 */
TeamWorkManager.prototype.init = function () {

    this.adverseDrivingView = $("#adverseDriving");

    this.statusInfoView = $("#statusInfoView");

    this.pilotView = $("#pilotView");
    this.pilotViewCopilotName = this.pilotView.find("[sid=copilotName]");
    this.pilotViewRemoveBtn = this.pilotView.find("[sid=removeBtn]");
    this.pilotViewSwitchBtn = this.pilotView.find("[sid=switchBtn]");

    this.copilotView = $("#copilotView");
    this.copilotViewPilotName = this.copilotView.find("[sid=pilotName]");
    this.copilotViewExitBtn = this.copilotView.find("[sid=exitBtn]");

    this.pilotWaitingView = $("#pilotWaitingView");
    this.pilotWaitingViewCopilotName = this.pilotWaitingView.find("[sid=copilotName]");
    this.pilotWaitingViewCancelBtn = this.pilotWaitingView.find("[sid=cancelBtn]");
};

TeamWorkManager.prototype.updateView = function (data) {

    var type = data.type;
    if (type == TeamWorkDashBoardEventType.BECOME_NORMAL) {

        this.becomeNormal(data);
    } else if (type == TeamWorkDashBoardEventType.BECOME_PILOT) {

        this.becomePilot(data);
    } else if (type == TeamWorkDashBoardEventType.COPILOT_ACCEPT_INVITE) {

        this.copilotAcceptInvite(data);
    } else if (type == TeamWorkDashBoardEventType.COPILOT_REFUSE_INVITE) {

        this.copilotRefuseInvite(data);
    } else if (type == TeamWorkDashBoardEventType.COPILOT_ACCEPT_SWITCH) {

        this.copilotAcceptSwitch(data);
    } else if (type == TeamWorkDashBoardEventType.COPILOT_REFUSE_SWITCH) {

        this.copilotRefuseSwitch(data);
    } else if (type == TeamWorkDashBoardEventType.COPILOT_EXIT) {

        this.copilotExit(data);
    } else if (type == TeamWorkDashBoardEventType.PILOT_REMOVE_COPILOT) {

        this.pilotRemoveCopilot(data);
    } else if (type == TeamWorkDashBoardEventType.BECOME_COPILOT) {

        this.becomeCopilot(data);
    }
};

/**
 * 恢复普通状态
 */
TeamWorkManager.prototype.becomeNormal = function (data) {

    this.statusInfoView.addClass("fit");

    this.pilotView.addClass("dn");
    this.copilotView.addClass("dn");
    this.pilotWaitingView.addClass("dn");

    this.pilotViewCopilotName.html("");
    this.pilotViewRemoveBtn.unbind("click");
    this.pilotViewSwitchBtn.unbind("click");

    this.copilotViewPilotName.html("");
    this.copilotViewExitBtn.unbind("click");

    this.pilotWaitingViewCopilotName.html("");
    this.pilotWaitingViewCancelBtn.unbind("click");

    this.showAdverseDrivingView();
};

/**
 * 成为主驾驶
 * @param data
 */
TeamWorkManager.prototype.becomePilot = function (data) {

    if (!this.copilotView.hasClass("dn")) {

        this.showAdverseDrivingView();

        this.copilotView.addClass("dn");
        this.copilotViewPilotName.html("");
        this.copilotViewExitBtn.unbind("click");

        this.pilotViewCopilotName.html(data.copilotName);
        this.pilotViewRemoveBtn.bind("click", $.proxy(this.onPilotViewRemoveBtnClick, this));
        this.pilotViewSwitchBtn.bind("click", $.proxy(this.onPilotViewSwitchBtnClick, this));

        this.pilotView.removeClass("dn");
    } else {

        this.pilotWaitingViewCopilotName.html(data.copilotName);
        this.pilotWaitingViewCancelBtn.bind("click", $.proxy(this.onPilotWaitingViewCancelBtnClick, this));

        this.pilotWaitingView.removeClass("dn");
    }

    this.statusInfoView.removeClass("fit");
};

/**
 * 主驾驶取消邀请副驾驶
 */
TeamWorkManager.prototype.onPilotWaitingViewCancelBtnClick = function () {

    SDK.showLoading();

    TEAMWORK.cancelInviteCopilot($.proxy(function (code) {

        SDK.hideLoading();

        if (code == Constants.CALLBACK_SUCCESS) {

            SDK.showSuccessPrompt($.proxy(function () {

                this.statusInfoView.addClass("fit");

                this.pilotWaitingView.addClass("dn");
                this.pilotWaitingViewCopilotName.html("");
                this.pilotWaitingViewCancelBtn.unbind("click");
            }, this));
        } else if (code == Constants.CALLBACK_FAILURE) {

            SDK.showFailedPrompt();
        }
    }, this));
};

/**
 * 副驾驶接受邀请
 * @param data
 */
TeamWorkManager.prototype.copilotAcceptInvite = function (data) {

    this.pilotWaitingViewCopilotName.html("");
    this.pilotWaitingViewCancelBtn.unbind("click");
    this.pilotWaitingView.addClass("dn");
    this.copilotView.addClass("dn");

    this.pilotViewCopilotName.html(data.copilotName);
    this.pilotViewRemoveBtn.bind("click", $.proxy(this.onPilotViewRemoveBtnClick, this));
    this.pilotViewSwitchBtn.bind("click", $.proxy(this.onPilotViewSwitchBtnClick, this));

    this.pilotView.removeClass("dn");
    this.showAdverseDrivingView();
};

/**
 * 主驾驶移除副驾驶
 */
TeamWorkManager.prototype.onPilotViewRemoveBtnClick = function () {

    SDK.showLoading();

    TEAMWORK.pilotRemoveCopilot($.proxy(function (code) {

        SDK.hideLoading();

        if (code == Constants.CALLBACK_SUCCESS) {

            SDK.showSuccessPrompt($.proxy(function () {

                this.statusInfoView.addClass("fit");

                this.pilotView.addClass("dn");
                this.pilotViewCopilotName.html("");
                this.pilotViewRemoveBtn.unbind("click");
                this.pilotViewSwitchBtn.unbind("click");
            }, this));
        } else if (code == Constants.CALLBACK_FAILURE) {

            SDK.showFailedPrompt();
        }
    }, this));
};

/**
 * 副驾驶拒绝邀请
 * @param data
 */
TeamWorkManager.prototype.copilotRefuseInvite = function (data) {

    this.statusInfoView.addClass("fit");

    this.pilotWaitingViewCopilotName.html("");
    this.pilotWaitingViewCancelBtn.unbind("click");
    this.pilotWaitingView.addClass("dn");
};

/**
 * 主驾驶发起角色切换请求
 */
TeamWorkManager.prototype.onPilotViewSwitchBtnClick = function () {

    SDK.showLoading();

    TEAMWORK.pilotRequestSwitch($.proxy(function (code) {

        SDK.hideLoading();

        if (code == Constants.CALLBACK_SUCCESS) {

            var copilotName = this.pilotViewCopilotName.html();

            this.pilotWaitingViewCopilotName.html(copilotName);
            this.pilotWaitingViewCancelBtn.bind("click", $.proxy(this.onSwitchWaitingCancelBtnClick, this));

            this.pilotView.addClass("dn");
            this.pilotWaitingView.removeClass("dn");

            this.pilotViewCopilotName.html("");
            this.pilotViewRemoveBtn.unbind("click");
            this.pilotViewSwitchBtn.unbind("click");
        } else if (code == Constants.CALLBACK_FAILURE) {

            SDK.showFailedPrompt();
        }
    }, this));
};

/**
 * 等待副驾驶回应状态切换请求时，Cancel按钮被点击
 */
TeamWorkManager.prototype.onSwitchWaitingCancelBtnClick = function () {

    SDK.showLoading();

    TEAMWORK.pilotCancelSwitchRequest($.proxy(function (code) {

        SDK.hideLoading();

        if (code == Constants.CALLBACK_SUCCESS) {

            SDK.showSuccessPrompt($.proxy(function () {

                var copilotName = this.pilotWaitingViewCopilotName.html();

                this.pilotViewCopilotName.html(copilotName);
                this.pilotViewRemoveBtn.bind("click", $.proxy(this.onPilotViewRemoveBtnClick, this));
                this.pilotViewSwitchBtn.bind("click", $.proxy(this.onPilotViewSwitchBtnClick, this));

                this.pilotWaitingViewCopilotName.html("");
                this.pilotWaitingViewCancelBtn.unbind("click");
                this.pilotWaitingView.addClass("dn");
                this.pilotView.removeClass("dn");
            }, this));
        } else if (code == Constants.CALLBACK_FAILURE) {

            SDK.showFailedPrompt();
        }
    }, this));
};

/**
 * 副驾驶接受切换请求
 * @param data
 * @constructor
 */
TeamWorkManager.prototype.copilotAcceptSwitch = function (data) {

    this.copilotViewPilotName.html(data.pilotName);
    this.copilotViewExitBtn.bind("click", $.proxy(this.onCopilotExitBtnClick, this));

    this.pilotWaitingViewCopilotName.html("");
    this.pilotWaitingViewCancelBtn.unbind("click");
    this.pilotWaitingView.addClass("dn");
    this.copilotView.removeClass("dn");

    this.hideAdverseDrivingView();
};

/**
 * 副驾驶离开按钮被点击
 */
TeamWorkManager.prototype.onCopilotExitBtnClick = function () {

    SDK.showLoading();

    TEAMWORK.copilotExit($.proxy(function (code) {

        SDK.hideLoading();

        if (code == Constants.CALLBACK_SUCCESS) {

            SDK.showSuccessPrompt($.proxy(function () {

                this.showAdverseDrivingView();

                this.statusInfoView.addClass("fit");

                this.copilotView.addClass("dn");
                this.copilotViewPilotName.html("");
                this.copilotViewExitBtn.unbind("click");
            }, this));
        } else if (code == Constants.CALLBACK_FAILURE) {

            SDK.showFailedPrompt();
        }
    }, this));
};

/**
 * 副驾驶拒绝切换请求
 */
TeamWorkManager.prototype.copilotRefuseSwitch = function (data) {

    this.pilotViewCopilotName.html(data.copilotName);
    this.pilotViewRemoveBtn.bind("click", $.proxy(this.onPilotViewRemoveBtnClick, this));
    this.pilotViewSwitchBtn.bind("click", $.proxy(this.onPilotViewSwitchBtnClick, this));

    this.pilotWaitingViewCopilotName.html("");
    this.pilotWaitingViewCancelBtn.unbind("click");
    this.pilotWaitingView.addClass("dn");
    this.pilotView.removeClass("dn");
};

/**
 * 副驾驶离开
 * @param data
 * @constructor
 */
TeamWorkManager.prototype.copilotExit = function (data) {

    this.statusInfoView.addClass("fit");

    this.pilotView.addClass("dn");
    this.pilotViewCopilotName.html("");
    this.pilotWaitingViewCancelBtn.unbind("click");
    this.pilotViewSwitchBtn.unbind("click");
};

/**
 * 主驾驶移除副驾驶
 * @param data
 * @constructor
 */
TeamWorkManager.prototype.pilotRemoveCopilot = function (data) {

    this.statusInfoView.addClass("fit");

    this.copilotView.addClass("dn");
    this.copilotViewPilotName.html("");
    this.copilotViewExitBtn.unbind("click");

    this.showAdverseDrivingView();
};

/**
 * 成为副驾驶
 * @param data
 * @constructor
 */
TeamWorkManager.prototype.becomeCopilot = function (data) {

    this.copilotViewPilotName.html(data.pilotName);
    this.copilotViewExitBtn.bind("click", $.proxy(this.onCopilotExitBtnClick, this));

    this.copilotView.removeClass("dn");

    this.statusInfoView.removeClass("fit");

    this.hideAdverseDrivingView();
};

/**
 * 展示adverse-driving按钮
 */
TeamWorkManager.prototype.showAdverseDrivingView = function () {

    this.adverseDrivingView.css({
        visibility: "visible"
    });
    this.adverseDrivingView.bind("click", $.proxy(function () {

        //防止重复点击
        if (Global.isButtonProcessing) {
            return;
        }
        Global.isButtonProcessing = true;
        setTimeout(function () {
            Global.isButtonProcessing = false;
        }, 1000);
        DASHBOARD.openAdverseDrivingDialog();
    }, this));
};

/**
 * 隐藏adverse-driving按钮
 */
TeamWorkManager.prototype.hideAdverseDrivingView = function () {

    this.adverseDrivingView.css({
        visibility: "hidden"
    });
    this.adverseDrivingView.unbind("click");
};
