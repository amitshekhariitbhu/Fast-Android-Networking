# Using Fast Android Networking Library with [RxJava](https://github.com/ReactiveX/RxJava)

Add this in your build.gradle
```groovy
    compile 'com.amitshekhar.android:rx-android-networking:0.1.0'
```

Then initialize it in onCreate() Method of application class :
```java
    AndroidNetworking.initialize(getApplicationContext());
```

### All these below examples are working and available on this repo in rx-sample-app.

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

### Using FlatMap And Filter Operators
```java

       /*    
       * First of all we are getting my friends list from
       * server, then by using flatMap we are emitting users
       * one by one and then after applying filter we are
       * returning only those who are following me one by one.
       */
       
       /*
       * This observable return the list of User who are my friends
       */    
       private Observable<List<User>> getAllMyFriendsObservable() {
           return RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllFriends/{userId}")
                   .addPathParameter("userId", "1")
                   .build()
                   .getParseObservable(new TypeToken<List<User>>() {
                   });
       }
   
       /*
       * This method does all
       */       
       public void flatMapAndFilter() {
           getAllMyFriendsObservable()
                   .flatMap(new Func1<List<User>, Observable<User>>() { // flatMap - to return users one by one
                       @Override
                       public Observable<User> call(List<User> usersList) {
                           return Observable.from(usersList); // returning(emitting) user one by one from usersList.
                       }
                   })
                   .filter(new Func1<User, Boolean>() { // filter operator
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
                       }
                   });
       }

```

### Using Take Operator
```java

    /* Here first of all, we get the list of users from server.
    * Then using using take operator, it only emits
    * required number of users. 
    */

    /*
    * This observable return the list of users.
    */
    private Observable<List<User>> getUserListObservable() {
        return RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "10")
                .build()
                .getParseObservable(new TypeToken<List<User>>() {});
    }

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
                    // do something onCompletion
                }

                @Override
                public void onError(Throwable e) {
                    // handle error
                }

                @Override
                public void onNext(User user) {
                    // only four user comes here one by one
                }
            });
```

### Using flatMap Operator
```java

    /* Here first of all, we get the list of users from server.
    * Then for each userId from user, it makes the network call to get the detail 
    * of that user. 
    * Finally, we get the userDetail for the corresponding user one by one
    */

    /*
    * This observable return the list of users.
    */
    private Observable<List<User>> getUserListObservable() {
        return RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "10")
                .build()
                .getParseObservable(new TypeToken<List<User>>() {});
    }
    
    /*
    * This observable return the userDetail corresponding to the user.
    */
    private Observable<UserDetail> getUserDetailObservable(long userId) {
        return RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUserDetail/{userId}")
                .addPathParameter("userId", String.valueOf(userId))
                .build()
                .getParseObservable(new TypeToken<UserDetail>() {});
    }
    
    /*
    * This method do the magic - first gets the list of users
    * from server.Then, for each user, it makes the network call to get the detail 
    * of that user.
    * Finally, we get the UserDetail for the corresponding user one by one
    */
    public void flatMap() {
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
                        }
    
                        @Override
                        public void onNext(UserDetail userDetail) {
                            // here we get userDetail one by one for all users
                            Log.d(TAG, "userDetail id : " + userDetail.id);
                            Log.d(TAG, "userDetail firstname : " + userDetail.firstname);
                            Log.d(TAG, "userDetail lastname : " + userDetail.lastname);
                        }
                    });
        }

```

### Using combination of flatMap with zip Operator
```java

    /* Very Similar to above example, only change is 
    *  that, here we are using zip after flatMap to 
    * combine(pair) User and UserDetail
    */ 
     
    /*
    * This method do the magic - first gets the list of users
    * from server.Then, for each user, it makes the network call to get the detail 
    * of that user.
    * Finally, we get the UserDetail for the corresponding user one by one
    */
    private void flatMapWithZip() {
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
```

