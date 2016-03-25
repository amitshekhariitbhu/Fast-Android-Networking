package com.androidnetworking.internal;

/**
 * Created by amitshekhar on 22/03/16.
 */

import android.support.annotation.NonNull;

import com.androidnetworking.common.AndroidNetworkingData;
import com.androidnetworking.error.AndroidNetworkingError;
import com.androidnetworking.requests.AndroidNetworkingRequest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;

import static com.androidnetworking.common.Method.DELETE;
import static com.androidnetworking.common.Method.GET;
import static com.androidnetworking.common.Method.HEAD;
import static com.androidnetworking.common.Method.PATCH;
import static com.androidnetworking.common.Method.POST;
import static com.androidnetworking.common.Method.PUT;

public class AndroidNetworkingOkHttp {

    public static final String HEADER_USER_AGENT = "User-Agent";

    private static OkHttpClient sHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public static void addNetworkInterceptor(@NonNull Interceptor interceptor) {
        sHttpClient = sHttpClient.newBuilder()
                .addNetworkInterceptor(interceptor)
                .build();
    }

    private static RequestBody convertBody(AndroidNetworkingRequest request, BufferedSource body) throws AndroidNetworkingError {
        if (body == null && request.getMethod() == DELETE) {
            return RequestBody.create(null, new byte[0]);
        }

        try {
            if (body == null) {
                body = new Buffer();
            }

            return RequestBody.create(MediaType.parse(request.getBodyContentType()), body.readByteArray());
        } catch (IOException ioe) {
            throw new AndroidNetworkingError(ioe);
        }
    }

    private static BufferedSource getBody(AndroidNetworkingRequest request) {
        if (request.getMethod() == GET || request.getMethod() == HEAD) {
            return null;
        }
        return request.getBody();
    }


    public static AndroidNetworkingData performNetworkRequest(AndroidNetworkingRequest<?> request) throws AndroidNetworkingError {
        AndroidNetworkingData data = new AndroidNetworkingData();
        Request okRequest = null;

//        sHttpClient = sHttpClient.newBuilder()
//                .cookieJar(new CookieJar() {
//                    @Override
//                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//
//                    }
//
//                    @Override
//                    public List<Cookie> loadForRequest(HttpUrl url) {
//                        return null;
//                    }
//                })
//                .build();

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

            BufferedSource body = getBody(request);
            switch (request.getMethod()) {
                case GET: {
                    okBuilder = okBuilder.get();
                    break;
                }
                case POST: {
                    okBuilder = okBuilder.post(convertBody(request, body));
                    break;
                }
                case PUT: {
                    okBuilder = okBuilder.put(convertBody(request, body));
                    break;
                }
                case DELETE: {
                    okBuilder = okBuilder.delete(convertBody(request, body));
                    break;
                }
                case HEAD: {
                    okBuilder = okBuilder.head();
                    break;
                }
                case PATCH: {
                    okBuilder = okBuilder.patch(convertBody(request, body));
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

            if (body != null) {
                body.close();
            }

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

    public static void removeNetworkInterceptor(@NonNull Interceptor interceptor) {
        OkHttpClient.Builder builder = sHttpClient.newBuilder();
        builder.networkInterceptors().remove(interceptor);

        sHttpClient = builder.build();
    }


}
