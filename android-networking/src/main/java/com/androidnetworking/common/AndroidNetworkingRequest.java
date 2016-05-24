package com.androidnetworking.common;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.androidnetworking.core.Core;
import com.androidnetworking.error.AndroidNetworkingError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.androidnetworking.internal.AndroidNetworkingRequestQueue;
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
import okhttp3.RequestBody;
import okio.Okio;

/**
 * Created by amitshekhar on 26/03/16.
 */
public class AndroidNetworkingRequest {

    private final static String TAG = AndroidNetworkingRequest.class.getSimpleName();

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

    private Bitmap.Config mDecodeConfig;
    private int mMaxWidth;
    private int mMaxHeight;
    private ImageView.ScaleType mScaleType;
    private CacheControl mCacheControl = null;
    private Executor mExecutor = null;

    private AndroidNetworkingRequest(GetRequestBuilder builder) {
        this.mRequestType = RequestType.SIMPLE;
        this.mMethod = Method.GET;
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
    }

    private AndroidNetworkingRequest(PostRequestBuilder builder) {
        this.mRequestType = RequestType.SIMPLE;
        this.mMethod = Method.POST;
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
    }

    private AndroidNetworkingRequest(DownloadBuilder builder) {
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
    }

    private AndroidNetworkingRequest(MultiPartBuilder builder) {
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
    }

    public void getAsJSONObject(JSONObjectRequestListener requestListener) {
        this.mResponseAs = RESPONSE.JSON_OBJECT;
        this.mJSONObjectRequestListener = requestListener;
        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
    }

    public void getAsJSONArray(JSONArrayRequestListener requestListener) {
        this.mResponseAs = RESPONSE.JSON_ARRAY;
        this.mJSONArrayRequestListener = requestListener;
        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
    }

    public void getAsString(StringRequestListener requestListener) {
        this.mResponseAs = RESPONSE.STRING;
        this.mStringRequestListener = requestListener;
        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
    }

    public void getAsBitmap(BitmapRequestListener requestListener) {
        this.mResponseAs = RESPONSE.BITMAP;
        this.mBitmapRequestListener = requestListener;
        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
    }

