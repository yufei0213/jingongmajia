/**
 * @author sunyq
 * @date 2018/1/24
 * @description 检查项历史列表详情页面
 * ?params={"busId": "101","compoundStatus": "","compoundStatusCode": 0,"creatorId": 1,"creatorName": "eldtest","defectList": [{"id" : 1,"name" : "defect1", "comment" : "111111111111111111"},{"id" : 1,"name" : "defect1","comment" : "222222222222222222"}],"defects": "1,2,3","driverSign": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAASwAAACWCAYAAABkW7XSAAAP6UlEQVR4Xu2dS4xNSxfHy4DQHs0dCZEmppImEUOPmIrHkIkWMUE8BmLmNRaPYIwJZogJI6RJSCRICJEILS0xQ0gECd/331f1rd76nD599j5n71X1q6Sj73XO3lW/VfW3atWqqkm//18cBQIQgIABApMQLANWoooQgEBGAMGiI0AAAmYIIFhmTEVFIQABBIs AAEImCGAYJkxFRWFAAQQLPoABCBghgCCZcZUVBQCEECw6AMQgIAZAgiWGVNRUQhAAMGiD0AAAmYIIFhmTEVFIQABBIs AAEImCGAYJkxFRWFAAQQLPoABCBghgCCZcZUVBQCEECw6AMQgIAZAgiWGVNRUQhAAMGiD0AAAmYIIFhmTEVFIQABBIs AAEImCGAYJkxFRWFAAQQLPoABCBghgCCZcZUVBQCEECw6AMQgIAZAgiWGVNRUQhAAMGiD0AAAmYIIFhmTEVFIQABBIs AAEImCGAYJkxFRWFAAQQLPoABCBghgCCZcZUVBQCEECw6AMQgIAZAgiWGVNRUQhAAMGiD0AAAmYIIFhmTEVFIQABBIs AAEImCGAYJkxFRWFAARMCNaTJ0/c27dv3dDQkPv06VP2 8KFCzPrzZ492y1dujT7vbe3N/vT/zfmhQAE4iJQS8GSIJ0 fdpJqO7cudM2cS9mEjAJnH76 voQtLaJ8kUIVEugdoJ15MgRd/To0Y5SmTJlips3b96IiEnIJGr9/f0jnltHK8DDIQCBtgjUSrDkWS1atOivhixYsMDNnTvXbdmyJZsC umgPq8posrLly/djx8/3K9fv7Ipo/7/06dP24KyevVqpx9ErC18fAkCHSNQK8FSKyVInz9/zuJRp06dyoTDC1Q7FCRcmlr6P1 9euVev37tvn//3rKg9fT0uBUrVmQC5kWMOFk71uA7EChGoHaCJe9IcauiQtUqFi9keq9 fNxMotmsSFi9J7Zhw4ZCotpqXfkcBFInUDvBqotB/LTyypUr7t69e 7Zs2eZ59eoyAvcuHGj27t3L JVFyNSj gIIFgTMKn3vuQB6nelWYxV/vnnH7d /frMA5P3JW MAgEIFCeAYBVgKC/s2rVr2c/du3cbPknxroGBAbdq1SpSKgrw5qsQQLBK6gOKhcnzknhdvnw5W7Ecq8jrknht3bq1pDfzGAikQwDB6pCtvXjpz7HSKzRNPHnyZCZeFAhAoDUCCFZrnAp9SlPHCxcuZD/5uJcSWJUsu2PHjkLv4MsQSIEAgtVlK0u0lF W97q0yijhYqrYZYPwOlMEEKyKzKWp4s6dO92LFy9G1UAxrsOHD2crjBQIQGA0AQSr4h4hj0ueVThVVHxr3759mXBRIACB/wggWDXpDZom6icULiWiXr16tSY1pBoQqJ4AglW9DUZqoOC8vK2LFy O/D J1vnz50k rZGdqEp1BBCs6tg3fLNES96W3wok0VIKRJFN4DVsJlWCwIQJIFgTRtadLygBddOmTSMv02kRt27dwtPqDn7eUlMCCFZNDaNqab/ismXLRmqolUNND/G0amw0qtZRAghWR/EWf/jg4KBbuXLlKNG6fft28QfzBAgYJIBgGTBafnooTwvRMmA4qlg6AQSrdKSdeWBetBSEV64WBQIpEUCwDFlbAqXbhFSUXPr48WPiWYbsR1WLE0CwijPs6hN0tpbfh6jTTZX QIFAKgQQLGOW1h7ENWvW4GUZsxvVLYcAglUOx64 RWdo Wx4vKyuoudlFRNAsCo2QDuv1xYeTQ19JvzHjx9JKG0HJN8xRwDBMmeyfyuMl2XUcFS7EAEEqxC 6r6sM TnzJkzUgG8rOpswZu7RwDB6h7r0t8UpjmQl1U6Xh5YQwIIVg2N0mqVFMtatGhR9nHFtJSXRYFAzASSEywNch2Sp43Fmlbpv31RMqYGfn9/f5aQaeECVG3T8XcivnnzhkTSmEcrbXPRCZYXISVX6nf93Lx5MxOmDx8 TMjkEi8Jwtq1a92SJUtqKQZKHN2/f3/WLp1OqrOzKBCIlUA0gqW9dspN0p tFnlSeS9KnpdPF/DPmTRpkvv9 3f2WQmCREzflaBVXcIjaMjJqtoavL/TBMwLljK/5WFo4OZLb29v5hXNmDEjE5fly5dnoqPfm50p5W9x1jP1/AcPHrjv37 PaQuJl/8ZSwA7bUA9X22SyK5atSqrLwUCsRIwLVhhLpI3kO71815QWTEoCZg8N/1IEPIeWNg5JIZ KikB6cZhe2EcS54gBQKxEjApWBKNbdu2jQqYSxwUz nGNE2elxcvH/Bu1EEkWF5AVceyRDR8XxjH0kphNxjEOiBoV70JmBMs3eMnsQrLrl273NmzZysh7aeP3gNr5n2pghIv/WzYsKE08QrPyjpx4sRIEL4SILwUAh0kYEqw8ofY9fX1OQlYnW5J9nEv1St/HX3ejmWKlxYGVCSEE1l46GDf4tEQKJ2AKcHywWVRUIBbA7MbMaJ2qYexr vXrzd9jOJxir 1K74 jiUeyseiQCBGAmYEK4zTaPVPeVWdiAd1ysitipcER/cSTnTKGC5AEHjvlBV5btUEzAiWBrK/xl1XXWmAWi1evCTCjaaNEmO1UblVrXiRoaDrgop2PTWrTKl3GgRMCFaYHNnT0 Pev39vyrtq1pX89fSa3jYK2CvWdfjw4aarf FJpNYFPY2hRyvbIWBCsMJTCdatW du3LjRTltr/R3vdWk66D3JfIWVruCni/m/CzdCS9z0OQoEYiNgQrDC6WAK  XkLWmV0R DnO904iERV5A jOP5lUIEK7ZhSns8gdoLVv669pQCyuNNF32cSwKl3/0qKqkNDPBYCdResMJE0VT3ykm4xEE/jaaL27dvd8 ePXMPHz5kT2Gso5V21f94mXC5PvWpznhxrsmTJ7ufP3 6 fPnu HhYbo3BKIjUHsPSytkPukydcEKe59WFRVYb5QWoRiXnypG12tpULIEai9Y4QohgvV3Px0cHHSHDh1qeKyMPFRxayWXK9lRQMPNEKi9YMmLOHr0aAaUYHLjfqXFid27d7v79  P SE8LjNjkoo2IVB7wQoTIqdPn 6 fv2KQRsQCFdUp02b5r59 zbqk1pF9MIFRAhYJFB7wQoTIgWYixaadzOfi3Xw4EE3derU7IywfAa9poe6Fozz3y0O2bTrXHvBknlmzZrlvnz5klmKbSfNO2w F0srixKtsYRL w0lXBz4l7YIWGq9CcEKVwqJYzXvXv6YmXzOmjxViZZyufIeF/EtS0M27bqaEKww8M55T8077HjHzEi4JFD587nEVdue8LbSFoS6t96EYOW353BueeNuFYp7s21MjfK48LbqPmTTrp8JwZKJwg3Q3L/XuNOG52J9/Phx3GN49HmJXDhNFGvFCjlTK21xqGPrzQhWmECqwLJWCy2dONot44fn3rfqiTaaJuJtdctqvKdVAmYEK5/eoNUtDSjKaALtCJZ/ggLyYhp6W4ppydsitkVPqwMBM4IlWGFAWd6VpjyU0QTCRNtWPazwCfqHQZzz9y1q2qgtPhQIVEnAlGDlg 94WX93naKC5Z8oT03ChbdV5fDk3XkCpgRLlQ9XwYhl/d2htRl65cqV2V8U3RWgpFOJVj4FgtgWQlIVAXOCpUGkeIo/yA4vq/GUsKzbcxp5W2LPSmJVQzfN95oTLJkpDCwvXrzYPXr0iBXDP/03nBKWJVh6tP6hkGeVP2cebytN4aiq1SYFS7DWr18/cnsO wv/6z6dEqwwtiWRCo9q1tRc Vy6FIMCgU4SMCtYYZoDK4bdEyzvbSmWePr06VF9s5X7EzvZmXl2/ATMCpZMEyaTEsv6t7OG0 Uyp4RjDQWt2soGYQqEv8lH9qBAoGwCpgUr72WR/T5asNrJw2qng42VcMoKbjsk c54BEwLlhrHrTqjTRymfXRLsPw0MR U7 b7x vo/H0cBMwLFl5WY8Gq4tJZTRMlmtpArUA8BQJlEjAvWHhZo7tDeNhhFYJ","id": 255,"inspResult": "1","location": "abc,efd,USA","mechanicId": 0,"mechanicName": "", "mechanicSign": "","odometer": 123.23,"remark": "没有发现问题","repairStatus": "","repairTime": 0,"reviewId": 0,"reviewSign": "","reviewStatus": "","reviewTime": 0,"reviewer": "","status": "","time": 1234567890123,"timeOffset": 0,"timeStr": "","type": 1,"typeName": ""}
 */
