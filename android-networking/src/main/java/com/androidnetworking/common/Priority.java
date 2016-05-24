package com.androidnetworking.common;

/**
 * Created by amitshekhar on 22/03/16.
 */


import android.support.annotation.Nullable;

/**
 * Priority levels recognized by the request server.
 */
public enum Priority {
    /**
     * NOTE: DO NOT CHANGE ORDERING OF THOSE CONSTANTS UNDER ANY CIRCUMSTANCES.
     * Doing so will make ordering incorrect.
     */

    /**
     * Lowest priority level. Used for prefetches of data.
     */
    LOW,

    /**
     * Medium priority level. Used for warming of data that might soon get visible.
     */
    MEDIUM,

    /**
     * Highest priority level. Used for data that are currently visible on screen.
     */
    HIGH,

    /**
     * Highest priority level. Used for data that are required instantly(mainly for emergency).
     */
    IMMEDIATE;


    /**
     * Gets the higher priority among the two.
     *
     * @param priority1 The priority
     * @param priority2 The priority
     * @return higher priority
     */
    public static Priority getHigherPriority(
            @Nullable Priority priority1,
            @Nullable Priority priority2) {
        if (priority1 == null) {
            return priority2;
        }
        if (priority2 == null) {
            return priority1;
        }
        if (priority1.ordinal() > priority2.ordinal()) {
            return priority1;
        } else {
            return priority2;
        }
    }
}
