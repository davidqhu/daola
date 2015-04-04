package com.who.daola.data;

import java.io.Serializable;

/**
 * Created by dave on 9/1/2014.
 */
public class Target implements Serializable {
    private long mId;
    private String mName;
    private String mRegId;

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

    @Override
    public String toString() {
        return getName();
    }
}
