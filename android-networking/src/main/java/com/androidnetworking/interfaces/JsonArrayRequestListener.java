package com.androidnetworking.interfaces;

import com.androidnetworking.error.AndroidNetworkingError;

import org.json.JSONArray;

/**
 * Created by amitshekhar on 23/05/16.
 */
public interface JSONArrayRequestListener {

    void onResponse(JSONArray response);

    void onError(AndroidNetworkingError error);

}
