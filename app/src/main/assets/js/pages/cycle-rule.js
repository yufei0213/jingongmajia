/**
 * @author liuzhe
 * @date 2018/7/20
 * @description 选择规则页面
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
var cycleRule;
Web.init = function (params) {

    cycleRule = new CycleRule(params);
    //初始化规则列表
};

var CycleRule = function(params) {

    this.allRuleListView = $("#allRuleList");
    this.ruleItemList = [];
    this.init();
};

CycleRule.prototype.init = function() {

    this.allRuleList = null;
    this.getRuleList();
};
/**
 * 初始化规则列表
 */
CycleRule.prototype.getRuleList = function () {

    SDK.getRuleList($.proxy(function (data, ruleId, userFunction) {

        this.allRuleList = JSON.parse(data);
        //初始化全部rule列表
        this.updateRuleList(ruleId, userFunction);
    }, this));
};

/**
 * 更新全部规则列表视图
 */
CycleRule.prototype.updateRuleList = function (ruleId, userFunction) {

    this.allRuleListView.empty();
    for(var i in this.allRuleList) {

        var rule = this.allRuleList[i];
        var ruleItem = null
        if(userFunction != 0 && (rule.id == 7 || rule.id == 8)) {

            continue;
        }
        if(rule.id == ruleId) {

            ruleItem = new CycleRuleItem(this.allRuleListView, rule, true);
        } else {

           ruleItem = new CycleRuleItem(this.allRuleListView, rule, false);
        }
        ruleItem.on(CycleRuleItem.EVENT_RULE_ITEM_SELECTED, new EventHandler(this.onRuleItemSelectedListener, this));
        this.ruleItemList.push(ruleItem);
    }
};

CycleRule.prototype.onRuleItemSelectedListener = function (data) {

    for(var i = 0; i < this.ruleItemList.length; i++) {

        var ruleItem = this.ruleItemList[i];
        if(data.id == ruleItem.rule.id) {

            ruleItem.checkImg.removeClass("dn");
        } else {

            ruleItem.checkImg.addClass("dn");
        }
    }
    SDK.setRule(data);
    SDK.back();
};

var CycleRuleItem = function(parentView ,rule, selected) {

    this.parentView = parentView;
    this.view = null;
    this.ruleNameView = null;
    this.checkImg = null;
    this.rule = rule;
    this.selected = selected
    this.init(this.rule, this.selected);
};

CycleRuleItem.extends(BaseWidget);

CycleRuleItem.EVENT_RULE_ITEM_SELECTED = "event_rule_item_selected";

CycleRuleItem.prototype.init = function(rule, selected) {

    this.view = $($("#rule-item-template").html());
    this.parentView.append(this.view);
    this.ruleNameView = this.view.find("[sid=ruleName]");
    this.checkImg = this.view.find("[sid=checkImg]");
    this.ruleNameView.html(rule.name);
    if(selected) {

        this.checkImg.removeClass("dn");
    } else {

        this.checkImg.addClass("dn");
    }
    this.view.on('click', $.proxy(this.onViewClickListener, this));
};

CycleRuleItem.prototype.onViewClickListener = function () {

    this.fire(CycleRuleItem.EVENT_RULE_ITEM_SELECTED, this.rule);
};

