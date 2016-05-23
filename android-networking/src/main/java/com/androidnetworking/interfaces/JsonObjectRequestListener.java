package com.androidnetworking.interfaces;

import com.androidnetworking.error.AndroidNetworkingError;

import org.json.JSONObject;

/**
 * Created by amitshekhar on 23/05/16.
 */
public interface JSONObjectRequestListener {

    void onResponse(JSONObject response);

    void onError(AndroidNetworkingError error);

}
