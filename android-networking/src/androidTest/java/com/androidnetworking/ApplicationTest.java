package com.androidnetworking;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.androidnetworking.error.AndroidNetworkingError;
import com.androidnetworking.interfaces.StringRequestListener;

import org.junit.Rule;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    @Rule
    public final MockWebServer server = new MockWebServer();

    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        createApplication();
    }

    public void testGetRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("getResponse"));

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
                    public void onError(AndroidNetworkingError error) {
                        assertTrue(false);
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertEquals("getResponse", responseRef.get());
    }

    public void testGetRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("getResponse"));

        final AtomicReference<String> errorRef = new AtomicReference<>();
        final AtomicReference<String> errorContentRef = new AtomicReference<>();
        final AtomicReference<Boolean> hasErrorFromServerRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.get(server.url("/").toString())
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        assertTrue(false);
                    }

                    @Override
                    public void onError(AndroidNetworkingError error) {
                        hasErrorFromServerRef.set(error.hasErrorFromServer());
                        errorContentRef.set(error.getContent());
                        errorRef.set(error.getError());
                        latch.countDown();
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertTrue(hasErrorFromServerRef.get());

        assertEquals("errorResponseFromServer", errorRef.get());

        assertEquals("getResponse", errorContentRef.get());

    }

    public void testPostRequest() throws InterruptedException {

        server.enqueue(new MockResponse().setBody("postResponse"));

        final AtomicReference<String> responseRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        responseRef.set(response);
                        latch.countDown();
                    }

                    @Override
                    public void onError(AndroidNetworkingError error) {
                        assertTrue(false);
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertEquals("postResponse", responseRef.get());
    }


    public void testPostRequest404() throws InterruptedException {

        server.enqueue(new MockResponse().setResponseCode(404).setBody("postResponse"));

        final AtomicReference<String> errorRef = new AtomicReference<>();
        final AtomicReference<String> errorContentRef = new AtomicReference<>();
        final AtomicReference<Boolean> hasErrorFromServerRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.post(server.url("/").toString())
                .addBodyParameter("fistName", "Amit")
                .addBodyParameter("lastName", "Shekhar")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        assertTrue(false);
                    }

                    @Override
                    public void onError(AndroidNetworkingError error) {
                        hasErrorFromServerRef.set(error.hasErrorFromServer());
                        errorContentRef.set(error.getContent());
                        errorRef.set(error.getError());
                        latch.countDown();
                    }
                });

        assertTrue(latch.await(2, SECONDS));

        assertTrue(hasErrorFromServerRef.get());

        assertEquals("errorResponseFromServer", errorRef.get());

        assertEquals("postResponse", errorContentRef.get());
    }


}