package com.rx2androidnetworking;

import android.graphics.Bitmap;

import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Method;
import com.androidnetworking.common.RequestType;
import com.androidnetworking.common.ResponseType;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import io.reactivex.Observable;

/**
 * Created by Prashant Gupta on 30-01-2017.
 */

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

    public Observable<String> getDownloadObservable() {
        return Rx2InternalNetworking.generateDownloadObservable(this);
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
