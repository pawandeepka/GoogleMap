package com.dcdineshk.googlemap.database;

import android.provider.BaseColumns;

/**
 * Created by DC\dineshk on 26/10/17.
 */

public class GeofenceContract {

    private GeofenceContract() {}

    public static class GeofenceEntry implements BaseColumns {
        public static final String TABLE_NAME = "geofences";
        public static final String COLUMN_NAME_KEY = "key";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LNG = "lng";
        public static final String COLUMN_NAME_EXPIRES = "expires";
    }

}
