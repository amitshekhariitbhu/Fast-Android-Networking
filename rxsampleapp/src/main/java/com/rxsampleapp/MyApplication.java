package com.rxsampleapp;

import android.app.Application;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ConnectionQuality;
import com.androidnetworking.interfaces.ConnectionQualityChangeListener;
import com.rxandroidnetworking.RxAndroidNetworking;

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();
    private static MyApplication appInstance = null;

    public static MyApplication getInstance() {
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        //For testing purpose only: network interceptor : enable it only for non-images request checking
//        Stetho.initializeWithDefaults(getApplicationContext());
//        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addNetworkInterceptor(new StethoInterceptor()).addInterceptor(new GzipRequestInterceptor()).build();
//        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
//                .addInterceptor(new GzipRequestInterceptor())
//                .build();
//        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);
        RxAndroidNetworking.initialize(getApplicationContext());
    }

}