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

package com.rx2sampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.rx2androidnetworking.Rx2AndroidNetworking;
import com.rx2sampleapp.utils.Utils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by amitshekhar on 31/07/16.
 */
public class SubscriptionActivity extends AppCompatActivity {

    private static final String TAG = SubscriptionActivity.class.getSimpleName();
    private static final String URL = "http://i.imgur.com/AtbX9iX.png";
    private String dirPath;
    private String fileName = "imgurimage.png";
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        dirPath = Utils.getRootDirPath(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    public Observable<String> getObservable() {
        return Rx2AndroidNetworking.download(URL, dirPath, fileName)
                .build()
                .getDownloadObservable();
    }

    private DisposableObserver<String> getDisposableObserver() {

        return new DisposableObserver<String>() {

            @Override
            public void onNext(String response) {
                Log.d(TAG, "onResponse response : " + response);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onCompleted");
            }
        };
    }

    public void downloadFile(View view) {
        disposables.add(getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver()));
    }
}