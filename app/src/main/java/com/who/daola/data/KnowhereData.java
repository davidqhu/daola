package com.who.daola.data;

import android.content.Context;
import android.os.Bundle;

import java.util.Map;

/**
 * Created by hud on 4/6/15.
 */
public abstract class KnowhereData {

    public static final String TABLE_NAME = "table";
    public abstract long getId();
    public abstract Map<String, String> toMap();
    public abstract KnowhereData fromBundle(Bundle bundle);
    public abstract KnowhereData add(Context context);
    public abstract KnowhereData update(Context context);
    public abstract KnowhereData delete(Context context);

    public static KnowhereData getKnowhereData(Bundle bundle)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String tableName = bundle.getString(KnowhereData.TABLE_NAME);
        String objectClassName = DbHelper.tableToObjectMap.get(tableName);
        if (objectClassName == null) {
            throw new IllegalArgumentException(
                    "Unable to find object class for table name: " + tableName);
        }
        Class c = Class.forName(objectClassName);
        Object o = c.newInstance();
        ((KnowhereData) o).fromBundle(bundle);
        return ((KnowhereData) o);
    }
}
