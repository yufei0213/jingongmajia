package com.unitedbustech.calculator.domain.dao;

import com.unitedbustech.calculator.domain.DataBaseHelper;
import com.unitedbustech.calculator.domain.entry.Driver;

import org.junit.Test;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description DriverDaoTest
 */
public class DriverDaoTest {
    @Test
    public void insert() throws Exception {

        Driver driver = new Driver();
        driver.setId(1);
        driver.setName("zhangsan");

        DataBaseHelper.getDataBase().driverDao().insert(driver);
    }

    @Test
    public void get() throws Exception {

        Driver driver = DataBaseHelper.getDataBase().driverDao().getDriver(1);

        if (driver != null) {

            System.out.println(driver.getName());
        }
    }
}