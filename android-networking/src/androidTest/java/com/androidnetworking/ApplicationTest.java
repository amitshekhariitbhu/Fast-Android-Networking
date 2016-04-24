package com.androidnetworking;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.androidnetworking.error.AndroidNetworkingError;
import com.androidnetworking.interfaces.RequestListener;

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

        server.enqueue(new MockResponse().setBody("getRequest"));

        final AtomicReference<String> responseRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidNetworking.get(server.url("/").toString())
                .build()
                .getAsString(new RequestListener<String>() {
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

        String response = responseRef.get();
        assertEquals("getRequest", response);
    }

}