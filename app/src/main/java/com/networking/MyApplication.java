package com.networking;

import android.app.Application;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;

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
        AndroidNetworking.initialize(getApplicationContext());
        //For testing purpose only: network interceptor : enable it only for non-images request checking
//        Stetho.initializeWithDefaults(getApplicationContext());
//        AndroidNetworkingOkHttp.addNetworkInterceptor(new StethoInterceptor());
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
