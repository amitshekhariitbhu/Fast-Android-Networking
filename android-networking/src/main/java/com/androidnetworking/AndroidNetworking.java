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

package com.androidnetworking;

import android.content.Context;

import com.androidnetworking.common.AndroidNetworkingRequest;
import com.androidnetworking.common.Constants;
import com.androidnetworking.core.Core;
import com.androidnetworking.internal.AndroidNetworkingImageLoader;
import com.androidnetworking.internal.AndroidNetworkingOkHttp;
import com.androidnetworking.internal.AndroidNetworkingRequestQueue;
import com.androidnetworking.utils.Utils;

import okhttp3.OkHttpClient;

/**
 * Created by amitshekhar on 24/03/16.
 */

/**
 * AndroidNetworking entry point.
 * You must initialize this class before use. The simplest way is to just do
 * {#code AndroidNetworking.initialize(context)}.
 */
public class AndroidNetworking {

    /**
     * private constructor to prevent instantiation of this class
     */
    private AndroidNetworking() {
    }

    /**
     * Initializes AndroidNetworking with the default config.
     *
     * @param context The context
     */
    public static void initialize(Context context) {
        AndroidNetworkingOkHttp.setClientWithCache(context.getApplicationContext());
        AndroidNetworkingRequestQueue.initialize();
        AndroidNetworkingImageLoader.initialize();
    }

    /**
     * Initializes AndroidNetworking with the specified config.
     *
     * @param context      The context
     * @param okHttpClient The okHttpClient
     */
    public static void initialize(Context context, OkHttpClient okHttpClient) {
        if (okHttpClient != null && okHttpClient.cache() == null) {
            okHttpClient = okHttpClient.newBuilder().cache(Utils.getCache(context.getApplicationContext(), Constants.MAX_CACHE_SIZE, Constants.CACHE_DIR_NAME)).build();
        }
        AndroidNetworkingOkHttp.setClient(okHttpClient);
        AndroidNetworkingRequestQueue.initialize();
        AndroidNetworkingImageLoader.initialize();
    }

    /**
     * Method to make GET request
     *
     * @param url The url on which request is to be made
     * @return The GetRequestBuilder
     */
    public static AndroidNetworkingRequest.GetRequestBuilder get(String url) {
        return new AndroidNetworkingRequest.GetRequestBuilder(url);
    }

    /**
     * Method to make POST request
     *
     * @param url The url on which request is to be made
     * @return The PostRequestBuilder
     */
    public static AndroidNetworkingRequest.PostRequestBuilder post(String url) {
        return new AndroidNetworkingRequest.PostRequestBuilder(url);
    }

    /**
     * Method to make download request
     *
     * @param url      The url on which request is to be made
     * @param dirPath  The directory path on which file is to be saved
     * @param fileName The file name with which file is to be saved
     * @return The DownloadBuilder
     */
    public static AndroidNetworkingRequest.DownloadBuilder download(String url, String dirPath, String fileName) {
        return new AndroidNetworkingRequest.DownloadBuilder(url, dirPath, fileName);
    }

    /**
     * Method to make upload request
     *
     * @param url The url on which request is to be made
     * @return The MultiPartBuilder
     */
    public static AndroidNetworkingRequest.MultiPartBuilder upload(String url) {
        return new AndroidNetworkingRequest.MultiPartBuilder(url);
    }

    /**
     * Method to cancel requests with the given tag
     *
     * @param tag The tag with which requests are to be cancelled
     */
    public static void cancel(Object tag) {
        AndroidNetworkingRequestQueue.getInstance().cancelRequestWithGivenTag(tag);
    }

    /**
     * Shuts AndroidNetworking down
     */
    public static void shutDown() {
        Core.shutDown();
    }
}
