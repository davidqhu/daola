package com.who.daola.data;

/**
 * Created by hud on 4/12/15.
 */

import java.io.Serializable;

public class KnowhereMessage implements Serializable {

    private long mId;
    private String mMessageId;
    private String mMessageType;
    private String mPayload;
    private long mTimeStamp;


    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getMessageId() {
        return mMessageId;
    }

    public void setMessageId(String messageId) {
        mMessageId = messageId;
    }

    public String getMessageType() {
        return mMessageType;
    }

    public void setMessageType(String messageType) {
        mMessageType = messageType;
    }

    public String getPayload() {
        return mPayload;
    }

    public void setPayload(String payload) {
        mPayload = payload;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        mTimeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return String.format("id %s, type %s, timestamp %i, payload %s",
                getMessageId(), getMessageType(), getTimeStamp(), getPayload());
    }
}
