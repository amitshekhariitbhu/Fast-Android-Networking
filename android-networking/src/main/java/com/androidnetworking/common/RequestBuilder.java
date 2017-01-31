/*
 *    Copyright (C) 2016 Amit Shekhar
 *    Copyright (C) 2011 Android Open Source Project
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidnetworking.common;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by amitshekhar on 26/03/16.
 */
public interface RequestBuilder {

    RequestBuilder setPriority(Priority priority);

    RequestBuilder setTag(Object tag);

    RequestBuilder addHeaders(String key, String value);

    RequestBuilder addHeaders(Map<String, String> headerMap);

    RequestBuilder addHeaders(Object object);

    RequestBuilder addQueryParameter(String key, String value);

    RequestBuilder addQueryParameter(Map<String, String> queryParameterMap);

    RequestBuilder addQueryParameter(Object object);

    RequestBuilder addPathParameter(String key, String value);

    RequestBuilder addPathParameter(Map<String, String> pathParameterMap);

    RequestBuilder addPathParameter(Object object);

    RequestBuilder doNotCacheResponse();

    RequestBuilder getResponseOnlyIfCached();

    RequestBuilder getResponseOnlyFromNetwork();

    RequestBuilder setMaxAgeCacheControl(int maxAge, TimeUnit timeUnit);

    RequestBuilder setMaxStaleCacheControl(int maxStale, TimeUnit timeUnit);

    RequestBuilder setExecutor(Executor executor);

    RequestBuilder setOkHttpClient(OkHttpClient okHttpClient);

    RequestBuilder setUserAgent(String userAgent);

}
