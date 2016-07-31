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
import com.androidnetworking.common.ANData;
import com.androidnetworking.common.ANLog;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.core.Core;
import com.androidnetworking.error.ANError;

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
                goForSimpleRequest();
                break;
            case DOWNLOAD:
                goForDownloadRequest();
                break;
            case MULTIPART:
                goForUploadRequest();
                break;
        }
        ANLog.d("execution done : " + request.toString());
    }

    private void goForSimpleRequest() {
        ANData data = null;
        try {
            data = InternalNetworking.performSimpleRequest(request);
            if (data.code == 304) {
                request.finish();
                return;
            }
            if (data.code >= 400) {
                ANError anError = new ANError(data);
                anError = request.parseNetworkError(anError);
                anError.setErrorCode(data.code);
                anError.setErrorDetail(ANConstants.RESPONSE_FROM_SERVER_ERROR);
                deliverError(request, anError);
                return;
            }

            ANResponse response = request.parseResponse(data);
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
            if (data != null && data.body != null && data.body.source() != null) {
                try {
                    data.body.source().close();
                } catch (Exception e) {
                    ANLog.d("Unable to close source data");
                }
            }
        }
    }

    private void goForDownloadRequest() {
        ANData data = null;
        try {
            data = InternalNetworking.performDownloadRequest(request);
            if (data.code >= 400) {
                ANError anError = new ANError();
                anError = request.parseNetworkError(anError);
                anError.setErrorCode(data.code);
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

    private void goForUploadRequest() {
        ANData data = null;
        try {
            data = InternalNetworking.performUploadRequest(request);
            if (data.code == 304) {
                request.finish();
                return;
            }
            if (data.code >= 400) {
                ANError anError = new ANError(data);
                anError = request.parseNetworkError(anError);
                anError.setErrorCode(data.code);
                anError.setErrorDetail(ANConstants.RESPONSE_FROM_SERVER_ERROR);
                deliverError(request, anError);
                return;
            }
            ANResponse response = request.parseResponse(data);
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
            if (data != null && data.body != null && data.body.source() != null) {
                try {
                    data.body.source().close();
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
