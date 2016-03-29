package com.androidnetworking;

import android.content.Context;

import com.androidnetworking.internal.AndroidNetworkingImageLoader;
import com.androidnetworking.internal.AndroidNetworkingOkHttp;
import com.androidnetworking.internal.AndroidNetworkingRequestQueue;

/**
 * Created by amitshekhar on 24/03/16.
 */
public class AndroidNetworking {

    public static Context mContext;

    private AndroidNetworking() {
    }

    public static void initialize(Context context) {
        mContext = context.getApplicationContext();
        AndroidNetworkingRequestQueue.initialize();
        AndroidNetworkingImageLoader.initialize();
    }

    public static Context getContext() {
        return mContext;
    }
}
