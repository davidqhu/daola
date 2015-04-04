package com.who.daola.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.who.daola.data.TargetContract.TargetEntry.COLUMN_NAME;
import static com.who.daola.data.TargetContract.TargetEntry.COLUMN_REG_ID;
import static com.who.daola.data.TargetContract.TargetEntry.TABLE_NAME;
import static com.who.daola.data.TargetContract.TargetEntry._ID;

/**
 * Target Data Source
 * <p/>
 * Created by dave on 9/1/2014.
 */
public class TargetDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private String[] allColumns = {_ID, COLUMN_NAME, COLUMN_REG_ID};

    public TargetDataSource(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Target createTarget(String name, String regId) {

        long insertId = database.insert(TABLE_NAME, null,
                getCotentValues(name, regId));

        return getTarget(insertId);
    }

    public Target updateTarget(long id, String name, String regId) {
        database.update(TABLE_NAME, getCotentValues(name, regId), _ID + "='" + id
                + "'", null);
        return getTarget(id);
    }

    private ContentValues getCotentValues(String name, String regId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        if (regId != null) {
            values.put(COLUMN_REG_ID, regId);
        }
        return values;
    }

    public Target getTarget(long id) {
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, _ID + " = " + id, null,
                null, null, null);
        try {
            cursor.moveToFirst();
            Target people = cursorToTarget(cursor);
            return people;
        } finally {
            cursor.close();
        }
    }

    public void deleteTarget(Target target) {
        long id = target.getId();
        Log.i(TargetDataSource.class.getName(), "target deleted with id: " + id);
        database.delete(TABLE_NAME, _ID + " = " + id, null);
    }

    public List<Target> getAllTargets() {
        List<Target> targets = new ArrayList<Target>();

        Cursor cursor = database.query(TABLE_NAME,
                allColumns, null, null, null, null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Target target = cursorToTarget(cursor);
                targets.add(target);
                cursor.moveToNext();
            }
            return targets;
        } finally {
            cursor.close();
        }
    }

    private Target cursorToTarget(Cursor cursor) {
        Target target = new Target();
        target.setId(cursor.getLong(0));
        target.setName(cursor.getString(1));
        target.setRegId(cursor.getString(2));
        return target;
    }
}
