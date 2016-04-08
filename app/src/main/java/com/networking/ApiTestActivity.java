package com.networking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.androidnetworking.common.AndroidNetworkingRequest;
import com.androidnetworking.common.Method;
import com.androidnetworking.common.Priority;
import com.androidnetworking.common.RESPONSE;
import com.androidnetworking.error.AndroidNetworkingError;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.RequestListener;
import com.networking.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

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

        androidNetworkingRequest.addRequest(new RequestListener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "onResponse array : " + response.toString());
            }

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

        androidNetworkingRequest.addRequest(new RequestListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse object : " + response.toString());
            }

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

        androidNetworkingRequest.addRequest(new RequestListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse object : " + response.toString());
            }

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

        androidNetworkingRequest.addRequest(new RequestListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse object : " + response.toString());
            }

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

        androidNetworkingRequest.addRequest(new RequestListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse object : " + response.toString());
            }

            @Override
            public void onError(AndroidNetworkingError error) {
                Log.d(TAG, "onError : " + error.getContent());
            }
        });
    }

    public void downloadFile(final View view) {
        String url = "http://www.colorado.edu/conflict/peace/download/peace_problem.ZIP";
        AndroidNetworkingRequest request = new AndroidNetworkingRequest.DownloadBuilder()
                .setUrl(url)
                .setDirPath(Utils.getRootDirPath(getApplicationContext()))
                .setFileName("file1.zip")
                .setPriority(Priority.MEDIUM)
                .setTag(this)
                .build();

        request.download(new DownloadProgressListener() {
            @Override
            public void onProgress(long bytesDownloaded, long totalBytes, boolean isCompleted) {
                Log.d(TAG, "bytesDownloaded : " + bytesDownloaded + " totalBytes : " + totalBytes);
                if (isCompleted) {
                    Log.d(TAG, "File download Completed");
                }
            }

            @Override
            public void onError(AndroidNetworkingError error) {
                Log.d(TAG, "onError : " + error.getContent());
            }
        });
    }

    public void downloadImage(final View view) {
        String url = "http://i.imgur.com/AtbX9iX.png";
        AndroidNetworkingRequest request = new AndroidNetworkingRequest.DownloadBuilder()
                .setUrl(url)
                .setDirPath(Utils.getRootDirPath(getApplicationContext()))
                .setFileName("image1.png")
                .setPriority(Priority.MEDIUM)
                .setTag(this)
                .build();

        request.download(new DownloadProgressListener() {
            @Override
            public void onProgress(long bytesDownloaded, long totalBytes, boolean isCompleted) {
                Log.d(TAG, "bytesDownloaded : " + bytesDownloaded + " totalBytes : " + totalBytes);
                if (isCompleted) {
                    Log.d(TAG, "Image download Completed");
                }
            }

            @Override
            public void onError(AndroidNetworkingError error) {
                Log.d(TAG, "onError : " + error.getContent());
            }
        });

    }
}
