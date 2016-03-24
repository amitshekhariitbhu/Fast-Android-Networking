package com.androidnetworking.core;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class Core {

    private static Core sInstance = null;
    private final ExecutorSupplier mExecutorSupplier;

    private Core() {
        this.mExecutorSupplier = new DefaultExecutorSupplier();
    }

    public static Core getInstance() {
        if(sInstance == null){
            sInstance = new Core();
        }
        return sInstance;
    }

    public ExecutorSupplier getExecutorSupplier() {
        return mExecutorSupplier;
    }

    public static void shutDown() {
        if (sInstance != null) {
            sInstance = null;
        }
    }
}
