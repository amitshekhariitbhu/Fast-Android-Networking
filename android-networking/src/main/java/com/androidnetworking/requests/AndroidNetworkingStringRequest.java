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
public class AndroidNetworkingStringRequest extends AndroidNetworkingRequest<String> {


    public AndroidNetworkingStringRequest(@MethodRes int method, String url, Priority priority, Object tag, AndroidNetworkingResponse.SuccessListener<String> successListener, AndroidNetworkingResponse.ErrorListener errorListener) {
        super(method, url, priority, tag, successListener, errorListener);
    }

    protected AndroidNetworkingResponse<String> parseResponse(AndroidNetworkingData data) {
        try {
            return AndroidNetworkingResponse.success(Okio.buffer(data.source).readUtf8());
        } catch (IOException e) {
            return AndroidNetworkingResponse.failed(new AndroidNetworkingError(e));
        }
    }

}
