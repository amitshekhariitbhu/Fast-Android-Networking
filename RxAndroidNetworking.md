# Using Fast Android Networking Library with [RxJava](https://github.com/ReactiveX/RxJava)

Add this in your build.gradle
```groovy
compile 'com.amitshekhar.android:rx-android-networking:0.1.0'
```

Then initialize it in onCreate() Method of application class, :
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

### Making a POST Request
```java
RxAndroidNetworking.post("http://api.localhost.com/createAnUser")
                 .addBodyParameter("firstname", "Amit")
                 .addBodyParameter("lastname", "Shekhar")
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
