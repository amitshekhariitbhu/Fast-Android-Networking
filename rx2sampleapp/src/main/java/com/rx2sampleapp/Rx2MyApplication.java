/*
 *    Copyright (C) 2016 Amit Shekhar
 *    Copyright (C) 2011 Android Open Source Project
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.rx2sampleapp;

import android.app.Application;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ConnectionQuality;
import com.androidnetworking.interfaces.ConnectionQualityChangeListener;

public class Rx2MyApplication extends Application {

    private static final String TAG = Rx2MyApplication.class.getSimpleName();
    private static Rx2MyApplication appInstance = null;

    public static Rx2MyApplication getInstance() {
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
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