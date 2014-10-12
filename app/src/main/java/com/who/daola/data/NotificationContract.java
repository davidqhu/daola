package com.who.daola.data;

import android.provider.BaseColumns;

/**
 * Created by dave on 10/7/2014.
 */
public class NotificationContract {
    private NotificationContract() {}

    public static abstract class NotificationEntry implements BaseColumns {
        public static final String TABLE_NAME = "notification";
        public static final String COLUMN_FENCE_ID = "fence_id";
        public static final String COLUMN_TARGET_ID = "target_id";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TRANSITION_TYPE = "transition_type";
    }
}
