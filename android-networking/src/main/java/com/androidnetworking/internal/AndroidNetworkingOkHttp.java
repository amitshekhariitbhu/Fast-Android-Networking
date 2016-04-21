package com.androidnetworking.internal;

/**
 * Created by amitshekhar on 22/03/16.
 */

import android.support.annotation.NonNull;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.AndroidNetworkingData;
import com.androidnetworking.common.AndroidNetworkingRequest;
import com.androidnetworking.common.Constants;
import com.androidnetworking.core.Core;
import com.androidnetworking.error.AndroidNetworkingError;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import static com.androidnetworking.common.Method.DELETE;
import static com.androidnetworking.common.Method.GET;
import static com.androidnetworking.common.Method.HEAD;
import static com.androidnetworking.common.Method.PATCH;
import static com.androidnetworking.common.Method.POST;
import static com.androidnetworking.common.Method.PUT;

public class AndroidNetworkingOkHttp {

    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final int DOWNLOAD_CHUNK_SIZE = 2048;

    private static OkHttpClient sHttpClient = new OkHttpClient().newBuilder()
            .cache(Utils.getCache(AndroidNetworking.getContext(), Constants.MAX_CACHE_SIZE, Constants.CACHE_DIR_NAME))
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public static AndroidNetworkingData performSimpleRequest(AndroidNetworkingRequest request) throws AndroidNetworkingError {
        AndroidNetworkingData data = new AndroidNetworkingData();
        Request okRequest = null;

        try {

            Request.Builder okBuilder = new Request.Builder().url(request.getUrl());
            okBuilder.addHeader(HEADER_USER_AGENT, "Android");

            Headers requestHeaders = request.getHeaders();
            if (requestHeaders != null) {
                okBuilder.headers(requestHeaders);
                if (!requestHeaders.names().contains(HEADER_USER_AGENT)) {
                    okBuilder.addHeader(HEADER_USER_AGENT, "Android");
                }
            }

            switch (request.getMethod()) {
                case GET: {
                    okBuilder = okBuilder.get();
                    break;
                }
                case POST: {
                    okBuilder = okBuilder.post(request.getRequestBody());
                    break;
                }
                case PUT: {
                    okBuilder = okBuilder.put(request.getRequestBody());
                    break;
                }
                case DELETE: {
                    okBuilder = okBuilder.delete(request.getRequestBody());
                    break;
                }
                case HEAD: {
                    okBuilder = okBuilder.head();
                    break;
                }
                case PATCH: {
                    okBuilder = okBuilder.patch(request.getRequestBody());
                    break;
                }
            }

            boolean previousFollowing = sHttpClient.followRedirects();
            if (previousFollowing != request.isFollowingRedirects()) {
                sHttpClient = sHttpClient.newBuilder()
                        .followRedirects(request.isFollowingRedirects())
                        .build();
            }

            okRequest = okBuilder.build();
            Response okResponse = sHttpClient.newCall(okRequest).execute();

            if (previousFollowing != sHttpClient.followRedirects()) {
                sHttpClient = sHttpClient.newBuilder()
                        .followRedirects(request.isFollowingRedirects())
                        .build();
            }

            data.url = okResponse.request().url();
            data.code = okResponse.code();
            data.headers = okResponse.headers();
            data.source = okResponse.body().source();
            data.length = okResponse.body().contentLength();
        } catch (IOException ioe) {
            if (okRequest != null) {
                data.url = okRequest.url();
            }

            throw new AndroidNetworkingError(data, ioe);
        }

        return data;
    }

