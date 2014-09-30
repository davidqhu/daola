package com.who.daola.data;

import android.provider.BaseColumns;

import com.who.daola.R;

/**
 * Trigger Contract
 * Created by dave on 9/7/2014.
 */
public class TriggerContract {
    private TriggerContract() {}

    public static abstract class TriggerEntry implements BaseColumns {
        public static final String TABLE_NAME = "trigger";
        public static final String COLUMN_TARGET = "target";
        public static final String COLUMN_FENCE = "fence";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_TRANSITION_TYPE = "transition_type";
        public static final String COLUMN_ENABLED = "enabled";
    }

    public static enum TransitionType {
        ENTER, EXIT, BOTH
    }

    public static TransitionType getTransitionTypeFromId(int id){
        if (id == R.id.radioButton_enter) {
            return TransitionType.ENTER;
        } else if (id == R.id.radioButton_exit) {
            return TransitionType.EXIT;
        } else if (id == R.id.radioButton_both) {
            return TransitionType.BOTH;
        }
        throw new IllegalArgumentException("Unknown id: " + id);
    }

    public static TransitionType getTransitionTypeFromIndex(int id){
        if (id>= TransitionType.values().length || id < 0){
            throw new IndexOutOfBoundsException("for index: " + id);
        }
        return TransitionType.values()[id];
    }
}
