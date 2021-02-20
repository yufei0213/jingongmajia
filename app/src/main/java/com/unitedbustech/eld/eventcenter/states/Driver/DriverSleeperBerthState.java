package com.unitedbustech.eld.eventcenter.states.Driver;

import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.eventcenter.core.StateAdditionInfo;
import com.unitedbustech.eld.eventcenter.model.DriverStatusModel;
import com.unitedbustech.eld.eventcenter.model.EventModel;
import com.unitedbustech.eld.eventcenter.model.SpecialEventModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description 司机的SleeperBerth状态
 */
public class DriverSleeperBerthState implements IDriverState {
    @Override
    public List<EventModel> changeStateToODND(StateAdditionInfo stateAdditionInfo) {

        List<EventModel> eventModelList = new ArrayList<>();

        eventModelList.add(new DriverStatusModel.Builder()
                .state(DriverState.ON_DUTY_NOT_DRIVING)
                .addition(stateAdditionInfo)
                .build());
        return eventModelList;
    }

    @Override
    public List<EventModel> changeStateToOD(StateAdditionInfo stateAdditionInfo) {

        List<EventModel> eventModelList = new ArrayList<>();

        eventModelList.add(new DriverStatusModel.Builder()
                .state(DriverState.OFF_DUTY)
                .addition(stateAdditionInfo)
                .build());
        return eventModelList;
    }

    @Override
    public List<EventModel> changeStateToSB(StateAdditionInfo stateAdditionInfo) {

        return new ArrayList<>();
    }

    @Override
    public List<EventModel> changeStateToD(StateAdditionInfo stateAdditionInfo) {

        List<EventModel> eventModelList = new ArrayList<>();

        eventModelList.add(new DriverStatusModel.Builder()
                .state(DriverState.DRIVING)
                .addition(stateAdditionInfo)
                .build());
        return eventModelList;
    }

    @Override
    public List<EventModel> changeStateToYM(StateAdditionInfo stateAdditionInfo) {

        List<EventModel> eventModelList = new ArrayList<>();

        eventModelList.add(new SpecialEventModel.Builder()
                .state(DriverState.YARD_MOVE)
                .isClear(false)
                .addition(stateAdditionInfo)
                .build());
        return eventModelList;
    }

    @Override
    public List<EventModel> changeStateToPU(StateAdditionInfo stateAdditionInfo) {

        List<EventModel> eventModelList = new ArrayList<>();
        eventModelList.add(new SpecialEventModel.Builder()
                .state(DriverState.PERSONAL_USE)
                .isClear(false)
                .build());

        return eventModelList;
    }
}