    public AndroidNetworkingRequest setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.mDownloadProgressListener = downloadProgressListener;
        return this;
    }

    public void startDownload(DownloadListener downloadListener) {
        this.mDownloadListener = downloadListener;
        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
    }

    public AndroidNetworkingRequest setUploadProgressListener(UploadProgressListener uploadProgressListener) {
        this.mUploadProgressListener = uploadProgressListener;
        return this;
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

    public Object getTag() {
        return mTag;
    }

    public int getRequestType() {
        return mRequestType;
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
        if (mDownloadListener != null) {
            isDelivered = true;
            if (!isCancelled) {
                if (mExecutor != null) {
                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (mDownloadListener != null) {
                                mDownloadListener.onDownloadComplete();
                            }
                            Log.d(TAG, "Delivering success response for : " + toString());
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
                            Log.d(TAG, "Delivering success response for : " + toString());
                            finish();
                        }
                    });
                }
            } else {
                deliverError(new AndroidNetworkingError());
                finish();
            }
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

    public void cancel() {
        if (mPercentageThresholdForCancelling == 0 || mProgress < mPercentageThresholdForCancelling) {
            Log.d(TAG, "cancelling request for sequenceNumber : " + sequenceNumber);
            isCancelled = true;
            if (call != null) {
                call.cancel();
            }
            if (future != null) {
                future.cancel(true);
            }
            if (!isDelivered) {
                deliverError(new AndroidNetworkingError());
                finish();
            }
        } else {
            Log.d(TAG, "not cancelling request for sequenceNumber : " + sequenceNumber);
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

    public void finish() {
        mJSONArrayRequestListener = null;
        mJSONArrayRequestListener = null;
        mStringRequestListener = null;
        mBitmapRequestListener = null;
        mDownloadProgressListener = null;
        AndroidNetworkingRequestQueue.getInstance().finish(this);
    }

    public AndroidNetworkingResponse parseResponse(AndroidNetworkingData data) {
        switch (mResponseAs) {
            case JSON_ARRAY:
                try {
                    JSONArray json = new JSONArray(Okio.buffer(data.source).readUtf8());
                    return AndroidNetworkingResponse.success(json);
                } catch (JSONException | IOException e) {
                    return AndroidNetworkingResponse.failed(new AndroidNetworkingError(e));
                }
            case JSON_OBJECT:
                try {
                    JSONObject json = new JSONObject(Okio.buffer(data.source).readUtf8());
                    return AndroidNetworkingResponse.success(json);
                } catch (JSONException | IOException e) {
                    return AndroidNetworkingResponse.failed(new AndroidNetworkingError(e));
                }
            case STRING:
                try {
                    return AndroidNetworkingResponse.success(Okio.buffer(data.source).readUtf8());
                } catch (IOException e) {
                    return AndroidNetworkingResponse.failed(new AndroidNetworkingError(e));
                }
            case BITMAP:
                synchronized (sDecodeLock) {
                    try {
                        return Utils.decodeBitmap(data, mMaxWidth, mMaxHeight, mDecodeConfig, mScaleType);
                    } catch (OutOfMemoryError e) {
                        return AndroidNetworkingResponse.failed(new AndroidNetworkingError(e));
                    }
                }
        }
        return null;
    }

    public AndroidNetworkingError parseNetworkError(AndroidNetworkingError error) {
        try {
            if (error.getData() != null && error.getData().source != null) {
                error.setErrorBody(Okio.buffer(error.getData().source).readUtf8());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return error;
    }

    public synchronized void deliverError(AndroidNetworkingError error) {
        if (!isDelivered) {
            if (isCancelled) {
                error.setCancellationMessageInError();
                error.setErrorCode(0);
            }
            if (mJSONObjectRequestListener != null) {
                mJSONObjectRequestListener.onError(error);
            } else if (mJSONArrayRequestListener != null) {
                mJSONArrayRequestListener.onError(error);
            } else if (mStringRequestListener != null) {
                mStringRequestListener.onError(error);
            } else if (mBitmapRequestListener != null) {
                mBitmapRequestListener.onError(error);
            } else if (mDownloadListener != null) {
                mDownloadListener.onError(error);
            }
            Log.d(TAG, "Delivering error response for : " + toString());
        }
        isDelivered = true;
    }

    public void deliverResponse(final AndroidNetworkingResponse response) {
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
        } else {
            AndroidNetworkingError error = new AndroidNetworkingError();
            error.setCancellationMessageInError();
            error.setErrorCode(0);
            if (mJSONObjectRequestListener != null) {
                mJSONObjectRequestListener.onError(error);
            } else if (mJSONArrayRequestListener != null) {
                mJSONArrayRequestListener.onError(error);
            } else if (mStringRequestListener != null) {
                mStringRequestListener.onError(error);
            } else if (mBitmapRequestListener != null) {
                mBitmapRequestListener.onError(error);
            }
        }
        Log.d(TAG, "Delivering success response for : " + toString());
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
            for (HashMap.Entry<String, String> entry : mBodyParameterMap.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            for (HashMap.Entry<String, String> entry : mUrlEncodedFormBodyParameterMap.entrySet()) {
                builder.addEncoded(entry.getKey(), entry.getValue());
            }
            return builder.build();
        }
    }

    public RequestBody getMultiPartRequestBody() {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (HashMap.Entry<String, String> entry : mMultiPartParameterMap.entrySet()) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""), RequestBody.create(null, entry.getValue()));
        }
        for (HashMap.Entry<String, File> entry : mMultiPartFileMap.entrySet()) {
            String fileName = entry.getValue().getName();
            RequestBody fileBody = RequestBody.create(MediaType.parse(Utils.getMimeType(fileName)), entry.getValue());
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\"; filename=\"" + fileName + "\""), fileBody);
        }
        return builder.build();
    }

    public Headers getHeaders() {
        Headers.Builder builder = new Headers.Builder();
        for (HashMap.Entry<String, String> entry : mHeadersMap.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }


    public static class GetRequestBuilder implements RequestBuilder {
        private Priority mPriority = Priority.MEDIUM;
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

        public GetRequestBuilder(String url) {
            this.mUrl = url;
        }

        @Override
        public GetRequestBuilder setPriority(Priority priority) {
            this.mPriority = priority;
            return this;
        }

        @Override
        public GetRequestBuilder setTag(Object tag) {
            this.mTag = tag;
            return this;
        }

        @Override
        public GetRequestBuilder addQueryParameter(String key, String value) {
            mQueryParameterMap.put(key, value);
            return this;
        }

        @Override
        public GetRequestBuilder addQueryParameter(HashMap<String, String> queryParameterMap) {
            if (queryParameterMap != null) {
                for (HashMap.Entry<String, String> entry : queryParameterMap.entrySet()) {
                    mQueryParameterMap.put(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        @Override
        public GetRequestBuilder addPathParameter(String key, String value) {
            mPathParameterMap.put(key, value);
            return this;
        }

        @Override
        public GetRequestBuilder addHeaders(String key, String value) {
            mHeadersMap.put(key, value);
            return this;
        }

        @Override
        public GetRequestBuilder addHeaders(HashMap<String, String> headerMap) {
            if (headerMap != null) {
                for (HashMap.Entry<String, String> entry : headerMap.entrySet()) {
                    mHeadersMap.put(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        @Override
        public GetRequestBuilder doNotCacheResponse() {
            mCacheControl = new CacheControl.Builder().noStore().build();
            return this;
        }

        @Override
        public GetRequestBuilder getResponseOnlyIfCached() {
            mCacheControl = CacheControl.FORCE_CACHE;
            return this;
        }

        @Override
        public GetRequestBuilder getResponseOnlyFromNetwork() {
            mCacheControl = CacheControl.FORCE_NETWORK;
            return this;
        }

        @Override
        public GetRequestBuilder setMaxAgeCacheControl(int maxAge, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxAge(maxAge, timeUnit).build();
            return this;
        }

        @Override
        public GetRequestBuilder setMaxStaleCacheControl(int maxStale, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxStale(maxStale, timeUnit).build();
            return this;
        }

        @Override
        public GetRequestBuilder setExecutor(Executor executor) {
            mExecutor = executor;
            return this;
        }

        public GetRequestBuilder setBitmapConfig(Bitmap.Config bitmapConfig) {
            this.mDecodeConfig = bitmapConfig;
            return this;
        }

        public GetRequestBuilder setBitmapMaxHeight(int maxHeight) {
            this.mMaxHeight = maxHeight;
            return this;
        }

        public GetRequestBuilder setBitmapMaxWidth(int maxWidth) {
            this.mMaxWidth = maxWidth;
            return this;
        }

        public GetRequestBuilder setImageScaleType(ImageView.ScaleType imageScaleType) {
            this.mScaleType = imageScaleType;
            return this;
        }

        public AndroidNetworkingRequest build() {
            return new AndroidNetworkingRequest(this);
        }
    }

    public static class PostRequestBuilder implements RequestBuilder {

        private Priority mPriority = Priority.MEDIUM;
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

        public PostRequestBuilder(String url) {
            this.mUrl = url;
        }

        @Override
        public PostRequestBuilder setPriority(Priority priority) {
            this.mPriority = priority;
            return this;
        }

        @Override
        public PostRequestBuilder setTag(Object tag) {
            this.mTag = tag;
            return this;
        }

        @Override
        public PostRequestBuilder addQueryParameter(String key, String value) {
            mQueryParameterMap.put(key, value);
            return this;
        }

        @Override
        public PostRequestBuilder addQueryParameter(HashMap<String, String> queryParameterMap) {
            if (queryParameterMap != null) {
                for (HashMap.Entry<String, String> entry : queryParameterMap.entrySet()) {
                    mQueryParameterMap.put(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        @Override
        public PostRequestBuilder addPathParameter(String key, String value) {
            mPathParameterMap.put(key, value);
            return this;
        }

        @Override
        public PostRequestBuilder addHeaders(String key, String value) {
            mHeadersMap.put(key, value);
            return this;
        }

        @Override
        public PostRequestBuilder addHeaders(HashMap<String, String> headerMap) {
            if (headerMap != null) {
                for (HashMap.Entry<String, String> entry : headerMap.entrySet()) {
                    mHeadersMap.put(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        @Override
        public PostRequestBuilder doNotCacheResponse() {
            mCacheControl = new CacheControl.Builder().noStore().build();
            return this;
        }

        @Override
        public PostRequestBuilder getResponseOnlyIfCached() {
            mCacheControl = CacheControl.FORCE_CACHE;
            return this;
        }

        @Override
        public PostRequestBuilder getResponseOnlyFromNetwork() {
            mCacheControl = CacheControl.FORCE_NETWORK;
            return this;
        }

        @Override
        public PostRequestBuilder setMaxAgeCacheControl(int maxAge, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxAge(maxAge, timeUnit).build();
            return this;
        }

        @Override
        public PostRequestBuilder setMaxStaleCacheControl(int maxStale, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxStale(maxStale, timeUnit).build();
            return this;
        }

        @Override
        public PostRequestBuilder setExecutor(Executor executor) {
            mExecutor = executor;
            return this;
        }

        public PostRequestBuilder addBodyParameter(String key, String value) {
            mBodyParameterMap.put(key, value);
            return this;
        }

        public PostRequestBuilder addUrlEncodeFormBodyParameter(String key, String value) {
            mUrlEncodedFormBodyParameterMap.put(key, value);
            return this;
        }

        public PostRequestBuilder addBodyParameter(HashMap<String, String> bodyParameterMap) {
            if (bodyParameterMap != null) {
                for (HashMap.Entry<String, String> entry : bodyParameterMap.entrySet()) {
                    mBodyParameterMap.put(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        public PostRequestBuilder addUrlEncodeFormBodyParameter(HashMap<String, String> bodyParameterMap) {
            if (bodyParameterMap != null) {
                for (HashMap.Entry<String, String> entry : bodyParameterMap.entrySet()) {
                    mUrlEncodedFormBodyParameterMap.put(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        public PostRequestBuilder addJSONObjectBody(JSONObject jsonObject) {
            mJsonObject = jsonObject;
            return this;
        }

        public PostRequestBuilder addJSONArrayBody(JSONArray jsonArray) {
            mJsonArray = jsonArray;
            return this;
        }

        public PostRequestBuilder addStringBody(String stringBody) {
            mStringBody = stringBody;
            return this;
        }

        public PostRequestBuilder addFileBody(File file) {
            mFile = file;
            return this;
        }

        public PostRequestBuilder addByteBody(byte[] bytes) {
            mByte = bytes;
            return this;
        }

        public AndroidNetworkingRequest build() {
            return new AndroidNetworkingRequest(this);
        }
    }

    public static class DownloadBuilder implements RequestBuilder {

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

        public DownloadBuilder(String url, String dirPath, String fileName) {
            this.mUrl = url;
            this.mDirPath = dirPath;
            this.mFileName = fileName;
        }

        @Override
        public DownloadBuilder setPriority(Priority priority) {
            this.mPriority = priority;
            return this;
        }

        @Override
        public DownloadBuilder setTag(Object tag) {
            this.mTag = tag;
            return this;
        }

        @Override
        public DownloadBuilder addHeaders(String key, String value) {
            mHeadersMap.put(key, value);
            return this;
        }

        @Override
        public DownloadBuilder addHeaders(HashMap<String, String> headerMap) {
            if (headerMap != null) {
                for (HashMap.Entry<String, String> entry : headerMap.entrySet()) {
                    mHeadersMap.put(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        @Override
        public DownloadBuilder addQueryParameter(String key, String value) {
            mQueryParameterMap.put(key, value);
            return this;
        }

        @Override
        public DownloadBuilder addQueryParameter(HashMap<String, String> queryParameterMap) {
            if (queryParameterMap != null) {
                for (HashMap.Entry<String, String> entry : queryParameterMap.entrySet()) {
                    mQueryParameterMap.put(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        @Override
        public DownloadBuilder addPathParameter(String key, String value) {
            mPathParameterMap.put(key, value);
            return this;
        }

        @Override
        public DownloadBuilder doNotCacheResponse() {
            mCacheControl = new CacheControl.Builder().noStore().build();
            return this;
        }

        @Override
        public DownloadBuilder getResponseOnlyIfCached() {
            mCacheControl = CacheControl.FORCE_CACHE;
            return this;
        }

        @Override
        public DownloadBuilder getResponseOnlyFromNetwork() {
            mCacheControl = CacheControl.FORCE_NETWORK;
            return this;
        }

        @Override
        public DownloadBuilder setMaxAgeCacheControl(int maxAge, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxAge(maxAge, timeUnit).build();
            return this;
        }

        @Override
        public DownloadBuilder setMaxStaleCacheControl(int maxStale, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxStale(maxStale, timeUnit).build();
            return this;
        }

        @Override
        public DownloadBuilder setExecutor(Executor executor) {
            mExecutor = executor;
            return this;
        }

        public DownloadBuilder setPercentageThresholdForCancelling(int percentageThresholdForCancelling) {
            this.mPercentageThresholdForCancelling = percentageThresholdForCancelling;
            return this;
        }

        public AndroidNetworkingRequest build() {
            return new AndroidNetworkingRequest(this);
        }
    }

    public static class MultiPartBuilder implements RequestBuilder {

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

        public MultiPartBuilder(String url) {
            this.mUrl = url;
        }

        @Override
        public MultiPartBuilder setPriority(Priority priority) {
            this.mPriority = priority;
            return this;
        }

        @Override
        public MultiPartBuilder setTag(Object tag) {
            this.mTag = tag;
            return this;
        }

        @Override
        public MultiPartBuilder addQueryParameter(String key, String value) {
            mQueryParameterMap.put(key, value);
            return this;
        }

        @Override
        public MultiPartBuilder addQueryParameter(HashMap<String, String> queryParameterMap) {
            if (queryParameterMap != null) {
                for (HashMap.Entry<String, String> entry : queryParameterMap.entrySet()) {
                    mQueryParameterMap.put(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        @Override
        public MultiPartBuilder addPathParameter(String key, String value) {
            mPathParameterMap.put(key, value);
            return this;
        }

        @Override
        public MultiPartBuilder addHeaders(String key, String value) {
            mHeadersMap.put(key, value);
            return this;
        }

        @Override
        public MultiPartBuilder addHeaders(HashMap<String, String> headerMap) {
            if (headerMap != null) {
                for (HashMap.Entry<String, String> entry : headerMap.entrySet()) {
                    mHeadersMap.put(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        @Override
        public MultiPartBuilder doNotCacheResponse() {
            mCacheControl = new CacheControl.Builder().noStore().build();
            return this;
        }

        @Override
        public MultiPartBuilder getResponseOnlyIfCached() {
            mCacheControl = CacheControl.FORCE_CACHE;
            return this;
        }

        @Override
        public MultiPartBuilder getResponseOnlyFromNetwork() {
            mCacheControl = CacheControl.FORCE_NETWORK;
            return this;
        }

        @Override
        public MultiPartBuilder setMaxAgeCacheControl(int maxAge, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxAge(maxAge, timeUnit).build();
            return this;
        }

        @Override
        public MultiPartBuilder setMaxStaleCacheControl(int maxStale, TimeUnit timeUnit) {
            mCacheControl = new CacheControl.Builder().maxStale(maxStale, timeUnit).build();
            return this;
        }

        @Override
        public MultiPartBuilder setExecutor(Executor executor) {
            mExecutor = executor;
            return this;
        }

        public MultiPartBuilder addMultipartParameter(String key, String value) {
            mMultiPartParameterMap.put(key, value);
            return this;
        }

        public MultiPartBuilder addMultipartParameter(HashMap<String, String> multiPartParameterMap) {
            if (multiPartParameterMap != null) {
                for (HashMap.Entry<String, String> entry : multiPartParameterMap.entrySet()) {
                    mMultiPartParameterMap.put(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        public MultiPartBuilder addMultipartFile(String key, File file) {
            mMultiPartFileMap.put(key, file);
            return this;
        }

        public MultiPartBuilder addMultipartFile(HashMap<String, File> multiPartFileMap) {
            if (multiPartFileMap != null) {
                for (HashMap.Entry<String, File> entry : multiPartFileMap.entrySet()) {
                    mMultiPartFileMap.put(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        public MultiPartBuilder setPercentageThresholdForCancelling(int percentageThresholdForCancelling) {
            this.mPercentageThresholdForCancelling = percentageThresholdForCancelling;
            return this;
        }

        public AndroidNetworkingRequest build() {
            return new AndroidNetworkingRequest(this);
        }
    }

    @Override
    public String toString() {
        return "AndroidNetworkingRequest{" +
                "mUrl='" + mUrl + '\'' +
                ", mMethod=" + mMethod +
                ", mPriority=" + mPriority +
                ", mRequestType=" + mRequestType +
                ", sequenceNumber=" + sequenceNumber +
                ", mTag=" + mTag +
                '}';
    }
}
