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
import com.rx2androidnetworking.model.User;

import org.junit.Rule;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by amitshekhar on 28/04/17.
 */

public class Rx2PostObjectApiTest extends ApplicationTestCase<Application> {

    @Rule
    public final MockWebServer server = new MockWebServer();

    public Rx2PostObjectApiTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        createApplication();
    }

    public void testObjectPostRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}"));

        final AtomicReference<String> firstNameRef = new AtomicReference<>();
        final AtomicReference<String> lastNameRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final AtomicReference<Boolean> isCompletedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(2);

        Rx2AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .build()
                .getObjectObservable(User.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onNext(User user) {
                        firstNameRef.set(user.firstName);
                        lastNameRef.set(user.lastName);
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

        assertEquals("Amit", firstNameRef.get());
        assertEquals("Shekhar", lastNameRef.get());
    }

    public void testObjectPostRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Rx2AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .build()
                .getObjectObservable(User.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onNext(User user) {
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

    public void testObjectListPostRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("[{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}]"));

        final AtomicReference<String> firstNameRef = new AtomicReference<>();
        final AtomicReference<String> lastNameRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final AtomicReference<Boolean> isCompletedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(2);

        Rx2AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .build()
                .getObjectListObservable(User.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onNext(List<User> userList) {
                        firstNameRef.set(userList.get(0).firstName);
                        lastNameRef.set(userList.get(0).lastName);
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

        assertEquals("Amit", firstNameRef.get());
        assertEquals("Shekhar", lastNameRef.get());
    }

    public void testObjectListPostRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final AtomicReference<Boolean> isSubscribedRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Rx2AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .build()
                .getObjectListObservable(User.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        isSubscribedRef.set(true);
                    }

                    @Override
                    public void onNext(List<User> userList) {
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
}
