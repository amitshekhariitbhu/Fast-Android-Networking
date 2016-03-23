package com.androidnetworking.runnables;

import android.util.Log;

import com.androidnetworking.common.AndroidNetworkingData;
import com.androidnetworking.common.AndroidNetworkingResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.core.Core;
import com.androidnetworking.error.AndroidNetworkingError;
import com.androidnetworking.internal.AndroidNetworkingOkHttp;
import com.androidnetworking.requests.AndroidNetworkingRequest;

import java.io.IOException;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class DataHunter implements Runnable {

    private static final String TAG = DataHunter.class.getSimpleName();
    private final Priority priority;
    public final int sequence;
    public final AndroidNetworkingRequest<?> request;

    public DataHunter(AndroidNetworkingRequest<?> request) {
        this.request = request;
        this.sequence = request.getSequenceNumber();
        this.priority = request.getPriority();
    }

    @Override
    public void run() {
        Log.d(TAG, "execution started for sequenceNumber: " + request.getSequenceNumber());
        AndroidNetworkingData data = null;
        try {
            data = AndroidNetworkingOkHttp.performNetworkRequest(request);
            if (data.isNotModified() && request.isResponseDelivered()) {
                request.finish();
                return;
            }
            request.setResponseDelivered(true);
            if (data.code >= 400) {
                AndroidNetworkingError error = new AndroidNetworkingError(data);
                error = request.parseNetworkError(error);
                deliverError(request, error);
                return;
            }

            AndroidNetworkingResponse<?> response = request.parseResponse(data);
            if (!response.isSuccess()) {
                deliverError(request, response.getError());
                return;
            }
            deliverResponse(request, response);
        } catch (AndroidNetworkingError se) {
            se = request.parseNetworkError(se);
            deliverError(request, se);
        } catch (Exception e) {
            AndroidNetworkingError se = new AndroidNetworkingError(e);
            deliverError(request, se);

        } finally {
            if (data != null && data.source != null) {
                try {
                    data.source.close();
                } catch (IOException ignored) {
                    Log.d(TAG, "Unable to close source data");
                }
            }
        }
        Log.d(TAG, "execution done for sequenceNumber: " + request.getSequenceNumber());
    }

    public Priority getPriority() {
        return priority;
    }

    private void deliverError(final AndroidNetworkingRequest request, final AndroidNetworkingError error) {
        Log.d(TAG, "Delivering failed response for " + request.getSequenceNumber());
        Core.getInstance().getExecutorSupplier().forMainThreadTasks().execute(new Runnable() {
            public void run() {
                request.deliverError(error);
                request.finish();
            }
        });
    }

    private void deliverResponse(final AndroidNetworkingRequest request, final AndroidNetworkingResponse response) {
        Log.d(TAG, "Delivering success response for " + request.getSequenceNumber());
        Core.getInstance().getExecutorSupplier().forMainThreadTasks().execute(new Runnable() {
            public void run() {
                request.deliverResponse(response);
                request.finish();
            }
        });
    }
}
