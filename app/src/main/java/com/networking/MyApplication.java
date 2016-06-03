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

package com.networking;

import android.app.Application;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ConnectionQuality;
import com.androidnetworking.interfaces.ConnectionQualityChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by amitshekhar on 22/03/16.
 */
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
        setVariableFromEnv();
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

    private void setVariableFromEnv() {
        try {
            File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(sdcard, "env.txt");
            if (!file.exists()) {
                Log.d(TAG, "Env file is not present in download folder");
                Toast.makeText(getApplicationContext(), "Env file is not present in download folder", Toast.LENGTH_LONG).show();
                return;
            }
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
                try {
                    JSONObject jsonObject = new JSONObject(text.toString());
                    ApiEndPoint.BASE_URL = jsonObject.getString("baseUrl");
                    ApiEndPoint.UPLOAD_IMAGE_URL = jsonObject.getString("uploadImageUrl");
                } catch (JSONException e) {
                    Log.d(TAG, "Check env file json in download folder");
                    Toast.makeText(getApplicationContext(), "Check env file json in download folder", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                Log.d(TAG, "Check env file in download folder");
                Toast.makeText(getApplicationContext(), "Check env file in download folder", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception in loading settingVariableFromEnv");
            Toast.makeText(getApplicationContext(), "Exception in loading settingVariableFromEnv", Toast.LENGTH_LONG).show();
        }
    }

}
