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

package com.androidnetworking.common;

/**
 * Created by amitshekhar on 29/03/16.
 */
public final class ANConstants {
    public static final int MAX_CACHE_SIZE = 10 * 1024 * 1024;
    public static final int UPDATE = 0x01;
    public static final String CACHE_DIR_NAME = "cache_an";
    public static final String CONNECTION_ERROR = "connectionError";
    public static final String RESPONSE_FROM_SERVER_ERROR = "responseFromServerError";
    public static final String REQUEST_CANCELLED_ERROR = "requestCancelledError";
    public static final String PARSE_ERROR = "parseError";
    public static final String PREFETCH = "prefetch";
    public static final String FAST_ANDROID_NETWORKING = "FastAndroidNetworking";
    public static final String USER_AGENT = "User-Agent";
    public static final String SUCCESS = "success";
    public static final String OPTIONS = "OPTIONS";
}
