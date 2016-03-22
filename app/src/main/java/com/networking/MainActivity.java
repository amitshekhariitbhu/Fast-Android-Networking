package com.networking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androidnetworking.common.Priority;
import com.androidnetworking.internal.Request;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Request requestOne = new Request("www.google.com", Priority.LOW, this);
        MyApplication.getInstance().getRequestManager().addRequest(requestOne);

        Request requestTwo = new Request("www.facebook.com", Priority.HIGH, this);
        MyApplication.getInstance().getRequestManager().addRequest(requestTwo);
    }
}
