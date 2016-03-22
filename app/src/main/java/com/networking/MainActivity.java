package com.networking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.androidnetworking.common.AndroidNetworkingResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.AndroidNetworkingError;
import com.androidnetworking.requests.AndroidNetworkingArrayRequest;
import com.androidnetworking.requests.AndroidNetworkingRequest;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void makeRequests(View view) {
        for (int i = 0; i < 20; i++) {
            AndroidNetworkingArrayRequest androidNetworkingArrayRequest = new AndroidNetworkingArrayRequest(AndroidNetworkingRequest.Method.GET, "www.google.com", Priority.LOW, this, new AndroidNetworkingResponse.SuccessListener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                }
            }, new AndroidNetworkingResponse.ErrorListener() {
                @Override
                public void onError(AndroidNetworkingError error) {

                }
            });
            MyApplication.getInstance().getAndroidNetworkingRequestQueue().addRequest(androidNetworkingArrayRequest);
        }
    }

    public void cancelAllRequests(View view) {
        MyApplication.getInstance().getAndroidNetworkingRequestQueue().cancelAll(this);
    }

}
