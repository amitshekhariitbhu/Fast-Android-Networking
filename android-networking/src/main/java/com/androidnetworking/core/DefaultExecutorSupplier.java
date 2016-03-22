package com.androidnetworking.core;

import android.os.Process;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class DefaultExecutorSupplier implements ExecutorSupplier {

    public static final int DEFAULT_MAX_NUM_THREADS = Runtime.getRuntime().availableProcessors();
    private final Executor mBackgroundExecutor;
    private final AndroidNetworkingExecutor mNetworkExecutor;
    private final Executor mMainThreadExecutor;

    public DefaultExecutorSupplier() {
        ThreadFactory backgroundPriorityThreadFactory = new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);
        mBackgroundExecutor = Executors.newFixedThreadPool(DEFAULT_MAX_NUM_THREADS, backgroundPriorityThreadFactory);
        mNetworkExecutor = new AndroidNetworkingExecutor(DEFAULT_MAX_NUM_THREADS,backgroundPriorityThreadFactory);
        mMainThreadExecutor = new MainThreadExecutor();
    }

    @Override
    public AndroidNetworkingExecutor forNetworkTasks() {
        return mNetworkExecutor;
    }

    @Override
    public Executor forBackgroundTasks() {
        return mBackgroundExecutor;
    }

    @Override
    public Executor forMainThreadTasks() {
        return mMainThreadExecutor;
    }
}
