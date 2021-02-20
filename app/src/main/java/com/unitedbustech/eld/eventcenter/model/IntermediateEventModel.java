package com.unitedbustech.eld.eventcenter.model;

import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.eventcenter.enums.EventItem;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description 中间日志模型
 */
public class IntermediateEventModel extends EventModel {

    /**
     * 中间日志上报间隔1小时（单位毫秒）
     */
    public static final long INTERMEDIATE_EVENT_INTERVAL = 1 * 60 * 60 * 1000L;

    public IntermediateEventModel(Builder builder) {

        super(builder);

        this.origin = DDLOriginEnum.AUTO_BY_ELD.getCode();
    }

    @Override
    EventItem eventItem() {

        return EventItem.INTERMEDIATE;
    }

    @Override
    public int getType() {

        return EventItem.INTERMEDIATE.getCode();
    }

    @Override
    int getCode(EventModel.Builder builder) {

        return 1;
    }

    public static class Builder extends EventModel.Builder {
        @Override
        public EventModel buildModel() {

            return new IntermediateEventModel(this);
        }
    }
}
