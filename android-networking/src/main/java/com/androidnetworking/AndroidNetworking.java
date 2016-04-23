package com.androidnetworking;

import android.content.Context;

import com.androidnetworking.common.AndroidNetworkingRequest;
import com.androidnetworking.internal.AndroidNetworkingImageLoader;
import com.androidnetworking.internal.AndroidNetworkingRequestQueue;

/**
 * Created by amitshekhar on 24/03/16.
 */
public class AndroidNetworking {

    private static Context mContext;

    private AndroidNetworking() {
    }

    public static void initialize(Context context) {
        mContext = context.getApplicationContext();
        AndroidNetworkingRequestQueue.initialize();
        AndroidNetworkingImageLoader.initialize();
    }

    public static Context getContext() {
        return mContext;
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

    public static AndroidNetworkingRequest.MultiPartBuilder uploadMultipart(String url) {
        return new AndroidNetworkingRequest.MultiPartBuilder(url);
    }
}
