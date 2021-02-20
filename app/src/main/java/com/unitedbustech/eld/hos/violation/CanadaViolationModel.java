package com.unitedbustech.eld.hos.violation;

import com.unitedbustech.eld.App;
import com.unitedbustech.eld.R;

/**
 * @author liuzhe
 * @date 2018/7/19
 * @description CanadaViolationModel
 */
public class CanadaViolationModel extends ViolationModel {

    private int shift_13_limit = 0;
    private int shift_14_limit = 0;
    private int shift_16_limit = 0;

    private int day_13_limit = 0;
    private int day_14_limit = 0;
    private int day_10_limit = 0;
    private int day_20_limit = 0;

    private int cycle_70_limit = 0;
    private int cycle_120_limit = 0;
    private int cycle_24_limit = 0;

    private int off_24_limit = 0;

    @Override
    public void resetLimitString() {

        this.limitString = "";
        if (shift_13_limit > 0) {

            this.limitString += App.getContext().getResources().getString(R.string.shift_13_limit);
        }
        if (shift_14_limit > 0) {

            this.limitString += App.getContext().getResources().getString(R.string.shift_14_limit);
        }
        if (shift_16_limit > 0) {

            this.limitString += App.getContext().getResources().getString(R.string.shift_16_limit);
        }
        if (day_13_limit > 0) {

            this.limitString += App.getContext().getResources().getString(R.string.day_13_limit);
        }
        if (day_14_limit > 0) {

            this.limitString += App.getContext().getResources().getString(R.string.day_14_limit);
        }
        if (day_10_limit > 0) {

            this.limitString += App.getContext().getResources().getString(R.string.day_10_limit);
        }
        if (day_20_limit > 0) {

            this.limitString += App.getContext().getResources().getString(R.string.day_20_limit);
        }
        if (cycle_70_limit > 0) {

            this.limitString += App.getContext().getResources().getString(R.string.cycle_70_limit);
        }
        if (cycle_120_limit > 0) {

            this.limitString += App.getContext().getResources().getString(R.string.cycle_120_limit);
        }
        if (cycle_24_limit > 0) {

            this.limitString += App.getContext().getResources().getString(R.string.cycle_24_limit);
        }
        if (off_24_limit > 0) {

            this.limitString += App.getContext().getResources().getString(R.string.off_24_limit);
        }
    }

    public int getShift_13_limit() {
        return shift_13_limit;
    }

    public void setShift_13_limit(int shift_13_limit) {
        this.shift_13_limit = shift_13_limit;
    }

    public int getShift_14_limit() {
        return shift_14_limit;
    }

    public void setShift_14_limit(int shift_14_limit) {
        this.shift_14_limit = shift_14_limit;
    }

    public int getShift_16_limit() {
        return shift_16_limit;
    }

    public void setShift_16_limit(int shift_16_limit) {
        this.shift_16_limit = shift_16_limit;
    }

    public int getDay_13_limit() {
        return day_13_limit;
    }

    public void setDay_13_limit(int day_13_limit) {
        this.day_13_limit = day_13_limit;
    }

    public int getDay_14_limit() {
        return day_14_limit;
    }

    public void setDay_14_limit(int day_14_limit) {
        this.day_14_limit = day_14_limit;
    }

    public int getDay_10_limit() {
        return day_10_limit;
    }

    public void setDay_10_limit(int day_10_limit) {
        this.day_10_limit = day_10_limit;
    }

    public int getDay_20_limit() {
        return day_20_limit;
    }

    public void setDay_20_limit(int day_20_limit) {
        this.day_20_limit = day_20_limit;
    }

    public int getCycle_70_limit() {
        return cycle_70_limit;
    }

    public void setCycle_70_limit(int cycle_70_limit) {
        this.cycle_70_limit = cycle_70_limit;
    }

    public int getCycle_120_limit() {
        return cycle_120_limit;
    }

    public void setCycle_120_limit(int cycle_120_limit) {
        this.cycle_120_limit = cycle_120_limit;
    }

    public int getCycle_24_limit() {
        return cycle_24_limit;
    }

    public void setCycle_24_limit(int cycle_24_limit) {
        this.cycle_24_limit = cycle_24_limit;
    }

    public int getOff_24_limit() {
        return off_24_limit;
    }

    public void setOff_24_limit(int off_24_limit) {
        this.off_24_limit = off_24_limit;
    }
}
