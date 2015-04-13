package com.who.daola.data;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.who.daola.gcm.KnowhereData;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dave on 9/1/2014.
 * A Target is something that is being tracked.
 */
public class TrackerTarget implements Serializable, KnowhereData {

    public static final String TAG = TrackerTarget.class.getName();
    private TrackerTargetDataSource mDataSource;
    private long mId;
    private long mRemoteId;
    private String mName;
    private String mRegId;
    private boolean mEnabled;
    private int mControlLevel;
    private String mTableName;

    public String getRegId() {
        return mRegId;
    }

    public void setRegId(String regId) {
        this.mRegId = regId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public void setRemoteId(long remoteId) {
        this.mRemoteId = remoteId;
    }

    public long getRemoteId() {
        return mRemoteId;
    }

    public boolean enabled() {
        return mEnabled;
    }

    public void enable(boolean enabled) {
        mEnabled = enabled;
    }

    public int getControlLevel() {
        return mControlLevel;
    }

    public void setControlLevel(int controlLevel) {
        mControlLevel = controlLevel;
    }

    public void setTableName(String tableName) {
        mTableName = tableName;
    }

    public String getTableName() {
        return mTableName;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_REG_ID, mRegId);
        map.put(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_NAME, mName);
        map.put(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_ENABLED, Boolean.toString(mEnabled));
        map.put(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_CONTROL_LEVEL, Integer.toString(mControlLevel));
        return map;
    }

    @Override
    public void fromBundle(Bundle bundle) {
        setRegId(bundle.getString(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_REG_ID));
        setName(bundle.getString(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_NAME));
        enable(Boolean.parseBoolean(bundle.getString(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_ENABLED)));
        setControlLevel(Integer.parseInt(bundle.getString(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_CONTROL_LEVEL)));
    }

    @Override
    public KnowhereData add(Context context) {
        try {
            initializeDataSources(context);

            mDataSource.createTarget(getName(), getRegId(),getControlLevel(),enabled());
        } finally {
            closeDataSources();
        }
        return null;
    }

    @Override
    public KnowhereData update(Context context) {
        try {
            initializeDataSources(context);
        } finally {
            closeDataSources();
        }
        return null;
    }

    @Override
    public KnowhereData delete(Context context) {
        try {
            initializeDataSources(context);

        } finally {
            closeDataSources();
        }
        return null;
    }

    private void initializeDataSources(Context context) {
        if (mDataSource == null) {
            mDataSource = new TrackerTargetDataSource(context, mTableName);
        }
        try {
            mDataSource.open();
        } catch (SQLException e) {
            Log.e(TAG, "Error opening database: " + e);
            closeDataSources();
        }
    }

    private void closeDataSources() {
        if (mDataSource != null) {
            mDataSource.close();
        }
    }
}
