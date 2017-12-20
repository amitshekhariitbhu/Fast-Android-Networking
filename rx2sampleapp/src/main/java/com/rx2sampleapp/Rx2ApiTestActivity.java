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
package com.rx2sampleapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.rx2androidnetworking.Rx2ANRequest;
import com.rx2androidnetworking.Rx2AndroidNetworking;
import com.rx2sampleapp.model.User;
import com.rx2sampleapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Prashant Gupta on 25-07-2016.
 */
public class Rx2ApiTestActivity extends AppCompatActivity {

    private static final String TAG = Rx2ApiTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_api_test);
    }

    public void getAllUsers(View view) {
        Rx2AndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
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
                })
                .getObjectListSingle(User.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<User>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<User> users) {
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                        Log.d(TAG, "userList size : " + users.size());
                        for (User user : users) {
                            Log.d(TAG, "id : " + user.id);
                            Log.d(TAG, "firstname : " + user.firstname);
                            Log.d(TAG, "lastname : " + user.lastname);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Utils.logError(TAG, throwable);
                    }
                });
    }

    public void getAnUser(View view) {
        Rx2AndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_OBJECT)
                .addPathParameter("userId", "1")
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
                .getObjectSingle(User.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onSuccess(@NonNull User user) {
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                        Log.d(TAG, "id : " + user.id);
                        Log.d(TAG, "firstname : " + user.firstname);
                        Log.d(TAG, "lastname : " + user.lastname);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Utils.logError(TAG, throwable);
                    }
                });
    }

    public void checkForHeaderGet(View view) {

        Rx2ANRequest.GetRequestBuilder getRequestBuilder = new Rx2ANRequest.GetRequestBuilder(ApiEndPoint.BASE_URL + ApiEndPoint.CHECK_FOR_HEADER);

        getRequestBuilder.addHeaders("token", "1234")
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
                .getJSONObjectSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onSuccess(@NonNull JSONObject jsonObject) {
                        Log.d(TAG, "onResponse object : " + jsonObject.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Utils.logError(TAG, throwable);
                    }
                });
    }

    public void checkForHeaderPost(View view) {

        Rx2ANRequest.PostRequestBuilder postRequestBuilder = Rx2AndroidNetworking.post(ApiEndPoint.BASE_URL + ApiEndPoint.CHECK_FOR_HEADER);

        postRequestBuilder.addHeaders("token", "1234");

        Rx2ANRequest rxAnRequest = postRequestBuilder.setTag(this)
                .build();

        rxAnRequest.setAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                Log.d(TAG, " bytesSent : " + bytesSent);
                Log.d(TAG, " bytesReceived : " + bytesReceived);
                Log.d(TAG, " isFromCache : " + isFromCache);
            }
        });

        rxAnRequest.getJSONObjectSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onSuccess(@NonNull JSONObject jsonObject) {
                        Log.d(TAG, "onResponse object : " + jsonObject.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Utils.logError(TAG, throwable);
                    }
                });
    }

    public void createAnUser(View view) {
        Rx2AndroidNetworking.post(ApiEndPoint.BASE_URL + ApiEndPoint.POST_CREATE_AN_USER)
                .addBodyParameter("firstname", "Amit")
                .addBodyParameter("lastname", "Shekhar")
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
                .getJSONObjectSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onSuccess(@NonNull JSONObject jsonObject) {
                        Log.d(TAG, "onResponse object : " + jsonObject.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Utils.logError(TAG, throwable);
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
        Rx2AndroidNetworking.post(ApiEndPoint.BASE_URL + ApiEndPoint.POST_CREATE_AN_USER)
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
                })
                .getJSONObjectSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onSuccess(@NonNull JSONObject jsonObject) {
                        Log.d(TAG, "onResponse object : " + jsonObject.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Utils.logError(TAG, throwable);
                    }
                });
    }

    public void downloadFile(final View view) {
        String url = "http://www.colorado.edu/conflict/peace/download/peace_problem.ZIP";
        Rx2AndroidNetworking.download(url, Utils.getRootDirPath(getApplicationContext()), "file1.zip")
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
                .getDownloadCompletable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "File download Completed");
                        Log.d(TAG, "onDownloadComplete isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Utils.logError(TAG, throwable);
                    }
                });
    }

    public void downloadImage(final View view) {
        String url = "http://i.imgur.com/AtbX9iX.png";
        Rx2AndroidNetworking.download(url, Utils.getRootDirPath(getApplicationContext()), "image1.png")
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
                .getDownloadCompletable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "File download Completed");
                        Log.d(TAG, "onDownloadComplete isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Utils.logError(TAG, throwable);
                    }
                });
    }

    public void uploadImage(final View view) {
        Single<JSONObject> single = Rx2AndroidNetworking.upload(ApiEndPoint.BASE_URL + ApiEndPoint.UPLOAD_IMAGE)
                .addMultipartFile("image", new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "test.png"))
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
                .getJSONObjectSingle();

        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onSuccess(@NonNull JSONObject jsonObject) {
                        Log.d(TAG + "_1", "Image upload Completed");
                        Log.d(TAG + "_1", "onResponse object : " + jsonObject.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Utils.logError(TAG, throwable);
                    }
                });

        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onSuccess(@NonNull JSONObject jsonObject) {
                        Log.d(TAG + "_2", "Image upload Completed");
                        Log.d(TAG + "_2", "onResponse object : " + jsonObject.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Utils.logError(TAG, throwable);
                    }
                });

    }

    public void getCurrentConnectionQuality(View view) {
        Log.d(TAG, "getCurrentConnectionQuality : " + AndroidNetworking.getCurrentConnectionQuality() + " currentBandwidth : " + AndroidNetworking.getCurrentBandwidth());
    }

    public void loadImage(View view) {
        final String URL_IMAGE = "http://i.imgur.com/2M7Hasn.png";
        Rx2AndroidNetworking.get(URL_IMAGE)
                .setImageScaleType(null)
                .setBitmapMaxHeight(0)
                .setBitmapMaxWidth(0)
                .setBitmapConfig(Bitmap.Config.ARGB_8888)
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
                .getBitmapSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Bitmap>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onSuccess(@NonNull Bitmap bitmap) {
                        Log.d(TAG, "onResponse Bitmap");
                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Utils.logError(TAG, throwable);
                    }
                });

    }

}
