package com.androidnetworking.requests;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.androidnetworking.common.AndroidNetworkingData;
import com.androidnetworking.common.AndroidNetworkingResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.AndroidNetworkingError;
import com.androidnetworking.internal.AndroidNetworkingRequestQueue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.Future;

import okhttp3.Headers;
import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by amitshekhar on 22/03/16.
 */
public abstract class AndroidNetworkingRequest<T> {

    private final static String TAG = AndroidNetworkingRequest.class.getSimpleName();

    private static final String PARAMS_ENCODING = "UTF-8";

    public interface Method {
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int PATCH = 5;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Method.GET, Method.POST, Method.PUT, Method.DELETE, Method.HEAD, Method.PATCH})
    public @interface MethodRes {
    }

    private Headers mHeaders;

    private final
    @MethodRes
    int mMethod;

    private AndroidNetworkingResponse.SuccessListener<T> mSuccessListener;

    private AndroidNetworkingResponse.ErrorListener mErrorListener;

    private boolean mResponseDelivered = false;


    private Priority mPriority;
    private String mUrl;
    private int sequenceNumber;
    private Future<?> future;
    private AndroidNetworkingRequestQueue mAndroidNetworkingRequestQueue;
    private Object mTag;

    public AndroidNetworkingRequest(@MethodRes int method, String url, Priority priority, Object tag, AndroidNetworkingResponse.SuccessListener<T> successListener, AndroidNetworkingResponse.ErrorListener errorListener) {
        this.mMethod = method;
        this.mSuccessListener = successListener;
        this.mErrorListener = errorListener;
        this.mUrl = url;
        this.mPriority = priority;
        this.mTag = tag;
    }

    public void deliverError(AndroidNetworkingError error) {
        mErrorListener.onError(error);
    }

    public void deliverResponse(AndroidNetworkingResponse<T> response) {
        mSuccessListener.onResponse(response.getResult());
    }

    private String encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();

        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString();
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    /**
     * Notifies the request queue that this request has finished (successfully or with error).
     */
    public void finish() {
        mErrorListener = null;
        mSuccessListener = null;

        if (mAndroidNetworkingRequestQueue != null) {
            mAndroidNetworkingRequestQueue.finish(this);
        }
    }

    protected BufferedSource getBody() {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            Buffer buffer = new Buffer();
            buffer.writeUtf8(encodeParameters(params, getParamsEncoding()));

            return buffer;
        }

        return null;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    public
    @Nullable
    Headers getHeaders() {
        return mHeaders;
    }

    @MethodRes
    int getMethod() {
        return mMethod;
    }

    protected Map<String, String> getParams() {
        return null;
    }

    protected String getParamsEncoding() {
        return PARAMS_ENCODING;
    }

    protected boolean isFollowingRedirects() {
        return true;
    }

    public boolean isResponseDelivered() {
        return mResponseDelivered;
    }

    protected AndroidNetworkingError parseNetworkError(AndroidNetworkingError error) {
        try {
            if (error.getData() != null && error.getData().source != null) {
                error.setContent(Okio.buffer(error.getData().source).readUtf8());
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to parse error response", ioe);
        }

        return error;
    }

    protected abstract AndroidNetworkingResponse<T> parseResponse(AndroidNetworkingData data);

    public void setHeaders(Headers headers) {
        this.mHeaders = headers;
    }

    public void setResponseDelivered(boolean responseDelivered) {
        this.mResponseDelivered = responseDelivered;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority priority) {
        this.mPriority = priority;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public Object getTag() {
        return mTag;
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void cancel() {
        Log.d(TAG, "cancelling request for sequenceNumber : " + sequenceNumber);
        future.cancel(true);
    }

    public boolean isCanceled() {
        return future.isCancelled();
    }

    public Future<?> getFuture() {
        return future;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public void setRequestQueue(AndroidNetworkingRequestQueue androidNetworkingRequestQueue) {
        mAndroidNetworkingRequestQueue = androidNetworkingRequestQueue;
    }

}
