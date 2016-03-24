package com.networking;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class MyApplication extends Application {

    private static MyApplication appInstance = null;

    public static MyApplication getInstance() {
        if (appInstance == null) {
            appInstance = new MyApplication();
        }
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        AndroidNetworking.initialize(getApplicationContext());
    }
}
