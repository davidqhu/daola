package com.who.daola.data;

import java.io.Serializable;

/**
 * Created by dave on 9/1/2014.
 */
public class Target implements Serializable {
    private long mId;
    private String mFirstName;
    private String mLastName;
    private String mNikeName;

    public String getNikeName() {
        return mNikeName;
    }

    public void setNikeName(String nickname) {
        this.mNikeName = nickname;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    @Override
    public String toString() {
        if (getNikeName() != null) {
            return getNikeName();
        }
        return getFirstName();
    }
}
