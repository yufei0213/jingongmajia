package com.unitedbustech.eld.domain.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.unitedbustech.eld.domain.entry.Rule;

import java.util.List;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description RuleDao
 */
@Dao
public interface RuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Rule> rules);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Rule rule);

    @Query("select * from rule where id = :ruleId limit 1")
    Rule getRule(int ruleId);

    @Query("select * from rule where id in (:ids)")
    List<Rule> getRuleList(List<Integer> ids);
}
