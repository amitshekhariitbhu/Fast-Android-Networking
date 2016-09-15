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

import com.androidnetworking.error.ANError;

import okhttp3.Response;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class ANResponse<T> {

    private final T mResult;

    private final ANError mANError;

    private Response response;

    public static <T> ANResponse<T> success(T result) {
        return new ANResponse<>(result);
    }

    public static <T> ANResponse<T> failed(ANError anError) {
        return new ANResponse<>(anError);
    }

    public ANResponse(T result) {
        this.mResult = result;
        this.mANError = null;
    }

    public ANResponse(ANError anError) {
        this.mResult = null;
        this.mANError = anError;
    }

    public T getResult() {
        return mResult;
    }

    public boolean isSuccess() {
        return mANError == null;
    }

    public ANError getError() {
        return mANError;
    }

    public void setOkHttpResponse(Response response) {
        this.response = response;
    }

    public Response getOkHttpResponse() {
        return response;
    }

}
