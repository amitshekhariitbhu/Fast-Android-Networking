package com.androidnetworking.interfaces;

import com.androidnetworking.error.AndroidNetworkingError;

/**
 * Created by amitshekhar on 29/04/16.
 */
public interface DownloadListener {

    void onDownloadComplete();

    void onError(AndroidNetworkingError error);
}
