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

package com.androidnetworking.internal;

/**
 * Created by amitshekhar on 22/03/16.
 */

import android.content.Context;
import android.net.TrafficStats;

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ConnectionClassManager;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.androidnetworking.interceptors.HttpLoggingInterceptor.Level;
import com.androidnetworking.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.androidnetworking.common.Method.DELETE;
import static com.androidnetworking.common.Method.GET;
import static com.androidnetworking.common.Method.HEAD;
import static com.androidnetworking.common.Method.OPTIONS;
import static com.androidnetworking.common.Method.PATCH;
import static com.androidnetworking.common.Method.POST;
import static com.androidnetworking.common.Method.PUT;

public final class InternalNetworking {

    private InternalNetworking() {

    }

    public static OkHttpClient sHttpClient = getClient();

    public static String sUserAgent = null;

    public static Response performSimpleRequest(ANRequest request) throws ANError {
        Request okHttpRequest;
        Response okHttpResponse;
        try {
            Request.Builder builder = new Request.Builder().url(request.getUrl());
            addHeadersToRequestBuilder(builder, request);
            RequestBody requestBody = null;
            switch (request.getMethod()) {
                case GET: {
                    builder = builder.get();
                    break;
                }
                case POST: {
                    requestBody = request.getRequestBody();
                    builder = builder.post(requestBody);
                    break;
                }
                case PUT: {
                    requestBody = request.getRequestBody();
                    builder = builder.put(requestBody);
                    break;
                }
                case DELETE: {
                    requestBody = request.getRequestBody();
                    builder = builder.delete(requestBody);
                    break;
                }
                case HEAD: {
                    builder = builder.head();
                    break;
                }
                case OPTIONS: {
                    builder = builder.method(ANConstants.OPTIONS, null);
                    break;
                }
                case PATCH: {
                    requestBody = request.getRequestBody();
                    builder = builder.patch(requestBody);
                    break;
                }
            }
            if (request.getCacheControl() != null) {
                builder.cacheControl(request.getCacheControl());
            }
            okHttpRequest = builder.build();

            if (request.getOkHttpClient() != null) {
                request.setCall(request.getOkHttpClient().newBuilder().cache(sHttpClient.cache()).build().newCall(okHttpRequest));
            } else {
                request.setCall(sHttpClient.newCall(okHttpRequest));
            }
            final long startTime = System.currentTimeMillis();
            final long startBytes = TrafficStats.getTotalRxBytes();
            okHttpResponse = request.getCall().execute();
            final long timeTaken = System.currentTimeMillis() - startTime;
            if (okHttpResponse.cacheResponse() == null) {
                final long finalBytes = TrafficStats.getTotalRxBytes();
                final long diffBytes;
                if (startBytes == TrafficStats.UNSUPPORTED || finalBytes == TrafficStats.UNSUPPORTED) {
                    diffBytes = okHttpResponse.body().contentLength();
                } else {
                    diffBytes = finalBytes - startBytes;
                }
                ConnectionClassManager.getInstance().updateBandwidth(diffBytes, timeTaken);
                Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken,
                        (requestBody != null &&
                                requestBody.contentLength() != 0) ? requestBody.contentLength() : -1,
                        okHttpResponse.body().contentLength(), false);
            } else if (request.getAnalyticsListener() != null) {
                if (okHttpResponse.networkResponse() == null) {
                    Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken, 0, 0, true);
                } else {
                    Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken,
                            (requestBody != null && requestBody.contentLength() != 0) ? requestBody.contentLength() : -1,
                            0, true);
                }
            }
        } catch (IOException ioe) {
            throw new ANError(ioe);
        }
        return okHttpResponse;
    }

    public static Response performDownloadRequest(final ANRequest request) throws ANError {
        Request okHttpRequest;
        Response okHttpResponse;
        try {
            Request.Builder builder = new Request.Builder().url(request.getUrl());
            addHeadersToRequestBuilder(builder, request);
            builder = builder.get();
            if (request.getCacheControl() != null) {
                builder.cacheControl(request.getCacheControl());
            }
            okHttpRequest = builder.build();

            OkHttpClient okHttpClient;

            if (request.getOkHttpClient() != null) {
                okHttpClient = request.getOkHttpClient().newBuilder().cache(sHttpClient.cache())
                        .addNetworkInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Response originalResponse = chain.proceed(chain.request());
                                return originalResponse.newBuilder()
                                        .body(new ResponseProgressBody(originalResponse.body(),
                                                request.getDownloadProgressListener()))
                                        .build();
                            }
                        }).build();
            } else {
                okHttpClient = sHttpClient.newBuilder()
                        .addNetworkInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Response originalResponse = chain.proceed(chain.request());
                                return originalResponse.newBuilder()
                                        .body(new ResponseProgressBody(originalResponse.body(),
                                                request.getDownloadProgressListener()))
                                        .build();
                            }
                        }).build();
            }
            request.setCall(okHttpClient.newCall(okHttpRequest));
            final long startTime = System.currentTimeMillis();
            final long startBytes = TrafficStats.getTotalRxBytes();
            okHttpResponse = request.getCall().execute();
            Utils.saveFile(okHttpResponse, request.getDirPath(), request.getFileName());
            final long timeTaken = System.currentTimeMillis() - startTime;
            if (okHttpResponse.cacheResponse() == null) {
                final long finalBytes = TrafficStats.getTotalRxBytes();
                final long diffBytes;
                if (startBytes == TrafficStats.UNSUPPORTED || finalBytes == TrafficStats.UNSUPPORTED) {
                    diffBytes = okHttpResponse.body().contentLength();
                } else {
                    diffBytes = finalBytes - startBytes;
                }
                ConnectionClassManager.getInstance().updateBandwidth(diffBytes, timeTaken);
                Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken, -1,
                        okHttpResponse.body().contentLength(), false);
            } else if (request.getAnalyticsListener() != null) {
                Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken, -1, 0, true);
            }
        } catch (IOException ioe) {
            try {
                File destinationFile = new File(request.getDirPath() + File.separator + request.getFileName());
                if (destinationFile.exists()) {
                    destinationFile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new ANError(ioe);
        }
        return okHttpResponse;
    }


    public static Response performUploadRequest(ANRequest request) throws ANError {
        Request okHttpRequest;
        Response okHttpResponse;
        try {
            Request.Builder builder = new Request.Builder().url(request.getUrl());
            addHeadersToRequestBuilder(builder, request);
            final RequestBody requestBody = request.getMultiPartRequestBody();
            final long requestBodyLength = requestBody.contentLength();
            builder = builder.post(new RequestProgressBody(requestBody, request.getUploadProgressListener()));
            if (request.getCacheControl() != null) {
                builder.cacheControl(request.getCacheControl());
            }
            okHttpRequest = builder.build();
            if (request.getOkHttpClient() != null) {
                request.setCall(request.getOkHttpClient()
                        .newBuilder()
                        .cache(sHttpClient.cache())
                        .build()
                        .newCall(okHttpRequest));
            } else {
                request.setCall(sHttpClient.newCall(okHttpRequest));
            }
            final long startTime = System.currentTimeMillis();
            okHttpResponse = request.getCall().execute();
            final long timeTaken = System.currentTimeMillis() - startTime;
            if (request.getAnalyticsListener() != null) {
                if (okHttpResponse.cacheResponse() == null) {
                    Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken,
                            requestBodyLength, okHttpResponse.body().contentLength(), false);
                } else {
                    if (okHttpResponse.networkResponse() == null) {
                        Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken, 0, 0, true);
                    } else {
                        Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken,
                                requestBodyLength != 0 ? requestBodyLength : -1, 0, true);
                    }
                }
            }
        } catch (IOException ioe) {
            throw new ANError(ioe);
        }
        return okHttpResponse;
    }

    public static OkHttpClient getClient() {
        if (sHttpClient == null) {
            return getDefaultClient();
        }
        return sHttpClient;
    }

    public static void addHeadersToRequestBuilder(Request.Builder builder, ANRequest request) {
        if (request.getUserAgent() != null) {
            builder.addHeader(ANConstants.USER_AGENT, request.getUserAgent());
        } else if (sUserAgent != null) {
            request.setUserAgent(sUserAgent);
            builder.addHeader(ANConstants.USER_AGENT, sUserAgent);
        }
        Headers requestHeaders = request.getHeaders();
        if (requestHeaders != null) {
            builder.headers(requestHeaders);
            if (request.getUserAgent() != null && !requestHeaders.names().contains(ANConstants.USER_AGENT)) {
                builder.addHeader(ANConstants.USER_AGENT, request.getUserAgent());
            }
        }
    }

    public static OkHttpClient getDefaultClient() {
        return new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public static void setClientWithCache(Context context) {
        sHttpClient = new OkHttpClient().newBuilder()
                .cache(Utils.getCache(context, ANConstants.MAX_CACHE_SIZE, ANConstants.CACHE_DIR_NAME))
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public static void setUserAgent(String userAgent) {
        sUserAgent = userAgent;
    }

    public static void setClient(OkHttpClient okHttpClient) {
        sHttpClient = okHttpClient;
    }

    public static void enableLogging(Level level) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(level);
        sHttpClient = getClient()
                .newBuilder()
                .addInterceptor(logging)
                .build();
    }

}