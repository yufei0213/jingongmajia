package com.unitedbustech.eld.eventcenter.model;

import com.unitedbustech.eld.eventcenter.enums.EventItem;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description 签名事件
 */
public class LogSignModel extends EventModel {

    /**
     * 表示签的名是哪一天的日志
     * 格式： MM/dd/yy
     */
    private String certifiedRecordDate;

    public LogSignModel(Builder builder) {

        super(builder);

        this.certifiedRecordDate = builder.certifiedRecordDate;
    }

    @Override
    EventItem eventItem() {

        return EventItem.SIGN;
    }

    @Override
    int getCode(EventModel.Builder builder) {

        return ((Builder) builder).count;
    }

    @Override
    public int getType() {

        return EventItem.SIGN.getCode();
    }

    public String getCertifiedRecordDate() {
        return certifiedRecordDate;
    }

    public void setCertifiedRecordDate(String certifiedRecordDate) {
        this.certifiedRecordDate = certifiedRecordDate;
    }

    public static class Builder extends EventModel.Builder {

        /**
         * 表示针对该日志，第几次签名
         */
        private int count;

        private String certifiedRecordDate;

        public Builder setCount(int count) {

            this.count = count;
            return this;
        }

        public Builder recordDate(String certifiedRecordDate) {

            this.certifiedRecordDate = certifiedRecordDate;
            return this;
        }

        @Override
        public EventModel buildModel() {

            return new LogSignModel(this);
        }
    }
}
