package com.unitedbustech.eld.domain.dao;

import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.DriverRule;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description DriverRuleDaoTest
 */
public class DriverRuleDaoTest {

    private DriverRule driverRule;

    @Before
    public void before() throws  Exception{

        driverRule = new DriverRule();
        driverRule.setDriverId(1);
        driverRule.setRuleId(1);
    }

    @Test
    public void insert() throws Exception {

        DataBaseHelper.getDataBase().driverRuleDao().insert(driverRule);
    }
}