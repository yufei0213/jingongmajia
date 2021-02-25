/**
 * @author zhangyu
 * @date 2018/1/22
 * @description
 */
var page = null;
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    page = new DriverEditPage($('#page'), params);

    // FireBase Analytics
    SDK.collectFirebaseScreen("more-driverinfo-edit");
};
/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function () {

    // FireBase Analytics
    SDK.collectFirebaseScreen("more-driverinfo-edit");
};

(function ($, window) {

// 默认数据
        var defaultSetting = {
            driver: null
        };

        var DriverEditPage = function (view, option) {

            $.extend(this, defaultSetting, option || {});

            this.view = view;

            this.phoneBox = null;
            this.phoneInput = null;
            this.emailBox = null;
            this.emailInput = null;
            this.saveBtn = null;

            this.init();
        };

        DriverEditPage.prototype.init = function () {

            this.phoneBox = this.view.find('[sid=phoneBox]');
            this.phoneInput = this.view.find('[sid=phoneInput]');
            this.emailBox = this.view.find('[sid=emailBox]');
            this.emailInput = this.view.find('[sid=emailInput]');
            this.saveBtn = this.view.find('[sid=saveBtn]');

            this.phoneInput.on("input propertychange", $.proxy(function () {
                this.phoneBox.removeClass('warning');
            }, this));
            this.emailInput.on("input propertychange", $.proxy(function () {
                this.emailBox.removeClass('warning');
            }, this));
            this.saveBtn.on('click', $.proxy(this.onSaveBtnClick, this));

            this.phoneInput.val(this.driver.phone);
            this.emailInput.val(this.driver.email);
        };

        DriverEditPage.prototype.isFormValid = function () {

            var valid = true;
            // 验证手机号
            if (this.phoneInput.val().trim() == "") {
                this.phoneBox.addClass('warning');
                this.phoneBox.find('[sid=tipLabel]').text(String.phoneEmptyValid);
                valid = false;
            } else if (this.phoneInput.val().trim().length != 10) {
                this.phoneBox.addClass('warning');
                this.phoneBox.find('[sid=tipLabel]').text(String.phoneLengthValid);
                valid = false;
            }
            // 验证邮箱
            var emailFormat = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
            if (this.emailInput.val().trim() == "") {
                this.emailBox.addClass('warning');
                this.emailBox.find('[sid=tipLabel]').text(String.emailEmptyValid);
                valid = false;
            } else if (!emailFormat.test(this.emailInput.val().trim())) {
                this.emailBox.addClass('warning');
                this.emailBox.find('[sid=tipLabel]').text(String.emailValid);
                valid = false;
            }

            return valid;
        };

        DriverEditPage.prototype.onSaveBtnClick = function () {

            if (!this.isFormValid()) return;

            // 如果数据没变，直接返回
            if (this.driver.phone == this.phoneInput.val() && this.driver.email == this.emailInput.val()) {
                SDK.back();
                return;
            }

            var params = {
                phone: this.phoneInput.val(),
                email: this.emailInput.val()
            };
            SDK.showLoading();
            USER.updateDriverInfo(params, $.proxy(this.onUpdateDriverListener, this));
        };

        DriverEditPage.prototype.onUpdateDriverListener = function (result) {

            SDK.hideLoading();
            if (result == Constants.CALLBACK_SUCCESS) {

                var params = {
                    phone: this.phoneInput.val(),
                    email: this.emailInput.val()
                }
                SDK.setWebData(Constants.UPDATE_DRIVER_KEY, params);
                SDK.back();
            } else {
                SDK.showFailedPrompt();
            }
        };

        window.DriverEditPage = DriverEditPage;
    }
    ($, window)
)
;
