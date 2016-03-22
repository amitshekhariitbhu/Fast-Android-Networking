package com.androidnetworking.runnable;

import com.androidnetworking.common.Priority;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class DataHunter implements Runnable{

    private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();

    Priority priority;
    public final int sequence;

    public DataHunter() {
        this.sequence = SEQUENCE_GENERATOR.incrementAndGet();
    }

    @Override
    public void run() {

    }

    public Priority getPriority() {
        return priority;
    }
}