    public static AndroidNetworkingData performDownloadRequest(AndroidNetworkingRequest request) throws AndroidNetworkingError {
        AndroidNetworkingData data = new AndroidNetworkingData();
        Request okRequest = null;
        try {
            Request.Builder okBuilder = new Request.Builder().url(request.getUrl());
            okBuilder.addHeader(HEADER_USER_AGENT, "Android");

            Headers requestHeaders = request.getHeaders();
            if (requestHeaders != null) {
                okBuilder.headers(requestHeaders);
                if (!requestHeaders.names().contains(HEADER_USER_AGENT)) {
                    okBuilder.addHeader(HEADER_USER_AGENT, "Android");
                }
            }
            okBuilder = okBuilder.get();
            okRequest = okBuilder.build();
            Response okResponse = sHttpClient.newCall(okRequest).execute();

            data.url = okResponse.request().url();
            data.code = okResponse.code();
            data.headers = okResponse.headers();

            ResponseBody body = okResponse.body();
            data.length = body.contentLength();
            BufferedSource source = body.source();
            File file = new File(request.getDirPath() + File.separator + request.getFileName());
            BufferedSink sink = Okio.buffer(Okio.sink(file));
            long bytesRead = 0;
            while (source.read(sink.buffer(), DOWNLOAD_CHUNK_SIZE) != -1) {
                bytesRead += DOWNLOAD_CHUNK_SIZE;
                updateProgress(bytesRead, data.length, request.getDownloadProgressListener());
            }
            sink.writeAll(source);
            sink.close();
            updateCompletion(data.length, request.getDownloadProgressListener());
        } catch (IOException ioe) {
            if (okRequest != null) {
                data.url = okRequest.url();
            }
            throw new AndroidNetworkingError(data, ioe);
        }

        return data;
    }

    public static AndroidNetworkingData performUploadRequest(AndroidNetworkingRequest request) throws AndroidNetworkingError {
        AndroidNetworkingData data = new AndroidNetworkingData();
        Request okRequest = null;
        try {
            Request.Builder okBuilder = new Request.Builder().url(request.getUrl());
            okBuilder.addHeader(HEADER_USER_AGENT, "Android");

            Headers requestHeaders = request.getHeaders();
            if (requestHeaders != null) {
                okBuilder.headers(requestHeaders);
                if (!requestHeaders.names().contains(HEADER_USER_AGENT)) {
                    okBuilder.addHeader(HEADER_USER_AGENT, "Android");
                }
            }
            okBuilder = okBuilder.post(new RequestProgressBody(request.getMultiPartRequestBody(), request.getUploadProgressListener()));
            okRequest = okBuilder.build();
            Response okResponse = sHttpClient.newCall(okRequest).execute();

            data.url = okResponse.request().url();
            data.code = okResponse.code();
            data.headers = okResponse.headers();
            data.source = okResponse.body().source();
            data.length = okResponse.body().contentLength();
        } catch (IOException ioe) {
            if (okRequest != null) {
                data.url = okRequest.url();
            }

            throw new AndroidNetworkingError(data, ioe);
        }
        return data;
    }


    public static void updateProgress(final long bytesDownloaded, final long totalBytes, final DownloadProgressListener downloadProgressListener) {
        if (downloadProgressListener != null) {
            Core.getInstance().getExecutorSupplier().forMainThreadTasks().execute(new Runnable() {
                @Override
                public void run() {
                    downloadProgressListener.onProgress(bytesDownloaded, totalBytes, false);
                }
            });
        }
    }

    public static void updateCompletion(final long totalBytes, final DownloadProgressListener downloadProgressListener) {
        if (downloadProgressListener != null) {
            Core.getInstance().getExecutorSupplier().forMainThreadTasks().execute(new Runnable() {
                @Override
                public void run() {
                    downloadProgressListener.onProgress(totalBytes, totalBytes, true);
                }
            });
        }
    }

    public static void addNetworkInterceptor(@NonNull Interceptor interceptor) {
        sHttpClient = sHttpClient.newBuilder()
                .addNetworkInterceptor(interceptor)
                .build();
    }

    public static void removeNetworkInterceptor(@NonNull Interceptor interceptor) {
        OkHttpClient.Builder builder = sHttpClient.newBuilder();
        builder.networkInterceptors().remove(interceptor);
        sHttpClient = builder.build();
    }


}
