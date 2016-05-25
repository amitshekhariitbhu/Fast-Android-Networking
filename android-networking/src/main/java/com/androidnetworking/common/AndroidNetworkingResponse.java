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

package com.androidnetworking.common;

import com.androidnetworking.error.AndroidNetworkingError;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class AndroidNetworkingResponse<T> {

    private final T mResult;

    private final AndroidNetworkingError mError;

    public static <T> AndroidNetworkingResponse<T> success(T result) {
        return new AndroidNetworkingResponse<>(result);
    }

    public static <T> AndroidNetworkingResponse<T> failed(AndroidNetworkingError error) {
        return new AndroidNetworkingResponse<>(error);
    }

    private AndroidNetworkingResponse(T result) {
        this.mResult = result;
        this.mError = null;
    }

    private AndroidNetworkingResponse(AndroidNetworkingError error) {
        this.mResult = null;
        this.mError = error;
        this.mError.setErrorCode(0);
        this.mError.setErrorDetail(Constants.PARSE_ERROR);
    }

    public T getResult() {
        return mResult;
    }

    public boolean isSuccess() {
        return mError == null;
    }

    public AndroidNetworkingError getError() {
        return mError;
    }

}
