package com.rxsampleapp;

import android.app.Application;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ConnectionQuality;
import com.androidnetworking.interfaces.ConnectionQualityChangeListener;

public class RxMyApplication extends Application {

    private static final String TAG = RxMyApplication.class.getSimpleName();
    private static RxMyApplication appInstance = null;

    public static RxMyApplication getInstance() {
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
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.enableLogging();
        AndroidNetworking.setConnectionQualityChangeListener(new ConnectionQualityChangeListener() {
            @Override
            public void onChange(ConnectionQuality currentConnectionQuality, int currentBandwidth) {
                Log.d(TAG, "onChange: currentConnectionQuality : " + currentConnectionQuality + " currentBandwidth : " + currentBandwidth);
            }
        });
    }

}