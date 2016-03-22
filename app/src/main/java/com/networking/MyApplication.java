package com.networking;

import android.app.Application;

import com.androidnetworking.internal.AndroidNetworking;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class MyApplication extends Application {

    public static MyApplication appInstance = null;
    private AndroidNetworking androidNetworking;

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
    }

    public AndroidNetworking getAndroidNetworking() {
        if (androidNetworking == null) {
            androidNetworking = new AndroidNetworking(getApplicationContext());
        }
        return androidNetworking;
    }
}
