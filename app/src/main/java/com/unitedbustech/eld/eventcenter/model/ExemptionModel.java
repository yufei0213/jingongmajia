package com.unitedbustech.eld.eventcenter.model;

import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.eventcenter.enums.EventItem;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description 豁免模式事件
 */
public class ExemptionModel extends EventModel {

    public ExemptionModel() {
    }

    public ExemptionModel(Builder builder) {

        super(builder);
        this.code = builder.mode;

        this.setOrigin(DDLOriginEnum.AUTO_BY_ELD.getCode());
    }

    @Override
    public int getType() {
        return EventItem.EXEMPTION_MODE.getCode();
    }

    @Override
    EventItem eventItem() {
        return EventItem.EXEMPTION_MODE;
    }

    @Override
    int getCode(EventModel.Builder builder) {

        Builder builder1 = (Builder) builder;
        return builder1.mode;
    }

    public static class Builder extends EventModel.Builder {

        private int mode;

        public Builder mode(int mode) {

            this.mode = mode;
            return this;
        }

        @Override
        public EventModel buildModel() {
            return new ExemptionModel(this);
        }
    }
}
