package com.rxandroidnetworking;

import android.net.TrafficStats;
import android.os.Build;
import android.os.NetworkOnMainThreadException;

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.common.ANData;
import com.androidnetworking.common.ANLog;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.ConnectionClassManager;
import com.androidnetworking.common.RequestType;
import com.androidnetworking.error.ANError;
import com.androidnetworking.internal.InternalNetworking;
import com.androidnetworking.internal.RequestProgressBody;
import com.androidnetworking.internal.ResponseProgressBody;
import com.androidnetworking.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Headers;
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
import static com.androidnetworking.common.Method.PATCH;
import static com.androidnetworking.common.Method.POST;
import static com.androidnetworking.common.Method.PUT;

/**
 * Created by Prashant Gupta on 25-07-2016.
 */
public class RxInternalNetworking {

    public static <T> Observable<T> generateSimpleObservable(RxANRequest request) {
        Request okHttpRequest = null;
        Request.Builder builder = new Request.Builder().url(request.getUrl());
        if (request.getUserAgent() != null) {
            builder.addHeader(ANConstants.USER_AGENT, request.getUserAgent());
        } else if (InternalNetworking.sUserAgent != null) {
            request.setUserAgent(InternalNetworking.sUserAgent);
            builder.addHeader(ANConstants.USER_AGENT, InternalNetworking.sUserAgent);
        }
        Headers requestHeaders = request.getHeaders();
        if (requestHeaders != null) {
            builder.headers(requestHeaders);
            if (request.getUserAgent() != null && !requestHeaders.names().contains(ANConstants.USER_AGENT)) {
                builder.addHeader(ANConstants.USER_AGENT, request.getUserAgent());
            }
        }
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
            request.setCall(request.getOkHttpClient().newBuilder().cache(InternalNetworking.sHttpClient.cache()).build().newCall(okHttpRequest));
        } else {
            request.setCall(InternalNetworking.sHttpClient.newCall(okHttpRequest));
        }
        ANLog.d("call generated successfully for simple observable");
        Observable<T> observable = Observable.create(new ANOnSubscribe<T>(request));
        return observable;
    }

    public static Observable generateDownloadObservable(final RxANRequest request) {
        Request okHttpRequest = null;
        Request.Builder builder = new Request.Builder().url(request.getUrl());
        if (request.getUserAgent() != null) {
            builder.addHeader(ANConstants.USER_AGENT, request.getUserAgent());
        } else if (InternalNetworking.sUserAgent != null) {
            request.setUserAgent(InternalNetworking.sUserAgent);
            builder.addHeader(ANConstants.USER_AGENT, InternalNetworking.sUserAgent);
        }
        Headers requestHeaders = request.getHeaders();
        if (requestHeaders != null) {
            builder.headers(requestHeaders);
            if (request.getUserAgent() != null && !requestHeaders.names().contains(ANConstants.USER_AGENT)) {
                builder.addHeader(ANConstants.USER_AGENT, request.getUserAgent());
            }
        }
        builder = builder.get();
        if (request.getCacheControl() != null) {
            builder.cacheControl(request.getCacheControl());
        }
        okHttpRequest = builder.build();

        OkHttpClient okHttpClient;

        if (request.getOkHttpClient() != null) {
            okHttpClient = request.getOkHttpClient().newBuilder().cache(InternalNetworking.sHttpClient.cache())
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
            okHttpClient = InternalNetworking.sHttpClient.newBuilder()
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
        Observable observable = Observable.create(new ANOnSubscribe(request));
        return observable;
    }

    public static <T> Observable<T> generateMultipartObservable(final RxANRequest request) {
        Observable<T> observable = Observable.create(new ANOnSubscribe<T>(request));
        return observable;
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
                    DownloadANResolver downloadANResolver = new DownloadANResolver(request, subscriber);
                    subscriber.add(downloadANResolver);
                    subscriber.setProducer(downloadANResolver);
                    break;
                case RequestType.MULTIPART:
                    MultipartANResolver multipartANResolver = new MultipartANResolver(request, subscriber);
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
            ANData data = new ANData();
            try {
                ANLog.d("initiate simple network call observable");
                final long startTime = System.currentTimeMillis();
                final long startBytes = TrafficStats.getTotalRxBytes();
                Response okResponse = call.execute();
                data.url = okResponse.request().url();
                data.code = okResponse.code();
                data.headers = okResponse.headers();
                data.source = okResponse.body().source();
                data.length = okResponse.body().contentLength();
                final long timeTaken = System.currentTimeMillis() - startTime;
                if (okResponse.cacheResponse() == null) {
                    final long finalBytes = TrafficStats.getTotalRxBytes();
                    final long diffBytes;
                    if (startBytes == TrafficStats.UNSUPPORTED || finalBytes == TrafficStats.UNSUPPORTED) {
                        diffBytes = data.length;
                    } else {
                        diffBytes = finalBytes - startBytes;
                    }
                    ConnectionClassManager.getInstance().updateBandwidth(diffBytes, timeTaken);
                    Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken, (request.getRequestBody() != null && request.getRequestBody().contentLength() != 0) ? request.getRequestBody().contentLength() : -1, data.length, false);
                } else if (request.getAnalyticsListener() != null) {
                    if (okResponse.networkResponse() == null) {
                        Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken, 0, 0, true);
                    } else {
                        Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken, (request.getRequestBody() != null && request.getRequestBody().contentLength() != 0) ? request.getRequestBody().contentLength() : -1, 0, true);
                    }
                }
                if (data.code == 304) {
                    ANLog.d("error code 304 simple observable");
                } else if (data.code >= 400) {
                    ANError anError = new ANError(data);
                    anError = request.parseNetworkError(anError);
                    anError.setErrorCode(data.code);
                    anError.setErrorDetail(ANConstants.RESPONSE_FROM_SERVER_ERROR);
                    if (!subscriber.isUnsubscribed()) {
                        ANLog.d("delivering error to subscriber from simple observable");
                        subscriber.onError(anError);
                    }
                } else {
                    ANResponse<T> response = request.parseResponse(data);
                    if (!response.isSuccess()) {
                        if (!subscriber.isUnsubscribed()) {
                            ANLog.d("delivering error to subscriber from simple observable");
                            subscriber.onError(response.getError());
                        }
                    } else {
                        if (!subscriber.isUnsubscribed()) {
                            ANLog.d("delivering response to subscriber from simple observable");
                            subscriber.onNext(response.getResult());
                        }
                        if (!subscriber.isUnsubscribed()) {
                            ANLog.d("delivering completion to subscriber from simple observable");
                            subscriber.onCompleted();
                        }
                    }
                }
            } catch (IOException ioe) {
                Request okHttpRequest = request.getCall().request();
                if (okHttpRequest != null) {
                    data.url = okHttpRequest.url();
                }
                if (!subscriber.isUnsubscribed()) {
                    ANLog.d("delivering error to subscriber from simple observable");
                    ANError anError = new ANError(data, ioe);
                    anError = request.parseNetworkError(anError);
                    anError.setErrorDetail(ANConstants.CONNECTION_ERROR);
                    anError.setErrorCode(0);
                    subscriber.onError(anError);
                }
            } catch (Exception e) {
                Exceptions.throwIfFatal(e);
                ANError se = new ANError(e);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && e instanceof NetworkOnMainThreadException) {
                    se.setErrorDetail(ANConstants.NETWORK_ON_MAIN_THREAD_ERROR);
                } else {
                    se.setErrorDetail(ANConstants.CONNECTION_ERROR);
                }
                se.setErrorCode(0);
                if (!subscriber.isUnsubscribed()) {
                    ANLog.d("delivering error to subscriber from simple observable");
                    subscriber.onError(se);
                }
            } finally {
                if (data != null && data.source != null) {
                    try {
                        data.source.close();
                    } catch (IOException ignored) {
                        ANLog.d("Unable to close source data");
                    }
                }
            }
        }

        @Override
        public void unsubscribe() {
            ANLog.d("unsubscribed from simple observable");
            call.cancel();
        }

        @Override
        public boolean isUnsubscribed() {
            return call.isCanceled();
        }
    }

    static final class DownloadANResolver extends AtomicBoolean implements Subscription, Producer {
        private final Call call;
        private final RxANRequest request;
        private final Subscriber subscriber;

        DownloadANResolver(RxANRequest request, Subscriber subscriber) {
            this.request = request;
            this.call = request.getCall();
            this.subscriber = subscriber;
        }

        @Override
        public void request(long n) {
            if (n < 0) throw new IllegalArgumentException("n < 0: " + n);
            if (n == 0) return; // Nothing to do when requesting 0.
            if (!compareAndSet(false, true)) return; // Request was already triggered.
            ANData data = new ANData();
            try {
                ANLog.d("initiate download network call observable");
                final long startTime = System.currentTimeMillis();
                final long startBytes = TrafficStats.getTotalRxBytes();
                Response okResponse = request.getCall().execute();
                data.url = okResponse.request().url();
                data.code = okResponse.code();
                data.headers = okResponse.headers();
                Utils.saveFile(okResponse, request.getDirPath(), request.getFileName());
                data.length = okResponse.body().contentLength();
                final long timeTaken = System.currentTimeMillis() - startTime;
                if (okResponse.cacheResponse() == null) {
                    final long finalBytes = TrafficStats.getTotalRxBytes();
                    final long diffBytes;
                    if (startBytes == TrafficStats.UNSUPPORTED || finalBytes == TrafficStats.UNSUPPORTED) {
                        diffBytes = data.length;
                    } else {
                        diffBytes = finalBytes - startBytes;
                    }
                    ConnectionClassManager.getInstance().updateBandwidth(diffBytes, timeTaken);
                    Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken, -1, data.length, false);
                } else if (request.getAnalyticsListener() != null) {
                    Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken, -1, 0, true);
                }
                if (data.code >= 400) {
                    ANError anError = new ANError();
                    anError = request.parseNetworkError(anError);
                    anError.setErrorCode(data.code);
                    anError.setErrorDetail(ANConstants.RESPONSE_FROM_SERVER_ERROR);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(anError);
                    }
                } else {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                }
            } catch (IOException ioe) {
                Request okHttpRequest = request.getCall().request();
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
                if (!subscriber.isUnsubscribed()) {
                    ANError anError = new ANError(data, ioe);
                    anError = request.parseNetworkError(anError);
                    anError.setErrorDetail(ANConstants.CONNECTION_ERROR);
                    anError.setErrorCode(0);
                    subscriber.onError(anError);
                }
            } catch (Exception e) {
                Exceptions.throwIfFatal(e);
                ANError se = new ANError(e);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && e instanceof NetworkOnMainThreadException) {
                    se.setErrorDetail(ANConstants.NETWORK_ON_MAIN_THREAD_ERROR);
                } else {
                    se.setErrorDetail(ANConstants.CONNECTION_ERROR);
                }
                se.setErrorCode(0);
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(se);
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
            ANData data = new ANData();
            Request okHttpRequest = null;
            try {
                Request.Builder builder = new Request.Builder().url(request.getUrl());
                if (request.getUserAgent() != null) {
                    builder.addHeader(ANConstants.USER_AGENT, request.getUserAgent());
                } else if (InternalNetworking.sUserAgent != null) {
                    request.setUserAgent(InternalNetworking.sUserAgent);
                    builder.addHeader(ANConstants.USER_AGENT, InternalNetworking.sUserAgent);
                }
                Headers requestHeaders = request.getHeaders();
                if (requestHeaders != null) {
                    builder.headers(requestHeaders);
                    if (request.getUserAgent() != null && !requestHeaders.names().contains(ANConstants.USER_AGENT)) {
                        builder.addHeader(ANConstants.USER_AGENT, request.getUserAgent());
                    }
                }
                final RequestBody requestBody = request.getMultiPartRequestBody();
                final long requestBodyLength = requestBody.contentLength();
                builder = builder.post(new RequestProgressBody(requestBody, request.getUploadProgressListener()));
                if (request.getCacheControl() != null) {
                    builder.cacheControl(request.getCacheControl());
                }
                okHttpRequest = builder.build();
                if (request.getOkHttpClient() != null) {
                    request.setCall(request.getOkHttpClient().newBuilder().cache(InternalNetworking.sHttpClient.cache()).build().newCall(okHttpRequest));
                } else {
                    request.setCall(InternalNetworking.sHttpClient.newCall(okHttpRequest));
                }
                final long startTime = System.currentTimeMillis();
                Response okResponse = request.getCall().execute();
                data.url = okResponse.request().url();
                data.code = okResponse.code();
                data.headers = okResponse.headers();
                data.source = okResponse.body().source();
                data.length = okResponse.body().contentLength();
                final long timeTaken = System.currentTimeMillis() - startTime;
                if (request.getAnalyticsListener() != null) {
                    if (okResponse.cacheResponse() == null) {
                        Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken, requestBodyLength, data.length, false);
                    } else {
                        if (okResponse.networkResponse() == null) {
                            Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken, 0, 0, true);
                        } else {
                            Utils.sendAnalytics(request.getAnalyticsListener(), timeTaken, requestBodyLength != 0 ? requestBodyLength : -1, 0, true);
                        }
                    }
                }
                if (data.code >= 400) {
                    ANError anError = new ANError(data);
                    anError = request.parseNetworkError(anError);
                    anError.setErrorCode(data.code);
                    anError.setErrorDetail(ANConstants.RESPONSE_FROM_SERVER_ERROR);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(anError);
                    }
                } else {
                    ANResponse<T> response = request.parseResponse(data);
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
                if (okHttpRequest != null) {
                    data.url = okHttpRequest.url();
                }
                if (!subscriber.isUnsubscribed()) {
                    ANError anError = new ANError(data, ioe);
                    anError = request.parseNetworkError(anError);
                    anError.setErrorDetail(ANConstants.CONNECTION_ERROR);
                    anError.setErrorCode(0);
                    subscriber.onError(anError);
                }
            } catch (Exception e) {
                Exceptions.throwIfFatal(e);
                ANError se = new ANError(e);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && e instanceof NetworkOnMainThreadException) {
                    se.setErrorDetail(ANConstants.NETWORK_ON_MAIN_THREAD_ERROR);
                } else {
                    se.setErrorDetail(ANConstants.CONNECTION_ERROR);
                }
                se.setErrorCode(0);
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(se);
                }
            } finally {
                if (data != null && data.source != null) {
                    try {
                        data.source.close();
                    } catch (IOException ignored) {
                        ANLog.d("Unable to close source data");
                    }
                }
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
