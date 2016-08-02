# Using Fast Android Networking Library with [RxJava](https://github.com/ReactiveX/RxJava)

Add this in your build.gradle
```groovy
compile 'com.amitshekhar.android:rx-android-networking:0.1.0'
```

Then initialize it in onCreate() Method of application class :
```java
AndroidNetworking.initialize(getApplicationContext());
```

### Making a GET Request
```java
RxAndroidNetworking.get("http://api.localhost.com/{pageNumber}/test")
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

### Using Map Operator
```java
/*    
* Here we are getting ApiUser Object from server
* then we are converting it into User Object
* Using Map Operator
*/
RxAndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_OBJECT)
                .addPathParameter("userId", "1")
                .build()
                .getParseObservable(new TypeToken<ApiUser>() {})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ApiUser, User>() {
                    @Override
                    public User call(ApiUser apiUser) {
                        User user = convertApiUserToUser(apiUser);
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

### Using Zip Operator
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
        return RxAndroidNetworking.get("http://api.localhost.com/getAllCricketFans")
                .build()
                .getParseObservable(new TypeToken<List<User>>() {
                });
    }

    /*
    * This observable return the list of User who loves Football
    */
    private Observable<List<User>> getFootballFansObservable() {
        return RxAndroidNetworking.get("http://api.localhost.com/getAllFootballFans")
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

### Making a POST Request
```java
RxAndroidNetworking.post("http://api.localhost.com/createAnUser")
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
RxAndroidNetworking.download(url,dirPath,fileName)
                 .build()
                 .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                    // do anything with progress  
                    }
                 })
                 .getDownloadObservable()
                 .subscribeOn(Schedulers.io())
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

### Using it with your own JAVA Object - JSON Parser
```java
/*--------------Example One -> Getting the userList----------------*/
RxAndroidNetworking.get("http://api.localhost.com/getAllUsers/{pageNumber}")
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
RxAndroidNetworking.get("http://api.localhost.com/getAnUser/{userId}")
                .addPathParameter("userId", "1")
                .setUserAgent("getAnUser")
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

### Uploading a file to server
```java
RxAndroidNetworking.upload("http://api.localhost.com/uploadImage")
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

### Using Operator like flatMap,Zip
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
        return RxAndroidNetworking.get("http://api.localhost.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "10")
                .build()
                .getParseObservable(new TypeToken<List<User>>() {});
    }
    
    /*
    * This observable return the userDetail corresponding to the user.
    */
    private Observable<UserDetail> getUserDetailObservable(long userId) {
        return RxAndroidNetworking.get("http://api.localhost.com/getAnUser/{userId}")
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
    private void doMagic() {
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
    private static final String URL = "http://api.localhost.com/file.ZIP";
    private String dirPath;
    private String fileName = "file1.zip";
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

### In RxJava, you can do too many things by applying the operators (flatMap,filter,map,mapMany,zip,etc) available in RxJava.