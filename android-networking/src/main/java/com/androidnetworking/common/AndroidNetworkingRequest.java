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
    private HashMap<String, String> mMultiPartParameterMap = new HashMap<String, String>();
    private HashMap<String, String> mQueryParameterMap = new HashMap<String, String>();
    private HashMap<String, String> mPathParameterMap = new HashMap<String, String>();
    private HashMap<String, File> mMultiPartFileMap = new HashMap<String, File>();
    private String mDirPath;
    private String mFileName;
    private static final Object sDecodeLock = new Object();

    private boolean mResponseDelivered = false;
    private Future future;
    private RequestListener mRequestListener;
    private DownloadProgressListener mDownloadProgressListener;
    private UploadProgressListener mUploadProgressListener;

    private Bitmap.Config mDecodeConfig;
    private int mMaxWidth;
    private int mMaxHeight;
    private ImageView.ScaleType mScaleType;

    private AndroidNetworkingRequest(Builder builder) {
        this.mRequestType = RequestType.SIMPLE;
        this.mMethod = builder.mMethod;
        this.mPriority = builder.mPriority;
        this.mUrl = builder.mUrl;
        this.mTag = builder.mTag;
        this.mHeadersMap = builder.mHeadersMap;
        this.mResponseAs = builder.mResponseAs;
        this.mDecodeConfig = builder.mDecodeConfig;
        this.mMaxHeight = builder.mMaxHeight;
        this.mMaxWidth = builder.mMaxWidth;
        this.mScaleType = builder.mScaleType;
        this.mBodyParameterMap = builder.mBodyParameterMap;
        this.mQueryParameterMap = builder.mQueryParameterMap;
        this.mPathParameterMap = builder.mPathParameterMap;
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
    }

    private AndroidNetworkingRequest(MultiPartBuilder builder) {
        this.mRequestType = RequestType.MULTIPART;
        this.mMethod = Method.POST;
        this.mPriority = builder.mPriority;
        this.mUrl = builder.mUrl;
        this.mTag = builder.mTag;
        this.mResponseAs = builder.mResponseAs;
        this.mHeadersMap = builder.mHeadersMap;
        this.mQueryParameterMap = builder.mQueryParameterMap;
        this.mPathParameterMap = builder.mPathParameterMap;
        this.mMultiPartParameterMap = builder.mMultiPartParameterMap;
        this.mMultiPartFileMap = builder.mMultiPartFileMap;
    }

    public void addRequest(RequestListener requestListener) {
        this.mRequestListener = requestListener;
        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
    }

    public void download(DownloadProgressListener downloadProgressListener) {
        this.mDownloadProgressListener = downloadProgressListener;
        AndroidNetworkingRequestQueue.getInstance().addRequest(this);
    }

    public void upload(UploadProgressListener uploadProgressListener) {
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

    public RESPONSE getResponseAs() {
        return mResponseAs;
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

    public Bitmap.Config getDecodeConfig() {
        return mDecodeConfig;
    }

    public int getMaxWidth() {
        return mMaxWidth;
    }

    public int getMaxHeight() {
        return mMaxHeight;
    }

    public String getDirPath() {
        return mDirPath;
    }

    public String getFileName() {
        return mFileName;
    }

    public ImageView.ScaleType getScaleType() {
        return mScaleType;
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
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to parse error response", ioe);
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
        FormBody.Builder builder = new FormBody.Builder();
        for (HashMap.Entry<String, String> entry : mBodyParameterMap.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
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

    public static class Builder implements RequestBuilder {

        private int mMethod;
        private Priority mPriority;
        private String mUrl;
        private Object mTag;
        private RESPONSE mResponseAs;
        private Bitmap.Config mDecodeConfig;
        private int mMaxWidth;
        private int mMaxHeight;
        private ImageView.ScaleType mScaleType;
        private HashMap<String, String> mHeadersMap = new HashMap<String, String>();
        private HashMap<String, String> mBodyParameterMap = new HashMap<String, String>();
        private HashMap<String, String> mQueryParameterMap = new HashMap<String, String>();
        private HashMap<String, String> mPathParameterMap = new HashMap<String, String>();

        public Builder() {

        }

        @Override
        public Builder setUrl(String url) {
            this.mUrl = url;
            return this;
        }

        @Override
        public Builder setPriority(Priority priority) {
            this.mPriority = priority;
            return this;
        }

        @Override
        public Builder setTag(Object tag) {
            this.mTag = tag;
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

        @Override
        public Builder addHeaders(String key, String value) {
            mHeadersMap.put(key, value);
            return this;
        }

        public Builder setMethod(int method) {
            this.mMethod = method;
            return this;
        }

        public Builder setResponseAs(RESPONSE responseAs) {
            this.mResponseAs = responseAs;
            return this;
        }

        public Builder setBitmapConfig(Bitmap.Config bitmapConfig) {
            this.mDecodeConfig = bitmapConfig;
            return this;
        }

        public Builder setBitmapMaxHeight(int maxHeight) {
            this.mMaxHeight = maxHeight;
            return this;
        }

        public Builder setBitmapMaxWidth(int maxWidth) {
            this.mMaxWidth = maxWidth;
            return this;
        }

        public Builder setImageScaleType(ImageView.ScaleType imageScaleType) {
            this.mScaleType = imageScaleType;
            return this;
        }

        public Builder addBodyParameter(String key, String value) {
            mBodyParameterMap.put(key, value);
            return this;
        }

        public AndroidNetworkingRequest build() {
            AndroidNetworkingRequest androidNetworkingRequest = new AndroidNetworkingRequest(this);
            return androidNetworkingRequest;
        }
    }

    public static class DownloadBuilder implements RequestBuilder {

        private Priority mPriority;
        private String mUrl;
        private Object mTag;
        private HashMap<String, String> mHeadersMap = new HashMap<String, String>();
        private HashMap<String, String> mQueryParameterMap = new HashMap<String, String>();
        private HashMap<String, String> mPathParameterMap = new HashMap<String, String>();
        private String mDirPath;
        private String mFileName;

        public DownloadBuilder() {

        }

        @Override
        public DownloadBuilder setPriority(Priority priority) {
            this.mPriority = priority;
            return this;
        }

        @Override
        public DownloadBuilder setUrl(String url) {
            this.mUrl = url;
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

        public DownloadBuilder setDirPath(String path) {
            this.mDirPath = path;
            return this;
        }

        public DownloadBuilder setFileName(String fileName) {
            this.mFileName = fileName;
            return this;
        }

        public AndroidNetworkingRequest build() {
            AndroidNetworkingRequest androidNetworkingRequest = new AndroidNetworkingRequest(this);
            return androidNetworkingRequest;
        }
    }

    public static class MultiPartBuilder implements RequestBuilder {

        private Priority mPriority;
        private String mUrl;
        private Object mTag;
        private RESPONSE mResponseAs;
        private HashMap<String, String> mHeadersMap = new HashMap<String, String>();
        private HashMap<String, String> mMultiPartParameterMap = new HashMap<String, String>();
        private HashMap<String, String> mQueryParameterMap = new HashMap<String, String>();
        private HashMap<String, String> mPathParameterMap = new HashMap<String, String>();
        private HashMap<String, File> mMultiPartFileMap = new HashMap<String, File>();

        public MultiPartBuilder() {

        }

        @Override
        public MultiPartBuilder setUrl(String url) {
            this.mUrl = url;
            return this;
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

        public MultiPartBuilder setResponseAs(RESPONSE responseAs) {
            this.mResponseAs = responseAs;
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
            AndroidNetworkingRequest androidNetworkingRequest = new AndroidNetworkingRequest(this);
            return androidNetworkingRequest;
        }
    }

}
