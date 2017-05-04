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

package com.androidnetworking;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndStringRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.androidnetworking.interfaces.StringRequestListener;

import org.junit.Rule;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static java.util.concurrent.TimeUnit.SECONDS;

public class GetStringApiTest extends ApplicationTestCase<Application> {

    @Rule
    public final MockWebServer server = new MockWebServer();

    public GetStringApiTest() {
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
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.get(server.url("/").toString())
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        responseRef.set(response);
                        latch.countDown();
                    }

                    @Override
                    public void onError(ANError anError) {
                        assertTrue(false);
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertEquals("data", responseRef.get());
    }

    public void testStringGetRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.get(server.url("/").toString())
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
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
    public void testSynchronousStringGetRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("data"));

        ANRequest request = AndroidNetworking.get(server.url("/").toString()).build();

        ANResponse<String> response = request.executeForString();

        assertEquals("data", response.getResult());
    }

    @SuppressWarnings("unchecked")
    public void testSynchronousStringGetRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        ANRequest request = AndroidNetworking.get(server.url("/").toString()).build();

        ANResponse<String> response = request.executeForString();

        ANError error = response.getError();

        assertEquals("data", error.getErrorBody());

        assertEquals(ANConstants.RESPONSE_FROM_SERVER_ERROR, error.getErrorDetail());

        assertEquals(404, error.getErrorCode());
    }

    public void testResponseBodyGet() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("data"));

        final AtomicReference<String> responseRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.get(server.url("/").toString())
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        try {
                            responseRef.set(response.body().string());
                            latch.countDown();
                        } catch (IOException e) {
                            assertTrue(false);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        assertTrue(false);
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertEquals("data", responseRef.get());
    }

    public void testResponseBodyGet404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.get(server.url("/").toString())
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        try {
                            errorBodyRef.set(response.body().string());
                            errorCodeRef.set(response.code());
                            latch.countDown();
                        } catch (IOException e) {
                            assertTrue(false);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        assertTrue(false);
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertEquals("data", errorBodyRef.get());

        assertEquals(404, errorCodeRef.get().intValue());
    }

    @SuppressWarnings("unchecked")
    public void testSyncResponseBodyGet() throws InterruptedException, IOException {

        server.enqueue(new MockResponse().setBody("data"));

        ANRequest request = AndroidNetworking.get(server.url("/").toString()).build();

        ANResponse<Response> response = request.executeForOkHttpResponse();

        assertEquals("data", response.getResult().body().string());

    }

    @SuppressWarnings("unchecked")
    public void testSyncResponseBodyGet404() throws InterruptedException, IOException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        ANRequest request = AndroidNetworking.get(server.url("/").toString()).build();

        ANResponse<Response> response = request.executeForOkHttpResponse();

        assertEquals("data", response.getResult().body().string());

        assertEquals(404, response.getResult().code());
    }

    public void testResponseBodyAndStringGet() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("data"));

        final AtomicReference<Boolean> responseBodySuccess = new AtomicReference<>();
        final AtomicReference<String> responseStringRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.get(server.url("/").toString())
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndString(new OkHttpResponseAndStringRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, String response) {
                        responseBodySuccess.set(okHttpResponse.isSuccessful());
                        responseStringRef.set(response);
                        latch.countDown();
                    }

                    @Override
                    public void onError(ANError anError) {
                        assertTrue(false);
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertTrue(responseBodySuccess.get());
        assertEquals("data", responseStringRef.get());
    }

    public void testResponseBodyAndStringGet404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.get(server.url("/").toString())
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndString(new OkHttpResponseAndStringRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, String response) {
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

    public void testHeaderGetRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("data"));

        final AtomicReference<String> responseRef = new AtomicReference<>();
        final AtomicReference<String> headerRef = new AtomicReference<>();
        final AtomicReference<Boolean> responseBodySuccess = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.get(server.url("/").toString())
                .addHeaders("headerKey", "headerValue")
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndString(new OkHttpResponseAndStringRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, String response) {
                        responseRef.set(response);
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
        assertEquals("data", responseRef.get());
        assertEquals("headerValue", headerRef.get());
    }
}