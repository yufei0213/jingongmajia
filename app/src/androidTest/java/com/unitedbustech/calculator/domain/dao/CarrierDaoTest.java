package com.unitedbustech.calculator.domain.dao;

import com.unitedbustech.calculator.domain.DataBaseHelper;
import com.unitedbustech.calculator.domain.entry.Carrier;

import org.junit.Before;
import org.junit.Test;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description CarrierDaoTest
 */
public class CarrierDaoTest {

    private Carrier carrier;

    @Before
    public void setUp() throws Exception {

        carrier = new Carrier();
        carrier.setId(1);
        carrier.setName("UBT");
    }

    @Test
    public void insert() throws Exception {

        DataBaseHelper.getDataBase().carrierDao().insert(carrier);
    }
}