package com.networking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.androidnetworking.common.AndroidNetworkingRequest;
import com.androidnetworking.common.AndroidNetworkingResponse;
import com.androidnetworking.common.Method;
import com.androidnetworking.common.Priority;
import com.androidnetworking.common.RESPONSE;
import com.androidnetworking.error.AndroidNetworkingError;
import com.networking.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by amitshekhar on 30/03/16.
 */
public class ApiTestActivity extends AppCompatActivity {

    private static final String TAG = ApiTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_test);
    }

    public void getAllUsers(View view) {
        AndroidNetworkingRequest androidNetworkingRequest = new AndroidNetworkingRequest.Builder()
                .setUrl(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
                .setMethod(Method.GET)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setTag(this)
                .setPriority(Priority.LOW)
                .setResponseAs(RESPONSE.JSON_ARRAY).build();

        androidNetworkingRequest.addRequest(new AndroidNetworkingResponse.SuccessListener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "onResponse array : " + response.toString());
            }
        }, new AndroidNetworkingResponse.ErrorListener() {
            @Override
            public void onError(AndroidNetworkingError error) {
                Log.d(TAG, "onError : " + error.getContent());
            }
        });
    }

    public void getAnUser(View view) {
        AndroidNetworkingRequest androidNetworkingRequest = new AndroidNetworkingRequest.Builder()
                .setUrl(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_OBJECT)
                .setMethod(Method.GET)
                .addPathParameter("userId", "1")
                .setTag(this)
                .setPriority(Priority.LOW)
                .setResponseAs(RESPONSE.JSON_OBJECT).build();

        androidNetworkingRequest.addRequest(new AndroidNetworkingResponse.SuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse object : " + response.toString());
            }
        }, new AndroidNetworkingResponse.ErrorListener() {
            @Override
            public void onError(AndroidNetworkingError error) {
                Log.d(TAG, "onError : " + error.getContent());
            }
        });
    }

    public void checkForHeaderGet(View view) {
        AndroidNetworkingRequest androidNetworkingRequest = new AndroidNetworkingRequest.Builder()
                .setUrl(ApiEndPoint.BASE_URL + ApiEndPoint.CHECK_FOR_HEADER)
                .setMethod(Method.GET)
                .addHeaders("token", "1234")
                .setTag(this)
                .setPriority(Priority.LOW)
                .setResponseAs(RESPONSE.JSON_OBJECT).build();

        androidNetworkingRequest.addRequest(new AndroidNetworkingResponse.SuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse object : " + response.toString());
            }
        }, new AndroidNetworkingResponse.ErrorListener() {
            @Override
            public void onError(AndroidNetworkingError error) {
                Log.d(TAG, "onError : " + error.getContent());
            }
        });
    }

    public void checkForHeaderPost(View view) {
        AndroidNetworkingRequest androidNetworkingRequest = new AndroidNetworkingRequest.Builder()
                .setUrl(ApiEndPoint.BASE_URL + ApiEndPoint.CHECK_FOR_HEADER)
                .setMethod(Method.POST)
                .addHeaders("token", "1234")
                .setTag(this)
                .setPriority(Priority.LOW)
                .setResponseAs(RESPONSE.JSON_OBJECT).build();

        androidNetworkingRequest.addRequest(new AndroidNetworkingResponse.SuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse object : " + response.toString());
            }
        }, new AndroidNetworkingResponse.ErrorListener() {
            @Override
            public void onError(AndroidNetworkingError error) {
                Log.d(TAG, "onError : " + error.getContent());
            }
        });
    }

    public void createAnUser(View view) {
        AndroidNetworkingRequest androidNetworkingRequest = new AndroidNetworkingRequest.Builder()
                .setUrl(ApiEndPoint.BASE_URL + ApiEndPoint.POST_CREATE_AN_USER)
                .setMethod(Method.POST)
                .addBodyParameter("firstname", "Suman")
                .addBodyParameter("lastname", "Shekhar")
                .setTag(this)
                .setPriority(Priority.LOW)
                .setResponseAs(RESPONSE.JSON_OBJECT).build();

        androidNetworkingRequest.addRequest(new AndroidNetworkingResponse.SuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse object : " + response.toString());
            }
        }, new AndroidNetworkingResponse.ErrorListener() {
            @Override
            public void onError(AndroidNetworkingError error) {
                Log.d(TAG, "onError : " + error.getContent());
            }
        });
    }

    public void downloadFile(final View view) {
        final int DOWNLOAD_CHUNK_SIZE = 2048;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder().url("http://www.colorado.edu/conflict/peace/download/peace_problem.ZIP").build();
                    Response response = new OkHttpClient().newBuilder().build().newCall(request).execute();
                    ResponseBody body = response.body();
                    long contentLength = body.contentLength();
                    Log.d(TAG, "contentLength : " + contentLength);
                    BufferedSource source = body.source();
                    File file = new File(Utils.getRootDirPath(getApplicationContext()) + File.separator + "test.zip");
                    BufferedSink sink = Okio.buffer(Okio.sink(file));
                    long bytesRead = 0;
                    while (source.read(sink.buffer(), DOWNLOAD_CHUNK_SIZE) != -1) {
                        bytesRead += DOWNLOAD_CHUNK_SIZE;
                        int progress = (int) ((bytesRead * 100) / contentLength);
                        Log.d(TAG, "bytesRead : " + bytesRead);
                        Log.d(TAG, "progress : " + progress);
                    }
                    sink.writeAll(source);
                    sink.close();
                } catch (Exception e) {
                    Log.d(TAG, "failed");
                }
            }
        };
        new Thread(runnable).start();
    }

    public void downloadImage(final View view) {
        final int DOWNLOAD_CHUNK_SIZE = 2048;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder().url("http://i.imgur.com/AtbX9iX.png").build();
                    Response response = new OkHttpClient().newBuilder().build().newCall(request).execute();
                    ResponseBody body = response.body();
                    long contentLength = body.contentLength();
                    Log.d(TAG, "contentLength : " + contentLength);
                    BufferedSource source = body.source();
                    File file = new File(Utils.getRootDirPath(getApplicationContext()) + File.separator + "image.png");
                    BufferedSink sink = Okio.buffer(Okio.sink(file));
                    long bytesRead = 0;
                    while (source.read(sink.buffer(), DOWNLOAD_CHUNK_SIZE) != -1) {
                        bytesRead += DOWNLOAD_CHUNK_SIZE;
                        int progress = (int) ((bytesRead * 100) / contentLength);
                        Log.d(TAG, "bytesRead : " + bytesRead);
                        Log.d(TAG, "progress : " + progress);
                    }
                    sink.writeAll(source);
                    sink.close();
                } catch (Exception e) {
                    Log.d(TAG, "failed");
                }
            }
        };
        new Thread(runnable).start();
    }
}
