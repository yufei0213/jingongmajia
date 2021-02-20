package com.unitedbustech.eld.hos.violation;

/**
 * @author yufei0213
 * @date 2018/7/16
 * @description ViolationCalculator
 */
public class ViolationEvent {

    private long start;
    private long end;

    public ViolationEvent() {
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
