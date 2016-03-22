package com.networking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.androidnetworking.common.Priority;
import com.androidnetworking.internal.Request;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void makeRequests(View view) {
        for (int i = 0; i < 20; i++) {
            Request request = new Request("www.google.com", Priority.LOW, this);
            MyApplication.getInstance().getRequestManager().addRequest(request);
        }
    }

    public void cancelAllRequests(View view){
        MyApplication.getInstance().getRequestManager().cancelAll(this);
    }

}
