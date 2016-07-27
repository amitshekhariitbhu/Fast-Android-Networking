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

import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.RESPONSE;

import org.json.JSONArray;
import org.json.JSONObject;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by amitshekhar on 10/06/16.
 */
public class RxANRequest extends ANRequest {

    public RxANRequest(GetRequestBuilder builder) {
        super(builder);
    }

    public Observable<JSONObject> getJsonObjectObservable() {
        this.setResponseAs(RESPONSE.JSON_OBJECT);
        return RxInternalNetworking.generateSimpleObservable(this);
    }

    public Observable<JSONArray> getJsonArrayObservable() {
        this.setResponseAs(RESPONSE.JSON_ARRAY);
        return RxInternalNetworking.generateSimpleObservable(this);
    }

    public static class GetRequestBuilder extends ANRequest.GetRequestBuilder<GetRequestBuilder> {

        public GetRequestBuilder(String url) {
            super(url);
        }

        public RxANRequest build() {
            return new RxANRequest(this);
        }
    }
}
