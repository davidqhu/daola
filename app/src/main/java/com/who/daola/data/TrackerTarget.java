package com.who.daola.data;

import android.content.Context;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;

import com.who.daola.gcm.GcmHelper;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dave on 9/1/2014.
 * A Target is something that is being tracked.
 */
public class TrackerTarget extends KnowhereData implements Serializable {

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

    @Override
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
        map.put(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_REMOTE_ID, Long.toString(getRemoteId()));
        map.put(TABLE_NAME, getTableName());
        return map;
    }

    @Override
    public KnowhereData fromBundle(Bundle bundle) {
        setId(Long.parseLong(bundle.getString(BaseColumns._ID)));
        setRegId(bundle.getString(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_REG_ID));
        setName(bundle.getString(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_NAME));
        enable(Boolean.parseBoolean(bundle.getString(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_ENABLED)));
        setControlLevel(Integer.parseInt(bundle.getString(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_CONTROL_LEVEL)));
        setRemoteId(bundle.getLong(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_REMOTE_ID));
        setTableName(bundle.getString(TABLE_NAME));
        return this;
    }

    @Override
    public KnowhereData add(Context context) {
        try {
            initializeDataSources(context);

            return mDataSource.createTarget(getName(), getRegId(),getControlLevel(),enabled());
        } finally {
            closeDataSources();
        }
    }

    @Override
    public KnowhereData update(Context context) {
        try {
            initializeDataSources(context);
            return mDataSource.updateTarget(getId(), getRemoteId(), getName(), getRegId(), getControlLevel(), enabled());
        } finally {
            closeDataSources();
        }
    }

    @Override
    public KnowhereData delete(Context context) {
        try {
            initializeDataSources(context);
            mDataSource.deleteTarget(this);
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

    /**
     * Create a tracker that represents the current user itself.
     * This tracker will be use to send a message to the remote target to update its tracker table
     * so that the remote target can be informed that the current user is tracking it.
     * @param target is the remote target the current user is tracking
     * @return the current user as a tracker
     */
    public static TrackerTarget createRemoteTrackerEntry(TrackerTarget target){
        TrackerTarget tracker = new TrackerTarget();
        tracker.setRegId(GcmHelper.REG_ID);
        // TODO set real user name
        tracker.setName("lei");
        tracker.setControlLevel(TrackerTargetContract.CONTROL_LEVEL_SOLE);
        tracker.setRemoteId(target.getId());
        tracker.enable(true);
        tracker.setTableName(TrackerTargetContract.TrackerEntry.TABLE_NAME);
        return tracker;
    }
}
