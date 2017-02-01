---
title: Using Fast Android Networking with RxJava2
tags: [getting_started]
keywords: "GET, http, https, android , get request, parser, rxjava, rxjava2, gradle"
published: true
sidebar: doc_sidebar
permalink: rxjava2_support.html
folder: doc
--- 

## Using Fast Android Networking Library in your application with RxJava2

Add this in your build.gradle

```groovy
compile 'com.amitshekhar.android:rx2-android-networking:0.0.1'
```

Do not forget to add internet permission in manifest if already not present

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Then initialize it in onCreate() Method of application class :

```java
AndroidNetworking.initialize(getApplicationContext());
```

## Using Map Operator
```java
/*    
* Here we are getting ApiUser Object from api server
* then we are converting it into User Object because 
* may be our database support User Not ApiUser Object
* Here we are using Map Operator to do that
*/
Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUser/{userId}")
                .addPathParameter("userId", "1")
                .build()
                .getObjectObservable(ApiUser.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ApiUser, User>() {
                    @Override
                    public User apply(ApiUser apiUser) throws Exception {
                        // here we get ApiUser from server
                        User user = new User(apiUser);
                        // then by converting, we are returning user
                        return user;
                    }
                })
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(User user) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
```

## Using Zip Operator - Combining two network request
```java

/*    
* Here we are making two network calls 
* One returns the list of cricket fans
* Another one returns the list of football fans
* Then we are finding the list of users who loves both
*/

/*
* This observable return the list of User who loves cricket
*/
private Observable<List<User>> getCricketFansObservable() {
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllCricketFans")
                .build()
                .getObjectListObservable(User.class);
}

/*
* This observable return the list of User who loves Football
*/
private Observable<List<User>> getFootballFansObservable() {
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllFootballFans")
                .build()
                .getObjectListObservable(User.class);
}

/*
* This do the complete magic, make both network call
* and then returns the list of user who loves both
* Using zip operator to get both response at a time
*/
private void findUsersWhoLovesBoth() {
        // here we are using zip operator to combine both request
        Observable.zip(getCricketFansObservable(), getFootballFansObservable(),
                new BiFunction<List<User>, List<User>, List<User>>() {
                    @Override
                    public List<User> apply(List<User> cricketFans, List<User> footballFans) throws Exception {
                        List<User> userWhoLovesBoth =
                                filterUserWhoLovesBoth(cricketFans, footballFans);
                        return userWhoLovesBoth;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<User> users) {
                        // do anything with user who loves both

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        
                    }
                });
}

private List<User> filterUserWhoLovesBoth(List<User> cricketFans, List<User> footballFans) {
    List<User> userWhoLovesBoth = new ArrayList<>();
    // your logic to filter who loves both
    return userWhoLovesBoth;
}
```

{% include links.html %}
