package com.androidnetworking.requests;

import com.androidnetworking.common.AndroidNetworkingData;
import com.androidnetworking.common.AndroidNetworkingResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.AndroidNetworkingError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okio.Okio;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class AndroidNetworkingObjectRequest extends AndroidNetworkingRequest<JSONObject> {


    public AndroidNetworkingObjectRequest(@MethodRes int method, String url, Priority priority, Object tag, AndroidNetworkingResponse.SuccessListener<JSONObject> successListener, AndroidNetworkingResponse.ErrorListener errorListener) {
        super(method, url, priority, tag, successListener, errorListener);
    }

    public AndroidNetworkingResponse<JSONObject> parseResponse(AndroidNetworkingData data) {
        try {
            JSONObject json = new JSONObject(Okio.buffer(data.source).readUtf8());
            return AndroidNetworkingResponse.success(json);
        } catch (JSONException | IOException e) {
            return AndroidNetworkingResponse.failed(new AndroidNetworkingError(e));
        }
    }

}
