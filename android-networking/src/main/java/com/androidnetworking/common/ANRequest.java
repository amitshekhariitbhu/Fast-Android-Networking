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

package com.androidnetworking.common;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.androidnetworking.core.Core;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.androidnetworking.internal.ANRequestQueue;
import com.androidnetworking.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.Okio;

/**
 * Created by amitshekhar on 26/03/16.
 */
public class ANRequest {

    private final static String TAG = ANRequest.class.getSimpleName();

    private int mMethod;
    private Priority mPriority;
    private int mRequestType;
    private String mUrl;
    private int sequenceNumber;
    private Object mTag;
    private RESPONSE mResponseAs;
    private HashMap<String, String> mHeadersMap = new HashMap<String, String>();
    private HashMap<String, String> mBodyParameterMap = new HashMap<String, String>();
    private HashMap<String, String> mUrlEncodedFormBodyParameterMap = new HashMap<String, String>();
    private HashMap<String, String> mMultiPartParameterMap = new HashMap<String, String>();
    private HashMap<String, String> mQueryParameterMap = new HashMap<String, String>();
    private HashMap<String, String> mPathParameterMap = new HashMap<String, String>();
    private HashMap<String, File> mMultiPartFileMap = new HashMap<String, File>();
    private String mDirPath;
    private String mFileName;
    private JSONObject mJsonObject = null;
    private JSONArray mJsonArray = null;
    private String mStringBody = null;
    private byte[] mByte = null;
    private File mFile = null;
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    private static final Object sDecodeLock = new Object();

    private Future future;
    private Call call;
    private int mProgress;
    private boolean isCancelled;
    private boolean isDelivered;
    private int mPercentageThresholdForCancelling = 0;
    private JSONArrayRequestListener mJSONArrayRequestListener;
    private JSONObjectRequestListener mJSONObjectRequestListener;
    private StringRequestListener mStringRequestListener;
    private BitmapRequestListener mBitmapRequestListener;
    private DownloadProgressListener mDownloadProgressListener;
    private UploadProgressListener mUploadProgressListener;
    private DownloadListener mDownloadListener;
    private AnalyticsListener mAnalyticsListener;

    private Bitmap.Config mDecodeConfig;
    private int mMaxWidth;
    private int mMaxHeight;
    private ImageView.ScaleType mScaleType;
    private CacheControl mCacheControl = null;
    private Executor mExecutor = null;
    private OkHttpClient mOkHttpClient = null;
    private String mUserAgent = null;

    public ANRequest(GetRequestBuilder builder) {
        this.mRequestType = RequestType.SIMPLE;
        this.mMethod = builder.mMethod;
        this.mPriority = builder.mPriority;
        this.mUrl = builder.mUrl;
        this.mTag = builder.mTag;
        this.mHeadersMap = builder.mHeadersMap;
        this.mDecodeConfig = builder.mDecodeConfig;
        this.mMaxHeight = builder.mMaxHeight;
        this.mMaxWidth = builder.mMaxWidth;
        this.mScaleType = builder.mScaleType;
        this.mQueryParameterMap = builder.mQueryParameterMap;
        this.mPathParameterMap = builder.mPathParameterMap;
        this.mCacheControl = builder.mCacheControl;
        this.mExecutor = builder.mExecutor;
        this.mOkHttpClient = builder.mOkHttpClient;
        this.mUserAgent = builder.mUserAgent;
    }

    public ANRequest(PostRequestBuilder builder) {
        this.mRequestType = RequestType.SIMPLE;
        this.mMethod = builder.mMethod;
        this.mPriority = builder.mPriority;
        this.mUrl = builder.mUrl;
        this.mTag = builder.mTag;
        this.mHeadersMap = builder.mHeadersMap;
        this.mBodyParameterMap = builder.mBodyParameterMap;
        this.mUrlEncodedFormBodyParameterMap = builder.mUrlEncodedFormBodyParameterMap;
        this.mQueryParameterMap = builder.mQueryParameterMap;
        this.mPathParameterMap = builder.mPathParameterMap;
        this.mJsonObject = builder.mJsonObject;
        this.mJsonArray = builder.mJsonArray;
        this.mStringBody = builder.mStringBody;
        this.mFile = builder.mFile;
        this.mByte = builder.mByte;
        this.mCacheControl = builder.mCacheControl;
        this.mExecutor = builder.mExecutor;
        this.mOkHttpClient = builder.mOkHttpClient;
        this.mUserAgent = builder.mUserAgent;
    }

