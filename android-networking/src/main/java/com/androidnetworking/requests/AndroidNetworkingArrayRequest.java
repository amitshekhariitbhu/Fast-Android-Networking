package com.androidnetworking.requests;

import com.androidnetworking.common.AndroidNetworkingData;
import com.androidnetworking.common.AndroidNetworkingResponse;
import com.androidnetworking.common.MethodRes;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.AndroidNetworkingError;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okio.Okio;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class AndroidNetworkingArrayRequest extends AndroidNetworkingRequest<JSONArray> {

    public AndroidNetworkingArrayRequest(@MethodRes int method, String url, Priority priority, Object tag, AndroidNetworkingResponse.SuccessListener<JSONArray> successListener, AndroidNetworkingResponse.ErrorListener errorListener) {
        super(method, url, priority, tag, successListener, errorListener);
    }

    public AndroidNetworkingResponse<JSONArray> parseResponse(AndroidNetworkingData data) {
        try {
            JSONArray json = new JSONArray(Okio.buffer(data.source).readUtf8());
            return AndroidNetworkingResponse.success(json);
        } catch (JSONException | IOException e) {
            return AndroidNetworkingResponse.failed(new AndroidNetworkingError(e));
        }
    }

}
