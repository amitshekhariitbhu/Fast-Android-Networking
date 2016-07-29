package com.rxsampleapp;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.rxandroidnetworking.RxANRequest;
import com.rxandroidnetworking.RxAndroidNetworking;
import com.rxsampleapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rx.Observer;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Prashant Gupta on 25-07-2016.
 */
public class RxTestActivity extends AppCompatActivity {

    private static final String TAG = RxTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_test);
    }

    public void getAllUsers(View view) {
        RxAndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
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
                .getJsonArrayObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<JSONArray>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onComplete Detail : getAllUsers completed");
                    }

                    @Override
                    public void onError(Throwable error) {
                        if (error instanceof ANError) {
                            ANError anError = (ANError) error;
                            if (anError.getErrorCode() != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                                Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            }
                        } else {
                            Log.d(TAG, "onError errorMessage : " + error.getMessage());
                        }
                    }

                    @Override
                    public void onNext(JSONArray jsonArray) {
                        Log.d(TAG, "onResponse array : " + jsonArray.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }
                });
    }

    public void getAnUser(View view) {
        RxAndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_OBJECT)
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
                .getJsonObjectObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onComplete Detail : getAnUser completed");
                    }

                    @Override
                    public void onError(Throwable error) {
                        if (error instanceof ANError) {
                            ANError anError = (ANError) error;
                            if (anError.getErrorCode() != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                                Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            }
                        } else {
                            Log.d(TAG, "onError errorMessage : " + error.getMessage());
                        }
                    }

                    @Override
                    public void onNext(JSONObject response) {
                        Log.d(TAG, "onResponse object : " + response.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }
                });
    }

    public void checkForHeaderGet(View view) {

        RxANRequest.GetRequestBuilder getRequestBuilder = new RxANRequest.GetRequestBuilder(ApiEndPoint.BASE_URL + ApiEndPoint.CHECK_FOR_HEADER);

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
                .getJsonObjectObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onComplete Detail : checkForHeaderGet completed");
                    }

                    @Override
                    public void onError(Throwable error) {
                        if (error instanceof ANError) {
                            ANError anError = (ANError) error;
                            if (anError.getErrorCode() != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                                Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            }
                        } else {
                            Log.d(TAG, "onError errorMessage : " + error.getMessage());
                        }
                    }

                    @Override
                    public void onNext(JSONObject response) {
                        Log.d(TAG, "onResponse object : " + response.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }
                });
    }

    public void checkForHeaderPost(View view) {

        RxANRequest.PostRequestBuilder postRequestBuilder = RxAndroidNetworking.post(ApiEndPoint.BASE_URL + ApiEndPoint.CHECK_FOR_HEADER);

        postRequestBuilder.addHeaders("token", "1234");

        RxANRequest rxAnRequest = postRequestBuilder.setTag(this)
                .setPriority(Priority.LOW)
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

        rxAnRequest.getJsonObjectObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onComplete Detail : checkForHeaderPost completed");
                    }

                    @Override
                    public void onError(Throwable error) {
                        if (error instanceof ANError) {
                            ANError anError = (ANError) error;
                            if (anError.getErrorCode() != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                                Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            }
                        } else {
                            Log.d(TAG, "onError errorMessage : " + error.getMessage());
                        }
                    }

                    @Override
                    public void onNext(JSONObject response) {
                        Log.d(TAG, "onResponse object : " + response.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }
                });
    }

    public void createAnUser(View view) {
        RxAndroidNetworking.post(ApiEndPoint.BASE_URL + ApiEndPoint.POST_CREATE_AN_USER)
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
                .getJsonObjectObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onComplete Detail : createAnUser completed");
                    }

                    @Override
                    public void onError(Throwable error) {
                        if (error instanceof ANError) {
                            ANError anError = (ANError) error;
                            if (anError.getErrorCode() != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                                Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            }
                        } else {
                            Log.d(TAG, "onError errorMessage : " + error.getMessage());
                        }
                    }

                    @Override
                    public void onNext(JSONObject response) {
                        Log.d(TAG, "onResponse object : " + response.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
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
        RxAndroidNetworking.post(ApiEndPoint.BASE_URL + ApiEndPoint.POST_CREATE_AN_USER)
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
                .getJsonObjectObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onComplete Detail : createAnUserJSONObject completed");
                    }

                    @Override
                    public void onError(Throwable error) {
                        if (error instanceof ANError) {
                            ANError anError = (ANError) error;
                            if (anError.getErrorCode() != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                                Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            }
                        } else {
                            Log.d(TAG, "onError errorMessage : " + error.getMessage());
                        }
                    }

                    @Override
                    public void onNext(JSONObject response) {
                        Log.d(TAG, "onResponse object : " + response.toString());
                        Log.d(TAG, "onResponse isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }
                });
    }

    public void downloadFile(final View view) {
        String url = "http://www.colorado.edu/conflict/peace/download/peace_problem.ZIP";
        RxAndroidNetworking.download(url, Utils.getRootDirPath(getApplicationContext()), "file1.zip")
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
                .getDownloadObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "File download Completed");
                        Log.d(TAG, "onDownloadComplete isMainThread : " + String.valueOf(Looper.myLooper() == Looper.getMainLooper()));
                    }

                    @Override
                    public void onError(Throwable error) {
                        if (error instanceof ANError) {
                            ANError anError = (ANError) error;
                            if (anError.getErrorCode() != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(TAG, "onError errorCode : " + anError.getErrorCode());
                                Log.d(TAG, "onError errorBody : " + anError.getErrorBody());
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(TAG, "onError errorDetail : " + anError.getErrorDetail());
                            }
                        } else {
                            Log.d(TAG, "onError errorMessage : " + error.getMessage());
                        }
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }
}