    public ANRequest(DownloadBuilder builder) {
        this.mRequestType = RequestType.DOWNLOAD;
        this.mMethod = Method.GET;
        this.mPriority = builder.mPriority;
        this.mUrl = builder.mUrl;
        this.mTag = builder.mTag;
        this.mDirPath = builder.mDirPath;
        this.mFileName = builder.mFileName;
        this.mHeadersMap = builder.mHeadersMap;
        this.mQueryParameterMap = builder.mQueryParameterMap;
        this.mPathParameterMap = builder.mPathParameterMap;
        this.mCacheControl = builder.mCacheControl;
        this.mPercentageThresholdForCancelling = builder.mPercentageThresholdForCancelling;
        this.mExecutor = builder.mExecutor;
        this.mOkHttpClient = builder.mOkHttpClient;
        this.mUserAgent = builder.mUserAgent;
    }

    public ANRequest(MultiPartBuilder builder) {
        this.mRequestType = RequestType.MULTIPART;
        this.mMethod = Method.POST;
        this.mPriority = builder.mPriority;
        this.mUrl = builder.mUrl;
        this.mTag = builder.mTag;
        this.mHeadersMap = builder.mHeadersMap;
        this.mQueryParameterMap = builder.mQueryParameterMap;
        this.mPathParameterMap = builder.mPathParameterMap;
        this.mMultiPartParameterMap = builder.mMultiPartParameterMap;
        this.mMultiPartFileMap = builder.mMultiPartFileMap;
        this.mCacheControl = builder.mCacheControl;
        this.mPercentageThresholdForCancelling = builder.mPercentageThresholdForCancelling;
        this.mExecutor = builder.mExecutor;
        this.mOkHttpClient = builder.mOkHttpClient;
        this.mUserAgent = builder.mUserAgent;
    }

