package com.who.daola.data;

import android.provider.BaseColumns;

/**
 * Target Contract
 * Created by dave on 9/7/2014.
 */
public final class TargetContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private TargetContract() {}

    /* Inner class that defines the table contents */
    public static abstract class TargetEntry implements BaseColumns {

        public static final String TABLE_NAME = "target";
        public static final String COLUMN_FIRSTNAME = "firstname";
        public static final String COLUMN_LASTNAME = "lastname";
        public static final String COLUMN_NICKNAME = "nickname";
    }
}