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
    private static final String CREATE_TABLE_TARGET = "create table "
            + TargetContract.TargetEntry.TABLE_NAME + "(" + TargetContract.TargetEntry._ID
            + " integer primary key autoincrement, "
            + TargetContract.TargetEntry.COLUMN_FIRSTNAME + " text,"
            + TargetContract.TargetEntry.COLUMN_LASTNAME + " text,"
            + TargetContract.TargetEntry.COLUMN_NICKNAME + " text not null);";

    private static final String CREATE_TABLE_FENCE = "create table "
            + FenceContract.FenceEntry.TABLE_NAME + "(" + TargetContract.TargetEntry._ID
            + " integer primary key autoincrement, "
            + FenceContract.FenceEntry.COLUMN_NAME + " text not null,"
            + FenceContract.FenceEntry.COLUMN_RADIUS + " real,"
            + FenceContract.FenceEntry.COLUMN_LATITUDE + " real,"
            + FenceContract.FenceEntry.COLUMN_LONGITUDE + " real);";

    private static final String CREATE_TABLE_TRIGGER = "create table "
            + TriggerContract.TriggerEntry.TABLE_NAME + "("
            + TriggerContract.TriggerEntry.COLUMN_TARGET + " integer not null references " + TargetContract.TargetEntry.TABLE_NAME + "(" + TargetContract.TargetEntry._ID + "),"
            + TriggerContract.TriggerEntry.COLUMN_FENCE + " integer not null references " + FenceContract.FenceEntry.TABLE_NAME + "(" + FenceContract.FenceEntry._ID + "), "
            + TriggerContract.TriggerEntry.COLUMN_ENABLED + " integer,"
            + TriggerContract.TriggerEntry.COLUMN_DURATION + " integer,"
            + TriggerContract.TriggerEntry.COLUMN_TRANSITION_TYPE + " integer,"
            + "primary key(" + TriggerContract.TriggerEntry.COLUMN_TARGET + "," + TriggerContract.TriggerEntry.COLUMN_FENCE + "));";

    private static final String CREATE_TABLE_NOTIFICATION = "create table "
            + NotificationContract.NotificationEntry.TABLE_NAME + "(" + TargetContract.TargetEntry._ID
            + " integer primary key autoincrement, "
            + NotificationContract.NotificationEntry.COLUMN_TARGET_ID + " integer not null references " + TargetContract.TargetEntry.TABLE_NAME + "(" + TargetContract.TargetEntry._ID + "),"
            + NotificationContract.NotificationEntry.COLUMN_FENCE_ID + " integer not null references " + FenceContract.FenceEntry.TABLE_NAME + "(" + FenceContract.FenceEntry._ID + "), "
            + NotificationContract.NotificationEntry.COLUMN_TIME + " integer,"
            + NotificationContract.NotificationEntry.COLUMN_TRANSITION_TYPE + " integer);";

    private static final String INSERT_SELF_TARGET_ENTRY = "insert into " + TargetContract.TargetEntry.TABLE_NAME
            + " ( " + TargetContract.TargetEntry.COLUMN_NICKNAME + " ) values('self')";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_TARGET);
        database.execSQL(CREATE_TABLE_FENCE);
        database.execSQL(CREATE_TABLE_TRIGGER);
        database.execSQL(CREATE_TABLE_NOTIFICATION);
        database.execSQL(INSERT_SELF_TARGET_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TriggerContract.TriggerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TargetContract.TargetEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FenceContract.FenceEntry.TABLE_NAME);
        onCreate(db);
    }
}

