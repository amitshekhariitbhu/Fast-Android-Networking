package com.androidnetworking.runnable;

import android.util.Log;

import com.androidnetworking.common.Priority;
import com.androidnetworking.internal.Request;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class DataHunter implements Runnable {

    private static final String TAG = DataHunter.class.getSimpleName();
    private final Priority priority;
    public final int sequence;
    public final Request request;

    public DataHunter(Request request) {
        this.request = request;
        this.sequence = request.getSequenceNumber();
        this.priority = request.getPriority();
    }

    @Override
    public void run() {
        Log.d(TAG, "getUrl : "+request.getUrl());
        Log.d(TAG, "getSequenceNumber : " + request.getSequenceNumber());
    }

    public Priority getPriority() {
        return priority;
    }
}
