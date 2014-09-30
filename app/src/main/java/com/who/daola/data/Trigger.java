package com.who.daola.data;

/**
 * Created by dave on 9/14/2014.
 */
public class Trigger {
    private long mTarget;
    private long mFence;
    private boolean mEnabled;
    private long mDuration;
    private int mTransitionType;

    public long getTarget() {
        return mTarget;
    }

    public void setTarget(long target) {
        this.mTarget = target;
    }

    public long getFence() {
        return mFence;
    }

    public void setFence(long fence) {
        this.mFence = fence;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public int getTransitionType() {
        return mTransitionType;
    }

    public void setTransitionType(int condition) {
        this.mTransitionType = condition;
    }

}
