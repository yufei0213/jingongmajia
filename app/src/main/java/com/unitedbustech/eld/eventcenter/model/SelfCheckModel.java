package com.unitedbustech.eld.eventcenter.model;

import com.unitedbustech.eld.common.DiagnosticType;
import com.unitedbustech.eld.common.MalfunctionType;
import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.eventcenter.enums.EventItem;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description 自检事件模型
 */
public class SelfCheckModel extends EventModel {

    /**
     * 异常码
     */
    private String abnormalCode;

    /**
     * 是否是清除事件
     */
    private boolean clear;

    public SelfCheckModel() {
    }

    public SelfCheckModel(Builder builder) {

        super(builder);
        this.clear = builder.clear;
        this.code = getCode(builder);
        this.origin = DDLOriginEnum.AUTO_BY_ELD.getCode();

        if (builder.malfunctionType != null) {

            abnormalCode = builder.malfunctionType.getCode();
        }
        if (builder.diagnosticType != null) {

            abnormalCode = builder.diagnosticType.getCode();
        }
    }

    @Override
    public int getType() {

        return 7;
    }

    @Override
    EventItem eventItem() {

        return EventItem.SELF_CHECK;
    }

    @Override
    int getCode(EventModel.Builder builder) {

        Builder builder1 = (Builder) builder;
        if (builder1.diagnosticType != null) {

            return clear ? 4 : 3;
        }
        return clear ? 2 : 1;
    }

    public static class Builder extends EventModel.Builder {

        private DiagnosticType diagnosticType;

        private MalfunctionType malfunctionType;

        private boolean clear;

        public Builder diagnostic(DiagnosticType diagnosticType) {

            this.diagnosticType = diagnosticType;
            return this;
        }

        public Builder malfunction(MalfunctionType malfunctionType) {

            this.malfunctionType = malfunctionType;
            return this;
        }

        /**
         * 是否是清除
         * 如果是清除事件，需要调用该方法。如果是新建事件,无需调用该方法。
         *
         * @return
         */
        public Builder clear(Boolean isClear) {

            this.clear = isClear;
            return this;
        }

        @Override
        public EventModel buildModel() {

            return new SelfCheckModel(this);
        }
    }

    public String getAbnormalCode() {
        return abnormalCode;
    }

    public void setAbnormalCode(String abnormalCode) {
        this.abnormalCode = abnormalCode;
    }
}
