package com.androidnetworking.internal;

import android.content.Context;

import com.androidnetworking.core.DefaultExecutorSupplier;
import com.androidnetworking.core.ExecutorSupplier;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class Monitor {

    private final Context mContext;
    private static Monitor sInstance = null;
    private final ExecutorSupplier mExecutorSupplier;

    public Monitor(Context mContext) {
        this.mContext = mContext;
        this.mExecutorSupplier = new DefaultExecutorSupplier();
    }

    public static void initialize(Context context) {
        sInstance = new Monitor(context);
    }

    public static Monitor getInstance() {
        return sInstance;
    }

    public ExecutorSupplier getExecutorSupplier() {
        return mExecutorSupplier;
    }

    public static void shutDown() {
        if (sInstance != null) {
            sInstance = null;
        }
    }
}
