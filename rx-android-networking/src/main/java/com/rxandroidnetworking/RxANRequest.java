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

package com.rxandroidnetworking;

import android.graphics.Bitmap;

import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Method;
import com.androidnetworking.common.RequestType;
import com.androidnetworking.common.ResponseType;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import rx.Observable;

/**
 * Created by amitshekhar on 10/06/16.
 */
@SuppressWarnings({"unchecked", "unused"})
public class RxANRequest extends ANRequest<RxANRequest> {

    public RxANRequest(GetRequestBuilder builder) {
        super(builder);
    }

    public RxANRequest(PostRequestBuilder builder) {
        super(builder);
    }

    public RxANRequest(DownloadBuilder builder) {
        super(builder);
    }

    public RxANRequest(MultiPartBuilder builder) {
        super(builder);
    }

    public Observable<JSONObject> getJSONObjectObservable() {
        this.setResponseAs(ResponseType.JSON_OBJECT);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return RxInternalNetworking.generateSimpleObservable(this);
        } else if (this.getRequestType() == RequestType.MULTIPART) {
            return RxInternalNetworking.generateMultipartObservable(this);
        } else {
            return null;
        }
    }

    public Observable<JSONArray> getJSONArrayObservable() {
        this.setResponseAs(ResponseType.JSON_ARRAY);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return RxInternalNetworking.generateSimpleObservable(this);
        } else if (this.getRequestType() == RequestType.MULTIPART) {
            return RxInternalNetworking.generateMultipartObservable(this);
        } else {
            return null;
        }
    }

    public Observable<Bitmap> getBitmapObservable() {
        this.setResponseAs(ResponseType.BITMAP);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return RxInternalNetworking.generateSimpleObservable(this);
        } else if (this.getRequestType() == RequestType.MULTIPART) {
            return RxInternalNetworking.generateMultipartObservable(this);
        } else {
            return null;
        }
    }

    public Observable<String> getStringObservable() {
        this.setResponseAs(ResponseType.STRING);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return RxInternalNetworking.generateSimpleObservable(this);
        } else if (this.getRequestType() == RequestType.MULTIPART) {
            return RxInternalNetworking.generateMultipartObservable(this);
        } else {
            return null;
        }
    }

    public Observable<String> getDownloadObservable() {
        return RxInternalNetworking.generateDownloadObservable(this);
    }

    public <T> Observable<T> getParseObservable(TypeToken<T> typeToken) {
        this.setType(typeToken.getType());
        this.setResponseAs(ResponseType.PARSED);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return RxInternalNetworking.generateSimpleObservable(this);
        } else if (this.getRequestType() == RequestType.MULTIPART) {
            return RxInternalNetworking.generateMultipartObservable(this);
        } else {
            return null;
        }
    }

    public <T> Observable<T> getObjectObservable(Class<T> objectClass) {
        this.setType(objectClass);
        this.setResponseAs(ResponseType.PARSED);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return RxInternalNetworking.generateSimpleObservable(this);
        } else if (this.getRequestType() == RequestType.MULTIPART) {
            return RxInternalNetworking.generateMultipartObservable(this);
        } else {
            return null;
        }
    }

    public <T> Observable<List<T>> getObjectListObservable(Class<T> objectClass) {
        this.setType($Gson$Types.newParameterizedTypeWithOwner(null, List.class, objectClass));
        this.setResponseAs(ResponseType.PARSED);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return RxInternalNetworking.generateSimpleObservable(this);
        } else if (this.getRequestType() == RequestType.MULTIPART) {
            return RxInternalNetworking.generateMultipartObservable(this);
        } else {
            return null;
        }
    }

    public static class GetRequestBuilder extends ANRequest.GetRequestBuilder<GetRequestBuilder> {

        public GetRequestBuilder(String url) {
            super(url);
        }

        private GetRequestBuilder(String url, int method) {
            super(url, method);
        }

        public RxANRequest build() {
            return new RxANRequest(this);
        }
    }

    public static class HeadRequestBuilder extends GetRequestBuilder {

        public HeadRequestBuilder(String url) {
            super(url, Method.HEAD);
        }
    }

    public static class PostRequestBuilder extends ANRequest.PostRequestBuilder<PostRequestBuilder> {

        public PostRequestBuilder(String url) {
            super(url);
        }

        private PostRequestBuilder(String url, int method) {
            super(url, method);
        }

        public RxANRequest build() {
            return new RxANRequest(this);
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

    public static class DownloadBuilder extends ANRequest.DownloadBuilder<DownloadBuilder> {

        public DownloadBuilder(String url, String dirPath, String fileName) {
            super(url, dirPath, fileName);
        }

        public RxANRequest build() {
            return new RxANRequest(this);
        }
    }

    public static class MultiPartBuilder extends ANRequest.MultiPartBuilder<MultiPartBuilder> {

        public MultiPartBuilder(String url) {
            super(url);
        }

        public RxANRequest build() {
            return new RxANRequest(this);
        }
    }
}
