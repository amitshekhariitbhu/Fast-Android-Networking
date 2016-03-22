package com.networking;

import android.app.Application;

import com.androidnetworking.internal.RequestManager;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class MyApplication extends Application {

    public static MyApplication appInstance = null;
    private RequestManager requestManager;

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

    public RequestManager getRequestManager() {
        if (requestManager == null) {
            requestManager = new RequestManager(getApplicationContext());
        }
        return requestManager;
    }
}
