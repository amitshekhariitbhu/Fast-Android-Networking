package com.networking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.androidnetworking.common.AndroidNetworkingResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.AndroidNetworkingError;
import com.androidnetworking.requests.AndroidNetworkingArrayRequest;
import com.androidnetworking.requests.AndroidNetworkingObjectRequest;
import com.androidnetworking.requests.AndroidNetworkingRequest;
import com.androidnetworking.requests.AndroidNetworkingStringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String URL_JSON_ARRAY = "http://api.androidhive.info/volley/person_array.json";
    private static final String URL_JSON_OBJECT = "http://api.androidhive.info/volley/person_object.json";
    private static final String URL_STRING = "http://api.androidhive.info/volley/string_response.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void makeRequests(View view) {
        for (int i = 0; i < 10; i++) {
            AndroidNetworkingArrayRequest androidNetworkingArrayRequest = new AndroidNetworkingArrayRequest(AndroidNetworkingRequest.Method.GET, URL_JSON_ARRAY, Priority.LOW, this, new AndroidNetworkingResponse.SuccessListener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d(TAG, "onResponse array : " + response.toString());
                }
            }, new AndroidNetworkingResponse.ErrorListener() {
                @Override
                public void onError(AndroidNetworkingError error) {
                    Log.d(TAG, "onError : " + error.toString());
                }
            });
            MyApplication.getInstance().getAndroidNetworkingRequestQueue().addRequest(androidNetworkingArrayRequest);

            AndroidNetworkingObjectRequest androidNetworkingObjectRequest = new AndroidNetworkingObjectRequest(AndroidNetworkingRequest.Method.GET, URL_JSON_OBJECT, Priority.LOW, this, new AndroidNetworkingResponse.SuccessListener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "onResponse object : " + response.toString());
                }
            }, new AndroidNetworkingResponse.ErrorListener() {
                @Override
                public void onError(AndroidNetworkingError error) {
                    Log.d(TAG, "onError : " + error.toString());
                }
            });
            MyApplication.getInstance().getAndroidNetworkingRequestQueue().addRequest(androidNetworkingObjectRequest);

//            AndroidNetworkingStringRequest androidNetworkingStringRequest = new AndroidNetworkingStringRequest(AndroidNetworkingRequest.Method.GET, URL_STRING, Priority.LOW, this, new AndroidNetworkingResponse.SuccessListener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.d(TAG, "onResponse string : " + response);
//                }
//            }, new AndroidNetworkingResponse.ErrorListener() {
//                @Override
//                public void onError(AndroidNetworkingError error) {
//                    Log.d(TAG, "onError : " + error.toString());
//                }
//            });
//            MyApplication.getInstance().getAndroidNetworkingRequestQueue().addRequest(androidNetworkingStringRequest);
        }
    }

    public void cancelAllRequests(View view) {
        MyApplication.getInstance().getAndroidNetworkingRequestQueue().cancelAll(this);
    }

}
