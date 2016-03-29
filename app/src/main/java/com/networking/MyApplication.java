package com.networking;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class MyApplication extends Application {

    private static MyApplication appInstance = null;

    public static MyApplication getInstance() {
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        AndroidNetworking.initialize(getApplicationContext());
        //For testing purpose only: network interceptor : enable it only for non-images request checking
//        Stetho.initializeWithDefaults(getApplicationContext());
//        AndroidNetworkingOkHttp.addNetworkInterceptor(new StethoInterceptor());
    }
}
