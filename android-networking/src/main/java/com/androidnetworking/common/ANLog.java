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

import android.util.Log;

/**
 * Created by amitshekhar on 01/06/16.
 */
public class ANLog {

    private static boolean IS_LOGGING_ENABLED = false;
    private static String TAG = ANConstants.ANDROID_NETWORKING;

    private ANLog() {

    }

    public static void enableLogging() {
        IS_LOGGING_ENABLED = true;
    }

    public static void disableLogging() {
        IS_LOGGING_ENABLED = false;
    }

    public static void setTag(String tag) {
        if (tag == null) {
            return;
        }
        TAG = tag;
    }

    public static void d(String message) {
        if (IS_LOGGING_ENABLED) {
            Log.d(TAG, message);
        }
    }

    public static void e(String message) {
        if (IS_LOGGING_ENABLED) {
            Log.e(TAG, message);
        }
    }

    public static void i(String message) {
        if (IS_LOGGING_ENABLED) {
            Log.i(TAG, message);
        }
    }

    public static void w(String message) {
        if (IS_LOGGING_ENABLED) {
            Log.w(TAG, message);
        }
    }

    public static void wtf(String message) {
        if (IS_LOGGING_ENABLED) {
            Log.wtf(TAG, message);
        }
    }

}
