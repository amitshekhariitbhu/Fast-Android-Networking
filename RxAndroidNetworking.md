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

### In RxJava, you can do too many things by applying the operators (flatMap,filter,map,mapMany,zip,etc) available in RxJava.