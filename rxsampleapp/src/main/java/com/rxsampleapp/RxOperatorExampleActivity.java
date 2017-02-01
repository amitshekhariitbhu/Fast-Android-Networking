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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.androidnetworking.interfaces.AnalyticsListener;
import com.google.gson.reflect.TypeToken;
import com.rxandroidnetworking.RxAndroidNetworking;
import com.rxsampleapp.model.ApiUser;
import com.rxsampleapp.model.User;
import com.rxsampleapp.model.UserDetail;
import com.rxsampleapp.utils.Utils;

import java.util.ArrayList;
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
        testApi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /************************************
     * Just an test api start
     ************************************/

    private void testApi() {
        RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
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
                .getParseObservable(new TypeToken<List<User>>() {
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<User>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onComplete Detail : getAllUsers completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.logError(TAG, e);
                    }

                    @Override
                    public void onNext(List<User> users) {
                        Log.d(TAG, "userList size : " + users.size());
                        for (User user : users) {
                            Log.d(TAG, "id : " + user.id);
                            Log.d(TAG, "firstname : " + user.firstname);
                            Log.d(TAG, "lastname : " + user.lastname);
                        }
                    }
                });
    }

    /************************************
     * map operator start
     ************************************/

    public void map(View view) {
        RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUser/{userId}")
                .addPathParameter("userId", "1")
                .build()
                .getParseObservable(new TypeToken<ApiUser>() {
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ApiUser, User>() {
                    @Override
                    public User call(ApiUser apiUser) {
                        // here we get ApiUser from server
                        User user = new User(apiUser);
                        // then by converting, we are returning user
                        return user;
                    }
                })
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {
                        // do anything onComplete
                    }

                    @Override
                    public void onError(Throwable e) {
                        // handle error
                        Utils.logError(TAG, e);
                    }

                    @Override
                    public void onNext(User user) {
                        Log.d(TAG, "user id : " + user.id);
                        Log.d(TAG, "user firstname : " + user.firstname);
                        Log.d(TAG, "user lastname : " + user.lastname);
                    }
                });
    }


    /************************************
     * zip operator start
     *********************************/

    /*
    * This observable return the list of User who loves cricket
    */
    private Observable<List<User>> getCricketFansObservable() {
        return RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllCricketFans")
                .build()
                .getParseObservable(new TypeToken<List<User>>() {
                });
    }

    /*
    * This observable return the list of User who loves Football
    */
    private Observable<List<User>> getFootballFansObservable() {
        return RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllFootballFans")
                .build()
                .getParseObservable(new TypeToken<List<User>>() {
                });
    }

    /*
    * This do the complete magic, make both network call
    * and then returns the list of user who loves both
    * Using zip operator to get both response at a time
    */
    private void findUsersWhoLovesBoth() {
        // here we are using zip operator to combine both request
        Observable.zip(getCricketFansObservable(), getFootballFansObservable(),
                new Func2<List<User>, List<User>, List<User>>() {
                    @Override
                    public List<User> call(List<User> cricketFans,
                                           List<User> footballFans) {
                        List<User> userWhoLovesBoth =
                                filterUserWhoLovesBoth(cricketFans, footballFans);
                        return userWhoLovesBoth;
                    }
                }
        ).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<User>>() {
                    @Override
                    public void onCompleted() {
                        // do anything onComplete
                    }

                    @Override
                    public void onError(Throwable e) {
                        // handle error
                    }

                    @Override
                    public void onNext(List<User> users) {
                        // do anything with user who loves both
                        Log.d(TAG, "userList size : " + users.size());
                        for (User user : users) {
                            Log.d(TAG, "id : " + user.id);
                            Log.d(TAG, "firstname : " + user.firstname);
                            Log.d(TAG, "lastname : " + user.lastname);
                        }
                    }
                });
    }

    private List<User> filterUserWhoLovesBoth(List<User> cricketFans, List<User> footballFans) {
        List<User> userWhoLovesBoth = new ArrayList<User>();
        for (User cricketFan : cricketFans) {
            for (User footballFan : footballFans) {
                if (cricketFan.id == footballFan.id) {
                    userWhoLovesBoth.add(cricketFan);
                }
            }
        }
        return userWhoLovesBoth;
    }


    public void zip(View view) {
        findUsersWhoLovesBoth();
    }

    /************************************
     * flatMap and filter operator start
     ************************************/

    private Observable<List<User>> getAllMyFriendsObservable() {
        return RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllFriends/{userId}")
                .addPathParameter("userId", "1")
                .build()
                .getParseObservable(new TypeToken<List<User>>() {
                });
    }

    public void flatMapAndFilter(View view) {
        getAllMyFriendsObservable()
                .flatMap(new Func1<List<User>, Observable<User>>() { // flatMap - to return users one by one
                    @Override
                    public Observable<User> call(List<User> usersList) {
                        return Observable.from(usersList); // returning user one by one from usersList.
                    }
                })
                .filter(new Func1<User, Boolean>() {
                    @Override
                    public Boolean call(User user) {
                        // filtering user who follows me.
                        return user.isFollowing;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {
                        // do anything onComplete
                    }

                    @Override
                    public void onError(Throwable e) {
                        // handle error
                    }

                    @Override
                    public void onNext(User user) {
                        // only the user who is following me comes here one by one
                        Log.d(TAG, "id : " + user.id);
                        Log.d(TAG, "firstname : " + user.firstname);
                        Log.d(TAG, "lastname : " + user.lastname);
                        Log.d(TAG, "isFollowing : " + user.isFollowing);
                    }
                });
    }

    /************************************
     * take operator start
     ************************************/

    public void take(View view) {
        getUserListObservable()
                .flatMap(new Func1<List<User>, Observable<User>>() { // flatMap - to return users one by one
                    @Override
                    public Observable<User> call(List<User> usersList) {
                        return Observable.from(usersList); // returning user one by one from usersList.
                    }
                })
                .take(4) // it will only emit first 4 users out of all
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(User user) {
                        // only four user comes here one by one
                        Log.d(TAG, "id : " + user.id);
                        Log.d(TAG, "firstname : " + user.firstname);
                        Log.d(TAG, "lastname : " + user.lastname);
                        Log.d(TAG, "isFollowing : " + user.isFollowing);
                    }
                });
    }


    /************************************
     * flatMap operator start
     ************************************/


    public void flatMap(View view) {
        getUserListObservable()
                .flatMap(new Func1<List<User>, Observable<User>>() { // flatMap - to return users one by one
                    @Override
                    public Observable<User> call(List<User> usersList) {
                        return Observable.from(usersList); // returning user one by one from usersList.
                    }
                })
                .flatMap(new Func1<User, Observable<UserDetail>>() {
                    @Override
                    public Observable<UserDetail> call(User user) {
                        // here we get the user one by one
                        // and returns corresponding getUserDetailObservable
                        // for that userId
                        return getUserDetailObservable(user.id);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserDetail>() {
                    @Override
                    public void onCompleted() {
                        // do something onCompleted
                    }

                    @Override
                    public void onError(Throwable e) {
                        // handle error
                        Utils.logError(TAG, e);
                    }

                    @Override
                    public void onNext(UserDetail userDetail) {
                        // do anything with userDetail
                        Log.d(TAG, "userDetail id : " + userDetail.id);
                        Log.d(TAG, "userDetail firstname : " + userDetail.firstname);
                        Log.d(TAG, "userDetail lastname : " + userDetail.lastname);
                    }
                });
    }

    /************************************
     * flatMapWithZip operator start
     ************************************/

    private Observable<List<User>> getUserListObservable() {
        return RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "10")
                .build()
                .getParseObservable(new TypeToken<List<User>>() {
                });
    }

    private Observable<UserDetail> getUserDetailObservable(long id) {
        return RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUserDetail/{userId}")
                .addPathParameter("userId", String.valueOf(id))
                .build()
                .getParseObservable(new TypeToken<UserDetail>() {
                });
    }

    public void flatMapWithZip(View view) {
        getUserListObservable()
                .flatMap(new Func1<List<User>, Observable<User>>() { // flatMap - to return users one by one
                    @Override
                    public Observable<User> call(List<User> usersList) {
                        return Observable.from(usersList); // returning user one by one from usersList.
                    }
                })
                .flatMap(new Func1<User, Observable<Pair<UserDetail, User>>>() {
                    @Override
                    public Observable<Pair<UserDetail, User>> call(User user) {
                        // here we get the user one by one and then we are zipping
                        // two observable - one getUserDetailObservable (network call to get userDetail)
                        // and another Observable.just(user) - just to emit user
                        return Observable.zip(getUserDetailObservable(user.id), // zip to combine two observable
                                Observable.just(user),
                                new Func2<UserDetail, User, Pair<UserDetail, User>>() {
                                    @Override
                                    public Pair<UserDetail, User> call(UserDetail userDetail, User user) {
                                        // runs when network call completes
                                        // we get here userDetail for the corresponding user
                                        return new Pair<>(userDetail, user); // returning the pair(userDetail, user)
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<UserDetail, User>>() {
                    @Override
                    public void onCompleted() {
                        // do something onCompleted
                    }

                    @Override
                    public void onError(Throwable e) {
                        // handle error
                        Utils.logError(TAG, e);
                    }

                    @Override
                    public void onNext(Pair<UserDetail, User> pair) {
                        // here we are getting the userDetail for the corresponding user one by one
                        UserDetail userDetail = pair.first;
                        User user = pair.second;
                        Log.d(TAG, "userId : " + user.id);
                        Log.d(TAG, "userDetail firstname : " + userDetail.firstname);
                        Log.d(TAG, "userDetail lastname : " + userDetail.lastname);
                    }
                });
    }

    /************************************
     * others start here
     ************************************/

    public void startRxApiTestActivity(View view) {
        startActivity(new Intent(RxOperatorExampleActivity.this, RxApiTestActivity.class));
    }

    public void startSubscriptionActivity(View view) {
        startActivity(new Intent(RxOperatorExampleActivity.this, SubscriptionActivity.class));
    }

}
