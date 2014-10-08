package com.who.daola.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.who.daola.data.FenceContract.FenceEntry.COLUMN_LATITUDE;
import static com.who.daola.data.FenceContract.FenceEntry.COLUMN_LONGITUDE;
import static com.who.daola.data.FenceContract.FenceEntry.COLUMN_NAME;
import static com.who.daola.data.FenceContract.FenceEntry.COLUMN_RADIUS;
import static com.who.daola.data.FenceContract.FenceEntry.TABLE_NAME;
import static com.who.daola.data.FenceContract.FenceEntry._ID;

/**
 * Target Data Source
 * <p/>
 * Created by dave on 9/1/2014.
 */
public class FenceDataSource {

    private static final String TAG = FenceDataSource.class.getName();
    // Database fields
    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private String[] allColumns = {_ID, COLUMN_NAME,
            COLUMN_RADIUS, COLUMN_LATITUDE, COLUMN_LONGITUDE};

    public FenceDataSource(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Fence createFence(String name, double radius, double latitude, double longitude) {

        long insertId = database.insert(TABLE_NAME, null,
                getCotentValues(name, radius, latitude, longitude));

        return getFence(insertId);
    }

    public Fence updateFence(long id, String name, double radius, double latitude, double longitude) {
        database.update(TABLE_NAME, getCotentValues(name, radius, latitude, longitude), _ID + "='" + id
                + "'", null);
        return getFence(id);
    }

    private ContentValues getCotentValues(String name, double radius, double latitude, double longitude) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_RADIUS, radius);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        return values;
    }

    public Fence getFence(long id) {
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, _ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        Fence fence = cursorToFence(cursor);
        cursor.close();
        return fence;
    }

    public void deleteFence(Fence fence) {
        long id = fence.getId();
        Log.i(TAG, "Fence deleted with id: " + id);
        database.delete(TABLE_NAME, _ID
                + " = " + id, null);
    }

    public List<Fence> getAllFences() {
        List<Fence> fences = new ArrayList<Fence>();

        Cursor cursor = database.query(TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Fence fence = cursorToFence(cursor);
            fences.add(fence);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return fences;
    }

    private Fence cursorToFence(Cursor cursor) {
        Fence fence = new Fence();
        fence.setId(cursor.getLong(0));
        fence.setName(cursor.getString(1));
        fence.setRadius(cursor.getFloat(2));
        fence.setLatitude(cursor.getFloat(3));
        fence.setLongitude(cursor.getFloat(4));
        return fence;
    }
}
