package com.who.daola.data;

/**
 * Created by hud on 4/12/15.
 */

import android.os.Bundle;
import android.util.Log;

import com.who.daola.gcm.server.Message;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KnowhereMessage implements Serializable {

    public static final String TAG = KnowhereMessage.class.getName();
    public static final String TYPE = "type";
    public static final String MESSAGE_ID = "message_id";

    private long mId;
    private String mMessageId;
    private Type mMessageType;
    private String mPayload;
    private long mTimeStamp;

    public enum Type {
        Notification, Add, Update, Delete, Ack;

        public static Type fromString(String type) {
            if (type != null) {
                for (Type t : Type.values()) {
                    if (type.equalsIgnoreCase(t.toString())) {
                        return t;
                    }
                }
            }
            return null;
        }
    }


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

    public Type getMessageType() {
        return mMessageType;
    }

    public void setMessageType(String messageType) {
        mMessageType = Type.fromString(messageType);
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

    public static String getPayloadStringFromMap(Map<String, String> map) {
        JSONObject j = new JSONObject(map);
        return j.toString();
    }

    public static String getPayloadStringFromBundle(Bundle bundle){
        JSONObject json = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                // json.put(key, bundle.get(key)); see edit below
                json.put(key, JSONObject.wrap(bundle.get(key)));
            } catch(JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }
        return json.toString();
    }

    public static Map<String, String> getMapFromPayload(String payload) throws ParseException {
        JSONParser parser = new JSONParser();
        ContainerFactory containerFactory = new ContainerFactory() {
            public List creatArrayContainer() {
                return new LinkedList();
            }

            public Map<String, String> createObjectContainer() {
                return new HashMap<String, String>();
            }
        };
        return (Map<String, String>) parser.parse(payload, containerFactory);
    }

    public static Bundle getBundleFromPayload(String payload) throws ParseException {
        Map<String, String> map = getMapFromPayload(payload);
        Bundle bundle = new Bundle();
        for (String key: map.keySet()){
                bundle.putString(key, map.get(key));
        }
        return bundle;
    }

    public Message toMessage() {
        Message.Builder builder = new Message.Builder();
        builder.timeToLive(0);
        try {
            builder.setData(getMapFromPayload(getPayload()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        builder.addData(TYPE, getMessageType().toString());
        if (!getMessageType().equals(Type.Ack)) {
            builder.addData(MESSAGE_ID, getMessageId());
        }
        builder.delayWhileIdle(true);
        Message msg = builder.build();
        return msg;
    }
}
