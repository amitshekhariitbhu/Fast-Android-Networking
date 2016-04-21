package com.androidnetworking.internal;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.cache.LruBitmapCache;
import com.androidnetworking.common.AndroidNetworkingRequest;
import com.androidnetworking.common.Method;
import com.androidnetworking.common.RESPONSE;
import com.androidnetworking.error.AndroidNetworkingError;
import com.androidnetworking.interfaces.RequestListener;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by amitshekhar on 23/03/16.
 */
public class AndroidNetworkingImageLoader {

    private int mBatchResponseDelayMs = 100;

    private final ImageCache mCache;

    private final HashMap<String, BatchedImageRequest> mInFlightRequests =
            new HashMap<String, BatchedImageRequest>();

    private final HashMap<String, BatchedImageRequest> mBatchedResponses =
            new HashMap<String, BatchedImageRequest>();

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private Runnable mRunnable;

    private static AndroidNetworkingImageLoader sInstance;

    public static void initialize() {
        getInstance();
    }

    public static AndroidNetworkingImageLoader getInstance() {
        if (sInstance == null) {
            synchronized (AndroidNetworkingImageLoader.class) {
                sInstance = new AndroidNetworkingImageLoader(new LruBitmapCache(LruBitmapCache.getCacheSize(AndroidNetworking.getContext())));
            }
        }
        return sInstance;
    }

    public interface ImageCache {
        public Bitmap getBitmap(String url);

        public void putBitmap(String url, Bitmap bitmap);
    }

    public AndroidNetworkingImageLoader(ImageCache imageCache) {
        mCache = imageCache;
    }

