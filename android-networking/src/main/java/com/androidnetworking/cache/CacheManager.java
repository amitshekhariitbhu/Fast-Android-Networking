package com.androidnetworking.cache;

import android.content.Context;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class CacheManager {

    private final Context mContext;
    private static CacheManager sInstance = null;

    public CacheManager(Context mContext) {
        this.mContext = mContext;
    }

    public static void initialize(Context context) {
        sInstance = new CacheManager(context);
    }

    public CacheManager getInstance() {
        return sInstance;
    }

    public void evictAll() {

    }

    public static void shutDown() {
        if (sInstance != null) {
            sInstance = null;
        }
    }
}
