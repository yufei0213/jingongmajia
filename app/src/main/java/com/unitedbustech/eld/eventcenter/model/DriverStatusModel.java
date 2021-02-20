package com.unitedbustech.eld.eventcenter.model;

import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.eventcenter.core.StateAdditionInfo;
import com.unitedbustech.eld.eventcenter.enums.EventItem;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description 司机状态模型
 */
public class DriverStatusModel extends EventModel {

    public DriverStatusModel(Builder builder) {

        super(builder);

        this.getAdditionInfo(builder.additionInfo);
    }

    public DriverStatusModel() {
    }

    @Override
    EventItem eventItem() {

        return EventItem.DRIVER_WORK_STATE;
    }

    @Override
    int getCode(EventModel.Builder builder) {

        switch (((Builder) builder).state) {
            case ON_DUTY_NOT_DRIVING:
                return 4;
            case OFF_DUTY:
                return 1;
            case DRIVING:
                return 3;
            case SLEEPER_BERTH:
                return 2;
            case YARD_MOVE:
                return 2;
            case PERSONAL_USE:
                return 1;
        }
        return 0;
    }

    public static class Builder extends EventModel.Builder {

        private DriverState state;

        private StateAdditionInfo additionInfo;

        public Builder state(DriverState driverState) {

            this.state = driverState;
            return this;
        }

        public Builder addition(StateAdditionInfo stateAdditionInfo) {

            this.additionInfo = stateAdditionInfo;
            this.date = stateAdditionInfo.getDate();
            return this;
        }

        @Override
        public EventModel buildModel() {

            return new DriverStatusModel(this);
        }
    }
}
