package com.who.daola.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.who.daola.data.TriggerContract.TriggerEntry.COLUMN_DURATION;
import static com.who.daola.data.TriggerContract.TriggerEntry.COLUMN_ENABLED;
import static com.who.daola.data.TriggerContract.TriggerEntry.COLUMN_FENCE;
import static com.who.daola.data.TriggerContract.TriggerEntry.COLUMN_TARGET;
import static com.who.daola.data.TriggerContract.TriggerEntry.COLUMN_TRANSITION_TYPE;
import static com.who.daola.data.TriggerContract.TriggerEntry.TABLE_NAME;

/**
 * Created by dave on 9/14/2014.
 */
public class TriggerDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private String[] allColumns = {COLUMN_TARGET, COLUMN_FENCE, COLUMN_ENABLED, COLUMN_DURATION, COLUMN_TRANSITION_TYPE};

    public TriggerDataSource(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Trigger createTrigger(long target, long fence, boolean enabled, long duration, TriggerContract.TransitionType transitionType) {

        long insertId = database.insert(TABLE_NAME, null,
                getCotentValues(target, fence, (enabled ? 1 : 0), duration, transitionType.ordinal()));

        return getTrigger(target, fence);
    }

    public Trigger updateTrigger(long target, long fence, boolean enabled, long duration, TriggerContract.TransitionType transitionType) {
        database.update(TABLE_NAME, getCotentValues(target, fence, (enabled ? 1 : 0), duration, transitionType.ordinal()), COLUMN_TARGET + "='" + target
                + "' and " + COLUMN_FENCE + "='" + fence + "'", null);
        return getTrigger(target, fence);
    }

    private ContentValues getCotentValues(long target, long fence, int enabled, long duration, int transitionType) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TARGET, target);
        values.put(COLUMN_FENCE, fence);
        values.put(COLUMN_ENABLED, enabled);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_TRANSITION_TYPE, transitionType);
        return values;
    }

    public Trigger getTrigger(long target, long fence) {
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, COLUMN_TARGET + " = '" + target + "' and " + COLUMN_FENCE + "='" + fence + "'", null,
                null, null, null);
        cursor.moveToFirst();
        Trigger people = cursorToTrigger(cursor);
        cursor.close();
        return people;
    }

    public void deleteTrigger(Trigger trigger) {
        long target = trigger.getTarget();
        long fence = trigger.getFence();
        Log.i("TriggerDataSource", String.format("Trigger deleted with target: %s and fence: %s", target, fence));
        database.delete(TABLE_NAME, COLUMN_TARGET + " = '" + target + "' and " + COLUMN_FENCE + "='" + fence + "'", null);
    }

    public List<Trigger> getAllTriggersForTarget(Target target) {
        return getAllTriggersForCondition(COLUMN_TARGET, Long.toString(target.getId()));
    }

    public List<Trigger> getAllTriggersForFence(Fence fence) {
        return getAllTriggersForCondition(COLUMN_FENCE, Long.toString(fence.getId()));
    }

    public List<Trigger> getAllTriggersForCondition(String column, String value) {
        List<Trigger> Triggers = new ArrayList<Trigger>();

        Cursor cursor = database.query(TABLE_NAME,
                allColumns, column + " = '" + value + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Trigger Trigger = cursorToTrigger(cursor);
            Triggers.add(Trigger);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return Triggers;
    }

    private Trigger cursorToTrigger(Cursor cursor) {
        Trigger Trigger = new Trigger();
        Trigger.setTarget(cursor.getInt(0));
        Trigger.setFence(cursor.getInt(1));
        Trigger.setEnabled(cursor.getInt(2) == 0 ? false : true);
        Trigger.setDuration(cursor.getInt(3));
        Trigger.setTransitionType(cursor.getInt(4));
        return Trigger;
    }
}
