package com.networking;

import android.app.Application;

import com.androidnetworking.internal.AndroidNetworkingImageLoader;
import com.androidnetworking.internal.AndroidNetworkingRequestQueue;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class MyApplication extends Application {

    public static MyApplication appInstance = null;
    private AndroidNetworkingRequestQueue androidNetworkingRequestQueue;
    private AndroidNetworkingImageLoader androidNetworkingImageLoader;

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

    public AndroidNetworkingRequestQueue getAndroidNetworkingRequestQueue() {
        if (androidNetworkingRequestQueue == null) {
            androidNetworkingRequestQueue = new AndroidNetworkingRequestQueue(getApplicationContext());
        }
        return androidNetworkingRequestQueue;
    }

    public AndroidNetworkingImageLoader getImageLoader() {
        getAndroidNetworkingRequestQueue();
        if (androidNetworkingImageLoader == null) {
            androidNetworkingImageLoader = new AndroidNetworkingImageLoader(this.androidNetworkingRequestQueue,
                    new LruBitmapCache());
        }
        return this.androidNetworkingImageLoader;
    }
}
