package com.androidnetworking.core;

import android.os.Process;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class DefaultExecutorSupplier implements ExecutorSupplier {

    public static final int DEFAULT_MAX_NUM_THREADS = Runtime.getRuntime().availableProcessors();
    private final AndroidNetworkingExecutor mNetworkExecutor;
    private final AndroidNetworkingExecutor mImmediateNetworkExecutor;
    private final Executor mMainThreadExecutor;

    public DefaultExecutorSupplier() {
        ThreadFactory backgroundPriorityThreadFactory = new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);
        mNetworkExecutor = new AndroidNetworkingExecutor(DEFAULT_MAX_NUM_THREADS, backgroundPriorityThreadFactory);
        mImmediateNetworkExecutor = new AndroidNetworkingExecutor(1, backgroundPriorityThreadFactory);
        mMainThreadExecutor = new MainThreadExecutor();
    }

    @Override
    public AndroidNetworkingExecutor forNetworkTasks() {
        return mNetworkExecutor;
    }

    @Override
    public AndroidNetworkingExecutor forImmediateNetworkTasks() {
        return mImmediateNetworkExecutor;
    }

    @Override
    public Executor forMainThreadTasks() {
        return mMainThreadExecutor;
    }
}
