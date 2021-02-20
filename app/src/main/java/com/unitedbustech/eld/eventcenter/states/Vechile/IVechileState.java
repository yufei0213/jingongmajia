package com.unitedbustech.eld.eventcenter.states.Vechile;

import com.unitedbustech.eld.eventcenter.model.EventModel;

import java.util.List;

/**
 * Created by zhangyu on 2018/1/12.
 * 车辆状态抽象类，代表的是三个状态。
 */

public interface IVechileState {


    List<EventModel> turnOnEngin();

    List<EventModel> turnOffEngin();

    List<EventModel> move();

}
