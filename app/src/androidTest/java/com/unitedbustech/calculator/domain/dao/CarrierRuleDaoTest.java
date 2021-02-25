package com.unitedbustech.calculator.domain.dao;

import com.unitedbustech.calculator.domain.DataBaseHelper;
import com.unitedbustech.calculator.domain.entry.CarrierRule;

import org.junit.Before;
import org.junit.Test;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description TODO
 */
public class CarrierRuleDaoTest {

    private CarrierRule carrierRule;

    @Before
    public void setUp() throws Exception {

        carrierRule = new CarrierRule();
    }

    @Test
    public void insert() throws Exception {

        carrierRule.setCarrierId(1);
        carrierRule.setRuleId(1);

        DataBaseHelper.getDataBase().carrierRuleDao().insert(carrierRule);
    }
}