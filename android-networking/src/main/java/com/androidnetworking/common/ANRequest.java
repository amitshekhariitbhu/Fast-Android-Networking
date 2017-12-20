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
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.androidnetworking.core.Core;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndBitmapRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONArrayRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndParsedRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndStringRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.androidnetworking.internal.ANRequestQueue;
import com.androidnetworking.internal.SynchronousCall;
import com.androidnetworking.utils.ParseUtil;
import com.androidnetworking.utils.Utils;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import okhttp3.Response;
import okio.Okio;

/**
 * Created by amitshekhar on 26/03/16.
 */
@SuppressWarnings({"unchecked", "unused"})
public class ANRequest<T extends ANRequest> {

    private final static String TAG = ANRequest.class.getSimpleName();

    private int mMethod;
    private Priority mPriority;
    private int mRequestType;
    private String mUrl;
    private int sequenceNumber;
    private Object mTag;
    private ResponseType mResponseType;
    private HashMap<String, List<String>> mHeadersMap = new HashMap<>();
    private HashMap<String, String> mBodyParameterMap = new HashMap<>();
    private HashMap<String, String> mUrlEncodedFormBodyParameterMap = new HashMap<>();
    private HashMap<String, String> mMultiPartParameterMap = new HashMap<>();
    private HashMap<String, List<String>> mQueryParameterMap = new HashMap<>();
    private HashMap<String, String> mPathParameterMap = new HashMap<>();
    private HashMap<String, File> mMultiPartFileMap = new HashMap<>();
    private String mDirPath;
    private String mFileName;
    private String mApplicationJsonString = null;
    private String mStringBody = null;
    private byte[] mByte = null;
    private File mFile = null;
    private static final MediaType JSON_MEDIA_TYPE =
            MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_MARKDOWN =
            MediaType.parse("text/x-markdown; charset=utf-8");
    private MediaType customMediaType = null;
    private static final Object sDecodeLock = new Object();

