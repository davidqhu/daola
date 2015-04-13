package com.who.daola.data;

import android.provider.BaseColumns;

/**
 * Fence table table contract
 * Created by dave on 4/12/2015.
 */
public class KnowhereMessageContract {
    private KnowhereMessageContract() {}

    public static abstract class KnowhereMessageEntry implements BaseColumns {
        public static final String TABLE_NAME = "knowheremessage";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_MESSAGE_ID = "message_id";
        public static final String COLUMN_PAYLOAD = "payload";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
