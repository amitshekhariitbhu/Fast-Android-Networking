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

package com.androidnetworking;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndParsedRequestListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.model.User;

import org.json.JSONException;
import org.junit.Rule;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by amitshekhar on 11/04/17.
 */

public class PostObjectApiTest extends ApplicationTestCase<Application> {

    @Rule
    public final MockWebServer server = new MockWebServer();

    public PostObjectApiTest() {
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
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .build()
                .getAsObject(User.class, new ParsedRequestListener<User>() {
                    @Override
                    public void onResponse(User user) {
                        firstNameRef.set(user.firstName);
                        lastNameRef.set(user.lastName);
                        latch.countDown();
                    }

                    @Override
                    public void onError(ANError anError) {
                        assertTrue(false);
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertEquals("Amit", firstNameRef.get());
        assertEquals("Shekhar", lastNameRef.get());
    }

    public void testObjectPostRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .build()
                .getAsObject(User.class, new ParsedRequestListener<User>() {
                    @Override
                    public void onResponse(User user) {
                        assertTrue(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        errorBodyRef.set(anError.getErrorBody());
                        errorDetailRef.set(anError.getErrorDetail());
                        errorCodeRef.set(anError.getErrorCode());
                        latch.countDown();
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertEquals(ANConstants.RESPONSE_FROM_SERVER_ERROR, errorDetailRef.get());

        assertEquals("data", errorBodyRef.get());

        assertEquals(404, errorCodeRef.get().intValue());

    }

    public void testObjectListPostRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("[{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}]"));

        final AtomicReference<String> firstNameRef = new AtomicReference<>();
        final AtomicReference<String> lastNameRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .build()
                .getAsObjectList(User.class, new ParsedRequestListener<List<User>>() {
                    @Override
                    public void onResponse(List<User> userList) {
                        firstNameRef.set(userList.get(0).firstName);
                        lastNameRef.set(userList.get(0).lastName);
                        latch.countDown();
                    }

                    @Override
                    public void onError(ANError anError) {
                        assertTrue(false);
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertEquals("Amit", firstNameRef.get());
        assertEquals("Shekhar", lastNameRef.get());
    }

    public void testObjectListPostRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .build()
                .getAsObjectList(User.class, new ParsedRequestListener<List<User>>() {
                    @Override
                    public void onResponse(List<User> userList) {
                        assertTrue(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        errorBodyRef.set(anError.getErrorBody());
                        errorDetailRef.set(anError.getErrorDetail());
                        errorCodeRef.set(anError.getErrorCode());
                        latch.countDown();
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertEquals(ANConstants.RESPONSE_FROM_SERVER_ERROR, errorDetailRef.get());

        assertEquals("data", errorBodyRef.get());

        assertEquals(404, errorCodeRef.get().intValue());

    }

    @SuppressWarnings("unchecked")
    public void testSynchronousObjectPostRequest() throws InterruptedException, JSONException {

        server.enqueue(new MockResponse().setBody("{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}"));

        ANRequest request = AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .build();

        ANResponse<User> response = request.executeForObject(User.class);

        assertEquals("Amit", response.getResult().firstName);

        assertEquals("Shekhar", response.getResult().lastName);
    }

    @SuppressWarnings("unchecked")
    public void testSynchronousObjectPostRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        ANRequest request = AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .build();

        ANResponse<User> response = request.executeForObject(User.class);

        ANError error = response.getError();

        assertEquals("data", error.getErrorBody());

        assertEquals(ANConstants.RESPONSE_FROM_SERVER_ERROR, error.getErrorDetail());

        assertEquals(404, error.getErrorCode());

    }

    @SuppressWarnings("unchecked")
    public void testSynchronousObjectListPostRequest() throws InterruptedException, JSONException {

        server.enqueue(new MockResponse().setBody("[{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}]"));

        ANRequest request = AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .build();

        ANResponse<List<User>> response = request.executeForObjectList(User.class);

        User user = response.getResult().get(0);

        assertEquals("Amit", user.firstName);

        assertEquals("Shekhar", user.lastName);

    }

    @SuppressWarnings("unchecked")
    public void testSynchronousObjectListPostRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        ANRequest request = AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .build();

        ANResponse<List<User>> response = request.executeForObjectList(User.class);

        ANError error = response.getError();

        assertEquals("data", error.getErrorBody());

        assertEquals(ANConstants.RESPONSE_FROM_SERVER_ERROR, error.getErrorDetail());

        assertEquals(404, error.getErrorCode());

    }

    public void testResponseBodyAndObjectPost() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}"));

        final AtomicReference<String> firstNameRef = new AtomicReference<>();
        final AtomicReference<String> lastNameRef = new AtomicReference<>();
        final AtomicReference<Boolean> responseBodySuccess = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndObject(User.class,
                        new OkHttpResponseAndParsedRequestListener<User>() {
                            @Override
                            public void onResponse(Response okHttpResponse, User user) {
                                firstNameRef.set(user.firstName);
                                lastNameRef.set(user.lastName);
                                responseBodySuccess.set(okHttpResponse.isSuccessful());
                                latch.countDown();
                            }

                            @Override
                            public void onError(ANError anError) {
                                assertTrue(false);
                            }
                        });

        assertTrue(latch.await(2, SECONDS));

        assertTrue(responseBodySuccess.get());
        assertEquals("Amit", firstNameRef.get());
        assertEquals("Shekhar", lastNameRef.get());
    }

    public void testResponseBodyAndObjectPost404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndObject(User.class,
                        new OkHttpResponseAndParsedRequestListener<User>() {
                            @Override
                            public void onResponse(Response okHttpResponse, User user) {
                                assertTrue(false);
                            }

                            @Override
                            public void onError(ANError anError) {
                                errorBodyRef.set(anError.getErrorBody());
                                errorDetailRef.set(anError.getErrorDetail());
                                errorCodeRef.set(anError.getErrorCode());
                                latch.countDown();
                            }
                        });

        assertTrue(latch.await(2, SECONDS));

        assertEquals(ANConstants.RESPONSE_FROM_SERVER_ERROR, errorDetailRef.get());

        assertEquals("data", errorBodyRef.get());

        assertEquals(404, errorCodeRef.get().intValue());
    }

    public void testResponseBodyAndObjectListPost() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("[{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}]"));

        final AtomicReference<String> firstNameRef = new AtomicReference<>();
        final AtomicReference<String> lastNameRef = new AtomicReference<>();
        final AtomicReference<Boolean> responseBodySuccess = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndObjectList(User.class,
                        new OkHttpResponseAndParsedRequestListener<List<User>>() {
                            @Override
                            public void onResponse(Response okHttpResponse, List<User> userList) {
                                firstNameRef.set(userList.get(0).firstName);
                                lastNameRef.set(userList.get(0).lastName);
                                responseBodySuccess.set(okHttpResponse.isSuccessful());
                                latch.countDown();
                            }

                            @Override
                            public void onError(ANError anError) {
                                assertTrue(false);
                            }
                        });

        assertTrue(latch.await(2, SECONDS));

        assertTrue(responseBodySuccess.get());
        assertEquals("Amit", firstNameRef.get());
        assertEquals("Shekhar", lastNameRef.get());
    }

    public void testResponseBodyAndObjectListPost404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndObjectList(User.class,
                        new OkHttpResponseAndParsedRequestListener<List<User>>() {
                            @Override
                            public void onResponse(Response okHttpResponse, List<User> userList) {
                                assertTrue(false);
                            }

                            @Override
                            public void onError(ANError anError) {
                                errorBodyRef.set(anError.getErrorBody());
                                errorDetailRef.set(anError.getErrorDetail());
                                errorCodeRef.set(anError.getErrorCode());
                                latch.countDown();
                            }
                        });

        assertTrue(latch.await(2, SECONDS));

        assertEquals(ANConstants.RESPONSE_FROM_SERVER_ERROR, errorDetailRef.get());

        assertEquals("data", errorBodyRef.get());

        assertEquals(404, errorCodeRef.get().intValue());
    }

    public void testHeaderPostRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}"));

        final AtomicReference<String> firstNameRef = new AtomicReference<>();
        final AtomicReference<String> lastNameRef = new AtomicReference<>();
        final AtomicReference<String> headerRef = new AtomicReference<>();
        final AtomicReference<Boolean> responseBodySuccess = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.post(server.url("/").toString())
                .addHeaders("headerKey", "headerValue")
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndObject(User.class,
                        new OkHttpResponseAndParsedRequestListener<User>() {
                            @Override
                            public void onResponse(Response okHttpResponse, User user) {
                                firstNameRef.set(user.firstName);
                                lastNameRef.set(user.lastName);
                                responseBodySuccess.set(okHttpResponse.isSuccessful());
                                headerRef.set(okHttpResponse.request().header("headerKey"));
                                latch.countDown();
                            }

                            @Override
                            public void onError(ANError anError) {
                                assertTrue(false);
                            }
                        });

        assertTrue(latch.await(2, SECONDS));

        assertTrue(responseBodySuccess.get());
        assertEquals("Amit", firstNameRef.get());
        assertEquals("Shekhar", lastNameRef.get());
        assertEquals("headerValue", headerRef.get());
    }

}
