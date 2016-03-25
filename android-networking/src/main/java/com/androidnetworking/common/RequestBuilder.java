package com.androidnetworking.common;

import java.io.File;

/**
 * Created by amitshekhar on 26/03/16.
 */
public interface RequestBuilder {

    RequestBuilder setMethod(int method);

    RequestBuilder setPriority(Priority priority);

    RequestBuilder setUrl(String url);

    RequestBuilder setTag(Object tag);

    RequestBuilder addHeaders(String key, String value);

    RequestBuilder addBodyParameter(String key, String value);

    RequestBuilder addMultipartFile(String key, String value);

    RequestBuilder addMultipartFile(String key, File file);

    RequestBuilder addMultipartFile(String key, String contentType, File file);

    RequestBuilder addMultipartFile(String key, String fileName, String contentType, File file);

    RequestBuilder addQueryParameter(String key, String value);

    RequestBuilder addPathParameter(String key, String value);

}
