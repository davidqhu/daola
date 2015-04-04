package com.who.daola;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.JsonReader;
import android.util.Log;

import com.who.daola.gcm.GcmHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by hud on 4/3/15.
 */
public class NfcHelper {

    public static final String NAME_TAG = "name";
    public static final String ID_TAG = "id";

    public static NdefMessage getRegIdMsg() {
        NdefRecord mimeRecord = NdefRecord.createMime("text/plain",
                getMsgContent().getBytes(Charset.forName("US-ASCII")));
        NdefMessage msg = new NdefMessage(
                new NdefRecord[]{mimeRecord
                        , NdefRecord.createApplicationRecord("com.who.daola")
                });
        return msg;
    }

    public static String getMsgContent() {
        JSONObject object = new JSONObject();
        try {
            object.put(NAME_TAG, "dave");
            object.put(ID_TAG, GcmHelper.REG_ID);
        } catch (JSONException e) {
            Log.e(NfcHelper.class.getName(), "Unable to create message content: " + e.getMessage());
        }
        return object.toString();
    }

    public static String getName(String msg) {
        return getTag(msg, NAME_TAG);
    }

    public static String getID(String msg) {
        return getTag(msg, ID_TAG);
    }

    private static String getTag(String msg, String tag) {
        try {
            Map<String, String> object = (Map) new JSONParser().parse(msg);
            return object.get(tag);
        } catch (ParseException e) {
            Log.e(NfcHelper.class.getName(), "Unable to parse message: " + e.getMessage());
        }
        return "";
    }
}
