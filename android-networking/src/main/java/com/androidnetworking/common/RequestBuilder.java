package com.androidnetworking.common;

/**
 * Created by amitshekhar on 26/03/16.
 */
public interface RequestBuilder {

    RequestBuilder setPriority(Priority priority);

    RequestBuilder setTag(Object tag);

    RequestBuilder addHeaders(String key, String value);

    RequestBuilder addQueryParameter(String key, String value);

    RequestBuilder addPathParameter(String key, String value);

}
