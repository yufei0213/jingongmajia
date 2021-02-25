/**
 * @author sunyq
 * @date 2018/1/24
 * @description 启动动画
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    SDK.openLauncherAnimation();
    new LauncherPage(params);
};

(function ($, window) {

    var LauncherPage = function (params) {

        this.init();
    };

    LauncherPage.prototype.init = function () {

        var eye = $(".eye");
        var text = $(".text");
        var mouth = $(".mouth");
        var mm = $(".mm");
        var wingright = $(".wingright");
        var wingleft = $(".wingleft");
        var dur = 0.2;
        var owl = $(".owl");
        var cloud = $(".cloud");
        var flyHeight = ($(window).height() / 2) + 60;

        var loadingEmo = new TimelineMax({onComplete: toLoad});
        loadingEmo
            .to(eye, dur, {x: 11}, dur)
            .to(eye, dur, {x: 14, delay: dur})
            .to(eye, dur, {x: 17, delay: dur})
            .to(eye, dur, {x: 14, delay: dur})

        var float = new TimelineMax();
        float
            .to(owl, 0.5, {y: 231}, 0)
            .to(wingright, 0.5, {rotation: -30, transformOrigin: "0 0"}, 0)
            .to(wingleft, 0.5, {rotation: 30, transformOrigin: "25 0"}, 0)
            .to(owl, 0.5, {y: 221}, 0.5)
            .to(wingright, 0.5, {rotation: 0, transformOrigin: "0 0", delay: 0.5}, 0)
            .to(wingleft, 0.5, {rotation: 0, transformOrigin: "25 0", delay: 0.5}, 0)
        float.repeat(-1);

        function swing() {
            var stageOut = new TimelineMax({repeat: 100});
            stageOut
                .to(eye, 0, {opacity: 0})
                .to(mm, 0, {opacity: 1})
                .to(wingright, 0.2, {rotation: -40, transformOrigin: "0 0"}, 0)
                .to(wingleft, 0.2, {rotation: 40, transformOrigin: "25 0"}, 0)
                .to(wingright, 0.2, {rotation: 0, transformOrigin: "0 0", delay: 0.2}, 0)
                .to(wingleft, 0.2, {rotation: 0, transformOrigin: "25 0", delay: 0.2}, 0)
        }

        function fly() {
            var stageOut = new TimelineMax({onComplete: removeStage});
            stageOut
                .to(owl, 1.2, {ease: Expo.easeIn, y: -(flyHeight * 2)}, 0)
                .to(cloud, 1, {ease: Expo.easeIn, y: flyHeight * 2}, 0)
        }

        var a = 0;

        function toLoad() {
            if (a == 0) {
                loadingEmo.restart();
            } else {
                float.pause();
                swing();
                fly();
            }
        }

        function removeStage() {
            SDK.closeLauncherAnimation();
        }

        setTimeout(function () {
            a = 1;
        }, 1000);
    };

    window.LauncherPage = LauncherPage;
}($, window));