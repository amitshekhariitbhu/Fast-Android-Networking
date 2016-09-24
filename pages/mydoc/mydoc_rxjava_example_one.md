---
title: RxJava Example
tags: [get]
keywords: "RxJava, http, https, android , example"
last_updated: "Aug 21, 2016"
summary: "RxJava : Example One"
published: true
sidebar: mydoc_sidebar
permalink: mydoc_rxjava_example_one.html
folder: mydoc
---


### Using Map Operator
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
                .getParseObservable(new TypeToken<ApiUser>() {
                })
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


{% include links.html %}
