package com.who.daola.data;

import java.io.Serializable;

/**
 * Created by dave on 9/7/2014.
 */
public class Fence implements Serializable {

    private long mId;
    private String mName;
    private double mRadius;
    private double mLatitude;
    private double mLongitude;

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

    public double getRadius() {
        return mRadius;
    }

    public void setRadius(long radius) {
        this.mRadius = radius;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    @Override
    public String toString() {
        return getName() + "(" + getRadius() + "m)";
    }
}
