package com.who.daola.data;

import android.provider.BaseColumns;

/**
 * Target Contract
 * Created by dave on 9/7/2014.
 */
public final class TrackerTargetContract {

    public static final int CONTROL_LEVEL_NO = 0;
    public static final int CONTROL_LEVEL_SHARED = 1;
    public static final int CONTROL_LEVEL_SOLE = 2;

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private TrackerTargetContract() {}

    public static abstract class TrackerTargetBaseColumns implements BaseColumns {
        public static final String COLUMN_CONTROL_LEVEL = "control_level";
        public static final String COLUMN_ENABLED = "enabled";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_REG_ID = "regid";
    }

    /* Inner class that defines the table contents */
    public static abstract class TargetEntry extends TrackerTargetBaseColumns {
        public static final String TABLE_NAME = "target";
    }

    /* Inner class that defines the table contents */
    public static abstract class TrackerEntry extends TrackerTargetBaseColumns {
        public static final String TABLE_NAME = "tracker";
    }
}