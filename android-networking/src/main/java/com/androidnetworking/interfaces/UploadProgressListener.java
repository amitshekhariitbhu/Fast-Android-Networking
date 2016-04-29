package com.androidnetworking.interfaces;

/**
 * Created by amitshekhar on 21/04/16.
 */
public interface UploadProgressListener {
    void onProgress(long bytesUploaded, long totalBytes);
}
