(function ($, window) {

    // 默认数据
    var defaultSetting = {

        data: null
    };

    /*
     *   Review界面
     **/
    var DateReview = function (parentView, option) {

        $.extend(this, defaultSetting, option || {});

        this.parentView = parentView;
        this.view = null;

        this.reviewHeadUsdot = null;
        this.reviewHeadCarrier = null;
        this.reviewHeadDriverName = null;
        this.reviewHeadDriverID = null;
        this.reviewHeadTruckTractorID = null;
        this.reviewHeadTruckTractorVIN = null;
        this.reviewHeadDriverLicenseNumber = null;
        this.reviewHeadDriverLicenseState = null;
        this.reviewHeadTimeZone = null;
        this.reviewHeadPeriodStartingTime = null;
        this.reviewHeadELDManufacturer = null;
        this.reviewHeadCoDriverName = null;
        this.reviewHeadCoDriverID = null;
        this.reviewHeadStartEndOdometer = null;
        this.reviewHeadStartEndEngineHours = null;
        this.reviewHeadDataDiagnosticIndicators = null;
        this.reviewHeadELDMalfunctionIndicators = null;
        this.reviewHeadUnidentifiedDriverRecords = null;
        this.reviewHeadExemptDriverStatus = null;
        this.grid = null;
        this.eventTable = null;

        this.reviewHeadShippingID = null;
        this.init();
    };

    DateReview.template = null;
    DateReview.prototype.init = function () {

        if (DateReview.template === null) {
            DateReview.template = $('#swiper_container_view_item_template').html();
        }
        this.view = $(DateReview.template);

        this.reviewHeadUsdot = this.view.find('[sid=reviewHeadUsdot]');
        this.reviewHeadCarrier = this.view.find('[sid=reviewHeadCarrier]');
        this.reviewHeadDriverName = this.view.find('[sid=reviewHeadDriverName]');
        this.reviewHeadDriverID = this.view.find('[sid=reviewHeadDriverID]');
        this.reviewHeadTruckTractorID = this.view.find('[sid=reviewHeadTruckTractorID]');
        this.reviewHeadTruckTractorVIN = this.view.find('[sid=reviewHeadTruckTractorVIN]');
        this.reviewHeadDriverLicenseNumber = this.view.find('[sid=reviewHeadDriverLicenseNumber]');
        this.reviewHeadDriverLicenseState = this.view.find('[sid=reviewHeadDriverLicenseState]');
        this.reviewHeadTimeZone = this.view.find('[sid=reviewHeadTimeZone]');
        this.reviewHeadPeriodStartingTime = this.view.find('[sid=reviewHeadPeriodStartingTime]');
        this.reviewHeadELDManufacturer = this.view.find('[sid=reviewHeadELDManufacturer]');
        this.reviewHeadELDID = this.view.find('[sid=reviewHeadELDID]');
        this.reviewHeadCoDriverName = this.view.find('[sid=reviewHeadCoDriverName]');
        this.reviewHeadCoDriverID = this.view.find('[sid=reviewHeadCoDriverID]');
        this.reviewHeadStartEndOdometer = this.view.find('[sid=reviewHeadStartEndOdometer]');
        this.reviewHeadStartEndEngineHours = this.view.find('[sid=reviewHeadStartEndEngineHours]');
        this.reviewHeadDataDiagnosticIndicators = this.view.find('[sid=reviewHeadDataDiagnosticIndicators]');
        this.reviewHeadELDMalfunctionIndicators = this.view.find('[sid=reviewHeadELDMalfunctionIndicators]');
        this.reviewHeadUnidentifiedDriverRecords = this.view.find('[sid=reviewHeadUnidentifiedDriverRecords]');
        this.reviewHeadExemptDriverStatus = this.view.find('[sid=reviewHeadExemptDriverStatus]');
        this.eventTable = this.view.find('[sid=eventTable]');

        this.reviewHeadShippingID = this.view.find('[sid =reviewHeadShippingID]');
        // 数据处理
        this.dataSetting();

        this.parentView.append(this.view);
    };

    /**
     * 数据处理
     */
    DateReview.prototype.dataSetting = function () {

        this.reviewHeadUsdot.html(this.data.dotReviewDataHeadVo.usdot);
        this.reviewHeadCarrier.html(this.data.dotReviewDataHeadVo.carrier);
        this.reviewHeadDriverName.html(this.data.dotReviewDataHeadVo.driverName);
        this.reviewHeadDriverID.html(this.data.dotReviewDataHeadVo.driverId);
        this.reviewHeadTruckTractorID.html(this.data.dotReviewDataHeadVo.truckTractorId);
        this.reviewHeadTruckTractorVIN.html(this.data.dotReviewDataHeadVo.truckTractorVin);
        this.reviewHeadDriverLicenseNumber.html(this.data.dotReviewDataHeadVo.driverLicenseNumber);
        this.reviewHeadDriverLicenseState.html(this.data.dotReviewDataHeadVo.driverLicenseState);
        this.reviewHeadTimeZone.html(this.data.dotReviewDataHeadVo.timeZone);
        this.reviewHeadPeriodStartingTime.html(this.data.dotReviewDataHeadVo.periodStartingTime);
        this.reviewHeadELDManufacturer.html(this.data.dotReviewDataHeadVo.eldManufacturer);
        this.reviewHeadELDID.html(this.data.dotReviewDataHeadVo.eldId);
        this.reviewHeadCoDriverName.html(this.data.dotReviewDataHeadVo.coDriverName);
        this.reviewHeadCoDriverID.html(this.data.dotReviewDataHeadVo.coDriverId);
        if (this.data.dotReviewDataHeadVo.startEndOdometer) {

            this.reviewHeadStartEndOdometer.html(parseFloat(this.data.dotReviewDataHeadVo.startEndOdometer).toFixed(2));
        }
        if (this.data.dotReviewDataHeadVo.startEndEngineHours) {

            this.reviewHeadStartEndEngineHours.html(parseFloat(this.data.dotReviewDataHeadVo.startEndEngineHours).toFixed(2));
        }
        this.reviewHeadDataDiagnosticIndicators.html(this.data.dotReviewDataHeadVo.dataDiagnosticIndicators);
        this.reviewHeadELDMalfunctionIndicators.html(this.data.dotReviewDataHeadVo.eldMalfunctionIndicators);
        this.reviewHeadUnidentifiedDriverRecords.html(this.data.dotReviewDataHeadVo.unidentifiedDriverRecords);
        this.reviewHeadExemptDriverStatus.html(this.data.dotReviewDataHeadVo.exemptDriverStatus);
        this.reviewHeadShippingID.html(this.data.dotReviewDataHeadVo.shippingId);
        SDK.getTotalHourByDateTime($.proxy(function (hour) {
            this.grid = new DriverGrid(this.view.find('#grid'), hour, $(".scale_panel").width());
            // 图形内容
            this.grid.draw(this.data.gridModelList, false);
        },this),this.data.datetime);

        // 事件列表
        for (var i = 0; i < this.data.dotReviewEventModelList.length; i++) {
            new DotReviewEvent(this.eventTable, this.data.dotReviewEventModelList[i]);
        }
    };

    var DotReviewEvent = function (parentView, data) {

        if (data.odometer) {

            data.odometer = parseFloat(data.odometer).toFixed(2);
        } else {

            data.odometer = "";
        }

        if (data.engineHour) {

            data.engineHour = parseFloat(data.engineHour).toFixed(2);
        } else {

            data.engineHour = "";
        }

        /* 初始化视图 */
        this.parentView = parentView;
        this.view = $("<tr>" +
            "<td>" + data.id + "</td>" +
            "<td>" + data.time + "</td>" +
            "<td>" + data.location + "</td>" +
            "<td>" + data.odometer + "</td>" +
            "<td>" + data.engineHour + "</td>" +
            "<td>" + data.type + "</td>" +
            "<td>" + data.origin + "</td>" +
            "<td>" + data.remark + "</td>" +

            "</tr>");
        /* 初始化方法 */
        this.parentView.append(this.view);
    };

    window.DateReview = DateReview;
}($, window));