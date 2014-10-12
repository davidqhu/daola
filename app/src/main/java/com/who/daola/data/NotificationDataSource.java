package com.who.daola.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.who.daola.data.NotificationContract.NotificationEntry.TABLE_NAME;
import static com.who.daola.data.NotificationContract.NotificationEntry.COLUMN_TIME;
import static com.who.daola.data.NotificationContract.NotificationEntry.COLUMN_TRANSITION_TYPE;
import static com.who.daola.data.NotificationContract.NotificationEntry.COLUMN_FENCE_ID;
import static com.who.daola.data.NotificationContract.NotificationEntry.COLUMN_TARGET_ID;

/**
 * Created by dave on 10/7/2014.
 */
public class NotificationDataSource {
    private static final String TAG = NotificationDataSource.class.getName();
    // Database fields
    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private String[] allColumns = {_ID, COLUMN_FENCE_ID, COLUMN_TARGET_ID, COLUMN_TIME, COLUMN_TRANSITION_TYPE};

    public NotificationDataSource(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Notification createNotification(long fenceId, long targetId, long time, int transitionType) {

        long insertId = database.insert(TABLE_NAME, null,
                getCotentValues(fenceId, targetId, time, transitionType));

        return getNotification(insertId);
    }

    public Notification updateNotification(long id, long fenceId, long targetId, long time, int transitionType) {
        database.update(TABLE_NAME, getCotentValues(fenceId, targetId, time, transitionType), _ID + "='" + id
                + "'", null);
        return getNotification(id);
    }

    private ContentValues getCotentValues(long fenceId, long targetId, long time, int transitionType) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FENCE_ID, fenceId);
        values.put(COLUMN_TARGET_ID, fenceId);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_TRANSITION_TYPE, transitionType);
        return values;
    }

    public Notification getNotification(long id) {
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, _ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        Notification Notification = cursorToNotification(cursor);
        cursor.close();
        return Notification;
    }

    public void deleteNotification(Notification Notification) {
        long id = Notification.getId();
        Log.i(TAG, "Notification deleted with id: " + id);
        database.delete(TABLE_NAME, _ID
                + " = " + id, null);
    }

    public List<Notification> getAllNotifications() {
        List<Notification> Notifications = new ArrayList<Notification>();

        Cursor cursor = database.query(TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Notification Notification = cursorToNotification(cursor);
            Notifications.add(Notification);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return Notifications;
    }

    private Notification cursorToNotification(Cursor cursor) {
        Notification Notification = new Notification();
        Notification.setId(cursor.getLong(0));
        Notification.setFenceId(cursor.getLong(1));
        Notification.setTargetId(cursor.getLong(2));
        Notification.setTime(cursor.getLong(3));
        Notification.setTransitionType(cursor.getInt(4));
        return Notification;
    }
}
