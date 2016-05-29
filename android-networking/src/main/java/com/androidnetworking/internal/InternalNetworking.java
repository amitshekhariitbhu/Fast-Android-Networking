/*
 *    Copyright (C) 2016 Amit Shekhar
 *    Copyright (C) 2011 The Android Open Source Project
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

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.common.ANData;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.androidnetworking.common.Method.DELETE;
import static com.androidnetworking.common.Method.GET;
import static com.androidnetworking.common.Method.HEAD;
import static com.androidnetworking.common.Method.PATCH;
import static com.androidnetworking.common.Method.POST;
import static com.androidnetworking.common.Method.PUT;

public class InternalNetworking {

    private static final String HEADER_USER_AGENT = "User-Agent";

    private static OkHttpClient sHttpClient = getClient();

    public static ANData performSimpleRequest(ANRequest request) throws ANError {
        ANData data = new ANData();
        Request okHttpRequest = null;
        try {
            Request.Builder builder = new Request.Builder().url(request.getUrl());
            builder.addHeader(HEADER_USER_AGENT, "Android");
            Headers requestHeaders = request.getHeaders();
            if (requestHeaders != null) {
                builder.headers(requestHeaders);
                if (!requestHeaders.names().contains(HEADER_USER_AGENT)) {
                    builder.addHeader(HEADER_USER_AGENT, "Android");
                }
            }
            switch (request.getMethod()) {
                case GET: {
                    builder = builder.get();
                    break;
                }
                case POST: {
                    builder = builder.post(request.getRequestBody());
                    break;
                }
                case PUT: {
                    builder = builder.put(request.getRequestBody());
                    break;
                }
                case DELETE: {
                    builder = builder.delete(request.getRequestBody());
                    break;
                }
                case HEAD: {
                    builder = builder.head();
                    break;
                }
                case PATCH: {
                    builder = builder.patch(request.getRequestBody());
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
            Response okResponse = request.getCall().execute();
            data.url = okResponse.request().url();
            data.code = okResponse.code();
            data.headers = okResponse.headers();
            data.source = okResponse.body().source();
            data.length = okResponse.body().contentLength();
        } catch (IOException ioe) {
            if (okHttpRequest != null) {
                data.url = okHttpRequest.url();
            }
            throw new ANError(data, ioe);
        }

        return data;
    }

    public static ANData performDownloadRequest(final ANRequest request) throws ANError {
        ANData data = new ANData();
        Request okHttpRequest = null;
        try {
            Request.Builder builder = new Request.Builder().url(request.getUrl());
            builder.addHeader(HEADER_USER_AGENT, "Android");
            Headers requestHeaders = request.getHeaders();
            if (requestHeaders != null) {
                builder.headers(requestHeaders);
                if (!requestHeaders.names().contains(HEADER_USER_AGENT)) {
                    builder.addHeader(HEADER_USER_AGENT, "Android");
                }
            }
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
                                        .body(new ResponseProgressBody(originalResponse.body(), request.getDownloadProgressListener()))
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
                                        .body(new ResponseProgressBody(originalResponse.body(), request.getDownloadProgressListener()))
                                        .build();
                            }
                        }).build();
            }
            request.setCall(okHttpClient.newCall(okHttpRequest));
            Response okResponse = request.getCall().execute();
            data.url = okResponse.request().url();
            data.code = okResponse.code();
            data.headers = okResponse.headers();
            Utils.saveFile(okResponse, request.getDirPath(), request.getFileName());
            request.updateDownloadCompletion();
        } catch (IOException ioe) {
            if (okHttpRequest != null) {
                data.url = okHttpRequest.url();
            }
            try {
                File destinationFile = new File(request.getDirPath() + File.separator + request.getFileName());
                if (destinationFile.exists()) {
                    destinationFile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new ANError(data, ioe);
        }
        return data;
    }


    public static ANData performUploadRequest(ANRequest request) throws ANError {
        ANData data = new ANData();
        Request okHttpRequest = null;
        try {
            Request.Builder builder = new Request.Builder().url(request.getUrl());
            builder.addHeader(HEADER_USER_AGENT, "Android");

            Headers requestHeaders = request.getHeaders();
            if (requestHeaders != null) {
                builder.headers(requestHeaders);
                if (!requestHeaders.names().contains(HEADER_USER_AGENT)) {
                    builder.addHeader(HEADER_USER_AGENT, "Android");
                }
            }
            builder = builder.post(new RequestProgressBody(request.getMultiPartRequestBody(), request.getUploadProgressListener()));
            if (request.getCacheControl() != null) {
                builder.cacheControl(request.getCacheControl());
            }
            okHttpRequest = builder.build();
            if (request.getOkHttpClient() != null) {
                request.setCall(request.getOkHttpClient().newBuilder().cache(sHttpClient.cache()).build().newCall(okHttpRequest));
            } else {
                request.setCall(sHttpClient.newCall(okHttpRequest));
            }
            Response okResponse = request.getCall().execute();
            data.url = okResponse.request().url();
            data.code = okResponse.code();
            data.headers = okResponse.headers();
            data.source = okResponse.body().source();
            data.length = okResponse.body().contentLength();
        } catch (IOException ioe) {
            if (okHttpRequest != null) {
                data.url = okHttpRequest.url();
            }
            throw new ANError(data, ioe);
        }
        return data;
    }

    public static OkHttpClient getClient() {
        if (sHttpClient == null) {
            return getDefaultClient();
        }
        return sHttpClient;
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

    public static void setClient(OkHttpClient okHttpClient) {
        sHttpClient = okHttpClient;
    }

}
