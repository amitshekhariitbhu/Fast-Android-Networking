---
title: Using Fast Android Networking with RxJava
tags: [getting_started]
keywords: "GET, http, https, android , get request, parser, rxjava, gradle"
published: true
sidebar: doc_sidebar
permalink: rxjava_support.html
folder: doc
--- 

## Using Fast Android Networking Library in your application with RxJava

Add this in your build.gradle

```groovy
compile 'com.amitshekhar.android:rx-android-networking:1.0.2'
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
RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUser/{userId}")
                .addPathParameter("userId", "1")
                .build()
                .getObjectObservable(ApiUser.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ApiUser, User>() { // takes ApiUser and returns User
                    @Override
                    public User call(ApiUser apiUser) {
                        // here we get ApiUser from server
                        User user = new User(apiUser);
                        // then by converting, we are returing user
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
                    }
                    @Override
                    public void onNext(User user) {
                        // do anything with user
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
    return RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllCricketFans")
            .build()
            .getObjectListObservable(User.class);
}

/*
* This observable return the list of User who loves Football
*/
private Observable<List<User>> getFootballFansObservable() {
    return RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllFootballFans")
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
