package com.androidnetworking.internal;

import android.content.Context;

import com.androidnetworking.cache.CacheManager;
import com.androidnetworking.core.Core;
import com.androidnetworking.runnable.DataHunter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class AndroidNetworking {

    private final static String TAG = AndroidNetworking.class.getSimpleName();
    private final Set<Request> mCurrentRequests = new HashSet<Request>();
    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    public AndroidNetworking(Context context) {
        CacheManager.initialize(context);
    }

    public interface RequestFilter {
        public boolean apply(Request request);
    }

    public void cancelAll(RequestFilter filter) {
        synchronized (mCurrentRequests) {
            for (Request request : mCurrentRequests) {
                if (filter.apply(request)) {
                    request.cancel();
                }
            }
        }
    }

    public void cancelAll(final Object tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Cannot cancelAll with a null tag");
        }
        cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request request) {
                return request.getTag() == tag;
            }
        });
    }

    public int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }

    public void addRequest(Request request) {
        synchronized (mCurrentRequests) {
            mCurrentRequests.add(request);
        }
        request.setRequestQueue(this);
        request.setSequenceNumber(getSequenceNumber());
        request.setFuture(Core.getInstance().getExecutorSupplier().forNetworkTasks().submit(new DataHunter(request)));
    }

    public void finish(Request request) {
        synchronized (mCurrentRequests) {
            mCurrentRequests.remove(request);
        }
    }
}
