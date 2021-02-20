package com.unitedbustech.eld.eventcenter.model;

import com.unitedbustech.eld.eventcenter.enums.EventItem;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description LoginModel
 */
public class LoginModel extends EventModel {

    public LoginModel(Builder builder) {

        super(builder);

        this.vehicleId = 0;

        this.totalOdometer = 0;
        this.accumulatedOdometer = 0;

        this.totalEngineHours = 0;
        this.accumulatedEngineHours = 0;
    }

    @Override
    EventItem eventItem() {

        return EventItem.LOGIN_STATE;
    }

    @Override
    int getCode(EventModel.Builder builder) {

        return ((Builder) builder).login ? 1 : 2;
    }

    public static class Builder extends EventModel.Builder {

        private boolean login;

        public Builder isLogin(boolean isLogin) {

            this.login = isLogin;
            return this;
        }

        @Override
        protected EventModel buildModel() {

            return new LoginModel(this);
        }
    }
}
