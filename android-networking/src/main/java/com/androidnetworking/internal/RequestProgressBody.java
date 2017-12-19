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

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.androidnetworking.model.Progress;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by amitshekhar on 21/04/16.
 */
public class RequestProgressBody extends RequestBody {
    private final RequestBody requestBody;
    private BufferedSink bufferedSink;
    private UploadProgressHandler uploadProgressHandler;

    public RequestProgressBody(RequestBody requestBody, UploadProgressListener uploadProgressListener) {
        this.requestBody = requestBody;
        if (uploadProgressListener != null) {
            this.uploadProgressHandler = new UploadProgressHandler(uploadProgressListener);
        }
    }

    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(sink(sink));
        }
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long bytesWritten = 0L;
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                if (uploadProgressHandler != null) {
                    uploadProgressHandler.obtainMessage(ANConstants.UPDATE,
                            new Progress(bytesWritten, contentLength)).sendToTarget();
                }
            }
        };
    }
}