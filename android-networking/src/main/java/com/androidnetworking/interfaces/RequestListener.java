package com.androidnetworking.interfaces;

import com.androidnetworking.error.AndroidNetworkingError;

/**
 * Created by amitshekhar on 08/04/16.
 */
public interface RequestListener<T> {
    void onResponse(T response);

    void onError(AndroidNetworkingError error);
}
