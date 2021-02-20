/**
 * @author zhangyu
 * @date 2018/1/24
 * @description TODO
 */
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    this.initSignCanvas();

    this.index = params.index;
    this.hasChange = false;
    this.canvas = $("#signCanvas");
    this.signaturePad = new SignaturePad(this.canvas.get(0), {
        backgroundColor: 'rgb(255, 255, 255)',
        minWidth: 1,
        maxWidth: 1
    });
    this.canvas.on("click", $.proxy(this.canvasChange, this));
    this.clearBtn = $("#clear");
    var env = this;
    this.clearBtn.on("click", function () {

        env.hasChange = false;
        env.signaturePad.clear();
    });
    this.saveBtn = $("#save");
    this.saveBtn.on("click", $.proxy(this.saveBtnClick, this));
    this.signImg = $("#signImg");
    var env = this;

    DAILYLOG.getSign(env.index, function (data) {

        data = JSON.parse(data);
        if (data.hasSign) {

            env.signImg.removeClass("dn");
            env.canvas.addClass("dn");
            env.saveBtn.addClass("dn");
            env.clearBtn.addClass("dn");
            if (data.sign) {

                env.signImg.attr("src", data.sign);
            } else {

                var dialogConfig = {
                    icon: DialogConfig.Icon_Awkward,
                    text: String.ddlGetSignRequesetailure,
                    positiveBtnText: String.ok,
                    cancelable: DialogConfig.Cancelable
                };
                SDK.showDialog(null, dialogConfig);
            }
        } else {

            env.signImg.addClass("dn");
            env.canvas.removeClass("dn");
            env.saveBtn.removeClass("dn");
            env.clearBtn.removeClass("dn");
        }
    });
};

Web.saveBtnClick = function () {

    if (this.signaturePad.isEmpty()) {
        var config = {
            icon: DialogConfig.Icon_Love,
            text: String.notSignatureTip,
            positiveBtnText: String.ok,
            cancelable: DialogConfig.NoCancelable
        };
        SDK.showDialog(null, config);
        return;
    }
    SDK.showLoading();
    var env = this;
    var dataURL = this.signaturePad.toDataURL();
    ImageUtil.zipBase64(dataURL, $.proxy(function (base64) {

        DAILYLOG.uploadSign(env.index, base64, function (info) {

            SDK.hideLoading();
            if (info != Constants.CALLBACK_SUCCESS) {

                var dialogConfig = {
                    icon: DialogConfig.Icon_Awkward,
                    text: String.ddlSignRequesetailure,
                    positiveBtnText: String.ok,
                    cancelable: 1
                };
                SDK.showDialog(null, dialogConfig);
            } else {

                env.canvas.addClass("dn");
                env.saveBtn.addClass("dn");
                env.clearBtn.addClass("dn");
                env.signImg.removeClass("dn");
                env.signImg.attr("src", base64);
            }
        });
    }, this));
};
Web.canvasChange = function () {

    this.hasChange = true;
};