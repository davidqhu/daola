package com.who.daola.data;

import android.provider.BaseColumns;

import com.google.android.gms.location.Geofence;
import com.who.daola.R;

/**
 * Trigger Contract
 * Created by dave on 9/7/2014.
 */
public class TriggerContract {
    private TriggerContract() {
    }

    public static abstract class TriggerEntry implements BaseColumns {
        public static final String TABLE_NAME = "trigger";
        public static final String COLUMN_TARGET = "target";
        public static final String COLUMN_FENCE = "fence";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_TRANSITION_TYPE = "transition_type";
        public static final String COLUMN_ENABLED = "enabled";
    }

    public static int getTransition(boolean enter, boolean exit, boolean dwell) {
        int transition = 0;
        if (enter) {
            transition = Geofence.GEOFENCE_TRANSITION_ENTER;
        }
        if (exit) {
            transition = transition | Geofence.GEOFENCE_TRANSITION_EXIT;
        }
        if (dwell) {
            transition = transition | Geofence.GEOFENCE_TRANSITION_DWELL;
        }
        return transition;
    }

    public static String getTransitionName(int transition) {
        switch (transition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "enter";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "exit";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "dwell";
            default:
                return "";

        }
    }
}
