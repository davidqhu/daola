package com.who.daola.data;

/**
 * Created by dave on 10/7/2014.
 */
public class Notification {

    private long mId;
    private long mFenceId;
    private long mTargetId;
    private long mTime;
    private int mTransitionType;

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


    @Override
    public String toString() {
        return "fence id: " + getFenceId() + " target id: " + getTargetId() + " transition type: " + getTransitionType();
    }
}