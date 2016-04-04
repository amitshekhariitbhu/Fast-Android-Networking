package com.androidnetworking.interfaces;

import com.androidnetworking.error.AndroidNetworkingError;

/**
 * Created by amitshekhar on 30/03/16.
 */
public interface DownloadProgressListener {
    void onProgress(long bytesDownloaded, long totalBytes, boolean isCompleted);

    void onError(AndroidNetworkingError error);
}
