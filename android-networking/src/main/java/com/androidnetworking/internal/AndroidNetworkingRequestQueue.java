package com.androidnetworking.internal;

import android.content.Context;

import com.androidnetworking.cache.CacheManager;
import com.androidnetworking.core.Core;
import com.androidnetworking.requests.AndroidNetworkingRequest;
import com.androidnetworking.runnables.DataHunter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class AndroidNetworkingRequestQueue {

    private final static String TAG = AndroidNetworkingRequestQueue.class.getSimpleName();
    private final Set<AndroidNetworkingRequest<?>> mCurrentRequests = new HashSet<AndroidNetworkingRequest<?>>();
    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    public AndroidNetworkingRequestQueue(Context context) {
        CacheManager.initialize(context);
    }

    public interface RequestFilter {
        public boolean apply(AndroidNetworkingRequest<?> request);
    }


    public void cancelAll(RequestFilter filter) {
        synchronized (mCurrentRequests) {
            for (AndroidNetworkingRequest<?> request : mCurrentRequests) {
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
            public boolean apply(AndroidNetworkingRequest<?> request) {
                return request.getTag() == tag;
            }
        });
    }

    public int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }

    public <T> AndroidNetworkingRequest<T> addRequest(AndroidNetworkingRequest<T> request) {
        synchronized (mCurrentRequests) {
            mCurrentRequests.add(request);
        }
        request.setRequestQueue(this);
        request.setSequenceNumber(getSequenceNumber());
        request.setFuture(Core.getInstance().getExecutorSupplier().forNetworkTasks().submit(new DataHunter(request)));
        return request;
    }

    public <T> void finish(AndroidNetworkingRequest<T> request) {
        synchronized (mCurrentRequests) {
            mCurrentRequests.remove(request);
        }
    }
}
