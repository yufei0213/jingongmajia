package com.unitedbustech.eld.eventcenter.states.Vechile;

import com.unitedbustech.eld.eventcenter.enums.EngineState;
import com.unitedbustech.eld.eventcenter.model.EventModel;

import java.util.List;

/**
 * Created by zhangyu on 2018/1/12.
 * 车辆状态状态机管理器
 */

public class EngineStateManager {

    private IVechileState state;

    public EngineStateManager() {

        this.state = new EnginOffState();
    }

    public List<EventModel> enginStart() {

        List<EventModel> result = state.turnOnEngin();
        resetLocalState(EngineState.ENGINE_ON);
        return result;
    }

    public List<EventModel> enginStop() {

        List<EventModel> result = state.turnOffEngin();
        resetLocalState(EngineState.ENGINE_OFF);
        return result;
    }

    public List<EventModel> move() {

        return state.move();
    }

    public List<EventModel> stop() {

        return state.turnOnEngin();
    }

    /**
     * 根据新的状态，改变本地真是状态。
     *
     * @param state 新状态
     */
    private void resetLocalState(EngineState state) {

        this.state = null;
        switch (state) {

            case ENGINE_ON:
                this.state = new EnginOnState();
                break;
            case ENGINE_OFF:
                this.state = new EnginOffState();
                break;
        }
    }
}
