package com.unitedbustech.eld.eventcenter.model;

import com.unitedbustech.eld.eventcenter.core.StateAdditionInfo;
import com.unitedbustech.eld.eventcenter.enums.EventItem;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description AdverseDrivingModel
 */
public class AdverseDrivingModel extends EventModel {

    public AdverseDrivingModel(Builder builder) {

        super(builder);

        this.getAdditionInfo(builder.additionInfo);
    }

    @Override
    EventItem eventItem() {

        return EventItem.ADVERSE_DRIVING;
    }

    @Override
    int getCode(EventModel.Builder builder) {

        return 0;
    }

    public static class Builder extends EventModel.Builder {

        private StateAdditionInfo additionInfo;

        public AdverseDrivingModel.Builder addition(StateAdditionInfo stateAdditionInfo) {

            this.additionInfo = stateAdditionInfo;
            this.date = stateAdditionInfo.getDate();
            return this;
        }

        @Override
        public EventModel buildModel() {

            return new AdverseDrivingModel(this);
        }
    }
}
