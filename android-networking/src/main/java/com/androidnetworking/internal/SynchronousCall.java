/*
 *
 *  *    Copyright (C) 2016 Amit Shekhar
 *  *    Copyright (C) 2011 Android Open Source Project
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package com.androidnetworking.internal;

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.common.ANLog;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.ResponseType;
import com.androidnetworking.error.ANError;

import okhttp3.Response;

import static com.androidnetworking.common.RequestType.DOWNLOAD;
import static com.androidnetworking.common.RequestType.MULTIPART;
import static com.androidnetworking.common.RequestType.SIMPLE;

/**
 * Created by amitshekhar on 14/09/16.
 */
public final class SynchronousCall {

    private SynchronousCall() {

    }

    public static <T> ANResponse<T> getResponse(ANRequest request) {
        switch (request.getRequestType()) {
            case SIMPLE:
                return executeSimpleRequest(request);
            case DOWNLOAD:
                return executeDownloadRequest(request);
            case MULTIPART:
                return executeUploadRequest(request);
        }
        return new ANResponse<>(new ANError());
    }


    private static <T> ANResponse<T> executeSimpleRequest(ANRequest request) {
        Response okHttpResponse = null;
        try {
            okHttpResponse = InternalNetworking.performSimpleRequest(request);

            if (okHttpResponse == null) {
                ANError anError = new ANError();
                anError = request.parseNetworkError(anError);
                anError.setErrorDetail(ANConstants.CONNECTION_ERROR);
                anError.setErrorCode(0);
                return new ANResponse(anError);
            }

            if (request.getResponseAs() == ResponseType.OK_HTTP_RESPONSE) {
                return new ANResponse(okHttpResponse);
            }
            if (okHttpResponse.code() >= 400) {
                ANError anError = new ANError(okHttpResponse);
                anError = request.parseNetworkError(anError);
                anError.setErrorCode(okHttpResponse.code());
                anError.setErrorDetail(ANConstants.RESPONSE_FROM_SERVER_ERROR);
                return new ANResponse(anError);
            }
            return request.parseResponse(okHttpResponse);
        } catch (ANError se) {
            se = request.parseNetworkError(se);
            se.setErrorDetail(ANConstants.CONNECTION_ERROR);
            se.setErrorCode(0);
            return new ANResponse(se);
        } catch (Exception e) {
            ANError se = new ANError(e);
            se.setErrorDetail(ANConstants.CONNECTION_ERROR);
            se.setErrorCode(0);
            return new ANResponse(se);
        } finally {
            if (request.getResponseAs() != ResponseType.OK_HTTP_RESPONSE &&
                    okHttpResponse != null && okHttpResponse.body() != null &&
                    okHttpResponse.body().source() != null) {
                try {
                    okHttpResponse.body().source().close();
                } catch (Exception e) {
                    ANLog.d("Unable to close source data");
                }
            }
        }
    }

    private static <T> ANResponse<T> executeDownloadRequest(ANRequest request) {
        Response okHttpResponse;
        try {
            okHttpResponse = InternalNetworking.performDownloadRequest(request);
            if (okHttpResponse == null) {
                ANError anError = new ANError();
                anError = request.parseNetworkError(anError);
                anError.setErrorDetail(ANConstants.CONNECTION_ERROR);
                anError.setErrorCode(0);
                return new ANResponse(anError);
            }
            if (okHttpResponse.code() >= 400) {
                ANError anError = new ANError();
                anError = request.parseNetworkError(anError);
                anError.setErrorCode(okHttpResponse.code());
                anError.setErrorDetail(ANConstants.RESPONSE_FROM_SERVER_ERROR);
                return new ANResponse(anError);
            }
            return new ANResponse("success");
        } catch (ANError se) {
            se.setErrorDetail(ANConstants.CONNECTION_ERROR);
            se.setErrorCode(0);
            return new ANResponse(se);
        } catch (Exception e) {
            ANError se = new ANError(e);
            se.setErrorDetail(ANConstants.CONNECTION_ERROR);
            se.setErrorCode(0);
            return new ANResponse(se);
        }
    }

    private static <T> ANResponse<T> executeUploadRequest(ANRequest request) {
        Response okHttpResponse = null;
        try {
            okHttpResponse = InternalNetworking.performUploadRequest(request);

            if (okHttpResponse == null) {
                ANError anError = new ANError();
                anError = request.parseNetworkError(anError);
                anError.setErrorDetail(ANConstants.CONNECTION_ERROR);
                anError.setErrorCode(0);
                return new ANResponse<T>(anError);
            }

            if (request.getResponseAs() == ResponseType.OK_HTTP_RESPONSE) {
                return new ANResponse(okHttpResponse);
            }

            if (okHttpResponse.code() >= 400) {
                ANError anError = new ANError(okHttpResponse);
                anError = request.parseNetworkError(anError);
                anError.setErrorCode(okHttpResponse.code());
                anError.setErrorDetail(ANConstants.RESPONSE_FROM_SERVER_ERROR);
                return new ANResponse<T>(anError);
            }
            return request.parseResponse(okHttpResponse);
        } catch (ANError se) {
            se = request.parseNetworkError(se);
            se.setErrorDetail(ANConstants.CONNECTION_ERROR);
            se.setErrorCode(0);
            return new ANResponse(se);
        } catch (Exception e) {
            ANError se = new ANError(e);
            se.setErrorDetail(ANConstants.CONNECTION_ERROR);
            se.setErrorCode(0);
            return new ANResponse(se);
        } finally {
            if (request.getResponseAs() != ResponseType.OK_HTTP_RESPONSE &&
                    okHttpResponse != null && okHttpResponse.body() != null &&
                    okHttpResponse.body().source() != null) {
                try {
                    okHttpResponse.body().source().close();
                } catch (Exception e) {
                    ANLog.d("Unable to close source data");
                }
            }
        }
    }
}
