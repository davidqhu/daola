package com.who.daola.data;

import java.io.Serializable;

/**
 * Created by dave on 9/7/2014.
 */
public class Fence implements Serializable {

    private long mId;
    private String mName;
    private float mRadius;
    private double mLatitude;
    private double mLongitude;
    private long mTrackerId;
    private boolean mEnable;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(float longitude) {
        this.mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(float latitude) {
        this.mLatitude = latitude;
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float radius) {
        this.mRadius = radius;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public long getTrackerId() {
        return mTrackerId;
    }

    public void setTrackerId(long trackerId) {
        mTrackerId = trackerId;
    }

    public boolean enabled() {
        return mEnable;
    }

    public void enable(boolean enable){
        mEnable = enable;
    }

    @Override
    public String toString() {
        return getName() + "(" + getRadius() + "m)";
    }
}
