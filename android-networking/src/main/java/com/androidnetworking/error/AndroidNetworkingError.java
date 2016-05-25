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

package com.androidnetworking.error;

import com.androidnetworking.common.AndroidNetworkingData;
import com.androidnetworking.common.Constants;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class AndroidNetworkingError extends Exception {

    private AndroidNetworkingData data;

    private String errorBody;

    private int errorCode = 0;

    private String errorDetail;

    public AndroidNetworkingError() {
    }

    public AndroidNetworkingError(AndroidNetworkingData data) {
        this.data = data;
    }

    public AndroidNetworkingError(String message) {
        super(message);
    }

    public AndroidNetworkingError(String message, AndroidNetworkingData data) {
        super(message);
        this.data = data;
    }

    public AndroidNetworkingError(String message, Throwable throwable) {
        super(message, throwable);
    }

    public AndroidNetworkingError(String message, AndroidNetworkingData data, Throwable throwable) {
        super(message, throwable);
        this.data = data;
    }

    public AndroidNetworkingError(AndroidNetworkingData data, Throwable throwable) {
        super(throwable);
        this.data = data;
    }

    public AndroidNetworkingError(Throwable throwable) {
        super(throwable);
    }

    public AndroidNetworkingData getData() {
        return data;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public String getErrorDetail() {
        return this.errorDetail;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setCancellationMessageInError() {
        this.errorDetail = Constants.REQUEST_CANCELLED_ERROR;
    }

    public String getErrorBody() {
        return errorBody;
    }

    public void setErrorBody(String errorBody) {
        this.errorBody = errorBody;
    }

}
