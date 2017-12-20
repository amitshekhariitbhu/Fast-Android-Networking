/*
 *
 *  *    Copyright (C) 2016 Amit Shekhar
 *  *    Copyright (C) 2011 Android Open Source Project
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package com.rx2androidnetworking;

import android.net.TrafficStats;

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.ConnectionClassManager;
import com.androidnetworking.error.ANError;
import com.androidnetworking.internal.InternalNetworking;
import com.androidnetworking.internal.RequestProgressBody;
import com.androidnetworking.internal.ResponseProgressBody;
import com.androidnetworking.utils.SourceCloseUtil;
import com.androidnetworking.utils.Utils;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Call;
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

/**
 * Created by Prashant Gupta on 30-01-2017.
 */
@SuppressWarnings("unchecked")
public class Rx2InternalNetworking {

    public static <T> Observable<T> generateSimpleObservable(Rx2ANRequest request) {
        Request okHttpRequest;
        Request.Builder builder = new Request.Builder().url(request.getUrl());
        InternalNetworking.addHeadersToRequestBuilder(builder, request);
        RequestBody requestBody;
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
            request.setCall(request
                    .getOkHttpClient()
                    .newBuilder()
                    .cache(InternalNetworking.sHttpClient.cache())
                    .build()
                    .newCall(okHttpRequest));
        } else {
            request.setCall(InternalNetworking.sHttpClient.newCall(okHttpRequest));
        }
        return new SimpleANObservable<>(request);
    }

    public static <T> Observable<T> generateDownloadObservable(final Rx2ANRequest request) {
        Request okHttpRequest;
        Request.Builder builder = new Request.Builder().url(request.getUrl());
        InternalNetworking.addHeadersToRequestBuilder(builder, request);
        builder = builder.get();
        if (request.getCacheControl() != null) {
            builder.cacheControl(request.getCacheControl());
        }
        okHttpRequest = builder.build();

        OkHttpClient okHttpClient;

        if (request.getOkHttpClient() != null) {
            okHttpClient = request
                    .getOkHttpClient()
                    .newBuilder()
                    .cache(InternalNetworking.sHttpClient.cache())
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
            okHttpClient = InternalNetworking.sHttpClient.newBuilder()
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
        return new DownloadANObservable<>(request);
    }

    public static <T> Observable<T> generateMultipartObservable(final Rx2ANRequest request) {
        return new MultipartANObservable<>(request);
    }

    static final class SimpleANObservable<T> extends Observable<T> {

        private Rx2ANRequest request;
        private final Call originalCall;

        SimpleANObservable(Rx2ANRequest request) {
            this.request = request;
            this.originalCall = request.getCall();
        }

        @Override
        protected void subscribeActual(Observer<? super T> observer) {
            Call call = originalCall.clone();
            observer.onSubscribe(new ANDisposable(call));
            boolean doNotSwallowError = false;
            Response okHttpResponse = null;
            try {
                final long startTime = System.currentTimeMillis();
                final long startBytes = TrafficStats.getTotalRxBytes();
                okHttpResponse = call.execute();
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
                            (request.getRequestBody() != null &&
                                    request.getRequestBody().contentLength() != 0) ?
                                    request.getRequestBody().contentLength() : -1,
                            okHttpResponse.body().contentLength(), false);
                } else if (request.getAnalyticsListener() != null) {
                    if (okHttpResponse.networkResponse() == null) {
                        Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken, 0, 0, true);
                    } else {
                        Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken,
                                (request.getRequestBody() != null && request.getRequestBody().contentLength() != 0) ?
                                        request.getRequestBody().contentLength() : -1, 0, true);
                    }
                }
                if (okHttpResponse.code() >= 400) {
                    if (!call.isCanceled()) {
                        observer.onError(Utils.getErrorForServerResponse(new ANError(okHttpResponse),
                                request, okHttpResponse.code()));
                    }
                } else {
                    ANResponse<T> response = request.parseResponse(okHttpResponse);
                    if (!response.isSuccess()) {
                        if (!call.isCanceled()) {
                            observer.onError(response.getError());
                        }
                    } else {
                        if (!call.isCanceled()) {
                            observer.onNext(response.getResult());
                        }
                        if (!call.isCanceled()) {
                            doNotSwallowError = true;
                            observer.onComplete();
                        }
                    }
                }
            } catch (IOException ioe) {
                if (!call.isCanceled()) {
                    observer.onError(Utils.getErrorForConnection(new ANError(ioe)));
                }
            } catch (Exception e) {
                Exceptions.throwIfFatal(e);
                if (doNotSwallowError) {
                    RxJavaPlugins.onError(e);
                } else if (!call.isCanceled()) {
                    try {
                        observer.onError(Utils.getErrorForConnection(new ANError(e)));
                    } catch (Exception e1) {
                        Exceptions.throwIfFatal(e1);
                        RxJavaPlugins.onError(new CompositeException(e, e1));
                    }
                }
            } finally {
                SourceCloseUtil.close(okHttpResponse, request);
            }
        }
    }

    static final class DownloadANObservable<T> extends Observable<T> {

        private final Rx2ANRequest request;
        private final Call originalCall;

        DownloadANObservable(Rx2ANRequest request) {
            this.request = request;
            this.originalCall = request.getCall();
        }

        @Override
        protected void subscribeActual(Observer<? super T> observer) {
            Call call = originalCall.clone();
            observer.onSubscribe(new ANDisposable(call));
            boolean doNotSwallowError = false;
            Response okHttpResponse;
            try {
                final long startTime = System.currentTimeMillis();
                final long startBytes = TrafficStats.getTotalRxBytes();
                okHttpResponse = request.getCall().execute();
                Utils.saveFile(okHttpResponse, request.getDirPath(), request.getFileName());
                final long timeTaken = System.currentTimeMillis() - startTime;
                if (okHttpResponse.cacheResponse() == null) {
                    final long finalBytes = TrafficStats.getTotalRxBytes();
                    final long diffBytes;
                    if (startBytes == TrafficStats.UNSUPPORTED ||
                            finalBytes == TrafficStats.UNSUPPORTED) {
                        diffBytes = okHttpResponse.body().contentLength();
                    } else {
                        diffBytes = finalBytes - startBytes;
                    }
                    ConnectionClassManager.getInstance().updateBandwidth(diffBytes, timeTaken);
                    Utils.sendAnalytics(request.getAnalyticsListener(),
                            timeTaken, -1, okHttpResponse.body().contentLength(), false);
                } else if (request.getAnalyticsListener() != null) {
                    Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken, -1, 0, true);
                }
                if (okHttpResponse.code() >= 400) {
                    if (!call.isCanceled()) {
                        observer.onError(Utils.getErrorForServerResponse(new ANError(okHttpResponse),
                                request, okHttpResponse.code()));
                    }
                } else {
                    if (!call.isCanceled()) {
                        ANResponse<T> response = (ANResponse<T>) ANResponse.success(ANConstants.SUCCESS);
                        observer.onNext(response.getResult());
                    }
                    if (!call.isCanceled()) {
                        doNotSwallowError = true;
                        observer.onComplete();
                    }
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
                if (!call.isCanceled()) {
                    observer.onError(Utils.getErrorForConnection(new ANError(ioe)));
                }
            } catch (Exception e) {
                Exceptions.throwIfFatal(e);
                if (doNotSwallowError) {
                    RxJavaPlugins.onError(e);
                } else if (!call.isCanceled()) {
                    try {
                        observer.onError(Utils.getErrorForConnection(new ANError(e)));
                    } catch (Exception e1) {
                        Exceptions.throwIfFatal(e1);
                        RxJavaPlugins.onError(new CompositeException(e, e1));
                    }
                }
            }
        }
    }

    static final class MultipartANObservable<T> extends Observable<T> {

        private final Rx2ANRequest request;

        MultipartANObservable(Rx2ANRequest request) {
            this.request = request;
        }

        @Override
        protected void subscribeActual(Observer<? super T> observer) {
            boolean doNotSwallowError = false;
            Response okHttpResponse = null;
            Request okHttpRequest;
            try {
                Request.Builder builder = new Request.Builder().url(request.getUrl());
                InternalNetworking.addHeadersToRequestBuilder(builder, request);
                final RequestBody requestBody = request.getMultiPartRequestBody();
                final long requestBodyLength = requestBody.contentLength();
                builder = builder.post(new RequestProgressBody(requestBody, request.getUploadProgressListener()));
                if (request.getCacheControl() != null) {
                    builder.cacheControl(request.getCacheControl());
                }
                okHttpRequest = builder.build();
                if (request.getOkHttpClient() != null) {
                    request.setCall(request
                            .getOkHttpClient()
                            .newBuilder()
                            .cache(InternalNetworking.sHttpClient.cache())
                            .build()
                            .newCall(okHttpRequest));
                } else {
                    request.setCall(InternalNetworking.sHttpClient.newCall(okHttpRequest));
                }
                observer.onSubscribe(new ANDisposable(request.getCall()));
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
                if (okHttpResponse.code() >= 400) {
                    if (!request.getCall().isCanceled()) {
                        observer.onError(Utils.getErrorForServerResponse(new ANError(okHttpResponse),
                                request, okHttpResponse.code()));
                    }
                } else {
                    ANResponse<T> response = request.parseResponse(okHttpResponse);
                    if (!response.isSuccess()) {
                        if (!request.getCall().isCanceled()) {
                            observer.onError(response.getError());
                        }
                    } else {
                        if (!request.getCall().isCanceled()) {
                            observer.onNext(response.getResult());
                        }
                        if (!request.getCall().isCanceled()) {
                            doNotSwallowError = true;
                            observer.onComplete();
                        }
                    }
                }
            } catch (IOException ioe) {
                if (!request.getCall().isCanceled()) {
                    observer.onError(Utils.getErrorForConnection(new ANError(ioe)));
                }
            } catch (Exception e) {
                Exceptions.throwIfFatal(e);
                if (doNotSwallowError) {
                    RxJavaPlugins.onError(e);
                } else if (!request.getCall().isCanceled()) {
                    try {
                        observer.onError(Utils.getErrorForConnection(new ANError(e)));
                    } catch (Exception e1) {
                        Exceptions.throwIfFatal(e1);
                        RxJavaPlugins.onError(new CompositeException(e, e1));
                    }
                }
            } finally {
                SourceCloseUtil.close(okHttpResponse, request);
            }
        }
    }

    private static final class ANDisposable implements Disposable {

        private final Call call;

        private ANDisposable(Call call) {
            this.call = call;
        }

        @Override
        public void dispose() {
            this.call.cancel();
        }

        @Override
        public boolean isDisposed() {
            return this.call.isCanceled();
        }
    }
}
