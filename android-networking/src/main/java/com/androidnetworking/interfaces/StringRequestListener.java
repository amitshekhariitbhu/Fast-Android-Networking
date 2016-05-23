package com.androidnetworking.interfaces;

import com.androidnetworking.error.AndroidNetworkingError;

/**
 * Created by amitshekhar on 23/05/16.
 */
public interface StringRequestListener {

    void onResponse(String response);

    void onError(AndroidNetworkingError error);

}
