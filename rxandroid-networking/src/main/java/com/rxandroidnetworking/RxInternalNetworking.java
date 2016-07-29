package com.rxandroidnetworking;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Build;
import android.os.NetworkOnMainThreadException;

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.common.ANData;
import com.androidnetworking.common.ANLog;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.ConnectionClassManager;
import com.androidnetworking.common.RequestType;
import com.androidnetworking.core.Core;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.internal.ResponseProgressBody;
import com.androidnetworking.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
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
import rx.Scheduler;
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

    private static final String USER_AGENT = "User-Agent";

    private static OkHttpClient sHttpClient = getClient();

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

    public static <T> Observable<T> generateSimpleObservable(RxANRequest request) {
        Request okHttpRequest = null;
        Request.Builder builder = new Request.Builder().url(request.getUrl());
        if (request.getUserAgent() != null) {
            builder.addHeader(USER_AGENT, request.getUserAgent());
        }
        Headers requestHeaders = request.getHeaders();
        if (requestHeaders != null) {
            builder.headers(requestHeaders);
            if (request.getUserAgent() != null && !requestHeaders.names().contains(USER_AGENT)) {
                builder.addHeader(USER_AGENT, request.getUserAgent());
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
            request.setCall(request.getOkHttpClient().newBuilder().cache(sHttpClient.cache()).build().newCall(okHttpRequest));
        } else {
            request.setCall(sHttpClient.newCall(okHttpRequest));
        }
        ANLog.d("call generated successfully for simple observale");
        Observable<T> observable = Observable.create(new ANOnSubscribe<T>(request));
        return observable;
    }

    public static Observable generateDownloadObservable(final RxANRequest request) {
        Request okHttpRequest = null;
        Request.Builder builder = new Request.Builder().url(request.getUrl());
        if (request.getUserAgent() != null) {
            builder.addHeader(USER_AGENT, request.getUserAgent());
        }
        Headers requestHeaders = request.getHeaders();
        if (requestHeaders != null) {
            builder.headers(requestHeaders);
            if (request.getUserAgent() != null && !requestHeaders.names().contains(USER_AGENT)) {
                builder.addHeader(USER_AGENT, request.getUserAgent());
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
        Observable observable = Observable.create(new ANOnSubscribe(request));
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
                    sendAnalytics(request.getAnalyticsListener(), timeTaken, (request.getRequestBody() != null && request.getRequestBody().contentLength() != 0) ? request.getRequestBody().contentLength() : -1, data.length, false);
                } else if (request.getAnalyticsListener() != null) {
                    if (okResponse.networkResponse() == null) {
                        sendAnalytics(request.getAnalyticsListener(), timeTaken, 0, 0, true);
                    } else {
                        sendAnalytics(request.getAnalyticsListener(), timeTaken, (request.getRequestBody() != null && request.getRequestBody().contentLength() != 0) ? request.getRequestBody().contentLength() : -1, 0, true);
                    }
                }
                if (data.code == 304) {
                    ANLog.d("error code 304 simple observable");
                } else if (data.code >= 400) {
                    ANError ANError = new ANError(data);
                    ANError = request.parseNetworkError(ANError);
                    ANError.setErrorCode(data.code);
                    ANError.setErrorDetail(ANConstants.RESPONSE_FROM_SERVER_ERROR);
                    if (!subscriber.isUnsubscribed()) {
                        ANLog.d("delivering error to subscriber from simple observable");
                        subscriber.onError(ANError);
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
                ANError se = new ANError(data, ioe);
                se = request.parseNetworkError(se);
                se.setErrorDetail(ANConstants.CONNECTION_ERROR);
                se.setErrorCode(0);
                if (!subscriber.isUnsubscribed()) {
                    ANLog.d("delivering error to subscriber from simple observable");
                    subscriber.onError(se);
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
                    sendAnalytics(request.getAnalyticsListener(), timeTaken, -1, data.length, false);
                } else if (request.getAnalyticsListener() != null) {
                    sendAnalytics(request.getAnalyticsListener(), timeTaken, -1, 0, true);
                }
                if (data.code >= 400) {
                    ANError ANError = new ANError();
                    ANError = request.parseNetworkError(ANError);
                    ANError.setErrorCode(data.code);
                    ANError.setErrorDetail(ANConstants.RESPONSE_FROM_SERVER_ERROR);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(ANError);
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
                ANError se = new ANError(data, ioe);
                try {
                    File destinationFile = new File(request.getDirPath() + File.separator + request.getFileName());
                    if (destinationFile.exists()) {
                        destinationFile.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!subscriber.isUnsubscribed()) {
                    ANLog.d("delivering error to subscriber from simple observable");
                    subscriber.onError(se);
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

    private static void sendAnalytics(final AnalyticsListener analyticsListener, final long timeTakenInMillis, final long bytesSent, final long bytesReceived, final boolean isFromCache) {
        Core.getInstance().getExecutorSupplier().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
                if (analyticsListener != null) {
                    analyticsListener.onReceived(timeTakenInMillis, bytesSent, bytesReceived, isFromCache);
                }
            }
        });
    }
}
