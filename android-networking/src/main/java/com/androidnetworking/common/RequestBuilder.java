package com.androidnetworking.common;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by amitshekhar on 26/03/16.
 */
public interface RequestBuilder {

    RequestBuilder setMethod(int method);

    RequestBuilder setResponseAs(RESPONSE responseAs);

    RequestBuilder setPriority(Priority priority);

    RequestBuilder setUrl(String url);

    RequestBuilder setTag(Object tag);

    RequestBuilder setBitmapConfig(Bitmap.Config bitmapConfig);

    RequestBuilder setBitmapMaxHeight(int maxHeight);

    RequestBuilder setBitmapMaxWidth(int maxWidth);

    RequestBuilder setImageScaleType(ImageView.ScaleType imageScaleType);

    RequestBuilder addHeaders(String key, String value);

    RequestBuilder addBodyParameter(String key, String value);

    RequestBuilder addMultipartFile(String key, String value);

    RequestBuilder addMultipartFile(String key, File file);

    RequestBuilder addMultipartFile(String key, String contentType, File file);

    RequestBuilder addMultipartFile(String key, String fileName, String contentType, File file);

    RequestBuilder addQueryParameter(String key, String value);

    RequestBuilder addPathParameter(String key, String value);

}
