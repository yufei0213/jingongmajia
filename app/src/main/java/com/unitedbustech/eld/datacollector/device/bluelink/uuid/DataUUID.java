package com.unitedbustech.eld.datacollector.device.bluelink.uuid;

import java.util.UUID;

/**
 * @author yufei0213
 * @date 2018/5/5
 * @description DataUUID
 */
public enum DataUUID {

    STATE("00001a01-cb73-437d-8fad-842c16c7aa6f", "Vehicle stateString"),
    EVSPEED("00001a02-cb73-437d-8fad-842c16c7aa6f", "Engine and Vehicle speed, load and others"),
    DISTANCE_ARRAY("00001a03-cb73-437d-8fad-842c16c7aa6f", "Total distances array (up to 4 sources)"),
    ODOMETER("00001a04-cb73-437d-8fad-842c16c7aa6f", "Odometer (filtered total distance) and engine hours"),
    FUEL("00001a05-cb73-437d-8fad-842c16c7aa6f", "Fuel information"),
    BATTERY("00001a07-cb73-437d-8fad-842c16c7aa6f", "Battery and fluids information"),
    VIN("00001b01-cb73-437d-8fad-842c16c7aa6f", "Vehicle ID numbers"),
    DIAGNOSTIC("00001b04-cb73-437d-8fad-842c16c7aa6f", "JBus diagnostic codes data array"),
    EVENT("00001b03-cb73-437d-8fad-842c16c7aa6f", "System Event ID"),
    COMBINED("00001b02-cb73-437d-8fad-842c16c7aa6f", "Combined (integrated) group info"),
    HISTORY("00001a11-cb73-437d-8fad-842c16c7aa6f", "Vehicle JBus history record");

    private String uuid;
    private String name;

    DataUUID(String uuid, String name) {

        this.uuid = uuid;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public static DataUUID getByUuid(UUID uuid) {

        DataUUID result = null;
        for (DataUUID dataUUID : DataUUID.values()) {

            if (UUID.fromString(dataUUID.getUuid()).equals(uuid)) {

                result = dataUUID;
                break;
            }
        }

        return result;
    }
}
