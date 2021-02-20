package com.unitedbustech.eld.eventcenter.states.Vechile;

import com.unitedbustech.eld.eventcenter.model.EngineEventModel;
import com.unitedbustech.eld.eventcenter.model.EventModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyu on 2018/1/12.
 * 汽车引擎发动状态
 */

public class EnginOnState implements IVechileState {
    @Override
    public List<EventModel> turnOnEngin() {
        return null;
    }

    @Override
    public List<EventModel> turnOffEngin() {
        List<EventModel> result = new ArrayList<>();
        result.add(new EngineEventModel.Builder().isOn(false).build());
        return result;
    }

    @Override
    public List<EventModel> move() {
        return null;
    }
}
