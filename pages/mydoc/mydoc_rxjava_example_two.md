---
title: RxJava Example
tags: [get]
keywords: "RxJava, http, https, android , example"
last_updated: "Aug 21, 2016"
summary: "RxJava : Example Two"
published: true
sidebar: mydoc_sidebar
permalink: mydoc_rxjava_example_two.html
folder: mydoc
---


### Using Zip Operator - Combining two network request
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
