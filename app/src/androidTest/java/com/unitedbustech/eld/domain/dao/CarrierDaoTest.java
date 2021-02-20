package com.unitedbustech.eld.domain.dao;

import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Carrier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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