package com.unitedbustech.eld.domain.dao;

import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Rule;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description RuleDaoTest
 */
public class RuleDaoTest {
    @Test
    public void insert() throws Exception {

        Rule rule = new Rule();
        rule.setId(1);
        rule.setDutyDays(7);

        DataBaseHelper.getDataBase().ruleDao().insert(rule);
    }
}