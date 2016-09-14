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

package com.androidnetworking.internal;

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.common.ANLog;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.common.RESPONSE;
import com.androidnetworking.core.Core;
import com.androidnetworking.error.ANError;

import okhttp3.Response;

import static com.androidnetworking.common.RequestType.DOWNLOAD;
import static com.androidnetworking.common.RequestType.MULTIPART;
import static com.androidnetworking.common.RequestType.SIMPLE;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class InternalRunnable implements Runnable {

    private final Priority priority;
    public final int sequence;
    public final ANRequest request;

    public InternalRunnable(ANRequest request) {
        this.request = request;
        this.sequence = request.getSequenceNumber();
        this.priority = request.getPriority();
    }

    @Override
    public void run() {
        ANLog.d("execution started : " + request.toString());
        switch (request.getRequestType()) {
            case SIMPLE:
                executeSimpleRequest();
                break;
            case DOWNLOAD:
                executeDownloadRequest();
                break;
            case MULTIPART:
                executeUploadRequest();
                break;
        }
        ANLog.d("execution done : " + request.toString());
    }

    private void executeSimpleRequest() {
        Response okHttpResponse = null;
        try {
            okHttpResponse = InternalNetworking.performSimpleRequest(request);

            if (okHttpResponse == null) {
                ANError anError = new ANError();
                anError = request.parseNetworkError(anError);
                anError.setErrorDetail(ANConstants.CONNECTION_ERROR);
                anError.setErrorCode(0);
                deliverError(request, anError);
                return;
            }

            if (request.getResponseAs() == RESPONSE.OK_HTTP_RESPONSE) {
                request.deliverOkHttpResponse(okHttpResponse);
                return;
            }
            if (okHttpResponse.code() >= 400) {
                ANError anError = new ANError(okHttpResponse);
                anError = request.parseNetworkError(anError);
                anError.setErrorCode(okHttpResponse.code());
                anError.setErrorDetail(ANConstants.RESPONSE_FROM_SERVER_ERROR);
                deliverError(request, anError);
                return;
            }

            ANResponse response = request.parseResponse(okHttpResponse);
            if (!response.isSuccess()) {
                deliverError(request, response.getError());
                return;
            }
            request.deliverResponse(response);
        } catch (ANError se) {
            se = request.parseNetworkError(se);
            se.setErrorDetail(ANConstants.CONNECTION_ERROR);
            se.setErrorCode(0);
            deliverError(request, se);
        } catch (Exception e) {
            ANError se = new ANError(e);
            se.setErrorDetail(ANConstants.CONNECTION_ERROR);
            se.setErrorCode(0);
            deliverError(request, se);

        } finally {
            if (request.getResponseAs() != RESPONSE.OK_HTTP_RESPONSE && okHttpResponse != null && okHttpResponse.body() != null && okHttpResponse.body().source() != null) {
                try {
                    okHttpResponse.body().source().close();
                } catch (Exception e) {
                    ANLog.d("Unable to close source data");
                }
            }
        }
    }

    private void executeDownloadRequest() {
        Response okHttpResponse = null;
        try {
            okHttpResponse = InternalNetworking.performDownloadRequest(request);
            if (okHttpResponse == null) {
                ANError anError = new ANError();
                anError = request.parseNetworkError(anError);
                anError.setErrorDetail(ANConstants.CONNECTION_ERROR);
                anError.setErrorCode(0);
                deliverError(request, anError);
                return;
            }
            if (okHttpResponse.code() >= 400) {
                ANError anError = new ANError();
                anError = request.parseNetworkError(anError);
                anError.setErrorCode(okHttpResponse.code());
                anError.setErrorDetail(ANConstants.RESPONSE_FROM_SERVER_ERROR);
                deliverError(request, anError);
                return;
            }
            request.updateDownloadCompletion();
        } catch (ANError se) {
            se.setErrorDetail(ANConstants.CONNECTION_ERROR);
            se.setErrorCode(0);
            deliverError(request, se);
        } catch (Exception e) {
            ANError se = new ANError(e);
            se.setErrorDetail(ANConstants.CONNECTION_ERROR);
            se.setErrorCode(0);
            deliverError(request, se);
        }
    }

    private void executeUploadRequest() {
        Response okHttpResponse = null;
        try {
            okHttpResponse = InternalNetworking.performUploadRequest(request);

            if (okHttpResponse == null) {
                ANError anError = new ANError();
                anError = request.parseNetworkError(anError);
                anError.setErrorDetail(ANConstants.CONNECTION_ERROR);
                anError.setErrorCode(0);
                deliverError(request, anError);
                return;
            }

            if (request.getResponseAs() == RESPONSE.OK_HTTP_RESPONSE) {
                request.deliverOkHttpResponse(okHttpResponse);
                return;
            }

            if (okHttpResponse.code() >= 400) {
                ANError anError = new ANError(okHttpResponse);
                anError = request.parseNetworkError(anError);
                anError.setErrorCode(okHttpResponse.code());
                anError.setErrorDetail(ANConstants.RESPONSE_FROM_SERVER_ERROR);
                deliverError(request, anError);
                return;
            }
            ANResponse response = request.parseResponse(okHttpResponse);
            if (!response.isSuccess()) {
                deliverError(request, response.getError());
                return;
            }
            request.deliverResponse(response);
        } catch (ANError se) {
            se = request.parseNetworkError(se);
            se.setErrorDetail(ANConstants.CONNECTION_ERROR);
            se.setErrorCode(0);
            deliverError(request, se);
        } catch (Exception e) {
            ANError se = new ANError(e);
            se.setErrorDetail(ANConstants.CONNECTION_ERROR);
            se.setErrorCode(0);
            deliverError(request, se);
        } finally {
            if (request.getResponseAs() != RESPONSE.OK_HTTP_RESPONSE && okHttpResponse != null && okHttpResponse.body() != null && okHttpResponse.body().source() != null) {
                try {
                    okHttpResponse.body().source().close();
                } catch (Exception e) {
                    ANLog.d("Unable to close source data");
                }
            }
        }
    }

    public Priority getPriority() {
        return priority;
    }

    private void deliverError(final ANRequest request, final ANError anError) {
        Core.getInstance().getExecutorSupplier().forMainThreadTasks().execute(new Runnable() {
            public void run() {
                request.deliverError(anError);
                request.finish();
            }
        });
    }
}