    public static ImageListener getImageListener(final ImageView view,
                                                 final int defaultImageResId, final int errorImageResId) {
        return new ImageListener() {
            @Override
            public void onResponse(ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    view.setImageBitmap(response.getBitmap());
                } else if (defaultImageResId != 0) {
                    view.setImageResource(defaultImageResId);
                }
            }

            @Override
            public void onError(AndroidNetworkingError error) {
                if (errorImageResId != 0) {
                    view.setImageResource(errorImageResId);
                }
            }
        };
    }

    public interface ImageListener {

        void onResponse(ImageContainer response, boolean isImmediate);

        void onError(AndroidNetworkingError error);
    }

    public boolean isCached(String requestUrl, int maxWidth, int maxHeight) {
        return isCached(requestUrl, maxWidth, maxHeight, ImageView.ScaleType.CENTER_INSIDE);
    }

    public boolean isCached(String requestUrl, int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
        throwIfNotOnMainThread();

        String cacheKey = getCacheKey(requestUrl, maxWidth, maxHeight, scaleType);
        return mCache.getBitmap(cacheKey) != null;
    }

    public ImageContainer get(String requestUrl, final ImageListener listener) {
        return get(requestUrl, listener, 0, 0);
    }


    public ImageContainer get(String requestUrl, ImageListener imageListener,
                              int maxWidth, int maxHeight) {
        return get(requestUrl, imageListener, maxWidth, maxHeight, ImageView.ScaleType.CENTER_INSIDE);
    }

    public ImageContainer get(String requestUrl, ImageListener imageListener,
                              int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {

        throwIfNotOnMainThread();

        final String cacheKey = getCacheKey(requestUrl, maxWidth, maxHeight, scaleType);

        Bitmap cachedBitmap = mCache.getBitmap(cacheKey);
        if (cachedBitmap != null) {
            ImageContainer container = new ImageContainer(cachedBitmap, requestUrl, null, null);
            imageListener.onResponse(container, true);
            return container;
        }

        ImageContainer imageContainer =
                new ImageContainer(null, requestUrl, cacheKey, imageListener);

        imageListener.onResponse(imageContainer, true);

        BatchedImageRequest request = mInFlightRequests.get(cacheKey);
        if (request != null) {
            request.addContainer(imageContainer);
            return imageContainer;
        }

        AndroidNetworkingRequest newRequest = makeImageRequest(requestUrl, maxWidth, maxHeight, scaleType,
                cacheKey);

        mInFlightRequests.put(cacheKey,
                new BatchedImageRequest(newRequest, imageContainer));
        return imageContainer;
    }

    protected AndroidNetworkingRequest makeImageRequest(String requestUrl, int maxWidth, int maxHeight,
                                                        ImageView.ScaleType scaleType, final String cacheKey) {
        AndroidNetworkingRequest androidNetworkingRequest = new AndroidNetworkingRequest.Builder(requestUrl, Method.GET, RESPONSE.BITMAP)
                .setTag("ImageRequestTag")
                .setBitmapMaxHeight(maxHeight)
                .setBitmapMaxWidth(maxWidth)
                .setImageScaleType(scaleType)
                .setBitmapConfig(Bitmap.Config.RGB_565)
                .build();

        androidNetworkingRequest.addRequest(new RequestListener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                onGetImageSuccess(cacheKey, response);
            }

            @Override
            public void onError(AndroidNetworkingError error) {
                onGetImageError(cacheKey, error);
            }
        });

        return androidNetworkingRequest;

    }


    public void setBatchedResponseDelay(int newBatchedResponseDelayMs) {
        mBatchResponseDelayMs = newBatchedResponseDelayMs;
    }


    protected void onGetImageSuccess(String cacheKey, Bitmap response) {
        mCache.putBitmap(cacheKey, response);

        BatchedImageRequest request = mInFlightRequests.remove(cacheKey);

        if (request != null) {
            request.mResponseBitmap = response;

            batchResponse(cacheKey, request);
        }
    }

    protected void onGetImageError(String cacheKey, AndroidNetworkingError error) {
        BatchedImageRequest request = mInFlightRequests.remove(cacheKey);

        if (request != null) {
            request.setError(error);
            batchResponse(cacheKey, request);
        }
    }

    public class ImageContainer {

        private Bitmap mBitmap;

        private final ImageListener mListener;

        private final String mCacheKey;

        private final String mRequestUrl;

        public ImageContainer(Bitmap bitmap, String requestUrl,
                              String cacheKey, ImageListener listener) {
            mBitmap = bitmap;
            mRequestUrl = requestUrl;
            mCacheKey = cacheKey;
            mListener = listener;
        }

        public void cancelRequest() {
            if (mListener == null) {
                return;
            }

            BatchedImageRequest request = mInFlightRequests.get(mCacheKey);
            if (request != null) {
                boolean canceled = request.removeContainerAndCancelIfNecessary(this);
                if (canceled) {
                    mInFlightRequests.remove(mCacheKey);
                }
            } else {
                request = mBatchedResponses.get(mCacheKey);
                if (request != null) {
                    request.removeContainerAndCancelIfNecessary(this);
                    if (request.mContainers.size() == 0) {
                        mBatchedResponses.remove(mCacheKey);
                    }
                }
            }
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }


        public String getRequestUrl() {
            return mRequestUrl;
        }
    }

    private class BatchedImageRequest {

        private final AndroidNetworkingRequest mRequest;

        private Bitmap mResponseBitmap;

        private AndroidNetworkingError mError;

        private final LinkedList<ImageContainer> mContainers = new LinkedList<ImageContainer>();

        public BatchedImageRequest(AndroidNetworkingRequest request, ImageContainer container) {
            mRequest = request;
            mContainers.add(container);
        }

        public void setError(AndroidNetworkingError error) {
            mError = error;
        }

        public AndroidNetworkingError getError() {
            return mError;
        }

        public void addContainer(ImageContainer container) {
            mContainers.add(container);
        }

        public boolean removeContainerAndCancelIfNecessary(ImageContainer container) {
            mContainers.remove(container);
            if (mContainers.size() == 0) {
                mRequest.cancel();
                return true;
            }
            return false;
        }
    }

    private void batchResponse(String cacheKey, BatchedImageRequest request) {
        mBatchedResponses.put(cacheKey, request);
        if (mRunnable == null) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    for (BatchedImageRequest bir : mBatchedResponses.values()) {
                        for (ImageContainer container : bir.mContainers) {
                            if (container.mListener == null) {
                                continue;
                            }
                            if (bir.getError() == null) {
                                container.mBitmap = bir.mResponseBitmap;
                                container.mListener.onResponse(container, false);
                            } else {
                                container.mListener.onError(bir.getError());
                            }
                        }
                    }
                    mBatchedResponses.clear();
                    mRunnable = null;
                }

            };
            mHandler.postDelayed(mRunnable, mBatchResponseDelayMs);
        }
    }

    private void throwIfNotOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("ImageLoader must be invoked from the main thread.");
        }
    }

    private static String getCacheKey(String url, int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
        return new StringBuilder(url.length() + 12).append("#W").append(maxWidth)
                .append("#H").append(maxHeight).append("#S").append(scaleType.ordinal()).append(url)
                .toString();
    }
}
