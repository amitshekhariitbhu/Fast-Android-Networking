package com.androidnetworking;

import android.content.Context;

import com.androidnetworking.common.AndroidNetworkingRequest;
import com.androidnetworking.core.Core;
import com.androidnetworking.internal.AndroidNetworkingImageLoader;
import com.androidnetworking.internal.AndroidNetworkingOkHttp;
import com.androidnetworking.internal.AndroidNetworkingRequestQueue;

import okhttp3.OkHttpClient;

/**
 * Created by amitshekhar on 24/03/16.
 */
public class AndroidNetworking {

    private AndroidNetworking() {
    }

    public static void initialize(Context context) {
        AndroidNetworkingOkHttp.setClientWithCache(context.getApplicationContext());
        AndroidNetworkingRequestQueue.initialize();
        AndroidNetworkingImageLoader.initialize();
    }

    public static void initialize(OkHttpClient okHttpClient) {
        AndroidNetworkingOkHttp.setClient(okHttpClient);
        AndroidNetworkingRequestQueue.initialize();
        AndroidNetworkingImageLoader.initialize();
    }

    public static AndroidNetworkingRequest.GetRequestBuilder get(String url) {
        return new AndroidNetworkingRequest.GetRequestBuilder(url);
    }

    public static AndroidNetworkingRequest.PostRequestBuilder post(String url) {
        return new AndroidNetworkingRequest.PostRequestBuilder(url);
    }

    public static AndroidNetworkingRequest.DownloadBuilder download(String url, String dirPath, String fileName) {
        return new AndroidNetworkingRequest.DownloadBuilder(url, dirPath, fileName);
    }

    public static AndroidNetworkingRequest.MultiPartBuilder upload(String url) {
        return new AndroidNetworkingRequest.MultiPartBuilder(url);
    }

    public static void cancel(Object tag) {
        AndroidNetworkingRequestQueue.getInstance().cancelRequestWithGivenTag(tag);
    }

    public static void shutDown() {
        Core.shutDown();
    }
}
