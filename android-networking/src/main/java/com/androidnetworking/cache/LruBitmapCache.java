package com.androidnetworking.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.androidnetworking.internal.AndroidNetworkingImageLoader;

/**
 * Created by amitshekhar on 24/03/16.
 */
public class LruBitmapCache extends LruCache<String, Bitmap>
        implements AndroidNetworkingImageLoader.ImageCache {

    public LruBitmapCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

}
