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
 * Created by amitshekhar on 22/04/17.
 */

public class Rx2GetStringApiTest extends ApplicationTestCase<Application> {

    @Rule
    public final MockWebServer server = new MockWebServer();

    public Rx2GetStringApiTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        createApplication();
    }

    public void testStringGetRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("data"));

        final AtomicReference<String> responseRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final AtomicReference<Boolean> isCompletedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(2);

        Rx2AndroidNetworking.get(server.url("/").toString())
                .build()
                .getStringObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onNext(String response) {
                        responseRef.set(response);
                        latch.countDown();
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

        assertEquals("data", responseRef.get());
    }

    public void testStringSingleGetRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("data"));

        final AtomicReference<String> responseRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Rx2AndroidNetworking.get(server.url("/").toString())
                .build()
                .getStringSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onSuccess(@NonNull String response) {
                        responseRef.set(response);
                        latch.countDown();
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        assertTrue(false);
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertTrue(isSubscribedRef.get());

        assertEquals("data", responseRef.get());
    }

    public void testStringGetRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Rx2AndroidNetworking.get(server.url("/").toString())
                .build()
                .getStringObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onNext(String response) {
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

    public void testStringGetSingleRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Rx2AndroidNetworking.get(server.url("/").toString())
                .build()
                .getStringSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onSuccess(@NonNull String s) {
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
