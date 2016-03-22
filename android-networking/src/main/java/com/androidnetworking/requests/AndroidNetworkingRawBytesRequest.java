package com.androidnetworking.requests;

import com.androidnetworking.common.AndroidNetworkingData;
import com.androidnetworking.common.AndroidNetworkingResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.AndroidNetworkingError;

import java.io.IOException;

import okio.Okio;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class AndroidNetworkingRawBytesRequest extends AndroidNetworkingRequest<byte[]> {

    public AndroidNetworkingRawBytesRequest(@MethodRes int method, String url, Priority priority, Object tag, AndroidNetworkingResponse.SuccessListener<byte[]> successListener, AndroidNetworkingResponse.ErrorListener errorListener) {
        super(method, url, priority, tag, successListener, errorListener);
    }

    public AndroidNetworkingResponse<byte[]> parseResponse(AndroidNetworkingData data) {
        try {
            return AndroidNetworkingResponse.success(Okio.buffer(data.source).readByteArray());
        } catch (IOException e) {
            return AndroidNetworkingResponse.failed(new AndroidNetworkingError(e));
        }
    }

}