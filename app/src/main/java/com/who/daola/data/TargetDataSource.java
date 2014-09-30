package com.who.daola.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.who.daola.data.TargetContract.TargetEntry.COLUMN_FIRSTNAME;
import static com.who.daola.data.TargetContract.TargetEntry._ID;
import static com.who.daola.data.TargetContract.TargetEntry.COLUMN_LASTNAME;
import static com.who.daola.data.TargetContract.TargetEntry.COLUMN_NICKNAME;
import static com.who.daola.data.TargetContract.TargetEntry.TABLE_NAME;

/**
 * Target Data Source
 * <p/>
 * Created by dave on 9/1/2014.
 */
public class TargetDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private String[] allColumns = {_ID,
            COLUMN_FIRSTNAME, COLUMN_LASTNAME, COLUMN_NICKNAME};

    public TargetDataSource(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Target createTarget(String firstName, String lastName, String nickName) {

        long insertId = database.insert(TABLE_NAME, null,
                getCotentValues(firstName, lastName, nickName));

        return getTarget(insertId);
    }

    public Target updateTarget(long id, String firstName, String lastName, String nickName) {
        database.update(TABLE_NAME, getCotentValues(firstName, lastName, nickName), _ID + "='" + id
                + "'", null);
        return getTarget(id);
    }

    private ContentValues getCotentValues(String firstName, String lastName, String nickName) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRSTNAME, firstName);
        if (lastName != null) {
            values.put(COLUMN_LASTNAME, lastName);
        }
        if (nickName != null) {
            values.put(COLUMN_NICKNAME, nickName);
        }
        return values;
    }

    public Target getTarget(long id) {
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, _ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        Target people = cursorToTarget(cursor);
        cursor.close();
        return people;
    }

    public void deletePeople(Target People) {
        long id = People.getId();
        Log.i("PeopleDataSource", "People deleted with id: " + id);
        database.delete(TABLE_NAME, _ID
                + " = " + id, null);
    }

    public List<Target> getAllTargets() {
        List<Target> targets = new ArrayList<Target>();

        Cursor cursor = database.query(TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Target target = cursorToTarget(cursor);
            targets.add(target);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return targets;
    }

    private Target cursorToTarget(Cursor cursor) {
        Target target = new Target();
        target.setId(cursor.getLong(0));
        target.setFirstName(cursor.getString(1));
        target.setLastName(cursor.getString(2));
        target.setNikeName(cursor.getString(3));
        return target;
    }
}
