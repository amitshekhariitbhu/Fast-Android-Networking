package com.androidnetworking.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.androidnetworking.common.Constants;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.androidnetworking.model.Progress;

import java.lang.ref.WeakReference;

/**
 * Created by amitshekhar on 24/05/16.
 */
public class UploadProgressHandler extends Handler {

    private final WeakReference<UploadProgressListener> mUploadProgressListenerWeakRef;

    public UploadProgressHandler(UploadProgressListener uploadProgressListener) {
        super(Looper.getMainLooper());
        mUploadProgressListenerWeakRef = new WeakReference<>(uploadProgressListener);
    }

    @Override
    public void handleMessage(Message msg) {
        final UploadProgressListener uploadProgressListener = mUploadProgressListenerWeakRef.get();
        switch (msg.what) {
            case Constants.UPDATE:
                if (uploadProgressListener != null) {
                    final Progress progress = (Progress) msg.obj;
                    uploadProgressListener.onProgress(progress.currentBytes, progress.totalBytes);
                }
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }
}
