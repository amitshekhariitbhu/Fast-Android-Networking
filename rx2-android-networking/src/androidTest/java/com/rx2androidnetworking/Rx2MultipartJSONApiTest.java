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

import android.app.Application;
import android.test.ApplicationTestCase;

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.error.ANError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by amitshekhar on 03/05/17.
 */

public class Rx2MultipartJSONApiTest extends ApplicationTestCase<Application> {

    @Rule
    public final MockWebServer server = new MockWebServer();

    public Rx2MultipartJSONApiTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        createApplication();
    }

    public void testJSONObjectMultipartRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}"));

        final AtomicReference<String> firstNameRef = new AtomicReference<>();
        final AtomicReference<String> lastNameRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final AtomicReference<Boolean> isCompletedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(2);

        Rx2AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build()
                .getJSONObjectObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onNext(JSONObject response) {
                        try {
                            firstNameRef.set(response.getString("firstName"));
                            lastNameRef.set(response.getString("lastName"));
                            latch.countDown();
                        } catch (JSONException e) {
                            assertTrue(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        assertTrue(false);
                    }

                    @Override
                    public void onComplete() {
                        isCompletedRef.set(true);
                        latch.countDown();
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertTrue(isSubscribedRef.get());
        assertTrue(isCompletedRef.get());

        assertEquals("Amit", firstNameRef.get());
        assertEquals("Shekhar", lastNameRef.get());
    }

    public void testJSONObjectSingleMultipartRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}"));

        final AtomicReference<String> firstNameRef = new AtomicReference<>();
        final AtomicReference<String> lastNameRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Rx2AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build()
                .getJSONObjectSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onSuccess(@NonNull JSONObject response) {
                        try {
                            firstNameRef.set(response.getString("firstName"));
                            lastNameRef.set(response.getString("lastName"));
                            latch.countDown();
                        } catch (JSONException e) {
                            assertTrue(false);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        assertTrue(false);
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertTrue(isSubscribedRef.get());

        assertEquals("Amit", firstNameRef.get());
        assertEquals("Shekhar", lastNameRef.get());
    }

    public void testJSONObjectMultipartRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Rx2AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build()
                .getJSONObjectObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onNext(JSONObject response) {
                        assertTrue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ANError anError = (ANError) e;
                        errorBodyRef.set(anError.getErrorBody());
                        errorDetailRef.set(anError.getErrorDetail());
                        errorCodeRef.set(anError.getErrorCode());
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        assertTrue(false);
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertTrue(isSubscribedRef.get());

        assertEquals(ANConstants.RESPONSE_FROM_SERVER_ERROR, errorDetailRef.get());

        assertEquals("data", errorBodyRef.get());

        assertEquals(404, errorCodeRef.get().intValue());

    }

    public void testJSONObjectSingleMultipartRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Rx2AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build()
                .getJSONObjectSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onSuccess(@NonNull JSONObject jsonObject) {
                        assertTrue(false);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ANError anError = (ANError) e;
                        errorBodyRef.set(anError.getErrorBody());
                        errorDetailRef.set(anError.getErrorDetail());
                        errorCodeRef.set(anError.getErrorCode());
                        latch.countDown();
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertTrue(isSubscribedRef.get());

        assertEquals(ANConstants.RESPONSE_FROM_SERVER_ERROR, errorDetailRef.get());

        assertEquals("data", errorBodyRef.get());

        assertEquals(404, errorCodeRef.get().intValue());

    }

    public void testJSONArrayMultipartRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("[{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}]"));

        final AtomicReference<String> firstNameRef = new AtomicReference<>();
        final AtomicReference<String> lastNameRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final AtomicReference<Boolean> isCompletedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(2);

        Rx2AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build()
                .getJSONArrayObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONArray>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onNext(JSONArray response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            firstNameRef.set(jsonObject.getString("firstName"));
                            lastNameRef.set(jsonObject.getString("lastName"));
                            latch.countDown();
                        } catch (JSONException e) {
                            assertTrue(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        assertTrue(false);
                    }

                    @Override
                    public void onComplete() {
                        isCompletedRef.set(true);
                        latch.countDown();
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertTrue(isSubscribedRef.get());
        assertTrue(isCompletedRef.get());

        assertEquals("Amit", firstNameRef.get());
        assertEquals("Shekhar", lastNameRef.get());
    }

    public void testJSONArraySingleMultipartRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("[{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}]"));

        final AtomicReference<String> firstNameRef = new AtomicReference<>();
        final AtomicReference<String> lastNameRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Rx2AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build()
                .getJSONArraySingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JSONArray>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onSuccess(@NonNull JSONArray response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            firstNameRef.set(jsonObject.getString("firstName"));
                            lastNameRef.set(jsonObject.getString("lastName"));
                            latch.countDown();
                        } catch (JSONException e) {
                            assertTrue(false);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        assertTrue(false);
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertTrue(isSubscribedRef.get());

        assertEquals("Amit", firstNameRef.get());
        assertEquals("Shekhar", lastNameRef.get());
    }

    public void testJSONArrayMultipartRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Rx2AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build()
                .getJSONArrayObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONArray>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onNext(JSONArray response) {
                        assertTrue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ANError anError = (ANError) e;
                        errorBodyRef.set(anError.getErrorBody());
                        errorDetailRef.set(anError.getErrorDetail());
                        errorCodeRef.set(anError.getErrorCode());
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        assertTrue(false);
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertTrue(isSubscribedRef.get());

        assertEquals(ANConstants.RESPONSE_FROM_SERVER_ERROR, errorDetailRef.get());

        assertEquals("data", errorBodyRef.get());

        assertEquals(404, errorCodeRef.get().intValue());

    }

    public void testJSONArraySingleMultipartRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Rx2AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build()
                .getJSONArraySingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JSONArray>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onSuccess(@NonNull JSONArray jsonArray) {
                        assertTrue(false);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ANError anError = (ANError) e;
                        errorBodyRef.set(anError.getErrorBody());
                        errorDetailRef.set(anError.getErrorDetail());
                        errorCodeRef.set(anError.getErrorCode());
                        latch.countDown();
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertTrue(isSubscribedRef.get());

        assertEquals(ANConstants.RESPONSE_FROM_SERVER_ERROR, errorDetailRef.get());

        assertEquals("data", errorBodyRef.get());

        assertEquals(404, errorCodeRef.get().intValue());

    }

}
