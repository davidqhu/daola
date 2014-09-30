package com.who.daola.data;

import android.provider.BaseColumns;

/**
 * Fence table table contract
 * Created by dave on 9/7/2014.
 */
public class FenceContract {
    private FenceContract() {}

    public static abstract class FenceEntry implements BaseColumns {
        public static final String TABLE_NAME = "fence";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_RADIUS = "radius";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_LATITUDE = "latitude";
    }
}
