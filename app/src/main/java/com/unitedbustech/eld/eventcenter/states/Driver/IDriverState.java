package com.unitedbustech.eld.eventcenter.states.Driver;

import com.unitedbustech.eld.eventcenter.core.StateAdditionInfo;
import com.unitedbustech.eld.eventcenter.model.EventModel;

import java.util.List;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description 抽象类，描述所有状态
 */
public interface IDriverState {

    //将状态改变成OnDutyNotDriving
    List<EventModel> changeStateToODND(StateAdditionInfo stateAdditionInfo);

    //将状态改变成OffDuty
    List<EventModel> changeStateToOD(StateAdditionInfo stateAdditionInfo);

    //将状态改变成SleeerBerth
    List<EventModel> changeStateToSB(StateAdditionInfo stateAdditionInfo);

    //将状态改变成Driving
    List<EventModel> changeStateToD(StateAdditionInfo stateAdditionInfo);

    //将状态改变成YardMove
    List<EventModel> changeStateToYM(StateAdditionInfo stateAdditionInfo);

    //将状态改变成PersonalUse
    List<EventModel> changeStateToPU(StateAdditionInfo stateAdditionInfo);
}
