package com.who.daola.gcm;

import android.os.Bundle;

import com.who.daola.data.DbHelper;
import com.who.daola.data.Fence;
import com.who.daola.data.TrackerTarget;
import com.who.daola.gcm.server.Message;

import java.util.Map;

/**
 * Created by hud on 4/5/15.
 */
public class KnowhereDataMessage<T extends KnowhereData> {

    public static final String TYPE = "type";
    public static final String TABLE_NAME = "table";
    private KnowhereMessageType mType;
    private Map<String, String> mPayload;
    private String mTableName;
    private T mData;
    private String messageId;

    public enum KnowhereMessageType {
        Notification, Add, Update, Delete, Ack
    }

    public KnowhereDataMessage(KnowhereMessageType type, String tableName, T data){
        mType = type;
        mData = data;
        mTableName= tableName;
    }

    public static KnowhereDataMessage getKnowhereMessage(Bundle bundle) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        KnowhereMessageType type = KnowhereMessageType.valueOf(bundle.getString(TYPE));
        String tableName = bundle.getString(TABLE_NAME);
        System.out.println("table name: " + tableName);
        Class c = Class.forName(DbHelper.tableToObjectMap.get(tableName));
        Object o = c.newInstance();
        ((KnowhereData) o).fromBundle(bundle);
        if (o instanceof TrackerTarget){
            ((TrackerTarget) o).setTableName(tableName);
            return new KnowhereDataMessage(type, tableName, (TrackerTarget) o);
        } else if (o instanceof Fence) {
            //return new KnowhereMessage(type, tableName, (Fence) o);
        }
        return null;
    }

    public Message toMessage(){
        Message.Builder builder = new Message.Builder();
        builder.timeToLive(0);
        mPayload = mData.toMap();
        mPayload.put(TYPE, mType.toString());
        mPayload.put(TABLE_NAME, mTableName);
        builder.setData(mPayload);
        builder.delayWhileIdle(true);
        Message msg = builder.build();
        return msg;
    }

    public KnowhereMessageType getType(){
        return mType;
    }

    public T getData(){
        return mData;
    }
}
