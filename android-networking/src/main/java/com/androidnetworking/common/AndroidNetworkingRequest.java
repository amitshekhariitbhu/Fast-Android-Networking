package com.androidnetworking.common;

import android.util.Log;

import com.androidnetworking.error.AndroidNetworkingError;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Future;

/**
 * Created by amitshekhar on 26/03/16.
 */
public class AndroidNetworkingRequest {

    private final static String TAG = AndroidNetworkingRequest.class.getSimpleName();

    private int mMethod;
    private Priority mPriority;
    private String mUrl;
    private int sequenceNumber;
    private Object mTag;
    private HashMap<String, String> mHeadersMap = new HashMap<String, String>();
    private HashMap<String, String> mBodyParameterMap = new HashMap<String, String>();
    private HashMap<String, String> mMultiPartParameterMap = new HashMap<String, String>();
    private HashMap<String, String> mQueryParameterMap = new HashMap<String, String>();
    private HashMap<String, String> mPathParameterMap = new HashMap<String, String>();
    private HashMap<String, File> mMultiPartFileMap = new HashMap<String, File>();

    private boolean mResponseDelivered = false;
    private Future<?> future;
    private AndroidNetworkingResponse.SuccessListener mSuccessListener;
    private AndroidNetworkingResponse.ErrorListener mErrorListener;

    private AndroidNetworkingRequest(Builder builder) {
        this.mMethod = builder.mMethod;
        this.mPriority = builder.mPriority;
        this.mUrl = builder.mUrl;
        this.mTag = builder.mTag;
        this.mHeadersMap = builder.mHeadersMap;
        this.mBodyParameterMap = builder.mBodyParameterMap;
        this.mMultiPartParameterMap = builder.mMultiPartParameterMap;
        this.mQueryParameterMap = builder.mQueryParameterMap;
        this.mPathParameterMap = builder.mPathParameterMap;
        this.mMultiPartFileMap = builder.mMultiPartFileMap;
    }

    public void addRequest(AndroidNetworkingResponse.SuccessListener successListener, AndroidNetworkingResponse.ErrorListener errorListener) {
        this.mSuccessListener = successListener;
        this.mErrorListener = errorListener;
//        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
    }

    public int getMethod() {
        return mMethod;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Object getTag() {
        return mTag;
    }

    public HashMap<String, String> getHeadersMap() {
        return mHeadersMap;
    }

    public HashMap<String, String> getBodyParameterMap() {
        return mBodyParameterMap;
    }

    public HashMap<String, String> getMultiPartParameterMap() {
        return mMultiPartParameterMap;
    }

    public HashMap<String, File> getMultiPartFileMap() {
        return mMultiPartFileMap;
    }

    public HashMap<String, String> getQueryParameterMap() {
        return mQueryParameterMap;
    }

    public HashMap<String, String> getPathParameterMap() {
        return mPathParameterMap;
    }

    public boolean isResponseDelivered() {
        return mResponseDelivered;
    }

    public void setResponseDelivered(boolean responseDelivered) {
        this.mResponseDelivered = responseDelivered;
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

    public void finish() {
        mErrorListener = null;
        mSuccessListener = null;
//        AndroidNetworkingRequestQueue.getInstance().finish(this);
    }

    public void deliverError(AndroidNetworkingError error) {
        mErrorListener.onError(error);
    }

    public void deliverResponse(AndroidNetworkingResponse response) {
        mSuccessListener.onResponse(response.getResult());
    }

    public static class Builder implements RequestBuilder {

        private int mMethod;
        private Priority mPriority;
        private String mUrl;
        private Object mTag;
        private HashMap<String, String> mHeadersMap = new HashMap<String, String>();
        private HashMap<String, String> mBodyParameterMap = new HashMap<String, String>();
        private HashMap<String, String> mMultiPartParameterMap = new HashMap<String, String>();
        private HashMap<String, String> mQueryParameterMap = new HashMap<String, String>();
        private HashMap<String, String> mPathParameterMap = new HashMap<String, String>();
        private HashMap<String, File> mMultiPartFileMap = new HashMap<String, File>();

        public Builder() {
        }

        @Override
        public Builder setMethod(int method) {
            this.mMethod = method;
            return this;
        }

        @Override
        public Builder setPriority(Priority priority) {
            this.mPriority = priority;
            return this;
        }

        @Override
        public Builder setUrl(String url) {
            this.mUrl = url;
            return this;
        }

        @Override
        public Builder setTag(Object tag) {
            this.mTag = tag;
            return this;
        }

        @Override
        public Builder addHeaders(String key, String value) {
            mHeadersMap.put(key, value);
            return this;
        }

        @Override
        public Builder addBodyParameter(String key, String value) {
            mBodyParameterMap.put(key, value);
            return this;
        }

        @Override
        public Builder addMultipartFile(String key, String value) {
            mMultiPartParameterMap.put(key, value);
            return this;
        }

        @Override
        public Builder addMultipartFile(String key, File file) {
            mMultiPartFileMap.put(key, file);
            return this;
        }

        @Override
        public Builder addMultipartFile(String key, String contentType, File file) {
            mMultiPartFileMap.put(key, file);
            return this;
        }

        @Override
        public Builder addMultipartFile(String key, String fileName, String contentType, File file) {
            mMultiPartFileMap.put(key, file);
            return this;
        }

        @Override
        public Builder addQueryParameter(String key, String value) {
            mQueryParameterMap.put(key, value);
            return this;
        }

        @Override
        public Builder addPathParameter(String key, String value) {
            mPathParameterMap.put(key, value);
            return this;
        }

        public AndroidNetworkingRequest build() {
            AndroidNetworkingRequest androidNetworkingRequest = new AndroidNetworkingRequest(this);
            return androidNetworkingRequest;
        }
    }

}