    public void getAsJSONObject(JSONObjectRequestListener requestListener) {
        this.mResponseAs = RESPONSE.JSON_OBJECT;
        this.mJSONObjectRequestListener = requestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsJSONArray(JSONArrayRequestListener requestListener) {
        this.mResponseAs = RESPONSE.JSON_ARRAY;
        this.mJSONArrayRequestListener = requestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsString(StringRequestListener requestListener) {
        this.mResponseAs = RESPONSE.STRING;
        this.mStringRequestListener = requestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsBitmap(BitmapRequestListener requestListener) {
        this.mResponseAs = RESPONSE.BITMAP;
        this.mBitmapRequestListener = requestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public ANRequest setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.mDownloadProgressListener = downloadProgressListener;
        return this;
    }

    public void startDownload(DownloadListener downloadListener) {
        this.mDownloadListener = downloadListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void prefetch() {
        this.mResponseAs = RESPONSE.PREFETCH;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public ANRequest setUploadProgressListener(UploadProgressListener uploadProgressListener) {
        this.mUploadProgressListener = uploadProgressListener;
        return this;
    }

    public ANRequest setAnalyticsListener(AnalyticsListener analyticsListener) {
        this.mAnalyticsListener = analyticsListener;
        return this;
    }

    public AnalyticsListener getAnalyticsListener() {
        return mAnalyticsListener;
    }

    public int getMethod() {
        return mMethod;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public String getUrl() {
        String tempUrl = mUrl;
        for (HashMap.Entry<String, String> entry : mPathParameterMap.entrySet()) {
            tempUrl = tempUrl.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        HttpUrl.Builder urlBuilder = HttpUrl.parse(tempUrl).newBuilder();
        for (HashMap.Entry<String, String> entry : mQueryParameterMap.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return urlBuilder.build().toString();
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
    }

    public void setResponseAs(RESPONSE responseAs) {
        this.mResponseAs = responseAs;
    }

    public Object getTag() {
        return mTag;
    }

    public int getRequestType() {
        return mRequestType;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public void setUserAgent(String userAgent) {
        this.mUserAgent = userAgent;
    }

    public String getUserAgent() {
        return mUserAgent;
    }

    public DownloadProgressListener getDownloadProgressListener() {
        return new DownloadProgressListener() {
            @Override
            public void onProgress(final long bytesDownloaded, final long totalBytes) {
                if (mDownloadProgressListener != null && !isCancelled) {
                    mDownloadProgressListener.onProgress(bytesDownloaded, totalBytes);
                }
            }
        };
    }

    public void updateDownloadCompletion() {
        isDelivered = true;
        if (mDownloadListener != null) {
            if (!isCancelled) {
                if (mExecutor != null) {
                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (mDownloadListener != null) {
                                mDownloadListener.onDownloadComplete();
                            }
                            ANLog.d("Delivering success : " + toString());
                            finish();
                        }
                    });
                } else {
                    Core.getInstance().getExecutorSupplier().forMainThreadTasks().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (mDownloadListener != null) {
                                mDownloadListener.onDownloadComplete();
                            }
                            ANLog.d("Delivering success : " + toString());
                            finish();
                        }
                    });
                }
            } else {
                deliverError(new ANError());
                finish();
            }
        } else {
            ANLog.d("Prefetch done : " + toString());
            finish();
        }
    }

    public UploadProgressListener getUploadProgressListener() {
        return new UploadProgressListener() {
            @Override
            public void onProgress(final long bytesUploaded, final long totalBytes) {
                mProgress = (int) ((bytesUploaded * 100) / totalBytes);
                if (mUploadProgressListener != null && !isCancelled) {
                    mUploadProgressListener.onProgress(bytesUploaded, totalBytes);
                }
            }
        };
    }

    public String getDirPath() {
        return mDirPath;
    }

    public String getFileName() {
        return mFileName;
    }

    public CacheControl getCacheControl() {
        return mCacheControl;
    }

    public ImageView.ScaleType getScaleType() {
        return mScaleType;
    }

    public void cancel(boolean forceCancel) {
        try {
            if (forceCancel || mPercentageThresholdForCancelling == 0 || mProgress < mPercentageThresholdForCancelling) {
                ANLog.d("cancelling request : " + toString());
                isCancelled = true;
                if (call != null) {
                    call.cancel();
                }
                if (future != null) {
                    future.cancel(true);
                }
                if (!isDelivered) {
                    deliverError(new ANError());
                }
            } else {
                ANLog.d("not cancelling request : " + toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isCanceled() {
        return isCancelled;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }

    public void destroy() {
        mJSONArrayRequestListener = null;
        mJSONArrayRequestListener = null;
        mStringRequestListener = null;
        mBitmapRequestListener = null;
        mDownloadProgressListener = null;
        mUploadProgressListener = null;
        mDownloadListener = null;
        mAnalyticsListener = null;
    }

    public void finish() {
        destroy();
        ANRequestQueue.getInstance().finish(this);
    }

    public ANResponse parseResponse(ANData data) {
        switch (mResponseAs) {
            case JSON_ARRAY:
                try {
                    JSONArray json = new JSONArray(Okio.buffer(data.source).readUtf8());
                    return ANResponse.success(json);
                } catch (JSONException | IOException e) {
                    return ANResponse.failed(new ANError(e));
                }
            case JSON_OBJECT:
                try {
                    JSONObject json = new JSONObject(Okio.buffer(data.source).readUtf8());
                    return ANResponse.success(json);
                } catch (JSONException | IOException e) {
                    return ANResponse.failed(new ANError(e));
                }
            case STRING:
                try {
                    return ANResponse.success(Okio.buffer(data.source).readUtf8());
                } catch (IOException e) {
                    return ANResponse.failed(new ANError(e));
                }
            case BITMAP:
                synchronized (sDecodeLock) {
                    try {
                        return Utils.decodeBitmap(data, mMaxWidth, mMaxHeight, mDecodeConfig, mScaleType);
                    } catch (OutOfMemoryError e) {
                        return ANResponse.failed(new ANError(e));
                    }
                }
            case PREFETCH:
                return ANResponse.success(ANConstants.PREFETCH);
        }
        return null;
    }

    public ANError parseNetworkError(ANError ANError) {
        try {
            if (ANError.getData() != null && ANError.getData().source != null) {
                ANError.setErrorBody(Okio.buffer(ANError.getData().source).readUtf8());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ANError;
    }

    public synchronized void deliverError(ANError ANError) {
        try {
            if (!isDelivered) {
                if (isCancelled) {
                    ANError.setCancellationMessageInError();
                    ANError.setErrorCode(0);
                }
                if (mJSONObjectRequestListener != null) {
                    mJSONObjectRequestListener.onError(ANError);
                } else if (mJSONArrayRequestListener != null) {
                    mJSONArrayRequestListener.onError(ANError);
                } else if (mStringRequestListener != null) {
                    mStringRequestListener.onError(ANError);
                } else if (mBitmapRequestListener != null) {
                    mBitmapRequestListener.onError(ANError);
                } else if (mDownloadListener != null) {
                    mDownloadListener.onError(ANError);
                }
                ANLog.d("Delivering ANError : " + toString());
            }
            isDelivered = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deliverResponse(final ANResponse response) {
        try {
            isDelivered = true;
            if (!isCancelled) {
                if (mExecutor != null) {
                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (mJSONObjectRequestListener != null) {
                                mJSONObjectRequestListener.onResponse((JSONObject) response.getResult());
                            } else if (mJSONArrayRequestListener != null) {
                                mJSONArrayRequestListener.onResponse((JSONArray) response.getResult());
                            } else if (mStringRequestListener != null) {
                                mStringRequestListener.onResponse((String) response.getResult());
                            } else if (mBitmapRequestListener != null) {
                                mBitmapRequestListener.onResponse((Bitmap) response.getResult());
                            }
                            finish();
                        }
                    });
                } else {
                    Core.getInstance().getExecutorSupplier().forMainThreadTasks().execute(new Runnable() {
                        public void run() {
                            if (mJSONObjectRequestListener != null) {
                                mJSONObjectRequestListener.onResponse((JSONObject) response.getResult());
                            } else if (mJSONArrayRequestListener != null) {
                                mJSONArrayRequestListener.onResponse((JSONArray) response.getResult());
                            } else if (mStringRequestListener != null) {
                                mStringRequestListener.onResponse((String) response.getResult());
                            } else if (mBitmapRequestListener != null) {
                                mBitmapRequestListener.onResponse((Bitmap) response.getResult());
                            }
                            finish();
                        }
                    });
                }
                ANLog.d("Delivering success : " + toString());
            } else {
                ANError anError = new ANError();
                anError.setCancellationMessageInError();
                anError.setErrorCode(0);
                if (mJSONObjectRequestListener != null) {
                    mJSONObjectRequestListener.onError(anError);
                } else if (mJSONArrayRequestListener != null) {
                    mJSONArrayRequestListener.onError(anError);
                } else if (mStringRequestListener != null) {
                    mStringRequestListener.onError(anError);
                } else if (mBitmapRequestListener != null) {
                    mBitmapRequestListener.onError(anError);
                }
                finish();
                ANLog.d("Delivering cancelled : " + toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RequestBody getRequestBody() {
        if (mJsonObject != null) {
            return RequestBody.create(JSON_MEDIA_TYPE, mJsonObject.toString());
        } else if (mJsonArray != null) {
            return RequestBody.create(JSON_MEDIA_TYPE, mJsonArray.toString());
        } else if (mStringBody != null) {
            return RequestBody.create(MEDIA_TYPE_MARKDOWN, mStringBody);
        } else if (mFile != null) {
            return RequestBody.create(MEDIA_TYPE_MARKDOWN, mFile);
        } else if (mByte != null) {
            return RequestBody.create(MEDIA_TYPE_MARKDOWN, mByte);
        } else {
            FormBody.Builder builder = new FormBody.Builder();
            try {
                for (HashMap.Entry<String, String> entry : mBodyParameterMap.entrySet()) {
                    builder.add(entry.getKey(), entry.getValue());
                }
                for (HashMap.Entry<String, String> entry : mUrlEncodedFormBodyParameterMap.entrySet()) {
                    builder.addEncoded(entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder.build();
        }
    }

    public RequestBody getMultiPartRequestBody() {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        try {
            for (HashMap.Entry<String, String> entry : mMultiPartParameterMap.entrySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""), RequestBody.create(null, entry.getValue()));
            }
            for (HashMap.Entry<String, File> entry : mMultiPartFileMap.entrySet()) {
                String fileName = entry.getValue().getName();
                RequestBody fileBody = RequestBody.create(MediaType.parse(Utils.getMimeType(fileName)), entry.getValue());
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\"; filename=\"" + fileName + "\""), fileBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    public Headers getHeaders() {
        Headers.Builder builder = new Headers.Builder();
        try {
            for (HashMap.Entry<String, String> entry : mHeadersMap.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    public static class HeadRequestBuilder extends GetRequestBuilder {

        public HeadRequestBuilder(String url) {
            super(url, Method.HEAD);
        }
    }

    public static class GetRequestBuilder<T extends GetRequestBuilder> implements RequestBuilder {
        private Priority mPriority = Priority.MEDIUM;
        private int mMethod = Method.GET;
        private String mUrl;
        private Object mTag;
        private Bitmap.Config mDecodeConfig;
        private int mMaxWidth;
        private int mMaxHeight;
        private ImageView.ScaleType mScaleType;
        private HashMap<String, String> mHeadersMap = new HashMap<String, String>();
        private HashMap<String, String> mQueryParameterMap = new HashMap<String, String>();
        private HashMap<String, String> mPathParameterMap = new HashMap<String, String>();
        private CacheControl mCacheControl;
        private Executor mExecutor;
        private OkHttpClient mOkHttpClient;
        private String mUserAgent;

        public GetRequestBuilder(String url) {
            this.mUrl = url;
            this.mMethod = Method.GET;
        }

        private GetRequestBuilder(String url, int method) {
            this.mUrl = url;
            this.mMethod = method;
        }

        @Override
        public T setPriority(Priority priority) {
            mPriority = priority;
            return (T) this;
        }

        @Override
        public T setTag(Object tag) {
            mTag = tag;
            return (T) this;
        }

        @Override
        public T addQueryParameter(String key, String value) {
            mQueryParameterMap.put(key, value);
            return (T) this;
        }

        @Override
        public T addQueryParameter(HashMap<String, String> queryParameterMap) {
            if (queryParameterMap != null) {
                for (HashMap.Entry<String, String> entry : queryParameterMap.entrySet()) {
                    mQueryParameterMap.put(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T addPathParameter(String key, String value) {
            mPathParameterMap.put(key, value);
            return (T) this;
        }

        @Override
        public T addHeaders(String key, String value) {
            mHeadersMap.put(key, value);
            return (T) this;
        }

        @Override
        public T addHeaders(HashMap<String, String> headerMap) {
            if (headerMap != null) {
                for (HashMap.Entry<String, String> entry : headerMap.entrySet()) {
                    mHeadersMap.put(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T doNotCacheResponse() {
            mCacheControl = new CacheControl.Builder().noStore().build();
            return (T) this;
        }

        @Override
        public T getResponseOnlyIfCached() {
            mCacheControl = CacheControl.FORCE_CACHE;
            return (T) this;
        }

        @Override
        public T getResponseOnlyFromNetwork() {
            mCacheControl = CacheControl.FORCE_NETWORK;
            return (T) this;
        }

        @Override
        public T setMaxAgeCacheControl(int maxAge, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxAge(maxAge, timeUnit).build();
            return (T) this;
        }

        @Override
        public T setMaxStaleCacheControl(int maxStale, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxStale(maxStale, timeUnit).build();
            return (T) this;
        }

        @Override
        public T setExecutor(Executor executor) {
            mExecutor = executor;
            return (T) this;
        }

        @Override
        public T setOkHttpClient(OkHttpClient okHttpClient) {
            mOkHttpClient = okHttpClient;
            return (T) this;
        }

        @Override
        public T setUserAgent(String userAgent) {
            mUserAgent = userAgent;
            return (T) this;
        }

        public T setBitmapConfig(Bitmap.Config bitmapConfig) {
            mDecodeConfig = bitmapConfig;
            return (T) this;
        }

        public T setBitmapMaxHeight(int maxHeight) {
            mMaxHeight = maxHeight;
            return (T) this;
        }

        public T setBitmapMaxWidth(int maxWidth) {
            mMaxWidth = maxWidth;
            return (T) this;
        }

        public T setImageScaleType(ImageView.ScaleType imageScaleType) {
            mScaleType = imageScaleType;
            return (T) this;
        }

        public ANRequest build() {
            return new ANRequest(this);
        }
    }

    public static class PutRequestBuilder extends PostRequestBuilder {

        public PutRequestBuilder(String url) {
            super(url, Method.PUT);
        }
    }

    public static class DeleteRequestBuilder extends PostRequestBuilder {

        public DeleteRequestBuilder(String url) {
            super(url, Method.DELETE);
        }
    }

    public static class PatchRequestBuilder extends PostRequestBuilder {

        public PatchRequestBuilder(String url) {
            super(url, Method.PATCH);
        }
    }

    public static class PostRequestBuilder<T extends PostRequestBuilder> implements RequestBuilder {

        private Priority mPriority = Priority.MEDIUM;
        private int mMethod = Method.POST;
        private String mUrl;
        private Object mTag;
        private JSONObject mJsonObject = null;
        private JSONArray mJsonArray = null;
        private String mStringBody = null;
        private byte[] mByte = null;
        private File mFile = null;
        private HashMap<String, String> mHeadersMap = new HashMap<String, String>();
        private HashMap<String, String> mBodyParameterMap = new HashMap<String, String>();
        private HashMap<String, String> mUrlEncodedFormBodyParameterMap = new HashMap<String, String>();
        private HashMap<String, String> mQueryParameterMap = new HashMap<String, String>();
        private HashMap<String, String> mPathParameterMap = new HashMap<String, String>();
        private CacheControl mCacheControl;
        private Executor mExecutor;
        private OkHttpClient mOkHttpClient;
        private String mUserAgent;

        public PostRequestBuilder(String url) {
            this.mUrl = url;
            this.mMethod = Method.POST;
        }

        private PostRequestBuilder(String url, int method) {
            this.mUrl = url;
            this.mMethod = method;
        }

        @Override
        public T setPriority(Priority priority) {
            mPriority = priority;
            return (T) this;
        }

        @Override
        public T setTag(Object tag) {
            mTag = tag;
            return (T) this;
        }

        @Override
        public T addQueryParameter(String key, String value) {
            mQueryParameterMap.put(key, value);
            return (T) this;
        }

        @Override
        public T addQueryParameter(HashMap<String, String> queryParameterMap) {
            if (queryParameterMap != null) {
                for (HashMap.Entry<String, String> entry : queryParameterMap.entrySet()) {
                    mQueryParameterMap.put(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T addPathParameter(String key, String value) {
            mPathParameterMap.put(key, value);
            return (T) this;
        }

        @Override
        public T addHeaders(String key, String value) {
            mHeadersMap.put(key, value);
            return (T) this;
        }

        @Override
        public T addHeaders(HashMap<String, String> headerMap) {
            if (headerMap != null) {
                for (HashMap.Entry<String, String> entry : headerMap.entrySet()) {
                    mHeadersMap.put(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T doNotCacheResponse() {
            mCacheControl = new CacheControl.Builder().noStore().build();
            return (T) this;
        }

        @Override
        public T getResponseOnlyIfCached() {
            mCacheControl = CacheControl.FORCE_CACHE;
            return (T) this;
        }

        @Override
        public T getResponseOnlyFromNetwork() {
            mCacheControl = CacheControl.FORCE_NETWORK;
            return (T) this;
        }

        @Override
        public T setMaxAgeCacheControl(int maxAge, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxAge(maxAge, timeUnit).build();
            return (T) this;
        }

        @Override
        public T setMaxStaleCacheControl(int maxStale, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxStale(maxStale, timeUnit).build();
            return (T) this;
        }

        @Override
        public T setExecutor(Executor executor) {
            mExecutor = executor;
            return (T) this;
        }

        @Override
        public T setOkHttpClient(OkHttpClient okHttpClient) {
            mOkHttpClient = okHttpClient;
            return (T) this;
        }

        @Override
        public T setUserAgent(String userAgent) {
            mUserAgent = userAgent;
            return (T) this;
        }

        public T addBodyParameter(String key, String value) {
            mBodyParameterMap.put(key, value);
            return (T) this;
        }

        public T addUrlEncodeFormBodyParameter(String key, String value) {
            mUrlEncodedFormBodyParameterMap.put(key, value);
            return (T) this;
        }

        public T addBodyParameter(HashMap<String, String> bodyParameterMap) {
            if (bodyParameterMap != null) {
                for (HashMap.Entry<String, String> entry : bodyParameterMap.entrySet()) {
                    mBodyParameterMap.put(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        public T addUrlEncodeFormBodyParameter(HashMap<String, String> bodyParameterMap) {
            if (bodyParameterMap != null) {
                for (HashMap.Entry<String, String> entry : bodyParameterMap.entrySet()) {
                    mUrlEncodedFormBodyParameterMap.put(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        public T addJSONObjectBody(JSONObject jsonObject) {
            mJsonObject = jsonObject;
            return (T) this;
        }

        public T addJSONArrayBody(JSONArray jsonArray) {
            mJsonArray = jsonArray;
            return (T) this;
        }

        public T addStringBody(String stringBody) {
            mStringBody = stringBody;
            return (T) this;
        }

        public T addFileBody(File file) {
            mFile = file;
            return (T) this;
        }

        public T addByteBody(byte[] bytes) {
            mByte = bytes;
            return (T) this;
        }

        public ANRequest build() {
            return new ANRequest(this);
        }
    }

    public static class DownloadBuilder<T extends DownloadBuilder> implements RequestBuilder {

        private Priority mPriority = Priority.MEDIUM;
        private String mUrl;
        private Object mTag;
        private HashMap<String, String> mHeadersMap = new HashMap<String, String>();
        private HashMap<String, String> mQueryParameterMap = new HashMap<String, String>();
        private HashMap<String, String> mPathParameterMap = new HashMap<String, String>();
        private String mDirPath;
        private String mFileName;
        private CacheControl mCacheControl;
        private int mPercentageThresholdForCancelling = 0;
        private Executor mExecutor;
        private OkHttpClient mOkHttpClient;
        private String mUserAgent;

        public DownloadBuilder(String url, String dirPath, String fileName) {
            this.mUrl = url;
            this.mDirPath = dirPath;
            this.mFileName = fileName;
        }

        @Override
        public T setPriority(Priority priority) {
            mPriority = priority;
            return (T) this;
        }

        @Override
        public T setTag(Object tag) {
            mTag = tag;
            return (T) this;
        }

        @Override
        public T addHeaders(String key, String value) {
            mHeadersMap.put(key, value);
            return (T) this;
        }

        @Override
        public T addHeaders(HashMap<String, String> headerMap) {
            if (headerMap != null) {
                for (HashMap.Entry<String, String> entry : headerMap.entrySet()) {
                    mHeadersMap.put(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T addQueryParameter(String key, String value) {
            mQueryParameterMap.put(key, value);
            return (T) this;
        }

        @Override
        public T addQueryParameter(HashMap<String, String> queryParameterMap) {
            if (queryParameterMap != null) {
                for (HashMap.Entry<String, String> entry : queryParameterMap.entrySet()) {
                    mQueryParameterMap.put(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T addPathParameter(String key, String value) {
            mPathParameterMap.put(key, value);
            return (T) this;
        }

        @Override
        public T doNotCacheResponse() {
            mCacheControl = new CacheControl.Builder().noStore().build();
            return (T) this;
        }

        @Override
        public T getResponseOnlyIfCached() {
            mCacheControl = CacheControl.FORCE_CACHE;
            return (T) this;
        }

        @Override
        public T getResponseOnlyFromNetwork() {
            mCacheControl = CacheControl.FORCE_NETWORK;
            return (T) this;
        }

        @Override
        public T setMaxAgeCacheControl(int maxAge, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxAge(maxAge, timeUnit).build();
            return (T) this;
        }

        @Override
        public T setMaxStaleCacheControl(int maxStale, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxStale(maxStale, timeUnit).build();
            return (T) this;
        }

        @Override
        public T setExecutor(Executor executor) {
            mExecutor = executor;
            return (T) this;
        }

        @Override
        public T setOkHttpClient(OkHttpClient okHttpClient) {
            mOkHttpClient = okHttpClient;
            return (T) this;
        }

        @Override
        public T setUserAgent(String userAgent) {
            mUserAgent = userAgent;
            return (T) this;
        }

        public T setPercentageThresholdForCancelling(int percentageThresholdForCancelling) {
            mPercentageThresholdForCancelling = percentageThresholdForCancelling;
            return (T) this;
        }

        public ANRequest build() {
            return new ANRequest(this);
        }
    }

    public static class MultiPartBuilder<T extends MultiPartBuilder> implements RequestBuilder {

        private Priority mPriority = Priority.MEDIUM;
        private String mUrl;
        private Object mTag;
        private HashMap<String, String> mHeadersMap = new HashMap<String, String>();
        private HashMap<String, String> mMultiPartParameterMap = new HashMap<String, String>();
        private HashMap<String, String> mQueryParameterMap = new HashMap<String, String>();
        private HashMap<String, String> mPathParameterMap = new HashMap<String, String>();
        private HashMap<String, File> mMultiPartFileMap = new HashMap<String, File>();
        private CacheControl mCacheControl;
        private int mPercentageThresholdForCancelling = 0;
        private Executor mExecutor;
        private OkHttpClient mOkHttpClient;
        private String mUserAgent;

        public MultiPartBuilder(String url) {
            this.mUrl = url;
        }

        @Override
        public MultiPartBuilder setPriority(Priority priority) {
            mPriority = priority;
            return this;
        }

        @Override
        public T setTag(Object tag) {
            mTag = tag;
            return (T) this;
        }

        @Override
        public T addQueryParameter(String key, String value) {
            mQueryParameterMap.put(key, value);
            return (T) this;
        }

        @Override
        public T addQueryParameter(HashMap<String, String> queryParameterMap) {
            if (queryParameterMap != null) {
                for (HashMap.Entry<String, String> entry : queryParameterMap.entrySet()) {
                    mQueryParameterMap.put(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T addPathParameter(String key, String value) {
            mPathParameterMap.put(key, value);
            return (T) this;
        }

        @Override
        public T addHeaders(String key, String value) {
            mHeadersMap.put(key, value);
            return (T) this;
        }

        @Override
        public T addHeaders(HashMap<String, String> headerMap) {
            if (headerMap != null) {
                for (HashMap.Entry<String, String> entry : headerMap.entrySet()) {
                    mHeadersMap.put(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T doNotCacheResponse() {
            mCacheControl = new CacheControl.Builder().noStore().build();
            return (T) this;
        }

        @Override
        public T getResponseOnlyIfCached() {
            mCacheControl = CacheControl.FORCE_CACHE;
            return (T) this;
        }

        @Override
        public T getResponseOnlyFromNetwork() {
            mCacheControl = CacheControl.FORCE_NETWORK;
            return (T) this;
        }

        @Override
        public T setMaxAgeCacheControl(int maxAge, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxAge(maxAge, timeUnit).build();
            return (T) this;
        }

        @Override
        public T setMaxStaleCacheControl(int maxStale, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxStale(maxStale, timeUnit).build();
            return (T) this;
        }

        @Override
        public T setExecutor(Executor executor) {
            mExecutor = executor;
            return (T) this;
        }

        @Override
        public T setOkHttpClient(OkHttpClient okHttpClient) {
            mOkHttpClient = okHttpClient;
            return (T) this;
        }

        @Override
        public T setUserAgent(String userAgent) {
            mUserAgent = userAgent;
            return (T) this;
        }

        public T addMultipartParameter(String key, String value) {
            mMultiPartParameterMap.put(key, value);
            return (T) this;
        }

        public T addMultipartParameter(HashMap<String, String> multiPartParameterMap) {
            if (multiPartParameterMap != null) {
                for (HashMap.Entry<String, String> entry : multiPartParameterMap.entrySet()) {
                    mMultiPartParameterMap.put(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        public T addMultipartFile(String key, File file) {
            mMultiPartFileMap.put(key, file);
            return (T) this;
        }

        public T addMultipartFile(HashMap<String, File> multiPartFileMap) {
            if (multiPartFileMap != null) {
                for (HashMap.Entry<String, File> entry : multiPartFileMap.entrySet()) {
                    mMultiPartFileMap.put(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        public T setPercentageThresholdForCancelling(int percentageThresholdForCancelling) {
            this.mPercentageThresholdForCancelling = percentageThresholdForCancelling;
            return (T) this;
        }

        public ANRequest build() {
            return new ANRequest(this);
        }
    }

    @Override
    public String toString() {
        return "ANRequest{" +
                "sequenceNumber='" + sequenceNumber +
                ", mMethod=" + mMethod +
                ", mPriority=" + mPriority +
                ", mRequestType=" + mRequestType +
                ", mUrl=" + mUrl +
                '}';
    }
}
