package com.unitedbustech.eld.eventcenter.model;

import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.eventcenter.enums.EventItem;
import com.unitedbustech.eld.hos.core.ModelCenter;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description EngineEventModel
 */
public class EngineEventModel extends EventModel {

    public EngineEventModel(Builder builder) {

        super(builder);

        this.origin = DDLOriginEnum.AUTO_BY_ELD.getCode();
    }

    public EngineEventModel() {

    }

    @Override
    EventItem eventItem() {

        return EventItem.ENGINE_STATE;
    }

    @Override
    int getCode(EventModel.Builder builder) {

        if (DriverState.isSpecial(ModelCenter.getInstance().getCurrentDriverState())) {

            return ((Builder) builder).isOn ? 2 : 4;
        } else {

            return ((Builder) builder).isOn ? 1 : 3;
        }

    }

    public static class Builder extends EventModel.Builder {

        private boolean isOn;

        public Builder isOn(boolean isOn) {

            this.isOn = isOn;
            return this;
        }

        @Override
        public EventModel buildModel() {

            return new EngineEventModel(this);
        }
    }
}
