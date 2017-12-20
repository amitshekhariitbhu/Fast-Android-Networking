/*
 *    Copyright (C) 2016 Amit Shekhar
 *    Copyright (C) 2011 Android Open Source Project
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidnetworking.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.model.Progress;

/**
 * Created by amitshekhar on 24/05/16.
 */
public class DownloadProgressHandler extends Handler {

    private final DownloadProgressListener mDownloadProgressListener;

    public DownloadProgressHandler(DownloadProgressListener downloadProgressListener) {
        super(Looper.getMainLooper());
        mDownloadProgressListener = downloadProgressListener;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ANConstants.UPDATE:
                if (mDownloadProgressListener != null) {
                    final Progress progress = (Progress) msg.obj;
                    mDownloadProgressListener.onProgress(progress.currentBytes, progress.totalBytes);
                }
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }
}
