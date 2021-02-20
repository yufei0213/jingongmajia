package com.unitedbustech.eld.domain.dao;

import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.CarrierRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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