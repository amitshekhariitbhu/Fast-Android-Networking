package com.androidnetworking.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.androidnetworking.common.Constants;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.model.Progress;

import java.lang.ref.WeakReference;

/**
 * Created by amitshekhar on 24/05/16.
 */
public class DownloadProgressHandler extends Handler {

    private final WeakReference<DownloadProgressListener> mDownloadProgressListenerWeakRef;

    public DownloadProgressHandler(DownloadProgressListener downloadProgressListener) {
        super(Looper.getMainLooper());
        mDownloadProgressListenerWeakRef = new WeakReference<>(downloadProgressListener);
    }

    @Override
    public void handleMessage(Message msg) {
        final DownloadProgressListener downloadProgressListener = mDownloadProgressListenerWeakRef.get();
        switch (msg.what) {
            case Constants.UPDATE:
                if (downloadProgressListener != null) {
                    final Progress progress = (Progress) msg.obj;
                    downloadProgressListener.onProgress(progress.currentBytes, progress.totalBytes);
                }
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }
}