//主页的page
var inspectionDetail;
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 *
 */
Web.init = function (params) {

    inspectionDetail = new InspectionDetail(params);
};

var InspectionDetail = function (params) {

    this.inspection = params;

    this.resultLabel = $('[sid=resultLabel]');
    this.dateLabel = $('[sid=dateLabel]');
    this.typeLabel = $('[sid=typeLabel]');
    this.driverNameLabel = $('[sid=driverNameLabel]');
    this.vehicleNoLabel = $('[sid=vehicleNoLabel]');
    this.odometerLabel = $('[sid=odometerLabel]');
    this.locationLabel = $('[sid=locationLabel]');
    this.defectBox = $('[sid=defectBox]');
    this.remarkLabel = $('[sid=remarkLabel]');
    this.remarkEmptyLabel = $('[sid=remarkEmptyLabel]');
    this.signImg = $('[sid=signImg]');
    this.init();
};

InspectionDetail.prototype.init = function () {

    switch (this.inspection.inspResult) {
        case '0':
            this.resultLabel.addClass('green');
            this.resultLabel.text(String.inspectionGreenTip);
            break;
        case '1':
            this.resultLabel.addClass('org');
            this.resultLabel.text(String.inspectionOrgTip);
            break;
        case '2':
            this.resultLabel.addClass('red');
            this.resultLabel.text(String.inspectionRedTip);
            break;
        default:
            break;
    }

    this.dateLabel.text(this.inspection.time);

    switch (this.inspection.type) {
        case 1:
            this.typeLabel.text(String.preTrip);
            break;
        case 2:
            this.typeLabel.text(String.interim);
            break;
        case 3:
            this.typeLabel.text(String.postTrip);
            break;
        default:
            break;
    }

    this.driverNameLabel.text(this.inspection.creatorName);
    this.vehicleNoLabel.text(this.inspection.busId);
    this.odometerLabel.text(parseFloat(this.inspection.odometer).toFixed(2));
    this.locationLabel.text(this.inspection.location);

    if (this.inspection.defects.length == 0) {
        this.defectBox.find('[sid=emptyLabel]').removeClass('dn');
    }

    for (var i = 0; i < this.inspection.defects.length; i++) {
        new DefectItem(this.defectBox, this.inspection.defects[i]);
    }

    if (this.inspection.remark) {
        this.remarkLabel.html(this.inspection.remark);
    } else {
        this.remarkLabel.addClass('dn');
        this.remarkEmptyLabel.removeClass('dn');
    }

    this.signImg.attr("src", this.inspection.driverSign);
};


/*********************InspectionItem********************************/

var DefectItem = function (parentView, receiveData) {

    // 初始化视图
    this.parentView = parentView;
    this.view = null;

    this.nameLabel = null;
    this.commentLabel = null;

    this.receiveData = receiveData;

    this.init();
};

DefectItem.template = null;

DefectItem.prototype.init = function () {

    if (DefectItem.template == null)
        DefectItem.template = $('#defect_template').html();
    this.view = $(DefectItem.template);
    this.parentView.append(this.view);

    this.nameLabel = this.view.find('[sid=nameLabel]');
    this.commentLabel = this.view.find('[sid=commentLabel]');

    this.nameLabel.text(this.receiveData.name);
    this.commentLabel.html(this.receiveData.comment);
};

