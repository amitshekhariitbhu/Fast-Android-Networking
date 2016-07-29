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

import android.content.Context;

/**
 * Created by amitshekhar on 10/06/16.
 */
public class RxAndroidNetworking {

    /**
     * Initializes RxAndroidNetworking with the default config.
     *
     * @param context The context
     */
    public static void initialize(Context context) {
        RxInternalNetworking.setClientWithCache(context.getApplicationContext());
    }

    /**
     * Method to make GET request
     *
     * @param url The url on which request is to be made
     * @return The GetRequestBuilder
     */
    public static RxANRequest.GetRequestBuilder get(String url) {
        return new RxANRequest.GetRequestBuilder(url);
    }

    /**
     * Method to make POST request
     *
     * @param url The url on which request is to be made
     * @return The PostRequestBuilder
     */
    public static RxANRequest.PostRequestBuilder post(String url) {
        return new RxANRequest.PostRequestBuilder(url);
    }
}
