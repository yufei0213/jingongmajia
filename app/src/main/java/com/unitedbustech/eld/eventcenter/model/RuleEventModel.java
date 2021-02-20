package com.unitedbustech.eld.eventcenter.model;

import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.eventcenter.enums.EventItem;

/**
 * @author liuzhe
 * @date 2018/7/24
 * @description 规则切换事件
 */
public class RuleEventModel extends EventModel {

    public RuleEventModel(Builder builder) {

        super(builder);

        this.origin = DDLOriginEnum.EDIT_BY_DRIVER.getCode();
    }

    @Override
    EventItem eventItem() {

        return EventItem.RULE_CHANGE;
    }

    @Override
    int getCode(EventModel.Builder builder) {

        return ((Builder)builder).ruleId;
    }

    public static class Builder extends EventModel.Builder {

        private int ruleId;

        public Builder setRuleId(int ruleId) {

            this.ruleId = ruleId;
            return this;
        }

        @Override
        public EventModel buildModel() {

            return new RuleEventModel(this);
        }
    }
}
