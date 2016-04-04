package com.androidnetworking.interfaces;

/**
 * Created by amitshekhar on 30/03/16.
 */
public interface ProgressListener {
    void onProgress(long totalBytesDownloaded, long totalLength, boolean completed);
}
