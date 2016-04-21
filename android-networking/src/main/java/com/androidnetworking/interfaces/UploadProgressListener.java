package com.androidnetworking.interfaces;

import com.androidnetworking.error.AndroidNetworkingError;

/**
 * Created by amitshekhar on 21/04/16.
 */
public interface UploadProgressListener<T> {
    void onProgress(long bytesUploaded, long totalBytes, boolean isCompleted);

    void onResponse(T response);

    void onError(AndroidNetworkingError error);
}
