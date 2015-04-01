package com.who.daola.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dave on 10/7/2014.
 */
public class Notification {

    private long mId;
    private long mFenceId;
    private long mTargetId;
    private long mTime;
    private int mTransitionType;
    private String mFenceName;
    private String mTargetName;
    DateFormat formatter = new SimpleDateFormat(" HH:mmm:ssz yyyy-MM-dd");


    public long getTargetId() {
        return mTargetId;
    }

    public void setTargetId(long targetId) {
        this.mTargetId = targetId;
    }

    public int getTransitionType() {
        return mTransitionType;
    }

    public void setTransitionType(int transitionType) {
        this.mTransitionType = transitionType;
    }

    public long getFenceId() {
        return mFenceId;
    }

    public void setFenceId(long fenceId) {
        this.mFenceId = fenceId;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        this.mTime = time;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public void setFenceName(String name) {
        this.mFenceName = name;
    }

    public String getFenceName() {
        return mFenceName;
    }

    public void setTargetName(String name) {
        this.mTargetName = name;
    }

    public String getTargetName() {
        return mTargetName;
    }

    @Override
    public String toString() {
        return "fence: " + getFenceName() + " target: " + getTargetName() + " transition: " +
                TriggerContract.getTransitionName(getTransitionType()) + " at: " +
                formatter.format(new Date(getTime()));
    }
}