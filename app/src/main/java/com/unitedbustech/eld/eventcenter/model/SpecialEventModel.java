package com.unitedbustech.eld.eventcenter.model;

import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.eventcenter.core.StateAdditionInfo;
import com.unitedbustech.eld.eventcenter.enums.EventItem;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description 特殊事件模型，指的是Yard Move,Personal Use
 */
public class SpecialEventModel extends EventModel {

    private int dataStatus;
    private int malfunctionStatus;

    public SpecialEventModel(Builder builder) {

        super(builder);

        this.getAdditionInfo(builder.additionInfo);
    }

    @Override
    EventItem eventItem() {

        return EventItem.SPECIAL_WORK_STATE;
    }

    @Override
    int getCode(EventModel.Builder builder) {

        Builder thisBuilder = (Builder) builder;
        if (thisBuilder.isClear) {

            return 0;
        }

        switch (thisBuilder.state) {

            case YARD_MOVE:
                return 2;
            case PERSONAL_USE:
                return 1;
        }
        return 0;
    }

    public int getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(int dataStatus) {
        this.dataStatus = dataStatus;
    }

    public int getMalfunctionStatus() {
        return malfunctionStatus;
    }

    public void setMalfunctionStatus(int malfunctionStatus) {
        this.malfunctionStatus = malfunctionStatus;
    }

    public static class Builder extends EventModel.Builder {

        private DriverState state;

        private boolean isClear;

        private StateAdditionInfo additionInfo;

        public Builder state(DriverState state) {

            this.state = state;
            return this;
        }

        public Builder isClear(boolean isClear) {

            this.isClear = isClear;
            return this;
        }

        public Builder addition(StateAdditionInfo additionInfo) {

            this.additionInfo = additionInfo;
            this.date = additionInfo.getDate();
            return this;
        }

        @Override
        public EventModel buildModel() {

            return new SpecialEventModel(this);
        }
    }
}
