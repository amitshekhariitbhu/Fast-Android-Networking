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
package com.rxandroidnetworking;

import android.net.TrafficStats;

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.ConnectionClassManager;
import com.androidnetworking.common.RequestType;
import com.androidnetworking.error.ANError;
import com.androidnetworking.internal.InternalNetworking;
import com.androidnetworking.internal.RequestProgressBody;
import com.androidnetworking.internal.ResponseProgressBody;
import com.androidnetworking.utils.SourceCloseUtil;
import com.androidnetworking.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;

import static com.androidnetworking.common.Method.DELETE;
import static com.androidnetworking.common.Method.GET;
import static com.androidnetworking.common.Method.HEAD;
import static com.androidnetworking.common.Method.OPTIONS;
import static com.androidnetworking.common.Method.PATCH;
import static com.androidnetworking.common.Method.POST;
import static com.androidnetworking.common.Method.PUT;

/**
 * Created by Prashant Gupta on 25-07-2016.
 */
@SuppressWarnings("unchecked")
public class RxInternalNetworking {

    public static <T> Observable<T> generateSimpleObservable(RxANRequest request) {
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
        return Observable.create(new ANOnSubscribe<T>(request));
    }

    public static <T> Observable<T> generateDownloadObservable(final RxANRequest request) {
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
        return Observable.create(new ANOnSubscribe<T>(request));
    }

    public static <T> Observable<T> generateMultipartObservable(final RxANRequest request) {
        return Observable.create(new ANOnSubscribe<T>(request));
    }

    static final class ANOnSubscribe<T> implements Observable.OnSubscribe<T> {

        private final RxANRequest request;

        public ANOnSubscribe(RxANRequest request) {
            this.request = request;
        }

        @Override
        public void call(Subscriber<? super T> subscriber) {
            switch (request.getRequestType()) {
                case RequestType.SIMPLE:
                    ANResolver<T> anResolver = new ANResolver<>(request, subscriber);
                    subscriber.add(anResolver);
                    subscriber.setProducer(anResolver);
                    break;
                case RequestType.DOWNLOAD:
                    DownloadANResolver<T> downloadANResolver = new DownloadANResolver<>(request,
                            subscriber);
                    subscriber.add(downloadANResolver);
                    subscriber.setProducer(downloadANResolver);
                    break;
                case RequestType.MULTIPART:
                    MultipartANResolver<T> multipartANResolver = new MultipartANResolver<>(request,
                            subscriber);
                    subscriber.add(multipartANResolver);
                    subscriber.setProducer(multipartANResolver);
                    break;
            }
        }
    }

    static final class ANResolver<T> extends AtomicBoolean implements Subscription, Producer {
        private final Call call;
        private final RxANRequest request;
        private final Subscriber<? super T> subscriber;

        ANResolver(RxANRequest request, Subscriber<? super T> subscriber) {
            this.request = request;
            this.call = request.getCall();
            this.subscriber = subscriber;
        }

        @Override
        public void request(long n) {
            if (n < 0) throw new IllegalArgumentException("n < 0: " + n);
            if (n == 0) return; // Nothing to do when requesting 0.
            if (!compareAndSet(false, true)) return; // Request was already triggered.
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
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(Utils.getErrorForServerResponse(new ANError(okHttpResponse),
                                request, okHttpResponse.code()));
                    }
                } else {
                    ANResponse<T> response = request.parseResponse(okHttpResponse);
                    if (!response.isSuccess()) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onError(response.getError());
                        }
                    } else {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(response.getResult());
                        }
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onCompleted();
                        }
                    }
                }
            } catch (IOException ioe) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(Utils.getErrorForConnection(new ANError(ioe)));
                }
            } catch (Exception e) {
                Exceptions.throwIfFatal(e);
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(Utils.getErrorForConnection(new ANError(e)));
                }
            } finally {
                SourceCloseUtil.close(okHttpResponse, request);
            }
        }

        @Override
        public void unsubscribe() {
            call.cancel();
        }

        @Override
        public boolean isUnsubscribed() {
            return call.isCanceled();
        }
    }

    static final class DownloadANResolver<T> extends AtomicBoolean implements Subscription, Producer {
        private final Call call;
        private final RxANRequest request;
        private final Subscriber<? super T> subscriber;

        DownloadANResolver(RxANRequest request, Subscriber<? super T> subscriber) {
            this.request = request;
            this.call = request.getCall();
            this.subscriber = subscriber;
        }

        @Override
        public void request(long n) {
            if (n < 0) throw new IllegalArgumentException("n < 0: " + n);
            if (n == 0) return; // Nothing to do when requesting 0.
            if (!compareAndSet(false, true)) return; // Request was already triggered.
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
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(Utils.getErrorForServerResponse(new ANError(okHttpResponse),
                                request, okHttpResponse.code()));
                    }
                } else {
                    if (!subscriber.isUnsubscribed()) {
                        ANResponse<T> response = (ANResponse<T>) ANResponse.success(ANConstants.SUCCESS);
                        subscriber.onNext(response.getResult());
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
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
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(Utils.getErrorForConnection(new ANError(ioe)));
                }
            } catch (Exception e) {
                Exceptions.throwIfFatal(e);
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(Utils.getErrorForConnection(new ANError(e)));
                }
            }
        }

        @Override
        public void unsubscribe() {
            call.cancel();
        }

        @Override
        public boolean isUnsubscribed() {
            return call.isCanceled();
        }
    }

    static final class MultipartANResolver<T> extends AtomicBoolean implements Subscription, Producer {
        private final RxANRequest request;
        private final Subscriber<? super T> subscriber;

        MultipartANResolver(RxANRequest request, Subscriber<? super T> subscriber) {
            this.request = request;
            this.subscriber = subscriber;
        }

        @Override
        public void request(long n) {
            if (n < 0) throw new IllegalArgumentException("n < 0: " + n);
            if (n == 0) return; // Nothing to do when requesting 0.
            if (!compareAndSet(false, true)) return; // Request was already triggered.
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
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(Utils.getErrorForServerResponse(new ANError(okHttpResponse),
                                request, okHttpResponse.code()));
                    }
                } else {
                    ANResponse<T> response = request.parseResponse(okHttpResponse);
                    if (!response.isSuccess()) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onError(response.getError());
                        }
                    } else {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(response.getResult());
                        }
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onCompleted();
                        }
                    }
                }
            } catch (IOException ioe) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(Utils.getErrorForConnection(new ANError(ioe)));
                }
            } catch (Exception e) {
                Exceptions.throwIfFatal(e);
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(Utils.getErrorForConnection(new ANError(e)));
                }
            } finally {
                SourceCloseUtil.close(okHttpResponse, request);
            }
        }

        @Override
        public void unsubscribe() {
            if (request.getCall() != null) {
                request.getCall().cancel();
            }
        }

        @Override
        public boolean isUnsubscribed() {
            return request.getCall() != null && request.getCall().isCanceled();
        }
    }


}
