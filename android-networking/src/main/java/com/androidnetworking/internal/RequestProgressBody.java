package com.androidnetworking.internal;

import com.androidnetworking.core.Core;
import com.androidnetworking.interfaces.UploadProgressListener;

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
    private final UploadProgressListener uploadProgressListener;
    private BufferedSink bufferedSink;

    public RequestProgressBody(RequestBody requestBody, UploadProgressListener uploadProgressListener) {
        this.requestBody = requestBody;
        this.uploadProgressListener = uploadProgressListener;
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
                if (uploadProgressListener != null) {
                    Core.getInstance().getExecutorSupplier().forMainThreadTasks().execute(new Runnable() {
                        @Override
                        public void run() {
                            uploadProgressListener.onProgress(bytesWritten, contentLength);
                        }
                    });
                }
            }
        };
    }
}
