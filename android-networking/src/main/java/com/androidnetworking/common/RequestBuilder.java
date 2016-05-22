package com.androidnetworking.common;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Created by amitshekhar on 26/03/16.
 */
public interface RequestBuilder {

    RequestBuilder setPriority(Priority priority);

    RequestBuilder setTag(Object tag);

    RequestBuilder addHeaders(String key, String value);

    RequestBuilder addHeaders(HashMap<String, String> headerMap);

    RequestBuilder addQueryParameter(String key, String value);

    RequestBuilder addQueryParameter(HashMap<String, String> queryParameterMap);

    RequestBuilder addPathParameter(String key, String value);

    RequestBuilder doNotCacheResponse();

    RequestBuilder getResponseOnlyIfCached();

    RequestBuilder getResponseOnlyFromNetwork();

    RequestBuilder setMaxAgeCacheControl(int maxAge, TimeUnit timeUnit);

    RequestBuilder setMaxStaleCacheControl(int maxStale, TimeUnit timeUnit);

    RequestBuilder setExecutor(Executor executor);

}
