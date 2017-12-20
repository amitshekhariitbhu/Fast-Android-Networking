/*
 *
 *  *    Copyright (C) 2016 Amit Shekhar
 *  *    Copyright (C) 2011 Android Open Source Project
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package com.rx2androidnetworking;

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

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by Prashant Gupta on 30-01-2017.
 */
@SuppressWarnings({"unchecked", "unused"})
public class Rx2ANRequest extends ANRequest<Rx2ANRequest> {

    public Rx2ANRequest(GetRequestBuilder builder) {
        super(builder);
    }

    public Rx2ANRequest(PostRequestBuilder builder) {
        super(builder);
    }

    public Rx2ANRequest(DownloadBuilder builder) {
        super(builder);
    }

    public Rx2ANRequest(MultiPartBuilder builder) {
        super(builder);
    }

    public Observable<JSONObject> getJSONObjectObservable() {
        this.setResponseAs(ResponseType.JSON_OBJECT);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return Rx2InternalNetworking.generateSimpleObservable(this);
        } else if (this.getRequestType() == RequestType.MULTIPART) {
            return Rx2InternalNetworking.generateMultipartObservable(this);
        } else {
            return null;
        }
    }

    public Flowable<JSONObject> getJSONObjectFlowable() {
        return getJSONObjectObservable().toFlowable(BackpressureStrategy.LATEST);
    }

    public Single<JSONObject> getJSONObjectSingle() {
        return getJSONObjectObservable().singleOrError();
    }

    public Maybe<JSONObject> getJSONObjectMaybe() {
        return getJSONObjectObservable().singleElement();
    }

    public Completable getJSONObjectCompletable() {
        return getJSONObjectObservable().ignoreElements();
    }

    public Observable<JSONArray> getJSONArrayObservable() {
        this.setResponseAs(ResponseType.JSON_ARRAY);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return Rx2InternalNetworking.generateSimpleObservable(this);
        } else if (this.getRequestType() == RequestType.MULTIPART) {
            return Rx2InternalNetworking.generateMultipartObservable(this);
        } else {
            return null;
        }
    }

    public Flowable<JSONArray> getJSONArrayFlowable() {
        return getJSONArrayObservable().toFlowable(BackpressureStrategy.LATEST);
    }

    public Single<JSONArray> getJSONArraySingle() {
        return getJSONArrayObservable().singleOrError();
    }

    public Maybe<JSONArray> getJSONArrayMaybe() {
        return getJSONArrayObservable().singleElement();
    }

    public Completable getJSONArrayCompletable() {
        return getJSONArrayObservable().ignoreElements();
    }

    public Observable<Bitmap> getBitmapObservable() {
        this.setResponseAs(ResponseType.BITMAP);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return Rx2InternalNetworking.generateSimpleObservable(this);
        } else if (this.getRequestType() == RequestType.MULTIPART) {
            return Rx2InternalNetworking.generateMultipartObservable(this);
        } else {
            return null;
        }
    }

    public Flowable<Bitmap> getBitmapFlowable() {
        return getBitmapObservable().toFlowable(BackpressureStrategy.LATEST);
    }

    public Single<Bitmap> getBitmapSingle() {
        return getBitmapObservable().singleOrError();
    }

    public Maybe<Bitmap> getBitmapMaybe() {
        return getBitmapObservable().singleElement();
    }

    public Completable getBitmapCompletable() {
        return getBitmapObservable().ignoreElements();
    }

    public Observable<String> getStringObservable() {
        this.setResponseAs(ResponseType.STRING);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return Rx2InternalNetworking.generateSimpleObservable(this);
        } else if (this.getRequestType() == RequestType.MULTIPART) {
            return Rx2InternalNetworking.generateMultipartObservable(this);
        } else {
            return null;
        }
    }

    public Flowable<String> getStringFlowable() {
        return getStringObservable().toFlowable(BackpressureStrategy.LATEST);
    }

    public Single<String> getStringSingle() {
        return getStringObservable().singleOrError();
    }

    public Maybe<String> getStringMaybe() {
        return getStringObservable().singleElement();
    }

    public Completable getStringCompletable() {
        return getStringObservable().ignoreElements();
    }

    public Observable<String> getDownloadObservable() {
        return Rx2InternalNetworking.generateDownloadObservable(this);
    }

    public Flowable<String> getDownloadFlowable() {
        return getDownloadObservable().toFlowable(BackpressureStrategy.LATEST);
    }

    public Single<String> getDownloadSingle() {
        return getDownloadObservable().singleOrError();
    }

    public Maybe<String> getDownloadMaybe() {
        return getDownloadObservable().singleElement();
    }

    public Completable getDownloadCompletable() {
        return getDownloadObservable().ignoreElements();
    }

    public <T> Observable<T> getParseObservable(TypeToken<T> typeToken) {
        this.setType(typeToken.getType());
        this.setResponseAs(ResponseType.PARSED);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return Rx2InternalNetworking.generateSimpleObservable(this);
        } else if (this.getRequestType() == RequestType.MULTIPART) {
            return Rx2InternalNetworking.generateMultipartObservable(this);
        } else {
            return null;
        }
    }

    public <T> Flowable<T> getParseFlowable(TypeToken<T> typeToken) {
        return getParseObservable(typeToken).toFlowable(BackpressureStrategy.LATEST);
    }

    public <T> Single<T> getParseSingle(TypeToken<T> typeToken) {
        return getParseObservable(typeToken).singleOrError();
    }

    public <T> Maybe<T> getParseMaybe(TypeToken<T> typeToken) {
        return getParseObservable(typeToken).singleElement();
    }

    public <T> Completable getParseCompletable(TypeToken<T> typeToken) {
        return getParseObservable(typeToken).ignoreElements();
    }

    public <T> Observable<T> getObjectObservable(Class<T> objectClass) {
        this.setType(objectClass);
        this.setResponseAs(ResponseType.PARSED);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return Rx2InternalNetworking.generateSimpleObservable(this);
        } else if (this.getRequestType() == RequestType.MULTIPART) {
            return Rx2InternalNetworking.generateMultipartObservable(this);
        } else {
            return null;
        }
    }

    public <T> Flowable<T> getObjectFlowable(Class<T> objectClass) {
        return getObjectObservable(objectClass).toFlowable(BackpressureStrategy.LATEST);
    }

    public <T> Single<T> getObjectSingle(Class<T> objectClass) {
        return getObjectObservable(objectClass).singleOrError();
    }

    public <T> Maybe<T> getObjectMaybe(Class<T> objectClass) {
        return getObjectObservable(objectClass).singleElement();
    }

    public <T> Completable getObjectCompletable(Class<T> objectClass) {
        return getObjectObservable(objectClass).ignoreElements();
    }

    public <T> Observable<List<T>> getObjectListObservable(Class<T> objectClass) {
        this.setType($Gson$Types.newParameterizedTypeWithOwner(null, List.class, objectClass));
        this.setResponseAs(ResponseType.PARSED);
        if (this.getRequestType() == RequestType.SIMPLE) {
            return Rx2InternalNetworking.generateSimpleObservable(this);
        } else if (this.getRequestType() == RequestType.MULTIPART) {
            return Rx2InternalNetworking.generateMultipartObservable(this);
        } else {
            return null;
        }
    }

    public <T> Flowable<List<T>> getObjectListFlowable(Class<T> objectClass) {
        return getObjectListObservable(objectClass).toFlowable(BackpressureStrategy.LATEST);
    }

    public <T> Single<List<T>> getObjectListSingle(Class<T> objectClass) {
        return getObjectListObservable(objectClass).singleOrError();
    }

    public <T> Maybe<List<T>> getObjectListMaybe(Class<T> objectClass) {
        return getObjectListObservable(objectClass).singleElement();
    }

    public <T> Completable getObjectListCompletable(Class<T> objectClass) {
        return getObjectListObservable(objectClass).ignoreElements();
    }

    public static class GetRequestBuilder extends ANRequest.GetRequestBuilder<GetRequestBuilder> {

        public GetRequestBuilder(String url) {
            super(url);
        }

        private GetRequestBuilder(String url, int method) {
            super(url, method);
        }

        public Rx2ANRequest build() {
            return new Rx2ANRequest(this);
        }
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

    public static class PostRequestBuilder extends ANRequest.PostRequestBuilder<PostRequestBuilder> {

        public PostRequestBuilder(String url) {
            super(url);
        }

        private PostRequestBuilder(String url, int method) {
            super(url, method);
        }

        public Rx2ANRequest build() {
            return new Rx2ANRequest(this);
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

    public static class DownloadBuilder extends ANRequest.DownloadBuilder<DownloadBuilder> {

        public DownloadBuilder(String url, String dirPath, String fileName) {
            super(url, dirPath, fileName);
        }

        public Rx2ANRequest build() {
            return new Rx2ANRequest(this);
        }
    }

    public static class MultiPartBuilder extends ANRequest.MultiPartBuilder<MultiPartBuilder> {

        public MultiPartBuilder(String url) {
            super(url);
        }

        public Rx2ANRequest build() {
            return new Rx2ANRequest(this);
        }
    }
}