### Binding Networking with Activity Lifecycle
```java
public class SubscriptionActivity extends Activity {

    private static final String TAG = SubscriptionActivity.class.getSimpleName();
    private static final String URL = "http://i.imgur.com/AtbX9iX.png";
    private String dirPath;
    private String fileName = "imgurimage.png";
    Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dirPath = Utils.getRootDirPath(getApplicationContext());
        subscription = getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            // unsubscribe it when activity onDestroy is called
            subscription.unsubscribe();
        }
    }

    public Observable<String> getObservable() {
        return RxAndroidNetworking.download(URL, dirPath, fileName)
                .build()
                .getDownloadObservable();
    }

    private Observer<String> getObserver() {
        return new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError " + e.getMessage());
            }

            @Override
            public void onNext(String response) {
                Log.d(TAG, "onResponse response : " + response);
            }
        };
    }
}

```

### Making a GET Request
```java
RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                 .addPathParameter("pageNumber", "0")
                 .addQueryParameter("limit", "3")
                 .build()
                 .getJSONArrayObservable()
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Observer<JSONArray>() {
                      @Override
                      public void onCompleted() {
                      // do anything onComplete
                      }
                      @Override
                      public void onError(Throwable e) {
                      // handle error
                      }
                      @Override
                      public void onNext(JSONArray response) {
                      //do anything with response
                      }
                  });
```

### Making a POST Request
```java
RxAndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/createAnUser")
                 .addBodyParameter("firstname", "Amit")
                 .addBodyParameter("lastname", "Shekhar")
                 .build()
                 .getJSONObjectObservable()
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Observer<JSONObject>() {
                      @Override
                      public void onCompleted() {
                      // do anything onComplete
                      }
                      @Override
                      public void onError(Throwable e) {
                      // handle error
                      }
                      @Override
                      public void onNext(JSONObject response) {
                      //do anything with response
                      }
                  });
```

### Downloading a file from server
```java
RxAndroidNetworking.download("http://i.imgur.com/AtbX9iX.png",dirPath,imgurimage.png)
                 .build()
                 .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                    // do anything with progress  
                    }
                 })
                 .getDownloadObservable()
                 .subscribeOn(Schedulers.io()
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Observer<String>() {
                      @Override
                      public void onCompleted() {
                      // do anything onComplete
                      }
                      @Override
                      public void onError(Throwable e) {
                      // handle error
                      }
                      @Override
                      public void onNext(String response) {
                      //gives response = "success"
                      }
                  });
```

### Uploading a file to server
```java
RxAndroidNetworking.upload("https://fierce-cove-29863.herokuapp.com/uploadImage")
                 .addMultipartFile("image", new File(imageFilePath)) 
                 .build()
                 .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                    // do anything with progress  
                    }
                 })
                 .getJSONObjectObservable()
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Observer<JSONObject>() {
                      @Override
                      public void onCompleted() {
                      // do anything onComplete
                      }
                      @Override
                      public void onError(Throwable e) {
                      // handle error
                      }
                      @Override
                      public void onNext(JSONObject response) {
                      //do anything with response
                      }
                  });
```

### Using it with your own JAVA Object - JSON Parser
```java
/*--------------Example One -> Getting the userList----------------*/
RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .build()
                .getParseObservable(new TypeToken<List<User>>() {})
                .subscribeOn(Schedulers.io())
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
                        // do anything with response    
                        Log.d(TAG, "userList size : " + users.size());
                        for (User user : users) {
                            Log.d(TAG, "id : " + user.id);
                            Log.d(TAG, "firstname : " + user.firstname);
                            Log.d(TAG, "lastname : " + user.lastname);
                        }
                    }
                });                
/*--------------Example Two -> Getting an user----------------*/
RxAndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUser/{userId}")
                .addPathParameter("userId", "1")
                .build()
                .getParseObservable(new TypeToken<User>() {})
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
                        // do anything with response 
                        Log.d(TAG, "id : " + user.id);
                        Log.d(TAG, "firstname : " + user.firstname);
                        Log.d(TAG, "lastname : " + user.lastname);
                    }
                });
/*-- Note : TypeToken and getParseObservable is important here --*/              
```

### Error Code Handling
```java
public void onError(Throwable e) {
        if (e instanceof ANError) {
            ANError anError = (ANError) e;
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
            Log.d(TAG, "onError errorMessage : " + e.getMessage());
        }
   }
```

### In RxJava, you can do too many things by applying the operators (flatMap, filter, map, mapMany, zip, etc) available in RxJava.