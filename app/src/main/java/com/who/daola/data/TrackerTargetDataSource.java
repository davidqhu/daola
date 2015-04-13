package com.who.daola.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.who.daola.data.TrackerTargetContract.TargetEntry.COLUMN_CONTROL_LEVEL;
import static com.who.daola.data.TrackerTargetContract.TargetEntry.COLUMN_ENABLED;
import static com.who.daola.data.TrackerTargetContract.TargetEntry.COLUMN_NAME;
import static com.who.daola.data.TrackerTargetContract.TargetEntry.COLUMN_REG_ID;
import static com.who.daola.data.TrackerTargetContract.TargetEntry._ID;
import static com.who.daola.data.TrackerTargetContract.TargetEntry.COLUMN_REMOTE_ID;

/**
 * Target Data Source
 * <p/>
 * Created by dave on 9/1/2014.
 */
public class TrackerTargetDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private String mTableName;
    private String[] allColumns = {_ID,
            COLUMN_REMOTE_ID, COLUMN_NAME, COLUMN_REG_ID, COLUMN_CONTROL_LEVEL, COLUMN_ENABLED};

    public TrackerTargetDataSource(Context context, String tableName) {
        if (!tableName.equals(TrackerTargetContract.TrackerEntry.TABLE_NAME) &&
                !tableName.equals(TrackerTargetContract.TargetEntry.TABLE_NAME)) {
            throw new IllegalArgumentException(
                    String.format("Table name %s is invalid.", tableName));
        }
        dbHelper = new DbHelper(context);
        mTableName = tableName;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public TrackerTarget createTarget(
            String name, String regId, int controlLevel, boolean enabled) {

        long insertId = database.insert(mTableName, null,
                getCotentValues(-1L, name, regId, controlLevel, enabled));

        return getTarget(insertId);
    }

    public TrackerTarget createTracker(
            long remoteId, String name, String regId, int controlLevel, boolean enabled) {

        long insertId = database.insert(mTableName, null,
                getCotentValues(remoteId, name, regId, controlLevel, enabled));

        return getTarget(insertId);
    }

    public TrackerTarget updateTarget(
            long id, long remoteId, String name, String regId, int controlLevel, boolean enabled) {
        database.update(mTableName,
                getCotentValues(remoteId, name, regId, controlLevel, enabled),
                _ID + "='" + id + "'", null);
        return getTarget(id);
    }

    private ContentValues getCotentValues(
            long remoteId, String name, String regId, int controlLevel, boolean enabled) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        if (regId != null) {
            values.put(COLUMN_REG_ID, regId);
        }
        values.put(COLUMN_REMOTE_ID, remoteId);
        values.put(COLUMN_CONTROL_LEVEL, controlLevel);
        values.put(COLUMN_ENABLED, enabled);
        return values;
    }

    public TrackerTarget getTarget(long id) {
        Cursor cursor = database.query(mTableName,
                allColumns, _ID + " = " + id, null,
                null, null, null);
        try {
            cursor.moveToFirst();
            TrackerTarget people = cursorToTarget(cursor);
            return people;
        } finally {
            cursor.close();
        }
    }

    public void deleteTarget(TrackerTarget target) {
        long id = target.getId();
        Log.i(TrackerTargetDataSource.class.getName(), "target deleted with id: " + id);
        database.delete(mTableName, _ID + " = " + id, null);
    }

    public List<TrackerTarget> getAllTargets() {
        List<TrackerTarget> trackersTargets = new ArrayList<TrackerTarget>();

        Cursor cursor = database.query(mTableName,
                allColumns, null, null, null, null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TrackerTarget target = cursorToTarget(cursor);
                trackersTargets.add(target);
                cursor.moveToNext();
            }
            return trackersTargets;
        } finally {
            cursor.close();
        }
    }

    private TrackerTarget cursorToTarget(Cursor cursor) {
        TrackerTarget trackerTarget = new TrackerTarget();
        trackerTarget.setId(cursor.getLong(0));
        trackerTarget.setRemoteId(cursor.getLong(1));
        trackerTarget.setName(cursor.getString(2));
        trackerTarget.setRegId(cursor.getString(3));
        trackerTarget.setControlLevel(cursor.getInt(4));
        trackerTarget.enable(Boolean.parseBoolean(Integer.toString(cursor.getInt(5))));
        return trackerTarget;
    }
}
