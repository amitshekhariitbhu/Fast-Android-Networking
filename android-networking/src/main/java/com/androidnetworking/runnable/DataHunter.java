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
        Log.d(TAG, "execution started for sequenceNumber: " + request.getSequenceNumber());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        request.finish();
        Log.d(TAG, "execution done for sequenceNumber: " + request.getSequenceNumber());
    }

    public Priority getPriority() {
        return priority;
    }
}
