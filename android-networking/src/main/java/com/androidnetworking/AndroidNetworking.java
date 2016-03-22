package com.androidnetworking;

import android.content.Context;

import com.androidnetworking.internal.CacheManager;
import com.androidnetworking.internal.Monitor;
import com.androidnetworking.internal.RequestManager;

/**
 * Created by amitshekhar on 21/03/16.
 */
public class AndroidNetworking {

    private AndroidNetworking() {
    }

    public static void initialize(Context context) {
        Monitor.initialize(context);
        CacheManager.initialize(context);
    }
}
