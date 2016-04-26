package com.androidnetworking.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.androidnetworking.error.AndroidNetworkingError;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.RequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.androidnetworking.internal.AndroidNetworkingRequestQueue;
import com.androidnetworking.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
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
    private RequestListener mRequestListener;
    private DownloadProgressListener mDownloadProgressListener;
    private UploadProgressListener mUploadProgressListener;

    private Bitmap.Config mDecodeConfig;
    private int mMaxWidth;
    private int mMaxHeight;
    private ImageView.ScaleType mScaleType;
    private CacheControl mCacheControl = null;

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
    }

    public void getAsJsonObject(RequestListener requestListener) {
        this.mResponseAs = RESPONSE.JSON_OBJECT;
        this.mRequestListener = requestListener;
        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
    }

    public void getAsJsonArray(RequestListener requestListener) {
        this.mResponseAs = RESPONSE.JSON_ARRAY;
        this.mRequestListener = requestListener;
        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
    }

    public void getAsString(RequestListener requestListener) {
        this.mResponseAs = RESPONSE.STRING;
        this.mRequestListener = requestListener;
        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
    }

    public void getAsBitmap(RequestListener requestListener) {
        this.mResponseAs = RESPONSE.BITMAP;
        this.mRequestListener = requestListener;
        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
    }

    public void startDownload(DownloadProgressListener downloadProgressListener) {
        this.mDownloadProgressListener = downloadProgressListener;
        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
    }

    public void getAsJsonObject(UploadProgressListener uploadProgressListener) {
        this.mResponseAs = RESPONSE.JSON_OBJECT;
        this.mUploadProgressListener = uploadProgressListener;
        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
    }

    public void getAsJsonArray(UploadProgressListener uploadProgressListener) {
        this.mResponseAs = RESPONSE.JSON_ARRAY;
        this.mUploadProgressListener = uploadProgressListener;
        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
    }

    public void getAsString(UploadProgressListener uploadProgressListener) {
        this.mResponseAs = RESPONSE.STRING;
        this.mUploadProgressListener = uploadProgressListener;
        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
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

    public Object getTag() {
        return mTag;
    }

    public int getRequestType() {
        return mRequestType;
    }

    public DownloadProgressListener getDownloadProgressListener() {
        return mDownloadProgressListener;
    }

    public UploadProgressListener getUploadProgressListener() {
        return mUploadProgressListener;
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
        Log.d(TAG, "cancelling request for sequenceNumber : " + sequenceNumber);
        future.cancel(true);
    }

    public boolean isCanceled() {
        return future.isCancelled();
    }

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }

    public void finish() {
        mRequestListener = null;
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
                        return doParse(data);
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
                error.setContent(Okio.buffer(error.getData().source).readUtf8());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return error;
    }

    public void deliverError(AndroidNetworkingError error) {
        if (mRequestListener != null) {
            mRequestListener.onError(error);
        } else if (mDownloadProgressListener != null) {
            mDownloadProgressListener.onError(error);
        } else if (mUploadProgressListener != null) {
            mUploadProgressListener.onError(error);
        }
    }

    public void deliverResponse(AndroidNetworkingResponse response) {
        if (mRequestListener != null) {
            mRequestListener.onResponse(response.getResult());
        } else if (mUploadProgressListener != null) {
            mUploadProgressListener.onResponse(response.getResult());
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


    private AndroidNetworkingResponse<Bitmap> doParse(AndroidNetworkingData response) {
        byte[] data = new byte[0];
        try {
            data = Okio.buffer(response.source).readByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        Bitmap bitmap = null;
        if (mMaxWidth == 0 && mMaxHeight == 0) {
            decodeOptions.inPreferredConfig = mDecodeConfig;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        } else {
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;

            int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
                    actualWidth, actualHeight, mScaleType);
            int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
                    actualHeight, actualWidth, mScaleType);

            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inSampleSize =
                    findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
            Bitmap tempBitmap =
                    BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);

            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }

        if (bitmap == null) {
            return AndroidNetworkingResponse.failed(new AndroidNetworkingError(response));
        } else {
            return AndroidNetworkingResponse.success(bitmap);
        }
    }

    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                           int actualSecondary, ImageView.ScaleType scaleType) {

        if ((maxPrimary == 0) && (maxSecondary == 0)) {
            return actualPrimary;
        }

        if (scaleType == ImageView.ScaleType.FIT_XY) {
            if (maxPrimary == 0) {
                return actualPrimary;
            }
            return maxPrimary;
        }

        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;

        if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            if ((resized * ratio) < maxSecondary) {
                resized = (int) (maxSecondary / ratio);
            }
            return resized;
        }

        if ((resized * ratio) > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    static int findBestSampleSize(
            int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
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
        public DownloadBuilder addQueryParameter(String key, String value) {
            mQueryParameterMap.put(key, value);
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

        public MultiPartBuilder addMultipartParameter(String key, String value) {
            mMultiPartParameterMap.put(key, value);
            return this;
        }

        public MultiPartBuilder addMultipartFile(String key, File file) {
            mMultiPartFileMap.put(key, file);
            return this;
        }

        public AndroidNetworkingRequest build() {
            return new AndroidNetworkingRequest(this);
        }
    }

}
