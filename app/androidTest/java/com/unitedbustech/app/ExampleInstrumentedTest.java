package com.unitedbustech.app;

import android.support.test.runner.AndroidJUnit4;

import com.unitedbustech.calculator.util.FileUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {

        FileUtil.zipLogs("2018-03-17","2018-03-16","2018-03-15","2018-03-14");
    }
}
