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

package com.rxsampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.rxandroidnetworking.RxAndroidNetworking;
import com.rxsampleapp.model.User;
import com.rxsampleapp.model.UserId;
import com.rxsampleapp.utils.Utils;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by amitshekhar on 31/07/16.
 */
public class RxOperatorExampleActivity extends AppCompatActivity {

    private static final String TAG = RxOperatorExampleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_operator_example);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private Observable<List<UserId>> getUserIdsObservable() {
        return RxAndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "10")
                .build()
                .getParseObservable(new TypeToken<List<UserId>>() {
                });
    }

    private Observable<User> getUserObservable(long id) {
        return RxAndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_OBJECT)
                .addPathParameter("userId", String.valueOf(id))
                .build()
                .getParseObservable(new TypeToken<User>() {
                });
    }

    public void flatMap(View view) {
        getUserIdsObservable()
                .flatMap(new Func1<List<UserId>, Observable<UserId>>() {
                    @Override
                    public Observable<UserId> call(List<UserId> userIds) {
                        return Observable.from(userIds);
                    }
                })
                .flatMap(new Func1<UserId, Observable<Pair<User, UserId>>>() {
                    @Override
                    public Observable<Pair<User, UserId>> call(UserId userId) {
                        Observable<User> _userObservable = getUserObservable(userId.id)
                                .filter(new Func1<User, Boolean>() {
                                    @Override
                                    public Boolean call(User user) {
                                        return user.id != 0;
                                    }
                                });

                        return Observable.zip(_userObservable,
                                Observable.just(userId),
                                new Func2<User, UserId, Pair<User, UserId>>() {
                                    @Override
                                    public Pair<User, UserId> call(User user, UserId userId1) {
                                        return new Pair<>(user, userId1);
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<User, UserId>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.logError(TAG, e);
                    }

                    @Override
                    public void onNext(Pair<User, UserId> pair) {
                        User user = pair.first;
                        UserId userId = pair.second;
                        Log.d(TAG, "userId : " + userId.id);
                        Log.d(TAG, "user id: " + user.id);
                        Log.d(TAG, "user firstname : " + user.firstname);
                        Log.d(TAG, "user lastname : " + user.lastname);
                    }
                });
    }
}
