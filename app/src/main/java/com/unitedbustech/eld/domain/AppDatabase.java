package com.unitedbustech.eld.domain;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.unitedbustech.eld.domain.dao.CarrierDao;
import com.unitedbustech.eld.domain.dao.CarrierRuleDao;
import com.unitedbustech.eld.domain.dao.CarrierVehicleDao;
import com.unitedbustech.eld.domain.dao.DailyLogDao;
import com.unitedbustech.eld.domain.dao.DriverDao;
import com.unitedbustech.eld.domain.dao.DriverRuleDao;
import com.unitedbustech.eld.domain.dao.IftaLogDao;
import com.unitedbustech.eld.domain.dao.InspectionLogDao;
import com.unitedbustech.eld.domain.dao.ProfileDao;
import com.unitedbustech.eld.domain.dao.RecentVehicleDao;
import com.unitedbustech.eld.domain.dao.RequestCacheDao;
import com.unitedbustech.eld.domain.dao.RuleDao;
import com.unitedbustech.eld.domain.dao.SignCacheDao;
import com.unitedbustech.eld.domain.dao.VehicleDao;
import com.unitedbustech.eld.domain.entry.Carrier;
import com.unitedbustech.eld.domain.entry.CarrierRule;
import com.unitedbustech.eld.domain.entry.CarrierVehicle;
import com.unitedbustech.eld.domain.entry.DailyLog;
import com.unitedbustech.eld.domain.entry.Driver;
import com.unitedbustech.eld.domain.entry.DriverRule;
import com.unitedbustech.eld.domain.entry.IftaLog;
import com.unitedbustech.eld.domain.entry.InspectionLog;
import com.unitedbustech.eld.domain.entry.ProfileEntity;
import com.unitedbustech.eld.domain.entry.RecentVehicle;
import com.unitedbustech.eld.domain.entry.RequestCache;
import com.unitedbustech.eld.domain.entry.Rule;
import com.unitedbustech.eld.domain.entry.SignCache;
import com.unitedbustech.eld.domain.entry.Vehicle;

/**
 * @author yufei0213
 * @date 2017/11/30
 * @description 数据库
 */
@Database(entities = {Driver.class,
        Rule.class,
        DriverRule.class,
        Carrier.class,
        CarrierRule.class,
        Vehicle.class,
        CarrierVehicle.class,
        RecentVehicle.class,
        DailyLog.class,
        InspectionLog.class,
        RequestCache.class,
        SignCache.class,
        ProfileEntity.class,
        IftaLog.class},
        version = 10)

public abstract class AppDatabase extends RoomDatabase {

    public abstract DriverDao driverDao();

    public abstract RuleDao ruleDao();

    public abstract DriverRuleDao driverRuleDao();

    public abstract CarrierDao carrierDao();

    public abstract CarrierRuleDao carrierRuleDao();

    public abstract DailyLogDao dailyLogDao();

    public abstract VehicleDao vehicleDao();

    public abstract CarrierVehicleDao carrierVehicleDao();

    public abstract RecentVehicleDao recentVehicleDao();

    public abstract InspectionLogDao inspectionLogDao();

    public abstract RequestCacheDao requestCacheDao();

    public abstract SignCacheDao signCacheDao();

    public abstract ProfileDao profileDao();

    public abstract IftaLogDao iftaLogDao();
}
