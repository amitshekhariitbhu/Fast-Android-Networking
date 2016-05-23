package com.androidnetworking.interfaces;

/**
 * Created by amitshekhar on 30/03/16.
 */
public interface DownloadProgressListener {
    void onProgress(long bytesDownloaded, long totalBytes);
}
