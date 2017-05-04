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
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONArrayRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by amitshekhar on 09/04/17.
 */

public class MultipartJSONApiTest extends ApplicationTestCase<Application> {

    @Rule
    public final MockWebServer server = new MockWebServer();

    public MultipartJSONApiTest() {
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
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            firstNameRef.set(response.getString("firstName"));
                            lastNameRef.set(response.getString("lastName"));
                            latch.countDown();
                        } catch (JSONException e) {
                            assertTrue(false);
                        }
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

    public void testJSONObjectMultipartRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
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

    public void testJSONArrayMultipartRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("[{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}]"));

        final AtomicReference<String> firstNameRef = new AtomicReference<>();
        final AtomicReference<String> lastNameRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
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
                    public void onError(ANError anError) {
                        assertTrue(false);
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertEquals("Amit", firstNameRef.get());
        assertEquals("Shekhar", lastNameRef.get());
    }

    public void testJSONArrayMultipartRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
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
    public void testSynchronousJSONObjectMultipartRequest() throws InterruptedException, JSONException {

        server.enqueue(new MockResponse().setBody("{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}"));

        ANRequest request = AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build();

        ANResponse<JSONObject> response = request.executeForJSONObject();

        assertEquals("Amit", response.getResult().getString("firstName"));

        assertEquals("Shekhar", response.getResult().getString("lastName"));
    }

    @SuppressWarnings("unchecked")
    public void testSynchronousJSONObjectMultipartRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        ANRequest request = AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build();

        ANResponse<JSONObject> response = request.executeForJSONObject();

        ANError error = response.getError();

        assertEquals("data", error.getErrorBody());

        assertEquals(ANConstants.RESPONSE_FROM_SERVER_ERROR, error.getErrorDetail());

        assertEquals(404, error.getErrorCode());

    }

    @SuppressWarnings("unchecked")
    public void testSynchronousJSONArrayMultipartRequest() throws InterruptedException, JSONException {

        server.enqueue(new MockResponse().setBody("[{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}]"));

        ANRequest request = AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build();

        ANResponse<JSONArray> response = request.executeForJSONArray();

        JSONObject jsonObject = response.getResult().getJSONObject(0);

        assertEquals("Amit", jsonObject.getString("firstName"));

        assertEquals("Shekhar", jsonObject.getString("lastName"));

    }

    @SuppressWarnings("unchecked")
    public void testSynchronousJSONArrayMultipartRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        ANRequest request = AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .build();

        ANResponse<JSONObject> response = request.executeForJSONArray();

        ANError error = response.getError();

        assertEquals("data", error.getErrorBody());

        assertEquals(ANConstants.RESPONSE_FROM_SERVER_ERROR, error.getErrorDetail());

        assertEquals(404, error.getErrorCode());

    }

    public void testResponseBodyAndJSONObjectMultipart() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}"));

        final AtomicReference<String> firstNameRef = new AtomicReference<>();
        final AtomicReference<String> lastNameRef = new AtomicReference<>();
        final AtomicReference<Boolean> responseBodySuccess = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        try {
                            firstNameRef.set(response.getString("firstName"));
                            lastNameRef.set(response.getString("lastName"));
                            responseBodySuccess.set(okHttpResponse.isSuccessful());
                            latch.countDown();
                        } catch (JSONException e) {
                            assertTrue(false);
                        }
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

    public void testResponseBodyAndJSONObjectMultipart404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
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

    public void testResponseBodyAndJSONArrayMultipart() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("[{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}]"));

        final AtomicReference<String> firstNameRef = new AtomicReference<>();
        final AtomicReference<String> lastNameRef = new AtomicReference<>();
        final AtomicReference<Boolean> responseBodySuccess = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndJSONArray(new OkHttpResponseAndJSONArrayRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONArray response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            firstNameRef.set(jsonObject.getString("firstName"));
                            lastNameRef.set(jsonObject.getString("lastName"));
                            responseBodySuccess.set(okHttpResponse.isSuccessful());
                            latch.countDown();
                        } catch (JSONException e) {
                            assertTrue(false);
                        }
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

    public void testResponseBodyAndJSONArrayMultipart404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("data"));

        final AtomicReference<String> errorBodyRef = new AtomicReference<>();
        final AtomicReference<Integer> errorCodeRef = new AtomicReference<>();
        final AtomicReference<String> errorDetailRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.upload(server.url("/").toString())
                .addMultipartParameter("key", "value")
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndJSONArray(new OkHttpResponseAndJSONArrayRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONArray response) {
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

    public void testHeaderMultipartRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("{\"firstName\":\"Amit\", \"lastName\":\"Shekhar\"}"));

        final AtomicReference<String> firstNameRef = new AtomicReference<>();
        final AtomicReference<String> lastNameRef = new AtomicReference<>();
        final AtomicReference<String> headerRef = new AtomicReference<>();
        final AtomicReference<Boolean> responseBodySuccess = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.upload(server.url("/").toString())
                .addHeaders("headerKey", "headerValue")
                .addMultipartParameter("key", "value")
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        try {
                            firstNameRef.set(response.getString("firstName"));
                            lastNameRef.set(response.getString("lastName"));
                            responseBodySuccess.set(okHttpResponse.isSuccessful());
                            headerRef.set(okHttpResponse.request().header("headerKey"));
                            latch.countDown();
                        } catch (JSONException e) {
                            assertTrue(false);
                        }
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
