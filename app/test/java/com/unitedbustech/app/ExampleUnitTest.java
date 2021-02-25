package com.unitedbustech.app;

import com.unitedbustech.calculator.util.ConvertUtil;

import org.junit.Test;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
//        vehicleDataRecorder.setTotalEngineHours(ConvertUtil.decimal2Point(ConvertUtil.minute2Hour(this.currentEngineHour)));

        Double date = 31.5d;
        System.out.println(ConvertUtil.minute2Hour(date));
        System.out.println(ConvertUtil.decimal2Point(ConvertUtil.minute2Hour(date)));
    }

    /**
     * 调试使用 todo 删除
     * @param second
     * @return
     */
    private String secondToHourString(int second) {

        return second / (60 * 60) + ":" + second % (60 * 60) / 60 + ":" + second % 60;
    }
}