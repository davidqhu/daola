package com.who.daola.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dave on 9/1/2014.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "daola.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String CREATE_TABLE_TRACKERTARGET = "create table %s ("
            + TrackerTargetContract.TargetEntry._ID + " integer primary key autoincrement, "
            + TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_NAME + " text not null,"
            + TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_REG_ID + " text,"
            + TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_CONTROL_LEVEL + " integer,"
            + TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_ENABLED + " numeric);";

    private static final String CREATE_TABLE_FENCE = "create table "
            + FenceContract.FenceEntry.TABLE_NAME + "(" + TrackerTargetContract.TargetEntry._ID
            + " integer primary key autoincrement, "
            + FenceContract.FenceEntry.COLUMN_NAME + " text not null,"
            + FenceContract.FenceEntry.COLUMN_TRACKER_ID + " integer not null references "
            + TrackerTargetContract.TrackerEntry.TABLE_NAME + "(" + TrackerTargetContract.TrackerTargetBaseColumns._ID + "),"
            + FenceContract.FenceEntry.COLUMN_RADIUS + " real,"
            + FenceContract.FenceEntry.COLUMN_LATITUDE + " real,"
            + FenceContract.FenceEntry.COLUMN_LONGITUDE + " real);";

    private static final String CREATE_TABLE_TRIGGER = "create table "
            + TriggerContract.TriggerEntry.TABLE_NAME + "("
            + TriggerContract.TriggerEntry.COLUMN_TARGET + " integer not null references "
            + TrackerTargetContract.TargetEntry.TABLE_NAME + "(" + TrackerTargetContract.TargetEntry._ID + "),"
            + TriggerContract.TriggerEntry.COLUMN_FENCE + " integer not null references "
            + FenceContract.FenceEntry.TABLE_NAME + "(" + FenceContract.FenceEntry._ID + "), "
            + TriggerContract.TriggerEntry.COLUMN_ENABLED + " integer,"
            + TriggerContract.TriggerEntry.COLUMN_DURATION + " integer,"
            + TriggerContract.TriggerEntry.COLUMN_TRANSITION_TYPE + " integer,"
            + "primary key(" + TriggerContract.TriggerEntry.COLUMN_TARGET + "," + TriggerContract.TriggerEntry.COLUMN_FENCE + "));";

    private static final String CREATE_TABLE_NOTIFICATION = "create table "
            + NotificationContract.NotificationEntry.TABLE_NAME + "(" + TrackerTargetContract.TargetEntry._ID
            + " integer primary key autoincrement, "
            + NotificationContract.NotificationEntry.COLUMN_TARGET_ID + " integer not null references "
            + TrackerTargetContract.TargetEntry.TABLE_NAME + "(" + TrackerTargetContract.TargetEntry._ID + "),"
            + NotificationContract.NotificationEntry.COLUMN_FENCE_ID + " integer not null references "
            + FenceContract.FenceEntry.TABLE_NAME + "(" + FenceContract.FenceEntry._ID + "), "
            + NotificationContract.NotificationEntry.COLUMN_TIME + " integer,"
            + NotificationContract.NotificationEntry.COLUMN_TRANSITION_TYPE + " integer);";

    private static final String INSERT_SELF_TRACKERTARGET_ENTRY =
            "insert into %s ( " +
                    TrackerTargetContract.TargetEntry.COLUMN_NAME + ", " +
                    TrackerTargetContract.TargetEntry.COLUMN_CONTROL_LEVEL + ", " +
                    TrackerTargetContract.TargetEntry.COLUMN_ENABLED + ") values('self', " +
                    TrackerTargetContract.CONTROL_LEVEL_SOLE + ", 0)";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(String.format(CREATE_TABLE_TRACKERTARGET,
                TrackerTargetContract.TrackerEntry.TABLE_NAME));
        database.execSQL(String.format(CREATE_TABLE_TRACKERTARGET,
                TrackerTargetContract.TargetEntry.TABLE_NAME));
        database.execSQL(CREATE_TABLE_FENCE);
        database.execSQL(CREATE_TABLE_TRIGGER);
        database.execSQL(CREATE_TABLE_NOTIFICATION);
        database.execSQL(String.format(INSERT_SELF_TRACKERTARGET_ENTRY,
                TrackerTargetContract.TrackerEntry.TABLE_NAME));
        database.execSQL(String.format(INSERT_SELF_TRACKERTARGET_ENTRY,
                TrackerTargetContract.TargetEntry.TABLE_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TriggerContract.TriggerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrackerTargetContract.TargetEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FenceContract.FenceEntry.TABLE_NAME);
        onCreate(db);
    }
}

