package com.unitedbustech.eld.domain;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;

import com.unitedbustech.eld.App;

/**
 * @author yufei0213
 * @date 2017/11/30
 * @description 数据库辅助类
 */
public class DataBaseHelper {

    private static final String DATABASE_NAME = "room";

    private static AppDatabase db;

    public static AppDatabase getDataBase() {

        if (db == null) {

            db = Room.databaseBuilder(App.getContext(),
                    AppDatabase.class, DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    .addMigrations(MIGRATION_4_5)
                    .addMigrations(MIGRATION_5_6)
                    .addMigrations(MIGRATION_6_7)
                    .addMigrations(MIGRATION_7_8)
                    .addMigrations(MIGRATION_8_9)
                    .addMigrations(MIGRATION_9_10)
                    .build();
        }

        return db;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE `driver` ADD `time_zone_alias` TEXT");
            database.execSQL("ALTER TABLE `carrier` ADD `time_zone_alias` TEXT");

            database.execSQL("DROP TABLE `inspection_log`");
            database.execSQL("CREATE TABLE `inspection_log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `driver_id` INTEGER NOT NULL, `vehicle_id` INTEGER NOT NULL, `time` INTEGER NOT NULL, `type` INTEGER NOT NULL, `json` TEXT)");

            database.execSQL("CREATE TABLE `request_cache` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `url` TEXT, `type` INTEGER NOT NULL, `param_json` TEXT)");
            database.execSQL("CREATE TABLE `sign` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT, `sign` TEXT)");
            database.execSQL("CREATE TABLE `profile` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT, `profile` TEXT)");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE `request_cache` ADD `daily_log_id` INTEGER NOT NULL DEFAULT(0)");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("CREATE TABLE `sign_cache` (`date` TEXT NOT NULL, `driver_id` INTEGER NOT NULL, `status` INTEGER NOT NULL, `sign` TEXT, PRIMARY KEY(`date`, `driver_id`))");
        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE `vehicle` ADD `odo_offset` REAL NOT NULL DEFAULT(0)");
            database.execSQL("ALTER TABLE `vehicle` ADD `ecm_sn` TEXT");
            database.execSQL("ALTER TABLE `vehicle` ADD `odo_offset_update_time` TEXT");
        }
    };

    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE `vehicle` ADD `fuel_type` TEXT");
        }
    };

    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE `request_cache` ADD `create_time` INTEGER NOT NULL DEFAULT(0)");
        }
    };

    static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE `rule` ADD `reset_cycle_duty_time` INTEGER NOT NULL DEFAULT(0)");
            database.execSQL("ALTER TABLE `driver_rule` ADD `create_time` TEXT");
            database.execSQL("ALTER TABLE `driver_rule` ADD `date_time` TEXT");
            database.execSQL("ALTER TABLE `driver_rule` ADD `team_drive` INTEGER NOT NULL DEFAULT(0)");
        }
    };

    static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("CREATE TABLE `ifta_log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `server_id` INTEGER NOT NULL, `origin_id` TEXT, `driver_id` INTEGER NOT NULL, `vehicle_id` INTEGER NOT NULL, `date` TEXT, `state` TEXT, `fuel_type` TEXT, `unit_price` REAL NOT NULL, `gallon` REAL NOT NULL, `total_price` REAL NOT NULL, `receipt_pics` TEXT, `receipt_urls` TEXT, `create_time` INTEGER NOT NULL)");

        }
    };

    static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE `carrier` ADD `city` TEXT");
            database.execSQL("ALTER TABLE `carrier` ADD `state` TEXT");

        }
    };
}
