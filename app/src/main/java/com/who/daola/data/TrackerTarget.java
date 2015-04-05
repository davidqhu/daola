package com.who.daola.data;

import java.io.Serializable;

/**
 * Created by dave on 9/1/2014.
 * A Target is something that is being tracked.
 */
public class TrackerTarget implements Serializable {
    private long mId;
    private String mName;
    private String mRegId;
    private boolean mDisabled;
    private int mControlLevel;

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

    public void setId(long mId) {
        this.mId = mId;
    }

    public boolean isDisabled() {
        return mDisabled;
    }

    public void disable(boolean disabled) {
        mDisabled = disabled;
    }

    public int getControlLevel() {
        return mControlLevel;
    }

    public void setControlLevel(int controlLevel) {
        mControlLevel = controlLevel;
    }

    @Override
    public String toString() {
        return getName();
    }
}
