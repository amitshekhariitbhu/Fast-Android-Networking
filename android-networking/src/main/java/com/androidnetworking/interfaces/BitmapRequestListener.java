package com.androidnetworking.interfaces;

import android.graphics.Bitmap;

import com.androidnetworking.error.AndroidNetworkingError;

/**
 * Created by amitshekhar on 23/05/16.
 */
public interface BitmapRequestListener {

    void onResponse(Bitmap response);

    void onError(AndroidNetworkingError error);

}
