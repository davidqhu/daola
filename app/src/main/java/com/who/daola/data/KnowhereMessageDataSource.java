package com.who.daola.data;

/**
 * Created by hud on 4/12/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.who.daola.data.KnowhereMessageContract.KnowhereMessageEntry.COLUMN_MESSAGE_ID;
import static com.who.daola.data.KnowhereMessageContract.KnowhereMessageEntry.COLUMN_PAYLOAD;
import static com.who.daola.data.KnowhereMessageContract.KnowhereMessageEntry.COLUMN_TIMESTAMP;
import static com.who.daola.data.KnowhereMessageContract.KnowhereMessageEntry.COLUMN_TYPE;
import static com.who.daola.data.KnowhereMessageContract.KnowhereMessageEntry.TABLE_NAME;

public class KnowhereMessageDataSource {

    private static final String TAG = KnowhereMessageDataSource.class.getName();
    // Database fields
    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private String[] allColumns = {_ID, COLUMN_MESSAGE_ID, COLUMN_TYPE,
            COLUMN_PAYLOAD, COLUMN_TIMESTAMP};

    public KnowhereMessageDataSource(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public KnowhereMessage createKnowhereMessage(String messageId, String type, String payload,
                                                 long timeStamp) {

        long insertId = database.insert(TABLE_NAME, null,
                getContentValues(messageId, type, payload, timeStamp));

        return getKnowhereMessage(insertId);
    }

    public KnowhereMessage createKnowhereMessage(Bundle bundle) {
        String messageId = bundle.getString(KnowhereMessage.MESSAGE_ID);
        if (messageId == null || messageId.isEmpty()) {
            throw new IllegalArgumentException(KnowhereMessage.MESSAGE_ID + " cannot be null");
        }
        bundle.remove(KnowhereMessage.MESSAGE_ID);
        String messageType = bundle.getString(KnowhereMessage.TYPE);
        if (KnowhereMessage.Type.fromString(messageType)==null) {
            throw new IllegalArgumentException(KnowhereMessage.TYPE + " is invalid: " + messageType);
        }
        bundle.remove(KnowhereMessage.TYPE);

        return createKnowhereMessage(messageId,
                messageType,
                KnowhereMessage.getPayloadStringFromBundle(bundle),
                System.currentTimeMillis());
    }

    public KnowhereMessage updateKnowhereMessage(long id, String messageId, String type,
                                                 String payload, long timeStamp) {
        database.update(TABLE_NAME,
                getContentValues(messageId, type, payload, timeStamp),
                _ID + "='" + id + "'", null);
        return getKnowhereMessage(id);
    }

    private ContentValues getContentValues(String messageId, String type, String payload,
                                           long timeStamp) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE_ID, messageId);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_PAYLOAD, payload);
        values.put(COLUMN_TIMESTAMP, timeStamp);

        return values;
    }

    public KnowhereMessage getKnowhereMessage(long id) {
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, _ID + " = " + id, null,
                null, null, null);
        try {
            cursor.moveToFirst();
            KnowhereMessage message = cursorToKnowhereMessage(cursor);
            return message;
        } finally {
            cursor.close();
        }
    }

    public KnowhereMessage getKnowhereMessage(String messageId){
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, COLUMN_MESSAGE_ID + " = '" + messageId + "'", null,
                null, null, null);
        try {
            cursor.moveToFirst();
            KnowhereMessage message = cursorToKnowhereMessage(cursor);
            return message;
        } finally {
            cursor.close();
        }
    }

    public void deleteKnowhereMessage(Fence fence) {
        long id = fence.getId();
        Log.i(TAG, "KnowhereMessage deleted with id: " + id);
        database.delete(TABLE_NAME, _ID + " = " + id, null);
    }

    public List<KnowhereMessage> getAllKnowhereMessages() {
        List<KnowhereMessage> messages = new ArrayList<>();

        Cursor cursor = database.query(TABLE_NAME,
                allColumns, null, null, null, null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                KnowhereMessage message = cursorToKnowhereMessage(cursor);
                messages.add(message);
                cursor.moveToNext();
            }
            return messages;
        } finally {
            cursor.close();
        }
    }

    private KnowhereMessage cursorToKnowhereMessage(Cursor cursor) {
        KnowhereMessage message = new KnowhereMessage();
        message.setId(cursor.getLong(0));
        message.setMessageId(cursor.getString(1));
        message.setMessageType(cursor.getString(2));
        message.setPayload(cursor.getString(3));
        message.setTimeStamp(cursor.getLong(4));
        return message;
    }
}
