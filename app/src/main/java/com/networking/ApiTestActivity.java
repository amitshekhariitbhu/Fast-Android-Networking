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

package com.networking;

import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.networking.model.User;
import com.networking.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Response;

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

    public void prefetch(View view) {
        AndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .prefetch();
    }

    public void prefetchDownload(View view) {
        String url = "http://www.colorado.edu/conflict/peace/download/peace_problem.ZIP";
        AndroidNetworking.download(url, Utils.getRootDirPath(getApplicationContext()), "file1.zip")
                .setPriority(Priority.HIGH)
                .setTag(this)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .prefetch();
    }

    public void getAllUsers(View view) {
        AndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsObjectList(User.class, new ParsedRequestListener<List<User>>() {
                    @Override
                    public void onResponse(List<User> users) {
                        Log.d(TAG, "userList size : " + users.size());
                        for (User user : users) {
                            Log.d(TAG, "id : " + user.id);
                            Log.d(TAG, "firstname : " + user.firstname);
                            Log.d(TAG, "lastname : " + user.lastname);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Utils.logError(TAG, anError);
                    }
                });
    }

    public void getAnUser(View view) {
        AndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_OBJECT)
                .addPathParameter("userId", "1")
                .setTag(this)
                .setPriority(Priority.LOW)
                .setUserAgent("getAnUser")
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsObject(User.class, new ParsedRequestListener<User>() {
                    @Override
                    public void onResponse(User user) {
                        Log.d(TAG, "id : " + user.id);
                        Log.d(TAG, "firstname : " + user.firstname);
                        Log.d(TAG, "lastname : " + user.lastname);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Utils.logError(TAG, anError);
                    }
                });
    }

    public void checkForHeaderGet(View view) {

        ANRequest.GetRequestBuilder getRequestBuilder = new ANRequest.GetRequestBuilder(ApiEndPoint.BASE_URL + ApiEndPoint.CHECK_FOR_HEADER);

        getRequestBuilder.addHeaders("token", "1234")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse object : " + response.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            // received ANError from server
                            // error.getErrorCode() - the ANError code from server
                            // error.getErrorBody() - the ANError body from server
                            // error.getErrorDetail() - just a ANError detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }

    public void checkForHeaderPost(View view) {

        ANRequest.PostRequestBuilder postRequestBuilder = AndroidNetworking.post(ApiEndPoint.BASE_URL + ApiEndPoint.CHECK_FOR_HEADER);

        postRequestBuilder.addHeaders("token", "1234");

        ANRequest anRequest = postRequestBuilder.setTag(this)
                .setPriority(Priority.LOW)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build();

        anRequest.setAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                Log.d(TAG, " bytesSent : " + bytesSent);
                Log.d(TAG, " bytesReceived : " + bytesReceived);
                Log.d(TAG, " isFromCache : " + isFromCache);
            }
        });

        anRequest.getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse object : " + response.toString());
                Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
            }

            @Override
            public void onError(ANError error) {
                if (error.getErrorCode() != 0) {
                    // received ANError from server
                    // error.getErrorCode() - the ANError code from server
                    // error.getErrorBody() - the ANError body from server
                    // error.getErrorDetail() - just a ANError detail
                    Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                    Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                    Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                } else {
                    // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                    Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                }
            }
        });
    }

    public void createAnUser(View view) {
        AndroidNetworking.post(ApiEndPoint.BASE_URL + ApiEndPoint.POST_CREATE_AN_USER)
                .addBodyParameter("firstname", "Suman")
                .addBodyParameter("lastname", "Shekhar")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse object : " + response.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            // received ANError from server
                            // error.getErrorCode() - the ANError code from server
                            // error.getErrorBody() - the ANError body from server
                            // error.getErrorDetail() - just a ANError detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }


    public void createAnUserJSONObject(View view) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstname", "Rohit");
            jsonObject.put("lastname", "Kumar");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(ApiEndPoint.BASE_URL + ApiEndPoint.POST_CREATE_AN_USER)
                .addJSONObjectBody(jsonObject)
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse object : " + response.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            // received ANError from server
                            // error.getErrorCode() - the ANError code from server
                            // error.getErrorBody() - the ANError body from server
                            // error.getErrorDetail() - just a ANError detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }

    public void downloadFile(final View view) {
        String url = "http://www.colorado.edu/conflict/peace/download/peace_problem.ZIP";
        AndroidNetworking.download(url, Utils.getRootDirPath(getApplicationContext()), "file1.zip")
                .setPriority(Priority.HIGH)
                .setTag(this)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        Log.d(TAG, "bytesDownloaded : " + bytesDownloaded + " totalBytes : " + totalBytes);
                        Log.d(TAG, "setDownloadProgressListener isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.d(TAG, "File download Completed");
                        Log.d(TAG, "onDownloadComplete isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            // received ANError from server
                            // error.getErrorCode() - the ANError code from server
                            // error.getErrorBody() - the ANError body from server
                            // error.getErrorDetail() - just an ANError detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }

    public void downloadImage(final View view) {
        String url = "http://i.imgur.com/AtbX9iX.png";
        AndroidNetworking.download(url, Utils.getRootDirPath(getApplicationContext()), "image1.png")
                .setPriority(Priority.MEDIUM)
                .setTag(this)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.d(TAG, "Image download Completed");
                        Log.d(TAG, "onDownloadComplete isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            // received ANError from server
                            // error.getErrorCode() - the ANError code from server
                            // error.getErrorBody() - the ANError body from server
                            // error.getErrorDetail() - just a ANError detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }

    public void uploadImage(final View view) {
        AndroidNetworking.upload(ApiEndPoint.BASE_URL + ApiEndPoint.UPLOAD_IMAGE)
                .setPriority(Priority.MEDIUM)
                .addMultipartFile("image", new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "test.png"))
                .setTag(this)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        Log.d(TAG, "bytesUploaded : " + bytesUploaded + " totalBytes : " + totalBytes);
                        Log.d(TAG, "setUploadProgressListener isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Image upload Completed");
                        Log.d(TAG, "onResponse object : " + response.toString());
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            // received ANError from server
                            // error.getErrorCode() - the ANError code from server
                            // error.getErrorBody() - the ANError body from server
                            // error.getErrorDetail() - just a ANError detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }

    public void doNotCacheResponse(View view) {
        AndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setTag(this)
                .setPriority(Priority.LOW)
                .doNotCacheResponse()
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse array : " + response.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            // received ANError from server
                            // error.getErrorCode() - the ANError code from server
                            // error.getErrorBody() - the ANError body from server
                            // error.getErrorDetail() - just a ANError detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }

    public void getResponseOnlyIfCached(View view) {
        AndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setTag(this)
                .setPriority(Priority.LOW)
                .getResponseOnlyIfCached()
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse array : " + response.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            // received ANError from server
                            // error.getErrorCode() - the ANError code from server
                            // error.getErrorBody() - the ANError body from server
                            // error.getErrorDetail() - just a ANError detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }

    public void getResponseOnlyFromNetwork(View view) {
        AndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setTag(this)
                .setPriority(Priority.LOW)
                .getResponseOnlyFromNetwork()
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse array : " + response.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            // received ANError from server
                            // error.getErrorCode() - the ANError code from server
                            // error.getErrorBody() - the ANError body from server
                            // error.getErrorDetail() - just a ANError detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }

    public void setMaxAgeCacheControl(View view) {
        AndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setTag(this)
                .setPriority(Priority.LOW)
                .setMaxAgeCacheControl(0, TimeUnit.SECONDS)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse array : " + response.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            // received ANError from server
                            // error.getErrorCode() - the ANError code from server
                            // error.getErrorBody() - the ANError body from server
                            // error.getErrorDetail() - just a ANError detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }

    public void setMaxStaleCacheControl(View view) {
        AndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setTag(this)
                .setPriority(Priority.LOW)
                .setMaxStaleCacheControl(365, TimeUnit.SECONDS)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse array : " + response.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            // received ANError from server
                            // error.getErrorCode() - the ANError code from server
                            // error.getErrorBody() - the ANError body from server
                            // error.getErrorDetail() - just a ANError detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }

    public void cleanupDestinationTest(View view) {
        String url = "http://i.imgur.com/m6K1DCQ.jpg";
        AndroidNetworking.download(url, Utils.getRootDirPath(getApplicationContext()), "cleanupDestinationTest.jpg")
                .setPriority(Priority.HIGH)
                .setTag("cleanupDestinationTest")
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        Log.d(TAG, "bytesDownloaded : " + bytesDownloaded + " totalBytes : " + totalBytes);
                        Log.d(TAG, "setDownloadProgressListener isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                        if (bytesDownloaded > 50) {
                            AndroidNetworking.cancel("cleanupDestinationTest");
                            Log.d(TAG, "cancel: cleanupDestinationTest");
                        }
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.d(TAG, "File download Completed");
                        Log.d(TAG, "onDownloadComplete isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            // received ANError from server
                            // error.getErrorCode() - the ANError code from server
                            // error.getErrorBody() - the ANError body from server
                            // error.getErrorDetail() - just an ANError detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }

    public void disableGzipForCustomRequest(View view) {
        AndroidNetworking.post(ApiEndPoint.BASE_URL + ApiEndPoint.POST_CREATE_AN_USER)
                .addBodyParameter("firstname", "Amit")
                .addBodyParameter("lastname", "Shekhar")
                .setTag(this)
                .setOkHttpClient(new OkHttpClient())
                .setPriority(Priority.LOW)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse object : " + response.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            // received ANError from server
                            // error.getErrorCode() - the ANError code from server
                            // error.getErrorBody() - the ANError body from server
                            // error.getErrorDetail() - just a ANError detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }

    public void checkCacheForCustomClient(View view) {
        String url = "http://www.colorado.edu/conflict/peace/download/peace_problem.ZIP";
        AndroidNetworking.download(url, Utils.getRootDirPath(getApplicationContext()), "file1.zip")
                .setPriority(Priority.HIGH)
                .setTag(this)
                .setOkHttpClient(new OkHttpClient())
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        Log.d(TAG, "bytesDownloaded : " + bytesDownloaded + " totalBytes : " + totalBytes);
                        Log.d(TAG, "setDownloadProgressListener isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.d(TAG, "File download Completed");
                        Log.d(TAG, "onDownloadComplete isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            // received ANError from server
                            // error.getErrorCode() - the ANError code from server
                            // error.getErrorBody() - the ANError body from server
                            // error.getErrorDetail() - just an ANError detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }

    public void checkOkHttpResponse(View view) {

        AndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        if (response != null) {
                            if (response.isSuccessful()) {
                                Log.d(TAG, "response is successful");
                                try {
                                    Log.d(TAG, "response : " + response.body().source().readUtf8());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d(TAG, "response is not successful");
                            }
                        } else {
                            Log.d(TAG, "response is null");
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Utils.logError(TAG, anError);
                    }
                });

        AndroidNetworking.post(ApiEndPoint.BASE_URL + ApiEndPoint.POST_CREATE_AN_USER)
                .addBodyParameter("firstname", "Suman")
                .addBodyParameter("lastname", "Shekhar")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        if (response != null) {
                            if (response.isSuccessful()) {
                                Log.d(TAG, "response is successful");
                                try {
                                    Log.d(TAG, "response : " + response.body().source().readUtf8());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d(TAG, "response is not successful");
                            }
                        } else {
                            Log.d(TAG, "response is null");
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Utils.logError(TAG, anError);
                    }
                });

        AndroidNetworking.upload(ApiEndPoint.BASE_URL + ApiEndPoint.UPLOAD_IMAGE)
                .setPriority(Priority.MEDIUM)
                .addMultipartFile("image", new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "test.png"))
                .setTag(this)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        Log.d(TAG, "bytesUploaded : " + bytesUploaded + " totalBytes : " + totalBytes);
                        Log.d(TAG, "setUploadProgressListener isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }
                })
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        if (response != null) {
                            if (response.isSuccessful()) {
                                Log.d(TAG, "response is successful");
                                try {
                                    Log.d(TAG, "response : " + response.body().source().readUtf8());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d(TAG, "response is not successful");
                            }
                        } else {
                            Log.d(TAG, "response is null");
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Utils.logError(TAG, anError);
                    }
                });
    }


    public void checkSynchronousCall(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = "http://www.colorado.edu/conflict/peace/download/peace_problem.ZIP";
                ANRequest requestOne = AndroidNetworking
                        .download(url, Utils.getRootDirPath(getApplicationContext()), "file1.zip")
                        .build()
                        .setAnalyticsListener(new AnalyticsListener() {
                            @Override
                            public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                                Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                                Log.d(TAG, " bytesSent : " + bytesSent);
                                Log.d(TAG, " bytesReceived : " + bytesReceived);
                                Log.d(TAG, " isFromCache : " + isFromCache);
                            }
                        })
                        .setDownloadProgressListener(new DownloadProgressListener() {
                            @Override
                            public void onProgress(long bytesDownloaded, long totalBytes) {
                                Log.d(TAG, "bytesDownloaded : " + bytesDownloaded + " totalBytes : " + totalBytes);
                                Log.d(TAG, "setDownloadProgressListener isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));

                            }
                        });
                ANResponse<String> responseOne = requestOne.executeForDownload();

                if (responseOne.isSuccess()) {
                    Log.d(TAG, "checkSynchronousCall : download success");
                    Log.d(TAG, "checkSynchronousCall : download result " + responseOne.getResult());
                    Response response = responseOne.getOkHttpResponse();
                    Log.d(TAG, "checkSynchronousCall : headers : " + response.headers().toString());
                } else {
                    Log.d(TAG, "checkSynchronousCall : download failed");
                    Utils.logError(TAG, responseOne.getError());
                }


                ANRequest requestTwo = AndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
                        .addPathParameter("pageNumber", "0")
                        .addQueryParameter("limit", "3")
                        .build()
                        .setAnalyticsListener(new AnalyticsListener() {
                            @Override
                            public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                                Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                                Log.d(TAG, " bytesSent : " + bytesSent);
                                Log.d(TAG, " bytesReceived : " + bytesReceived);
                                Log.d(TAG, " isFromCache : " + isFromCache);
                            }
                        });
                ANResponse<List<User>> responseTwo = requestTwo.executeForObjectList(User.class);

                if (responseTwo.isSuccess()) {
                    Log.d(TAG, "checkSynchronousCall : response success");
                    List<User> users = responseTwo.getResult();
                    Log.d(TAG, "userList size : " + users.size());
                    for (User user : users) {
                        Log.d(TAG, "id : " + user.id);
                        Log.d(TAG, "firstname : " + user.firstname);
                        Log.d(TAG, "lastname : " + user.lastname);
                    }
                    Response response = responseTwo.getOkHttpResponse();
                    Log.d(TAG, "checkSynchronousCall : headers : " + response.headers().toString());
                } else {
                    Log.d(TAG, "checkSynchronousCall : response failed");
                    Utils.logError(TAG, responseTwo.getError());
                }


                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("firstname", "Rohit");
                    jsonObject.put("lastname", "Kumar");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                ANRequest requestThree = AndroidNetworking.post(ApiEndPoint.BASE_URL + ApiEndPoint.POST_CREATE_AN_USER)
                        .addJSONObjectBody(jsonObject)
                        .build()
                        .setAnalyticsListener(new AnalyticsListener() {
                            @Override
                            public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                                Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                                Log.d(TAG, " bytesSent : " + bytesSent);
                                Log.d(TAG, " bytesReceived : " + bytesReceived);
                                Log.d(TAG, " isFromCache : " + isFromCache);
                            }
                        });
                ANResponse<JSONObject> responseThree = requestThree.executeForJSONObject();

                if (responseThree.isSuccess()) {
                    Log.d(TAG, "checkSynchronousCall : jsonObjectANResponse success");
                    JSONObject jsonObjectFinal = responseThree.getResult();
                    Log.d(TAG, "checkSynchronousCall : jsonObjectANResponse result " + jsonObjectFinal.toString());
                    Response response = responseThree.getOkHttpResponse();
                    Log.d(TAG, "checkSynchronousCall : headers : " + response.headers().toString());
                } else {
                    Log.d(TAG, "checkSynchronousCall : jsonObjectANResponse failed");
                    Utils.logError(TAG, responseThree.getError());
                }


                ANRequest requestFour = AndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
                        .addPathParameter("pageNumber", "0")
                        .addQueryParameter("limit", "3")
                        .build()
                        .setAnalyticsListener(new AnalyticsListener() {
                            @Override
                            public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                                Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                                Log.d(TAG, " bytesSent : " + bytesSent);
                                Log.d(TAG, " bytesReceived : " + bytesReceived);
                                Log.d(TAG, " isFromCache : " + isFromCache);
                            }
                        });
                ANResponse<Response> responseFour = requestFour.executeForOkHttpResponse();


                if (responseFour.isSuccess()) {
                    Log.d(TAG, "checkSynchronousCall : okHttpResponse success");
                    Response okHttpResponse = responseFour.getResult();
                    if (okHttpResponse != null) {
                        if (okHttpResponse.isSuccessful()) {
                            Log.d(TAG, "response is successful");
                            try {
                                Log.d(TAG, "response : " + okHttpResponse.body().source().readUtf8());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d(TAG, "response is not successful");
                        }
                    } else {
                        Log.d(TAG, "response is null");
                    }
                } else {
                    Log.d(TAG, "checkSynchronousCall : okHttpResponse failed");
                    Utils.logError(TAG, responseFour.getError());
                }
            }
        }).start();
    }

    public void checkOptionsRequest(View view) {
        AndroidNetworking.options("https://api.github.com/square/okhttp/issues")
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        Log.d(TAG, "response : " + response.headers().toString());
                    }

                    @Override
                    public void onError(ANError anError) {
                        Utils.logError(TAG, anError);
                    }
                });
    }

    public void getCurrentConnectionQuality(View view) {
        Log.d(TAG, "getCurrentConnectionQuality : " + AndroidNetworking.getCurrentConnectionQuality() + " currentBandwidth : " + AndroidNetworking.getCurrentBandwidth());
    }
}
