package com.who.daola.gcm;

import android.content.Context;
import android.os.Bundle;

import java.util.Map;

/**
 * Created by hud on 4/6/15.
 */
public interface KnowhereData {
    public Map<String, String> toMap();
    public void fromBundle(Bundle bundle);
    public KnowhereData add(Context context);
    public KnowhereData update(Context context);
    public KnowhereData delete(Context context);
}