    private Future future;
    private Call call;
    private int mProgress;
    private boolean isCancelled;
    private boolean isDelivered;
    private boolean isRunning;
    private int mPercentageThresholdForCancelling = 0;
    private JSONArrayRequestListener mJSONArrayRequestListener;
    private JSONObjectRequestListener mJSONObjectRequestListener;
    private StringRequestListener mStringRequestListener;
    private OkHttpResponseListener mOkHttpResponseListener;
    private BitmapRequestListener mBitmapRequestListener;
    private ParsedRequestListener mParsedRequestListener;
    private OkHttpResponseAndJSONObjectRequestListener mOkHttpResponseAndJSONObjectRequestListener;
    private OkHttpResponseAndJSONArrayRequestListener mOkHttpResponseAndJSONArrayRequestListener;
    private OkHttpResponseAndStringRequestListener mOkHttpResponseAndStringRequestListener;
    private OkHttpResponseAndBitmapRequestListener mOkHttpResponseAndBitmapRequestListener;
    private OkHttpResponseAndParsedRequestListener mOkHttpResponseAndParsedRequestListener;
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
    private Type mType = null;

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
        this.mApplicationJsonString = builder.mApplicationJsonString;
        this.mStringBody = builder.mStringBody;
        this.mFile = builder.mFile;
        this.mByte = builder.mByte;
        this.mCacheControl = builder.mCacheControl;
        this.mExecutor = builder.mExecutor;
        this.mOkHttpClient = builder.mOkHttpClient;
        this.mUserAgent = builder.mUserAgent;
        if (builder.mCustomContentType != null) {
            this.customMediaType = MediaType.parse(builder.mCustomContentType);
        }
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
        if (builder.mCustomContentType != null) {
            this.customMediaType = MediaType.parse(builder.mCustomContentType);
        }
    }

    public void getAsJSONObject(JSONObjectRequestListener requestListener) {
        this.mResponseType = ResponseType.JSON_OBJECT;
        this.mJSONObjectRequestListener = requestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsJSONArray(JSONArrayRequestListener requestListener) {
        this.mResponseType = ResponseType.JSON_ARRAY;
        this.mJSONArrayRequestListener = requestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsString(StringRequestListener requestListener) {
        this.mResponseType = ResponseType.STRING;
        this.mStringRequestListener = requestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsOkHttpResponse(OkHttpResponseListener requestListener) {
        this.mResponseType = ResponseType.OK_HTTP_RESPONSE;
        this.mOkHttpResponseListener = requestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsBitmap(BitmapRequestListener requestListener) {
        this.mResponseType = ResponseType.BITMAP;
        this.mBitmapRequestListener = requestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsParsed(TypeToken typeToken, ParsedRequestListener parsedRequestListener) {
        this.mType = typeToken.getType();
        this.mResponseType = ResponseType.PARSED;
        this.mParsedRequestListener = parsedRequestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsObject(Class objectClass, ParsedRequestListener parsedRequestListener) {
        this.mType = objectClass;
        this.mResponseType = ResponseType.PARSED;
        this.mParsedRequestListener = parsedRequestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsObjectList(Class objectClass, ParsedRequestListener parsedRequestListener) {
        this.mType = $Gson$Types.newParameterizedTypeWithOwner(null, List.class, objectClass);
        this.mResponseType = ResponseType.PARSED;
        this.mParsedRequestListener = parsedRequestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsOkHttpResponseAndJSONObject(OkHttpResponseAndJSONObjectRequestListener requestListener) {
        this.mResponseType = ResponseType.JSON_OBJECT;
        this.mOkHttpResponseAndJSONObjectRequestListener = requestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsOkHttpResponseAndJSONArray(OkHttpResponseAndJSONArrayRequestListener requestListener) {
        this.mResponseType = ResponseType.JSON_ARRAY;
        this.mOkHttpResponseAndJSONArrayRequestListener = requestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsOkHttpResponseAndString(OkHttpResponseAndStringRequestListener requestListener) {
        this.mResponseType = ResponseType.STRING;
        this.mOkHttpResponseAndStringRequestListener = requestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }


    public void getAsOkHttpResponseAndBitmap(OkHttpResponseAndBitmapRequestListener requestListener) {
        this.mResponseType = ResponseType.BITMAP;
        this.mOkHttpResponseAndBitmapRequestListener = requestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsOkHttpResponseAndParsed(TypeToken typeToken, OkHttpResponseAndParsedRequestListener parsedRequestListener) {
        this.mType = typeToken.getType();
        this.mResponseType = ResponseType.PARSED;
        this.mOkHttpResponseAndParsedRequestListener = parsedRequestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsOkHttpResponseAndObject(Class objectClass, OkHttpResponseAndParsedRequestListener parsedRequestListener) {
        this.mType = objectClass;
        this.mResponseType = ResponseType.PARSED;
        this.mOkHttpResponseAndParsedRequestListener = parsedRequestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void getAsOkHttpResponseAndObjectList(Class objectClass, OkHttpResponseAndParsedRequestListener parsedRequestListener) {
        this.mType = $Gson$Types.newParameterizedTypeWithOwner(null, List.class, objectClass);
        this.mResponseType = ResponseType.PARSED;
        this.mOkHttpResponseAndParsedRequestListener = parsedRequestListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void startDownload(DownloadListener downloadListener) {
        this.mDownloadListener = downloadListener;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public void prefetch() {
        this.mResponseType = ResponseType.PREFETCH;
        ANRequestQueue.getInstance().addRequest(this);
    }

    public ANResponse executeForJSONObject() {
        this.mResponseType = ResponseType.JSON_OBJECT;
        return SynchronousCall.execute(this);
    }

    public ANResponse executeForJSONArray() {
        this.mResponseType = ResponseType.JSON_ARRAY;
        return SynchronousCall.execute(this);
    }

    public ANResponse executeForString() {
        this.mResponseType = ResponseType.STRING;
        return SynchronousCall.execute(this);
    }

    public ANResponse executeForOkHttpResponse() {
        this.mResponseType = ResponseType.OK_HTTP_RESPONSE;
        return SynchronousCall.execute(this);
    }

    public ANResponse executeForBitmap() {
        this.mResponseType = ResponseType.BITMAP;
        return SynchronousCall.execute(this);
    }

    public ANResponse executeForParsed(TypeToken typeToken) {
        this.mType = typeToken.getType();
        this.mResponseType = ResponseType.PARSED;
        return SynchronousCall.execute(this);
    }

    public ANResponse executeForObject(Class objectClass) {
        this.mType = objectClass;
        this.mResponseType = ResponseType.PARSED;
        return SynchronousCall.execute(this);
    }

    public ANResponse executeForObjectList(Class objectClass) {
        this.mType = $Gson$Types.newParameterizedTypeWithOwner(null, List.class, objectClass);
        this.mResponseType = ResponseType.PARSED;
        return SynchronousCall.execute(this);
    }

    public ANResponse executeForDownload() {
        return SynchronousCall.execute(this);
    }

    public T setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.mDownloadProgressListener = downloadProgressListener;
        return (T) this;
    }

    public T setUploadProgressListener(UploadProgressListener uploadProgressListener) {
        this.mUploadProgressListener = uploadProgressListener;
        return (T) this;
    }

    public T setAnalyticsListener(AnalyticsListener analyticsListener) {
        this.mAnalyticsListener = analyticsListener;
        return (T) this;
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
        if (mQueryParameterMap != null) {
            Set<Map.Entry<String, List<String>>> entries = mQueryParameterMap.entrySet();
            for (Map.Entry<String, List<String>> entry : entries) {
                String name = entry.getKey();
                List<String> list = entry.getValue();
                if (list != null) {
                    for (String value : list) {
                        urlBuilder.addQueryParameter(name, value);
                    }
                }
            }
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

    public void setResponseAs(ResponseType responseType) {
        this.mResponseType = responseType;
    }

    public ResponseType getResponseAs() {
        return mResponseType;
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

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        this.mType = type;
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
                            finish();
                        }
                    });
                }
            } else {
                deliverError(new ANError());
                finish();
            }
        } else {
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
            if (forceCancel || mPercentageThresholdForCancelling == 0
                    || mProgress < mPercentageThresholdForCancelling) {
                isCancelled = true;
                isRunning = false;
                if (call != null) {
                    call.cancel();
                }
                if (future != null) {
                    future.cancel(true);
                }
                if (!isDelivered) {
                    deliverError(new ANError());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isCanceled() {
        return isCancelled;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
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
        mJSONObjectRequestListener = null;
        mStringRequestListener = null;
        mBitmapRequestListener = null;
        mParsedRequestListener = null;
        mDownloadProgressListener = null;
        mUploadProgressListener = null;
        mDownloadListener = null;
        mAnalyticsListener = null;
    }

    public void finish() {
        destroy();
        ANRequestQueue.getInstance().finish(this);
    }

    public ANResponse parseResponse(Response response) {
        switch (mResponseType) {
            case JSON_ARRAY:
                try {
                    JSONArray json = new JSONArray(Okio.buffer(response.body().source()).readUtf8());
                    return ANResponse.success(json);
                } catch (Exception e) {
                    return ANResponse.failed(Utils.getErrorForParse(new ANError(e)));
                }
            case JSON_OBJECT:
                try {
                    JSONObject json = new JSONObject(Okio.buffer(response.body()
                            .source()).readUtf8());
                    return ANResponse.success(json);
                } catch (Exception e) {
                    return ANResponse.failed(Utils.getErrorForParse(new ANError(e)));
                }
            case STRING:
                try {
                    return ANResponse.success(Okio.buffer(response
                            .body().source()).readUtf8());
                } catch (Exception e) {
                    return ANResponse.failed(Utils.getErrorForParse(new ANError(e)));
                }
            case BITMAP:
                synchronized (sDecodeLock) {
                    try {
                        return Utils.decodeBitmap(response, mMaxWidth, mMaxHeight,
                                mDecodeConfig, mScaleType);
                    } catch (Exception e) {
                        return ANResponse.failed(Utils.getErrorForParse(new ANError(e)));
                    }
                }
            case PARSED:
                try {
                    return ANResponse.success(ParseUtil.getParserFactory()
                            .responseBodyParser(mType).convert(response.body()));
                } catch (Exception e) {
                    return ANResponse.failed(Utils.getErrorForParse(new ANError(e)));
                }
            case PREFETCH:
                try {
                    Okio.buffer(response.body().source()).skip(Long.MAX_VALUE);
                    return ANResponse.success(ANConstants.PREFETCH);
                } catch (Exception e) {
                    return ANResponse.failed(Utils.getErrorForParse(new ANError(e)));
                }
        }
        return null;
    }

    public ANError parseNetworkError(ANError anError) {
        try {
            if (anError.getResponse() != null && anError.getResponse().body() != null
                    && anError.getResponse().body().source() != null) {
                anError.setErrorBody(Okio.buffer(anError
                        .getResponse().body().source()).readUtf8());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return anError;
    }

    public synchronized void deliverError(ANError anError) {
        try {
            if (!isDelivered) {
                if (isCancelled) {
                    anError.setCancellationMessageInError();
                    anError.setErrorCode(0);
                }
                deliverErrorResponse(anError);
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
                            deliverSuccessResponse(response);
                        }
                    });
                } else {
                    Core.getInstance().getExecutorSupplier().forMainThreadTasks().execute(new Runnable() {
                        public void run() {
                            deliverSuccessResponse(response);
                        }
                    });
                }
            } else {
                ANError anError = new ANError();
                anError.setCancellationMessageInError();
                anError.setErrorCode(0);
                deliverErrorResponse(anError);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deliverSuccessResponse(ANResponse response) {
        if (mJSONObjectRequestListener != null) {
            mJSONObjectRequestListener.onResponse((JSONObject) response.getResult());
        } else if (mJSONArrayRequestListener != null) {
            mJSONArrayRequestListener.onResponse((JSONArray) response.getResult());
        } else if (mStringRequestListener != null) {
            mStringRequestListener.onResponse((String) response.getResult());
        } else if (mBitmapRequestListener != null) {
            mBitmapRequestListener.onResponse((Bitmap) response.getResult());
        } else if (mParsedRequestListener != null) {
            mParsedRequestListener.onResponse(response.getResult());
        } else if (mOkHttpResponseAndJSONObjectRequestListener != null) {
            mOkHttpResponseAndJSONObjectRequestListener.onResponse(response.getOkHttpResponse(), (JSONObject) response.getResult());
        } else if (mOkHttpResponseAndJSONArrayRequestListener != null) {
            mOkHttpResponseAndJSONArrayRequestListener.onResponse(response.getOkHttpResponse(), (JSONArray) response.getResult());
        } else if (mOkHttpResponseAndStringRequestListener != null) {
            mOkHttpResponseAndStringRequestListener.onResponse(response.getOkHttpResponse(), (String) response.getResult());
        } else if (mOkHttpResponseAndBitmapRequestListener != null) {
            mOkHttpResponseAndBitmapRequestListener.onResponse(response.getOkHttpResponse(), (Bitmap) response.getResult());
        } else if (mOkHttpResponseAndParsedRequestListener != null) {
            mOkHttpResponseAndParsedRequestListener.onResponse(response.getOkHttpResponse(), response.getResult());
        }
        finish();
    }

    private void deliverErrorResponse(ANError anError) {
        if (mJSONObjectRequestListener != null) {
            mJSONObjectRequestListener.onError(anError);
        } else if (mJSONArrayRequestListener != null) {
            mJSONArrayRequestListener.onError(anError);
        } else if (mStringRequestListener != null) {
            mStringRequestListener.onError(anError);
        } else if (mBitmapRequestListener != null) {
            mBitmapRequestListener.onError(anError);
        } else if (mParsedRequestListener != null) {
            mParsedRequestListener.onError(anError);
        } else if (mOkHttpResponseListener != null) {
            mOkHttpResponseListener.onError(anError);
        } else if (mOkHttpResponseAndJSONObjectRequestListener != null) {
            mOkHttpResponseAndJSONObjectRequestListener.onError(anError);
        } else if (mOkHttpResponseAndJSONArrayRequestListener != null) {
            mOkHttpResponseAndJSONArrayRequestListener.onError(anError);
        } else if (mOkHttpResponseAndStringRequestListener != null) {
            mOkHttpResponseAndStringRequestListener.onError(anError);
        } else if (mOkHttpResponseAndBitmapRequestListener != null) {
            mOkHttpResponseAndBitmapRequestListener.onError(anError);
        } else if (mOkHttpResponseAndParsedRequestListener != null) {
            mOkHttpResponseAndParsedRequestListener.onError(anError);
        } else if (mDownloadListener != null) {
            mDownloadListener.onError(anError);
        }
    }

    public void deliverOkHttpResponse(final Response response) {
        try {
            isDelivered = true;
            if (!isCancelled) {
                if (mExecutor != null) {
                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (mOkHttpResponseListener != null) {
                                mOkHttpResponseListener.onResponse(response);
                            }
                            finish();
                        }
                    });
                } else {
                    Core.getInstance().getExecutorSupplier().forMainThreadTasks().execute(new Runnable() {
                        public void run() {
                            if (mOkHttpResponseListener != null) {
                                mOkHttpResponseListener.onResponse(response);
                            }
                            finish();
                        }
                    });
                }
            } else {
                ANError anError = new ANError();
                anError.setCancellationMessageInError();
                anError.setErrorCode(0);
                if (mOkHttpResponseListener != null) {
                    mOkHttpResponseListener.onError(anError);
                }
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RequestBody getRequestBody() {
        if (mApplicationJsonString != null) {
            if (customMediaType != null) {
                return RequestBody.create(customMediaType, mApplicationJsonString);
            }
            return RequestBody.create(JSON_MEDIA_TYPE, mApplicationJsonString);
        } else if (mStringBody != null) {
            if (customMediaType != null) {
                return RequestBody.create(customMediaType, mStringBody);
            }
            return RequestBody.create(MEDIA_TYPE_MARKDOWN, mStringBody);
        } else if (mFile != null) {
            if (customMediaType != null) {
                return RequestBody.create(customMediaType, mFile);
            }
            return RequestBody.create(MEDIA_TYPE_MARKDOWN, mFile);
        } else if (mByte != null) {
            if (customMediaType != null) {
                return RequestBody.create(customMediaType, mByte);
            }
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
        MultipartBody.Builder builder = new MultipartBody
                .Builder()
                .setType((customMediaType == null) ? MultipartBody.FORM : customMediaType);
        try {
            for (HashMap.Entry<String, String> entry : mMultiPartParameterMap.entrySet()) {
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + entry.getKey() + "\""),
                        RequestBody.create(null, entry.getValue()));
            }
            for (HashMap.Entry<String, File> entry : mMultiPartFileMap.entrySet()) {
                String fileName = entry.getValue().getName();
                RequestBody fileBody = RequestBody.create(MediaType.parse(Utils.getMimeType(fileName)),
                        entry.getValue());
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + entry.getKey() + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    public Headers getHeaders() {
        Headers.Builder builder = new Headers.Builder();
        try {
            if (mHeadersMap != null) {
                Set<Map.Entry<String, List<String>>> entries = mHeadersMap.entrySet();
                for (Map.Entry<String, List<String>> entry : entries) {
                    String name = entry.getKey();
                    List<String> list = entry.getValue();
                    if (list != null) {
                        for (String value : list) {
                            builder.add(name, value);
                        }
                    }
                }
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

    public static class OptionsRequestBuilder extends GetRequestBuilder {

        public OptionsRequestBuilder(String url) {
            super(url, Method.OPTIONS);
        }
    }

    public static class GetRequestBuilder<T extends GetRequestBuilder> implements RequestBuilder {
        private Priority mPriority = Priority.MEDIUM;
        private int mMethod = Method.GET;
        private String mUrl;
        private Object mTag;
        private Bitmap.Config mDecodeConfig;
        private BitmapFactory.Options mBitmapOptions;
        private int mMaxWidth;
        private int mMaxHeight;
        private ImageView.ScaleType mScaleType;
        private HashMap<String, List<String>> mHeadersMap = new HashMap<>();
        private HashMap<String, List<String>> mQueryParameterMap = new HashMap<>();
        private HashMap<String, String> mPathParameterMap = new HashMap<>();
        private CacheControl mCacheControl;
        private Executor mExecutor;
        private OkHttpClient mOkHttpClient;
        private String mUserAgent;

        public GetRequestBuilder(String url) {
            this.mUrl = url;
            this.mMethod = Method.GET;
        }

        public GetRequestBuilder(String url, int method) {
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
            List<String> list = mQueryParameterMap.get(key);
            if (list == null) {
                list = new ArrayList<>();
                mQueryParameterMap.put(key, list);
            }
            if (!list.contains(value)) {
                list.add(value);
            }
            return (T) this;
        }

        @Override
        public T addQueryParameter(Map<String, String> queryParameterMap) {
            if (queryParameterMap != null) {
                for (HashMap.Entry<String, String> entry : queryParameterMap.entrySet()) {
                    addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T addQueryParameter(Object object) {
            if (object != null) {
                return addQueryParameter(ParseUtil
                        .getParserFactory()
                        .getStringMap(object));
            }
            return (T) this;
        }

        @Override
        public T addPathParameter(String key, String value) {
            mPathParameterMap.put(key, value);
            return (T) this;
        }

        @Override
        public T addPathParameter(Map<String, String> pathParameterMap) {
            if (pathParameterMap != null) {
                mPathParameterMap.putAll(pathParameterMap);
            }
            return (T) this;
        }

        @Override
        public T addPathParameter(Object object) {
            if (object != null) {
                mPathParameterMap.putAll(ParseUtil
                        .getParserFactory()
                        .getStringMap(object));
            }
            return (T) this;
        }

        @Override
        public T addHeaders(String key, String value) {
            List<String> list = mHeadersMap.get(key);
            if (list == null) {
                list = new ArrayList<>();
                mHeadersMap.put(key, list);
            }
            if (!list.contains(value)) {
                list.add(value);
            }
            return (T) this;
        }

        @Override
        public T addHeaders(Map<String, String> headerMap) {
            if (headerMap != null) {
                for (HashMap.Entry<String, String> entry : headerMap.entrySet()) {
                    addHeaders(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T addHeaders(Object object) {
            if (object != null) {
                return addHeaders(ParseUtil
                        .getParserFactory()
                        .getStringMap(object));
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

        public T setBitmapOptions(BitmapFactory.Options bitmapOptions) {
            mBitmapOptions = bitmapOptions;
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

    public static class DynamicRequestBuilder extends PostRequestBuilder {

        public DynamicRequestBuilder(String url, int method) {
            super(url, method);
        }
    }

    public static class PostRequestBuilder<T extends PostRequestBuilder> implements RequestBuilder {

        private Priority mPriority = Priority.MEDIUM;
        private int mMethod = Method.POST;
        private String mUrl;
        private Object mTag;
        private String mApplicationJsonString = null;
        private String mStringBody = null;
        private byte[] mByte = null;
        private File mFile = null;
        private HashMap<String, List<String>> mHeadersMap = new HashMap<>();
        private HashMap<String, String> mBodyParameterMap = new HashMap<>();
        private HashMap<String, String> mUrlEncodedFormBodyParameterMap = new HashMap<>();
        private HashMap<String, List<String>> mQueryParameterMap = new HashMap<>();
        private HashMap<String, String> mPathParameterMap = new HashMap<>();
        private CacheControl mCacheControl;
        private Executor mExecutor;
        private OkHttpClient mOkHttpClient;
        private String mUserAgent;
        private String mCustomContentType;

        public PostRequestBuilder(String url) {
            this.mUrl = url;
            this.mMethod = Method.POST;
        }

        public PostRequestBuilder(String url, int method) {
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
            List<String> list = mQueryParameterMap.get(key);
            if (list == null) {
                list = new ArrayList<>();
                mQueryParameterMap.put(key, list);
            }
            if (!list.contains(value)) {
                list.add(value);
            }
            return (T) this;
        }

        @Override
        public T addQueryParameter(Map<String, String> queryParameterMap) {
            if (queryParameterMap != null) {
                for (HashMap.Entry<String, String> entry : queryParameterMap.entrySet()) {
                    addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T addQueryParameter(Object object) {
            if (object != null) {
                return addQueryParameter(ParseUtil
                        .getParserFactory()
                        .getStringMap(object));
            }
            return (T) this;
        }

        @Override
        public T addPathParameter(String key, String value) {
            mPathParameterMap.put(key, value);
            return (T) this;
        }

        @Override
        public T addPathParameter(Map<String, String> pathParameterMap) {
            if (pathParameterMap != null) {
                mPathParameterMap.putAll(pathParameterMap);
            }
            return (T) this;
        }

        @Override
        public T addPathParameter(Object object) {
            if (object != null) {
                mPathParameterMap.putAll(ParseUtil
                        .getParserFactory()
                        .getStringMap(object));
            }
            return (T) this;
        }

        @Override
        public T addHeaders(String key, String value) {
            List<String> list = mHeadersMap.get(key);
            if (list == null) {
                list = new ArrayList<>();
                mHeadersMap.put(key, list);
            }
            if (!list.contains(value)) {
                list.add(value);
            }
            return (T) this;
        }

        @Override
        public T addHeaders(Map<String, String> headerMap) {
            if (headerMap != null) {
                for (HashMap.Entry<String, String> entry : headerMap.entrySet()) {
                    addHeaders(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T addHeaders(Object object) {
            if (object != null) {
                return addHeaders(ParseUtil
                        .getParserFactory()
                        .getStringMap(object));
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

        public T addBodyParameter(Map<String, String> bodyParameterMap) {
            if (bodyParameterMap != null) {
                mBodyParameterMap.putAll(bodyParameterMap);
            }
            return (T) this;
        }

        public T addBodyParameter(Object object) {
            if (object != null) {
                mBodyParameterMap.putAll(ParseUtil
                        .getParserFactory()
                        .getStringMap(object));
            }
            return (T) this;
        }

        public T addUrlEncodeFormBodyParameter(String key, String value) {
            mUrlEncodedFormBodyParameterMap.put(key, value);
            return (T) this;
        }

        public T addUrlEncodeFormBodyParameter(Map<String, String> bodyParameterMap) {
            if (bodyParameterMap != null) {
                mUrlEncodedFormBodyParameterMap.putAll(bodyParameterMap);
            }
            return (T) this;
        }

        public T addUrlEncodeFormBodyParameter(Object object) {
            if (object != null) {
                mUrlEncodedFormBodyParameterMap.putAll(ParseUtil
                        .getParserFactory()
                        .getStringMap(object));
            }
            return (T) this;
        }

        public T addApplicationJsonBody(Object object) {
            if (object != null) {
                mApplicationJsonString = ParseUtil
                        .getParserFactory()
                        .getString(object);
            }
            return (T) this;
        }

        public T addJSONObjectBody(JSONObject jsonObject) {
            if (jsonObject != null) {
                mApplicationJsonString = jsonObject.toString();
            }
            return (T) this;
        }

        public T addJSONArrayBody(JSONArray jsonArray) {
            if (jsonArray != null) {
                mApplicationJsonString = jsonArray.toString();
            }
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

        public T setContentType(String contentType) {
            mCustomContentType = contentType;
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
        private HashMap<String, List<String>> mHeadersMap = new HashMap<>();
        private HashMap<String, List<String>> mQueryParameterMap = new HashMap<>();
        private HashMap<String, String> mPathParameterMap = new HashMap<>();
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
            List<String> list = mHeadersMap.get(key);
            if (list == null) {
                list = new ArrayList<>();
                mHeadersMap.put(key, list);
            }
            if (!list.contains(value)) {
                list.add(value);
            }
            return (T) this;
        }

        @Override
        public T addHeaders(Map<String, String> headerMap) {
            if (headerMap != null) {
                for (HashMap.Entry<String, String> entry : headerMap.entrySet()) {
                    addHeaders(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T addHeaders(Object object) {
            if (object != null) {
                return addHeaders(ParseUtil
                        .getParserFactory()
                        .getStringMap(object));
            }
            return (T) this;
        }

        @Override
        public T addQueryParameter(String key, String value) {
            List<String> list = mQueryParameterMap.get(key);
            if (list == null) {
                list = new ArrayList<>();
                mQueryParameterMap.put(key, list);
            }
            if (!list.contains(value)) {
                list.add(value);
            }
            return (T) this;
        }

        @Override
        public T addQueryParameter(Map<String, String> queryParameterMap) {
            if (queryParameterMap != null) {
                for (HashMap.Entry<String, String> entry : queryParameterMap.entrySet()) {
                    addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T addQueryParameter(Object object) {
            if (object != null) {
                return addQueryParameter(ParseUtil
                        .getParserFactory()
                        .getStringMap(object));
            }
            return (T) this;
        }

        @Override
        public T addPathParameter(String key, String value) {
            mPathParameterMap.put(key, value);
            return (T) this;
        }

        @Override
        public T addPathParameter(Map<String, String> pathParameterMap) {
            if (pathParameterMap != null) {
                mPathParameterMap.putAll(pathParameterMap);
            }
            return (T) this;
        }

        @Override
        public T addPathParameter(Object object) {
            if (object != null) {
                mPathParameterMap.putAll(ParseUtil
                        .getParserFactory()
                        .getStringMap(object));
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
        private HashMap<String, List<String>> mHeadersMap = new HashMap<>();
        private HashMap<String, String> mMultiPartParameterMap = new HashMap<>();
        private HashMap<String, List<String>> mQueryParameterMap = new HashMap<>();
        private HashMap<String, String> mPathParameterMap = new HashMap<>();
        private HashMap<String, File> mMultiPartFileMap = new HashMap<>();
        private CacheControl mCacheControl;
        private int mPercentageThresholdForCancelling = 0;
        private Executor mExecutor;
        private OkHttpClient mOkHttpClient;
        private String mUserAgent;
        private String mCustomContentType;

        public MultiPartBuilder(String url) {
            this.mUrl = url;
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
            List<String> list = mQueryParameterMap.get(key);
            if (list == null) {
                list = new ArrayList<>();
                mQueryParameterMap.put(key, list);
            }
            if (!list.contains(value)) {
                list.add(value);
            }
            return (T) this;
        }

        @Override
        public T addQueryParameter(Map<String, String> queryParameterMap) {
            if (queryParameterMap != null) {
                for (HashMap.Entry<String, String> entry : queryParameterMap.entrySet()) {
                    addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T addQueryParameter(Object object) {
            if (object != null) {
                return addQueryParameter(ParseUtil
                        .getParserFactory()
                        .getStringMap(object));
            }
            return (T) this;
        }

        @Override
        public T addPathParameter(String key, String value) {
            mPathParameterMap.put(key, value);
            return (T) this;
        }

        @Override
        public T addPathParameter(Map<String, String> pathParameterMap) {
            if (pathParameterMap != null) {
                mPathParameterMap.putAll(pathParameterMap);
            }
            return (T) this;
        }

        @Override
        public T addPathParameter(Object object) {
            if (object != null) {
                mPathParameterMap.putAll(ParseUtil
                        .getParserFactory()
                        .getStringMap(object));
            }
            return (T) this;
        }

        @Override
        public T addHeaders(String key, String value) {
            List<String> list = mHeadersMap.get(key);
            if (list == null) {
                list = new ArrayList<>();
                mHeadersMap.put(key, list);
            }
            if (!list.contains(value)) {
                list.add(value);
            }
            return (T) this;
        }

        @Override
        public T addHeaders(Map<String, String> headerMap) {
            if (headerMap != null) {
                for (HashMap.Entry<String, String> entry : headerMap.entrySet()) {
                    addHeaders(entry.getKey(), entry.getValue());
                }
            }
            return (T) this;
        }

        @Override
        public T addHeaders(Object object) {
            if (object != null) {
                return addHeaders(ParseUtil
                        .getParserFactory()
                        .getStringMap(object));
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

        public T addMultipartParameter(Map<String, String> multiPartParameterMap) {
            if (multiPartParameterMap != null) {
                mMultiPartParameterMap.putAll(multiPartParameterMap);
            }
            return (T) this;
        }

        public T addMultipartParameter(Object object) {
            if (object != null) {
                mMultiPartParameterMap.putAll(ParseUtil
                        .getParserFactory()
                        .getStringMap(object));
            }
            return (T) this;
        }

        public T addMultipartFile(String key, File file) {
            mMultiPartFileMap.put(key, file);
            return (T) this;
        }

        public T addMultipartFile(Map<String, File> multiPartFileMap) {
            if (multiPartFileMap != null) {
                mMultiPartFileMap.putAll(multiPartFileMap);
            }
            return (T) this;
        }

        public T setPercentageThresholdForCancelling(int percentageThresholdForCancelling) {
            this.mPercentageThresholdForCancelling = percentageThresholdForCancelling;
            return (T) this;
        }

        public T setContentType(String contentType) {
            mCustomContentType = contentType;
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
