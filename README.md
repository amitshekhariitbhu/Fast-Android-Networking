<img src=https://raw.githubusercontent.com/amitshekhariitbhu/Fast-Android-Networking/master/assets/androidnetworking.png >

# Fast Android Networking Library

[![Build Status](https://travis-ci.org/amitshekhariitbhu/Fast-Android-Networking.svg?branch=master)](https://travis-ci.org/amitshekhariitbhu/Fast-Android-Networking)
[![Mindorks](https://img.shields.io/badge/mindorks-opensource-blue.svg)](https://mindorks.com/open-source-projects)
[![Mindorks Community](https://img.shields.io/badge/join-community-blue.svg)](https://mindorks.com/join-community)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android%20Networking-blue.svg?style=flat)](http://android-arsenal.com/details/1/3695)
[![API](https://img.shields.io/badge/API-9%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=9)
[![Download](https://api.bintray.com/packages/amitshekhariitbhu/maven/android-networking/images/download.svg) ](https://bintray.com/amitshekhariitbhu/maven/android-networking/_latestVersion)
[![Open Source Love](https://badges.frapsoft.com/os/v1/open-source.svg?v=102)](https://opensource.org/licenses/Apache-2.0)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/amitshekhariitbhu/Fast-Android-Networking/blob/master/LICENSE)

### About Fast Android Networking Library

Fast Android Networking Library is a powerful library for doing any type of networking in Android applications which is made on top of [OkHttp Networking Layer](http://square.github.io/okhttp/).

Fast Android Networking Library takes care of each and everything. So you don't have to do anything, just make request and listen for the response.

### Why use Fast Android Networking ?
* Recent removal of HttpClient in Android Marshmallow(Android M) made other networking library obsolete.
* No other single library do each and everything like making request, downloading any type of file, uploading file, loading
  image from network in ImageView, etc. There are libraries but they are outdated.
* No other library provided simple interface for doing all types of things in networking like setting priority, cancelling, etc.
* As it uses [Okio](https://github.com/square/okio) , No more GC overhead in android application.
  [Okio](https://github.com/square/okio) is made to handle GC overhead while allocating memory.
  [Okio](https://github.com/square/okio) do some clever things to save CPU and memory.
* As it uses [OkHttp](http://square.github.io/okhttp/) , most important it supports HTTP/2.  


### RxJava2 Support, [check here](https://amitshekhariitbhu.github.io/Fast-Android-Networking/rxjava2_support.html).

### RxJava2 + Fast Android Networking + Dagger2 with MVP Architecture Project, [Check here](https://github.com/MindorksOpenSource/android-mvp-architecture)

### Another awesome library for debugging databases and shared preferences, [Check here](https://github.com/amitshekhariitbhu/Android-Debug-Database)

### RxJava2 + Fast Android Networking + Dagger2 with MVVM Architecture Project, [Check here](https://github.com/MindorksOpenSource/android-mvvm-architecture)

### PRDownloader library for downloading file with pause and resume support, [Check here](https://github.com/MindorksOpenSource/PRDownloader)

### Find this project useful ? :heart:
* Support it by clicking the :star: button on the upper right of this page. :v:

For full details, visit the documentation on our web site :

<a href="https://amitshekhariitbhu.github.io/Fast-Android-Networking" target="_blank"><img src="https://raw.githubusercontent.com/amitshekhariitbhu/Fast-Android-Networking/master/assets/get_started.png" width="150" height="42"/></a>

## Requirements

Fast Android Networking Library can be included in any Android application. 

Fast Android Networking Library supports Android 2.3 (Gingerbread) and later. 

## Using Fast Android Networking Library in your application

Add this in your build.gradle
```groovy
compile 'com.amitshekhar.android:android-networking:1.0.1'
```
Do not forget to add internet permission in manifest if already not present
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
Then initialize it in onCreate() Method of application class :
```java
AndroidNetworking.initialize(getApplicationContext());
```
Initializing it with some customization , as it uses [OkHttp](http://square.github.io/okhttp/) as networking layer, you can pass custom okHttpClient while initializing it.
```java
// Adding an Network Interceptor for Debugging purpose :
OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                        .addNetworkInterceptor(new StethoInterceptor())
                        .build();
AndroidNetworking.initialize(getApplicationContext(),okHttpClient);                        
```
Using the Fast Android Networking with Jackson Parser
```groovy
compile 'com.amitshekhar.android:jackson-android-networking:1.0.1'
```
```java
// Then set the JacksonParserFactory like below
AndroidNetworking.setParserFactory(new JacksonParserFactory());
```

### Making a GET Request
```java
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                 .addPathParameter("pageNumber", "0")
                 .addQueryParameter("limit", "3")
                 .addHeaders("token", "1234")
                 .setTag("test")
                 .setPriority(Priority.LOW)
                 .build()
                 .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                      // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                      // handle error
                    }
                });                
```
### Making a POST Request
```java
AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/createAnUser")
                 .addBodyParameter("firstname", "Amit")
                 .addBodyParameter("lastname", "Shekhar")
                 .setTag("test")
                 .setPriority(Priority.MEDIUM)
                 .build()
                 .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                      // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                      // handle error
                    }
                });
```
You can also post java object, json, file, etc in POST request like this.
```java
User user = new User();
user.firstname = "Amit";
user.lastname = "Shekhar";

AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/createUser")
                 .addBodyParameter(user) // posting java object
                 .setTag("test")
                 .setPriority(Priority.MEDIUM)
                 .build()
                 .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                      // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                      // handle error
                    }
                });


JSONObject jsonObject = new JSONObject();
try {
    jsonObject.put("firstname", "Amit");
    jsonObject.put("lastname", "Shekhar");
} catch (JSONException e) {
  e.printStackTrace();
}
       
AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/createUser")
                 .addJSONObjectBody(jsonObject) // posting json
                 .setTag("test")
                 .setPriority(Priority.MEDIUM)
                 .build()
                 .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                      // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                      // handle error
                    }
                });
                
AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/postFile")
                 .addFileBody(file) // posting any type of file
                 .setTag("test")
                 .setPriority(Priority.MEDIUM)
                 .build()
                 .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                      // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                      // handle error
                    }
                });               
```

### Using it with your own JAVA Object - JSON Parser
```java
/*--------------Example One -> Getting the userList----------------*/
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsObjectList(User.class, new ParsedRequestListener<List<User>>() {
                    @Override
                    public void onResponse(List<User> users) {
                      // do anything with response
                      Log.d(TAG, "userList size : " + users.size());
                      for (User user : users) {
                        Log.d(TAG, "id : " + user.id);
                        Log.d(TAG, "firstname : " + user.firstname);
                        Log.d(TAG, "lastname : " + user.lastname);
                      }
                    }
                    @Override
                    public void onError(ANError anError) {
                     // handle error
                    }
                });
/*--------------Example Two -> Getting an user----------------*/
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUserDetail/{userId}")
                .addPathParameter("userId", "1")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(User.class, new ParsedRequestListener<User>() {
                     @Override
                     public void onResponse(User user) {
                        // do anything with response
                        Log.d(TAG, "id : " + user.id);
                        Log.d(TAG, "firstname : " + user.firstname);
                        Log.d(TAG, "lastname : " + user.lastname);
                     }
                     @Override
                     public void onError(ANError anError) {
                        // handle error
                     }
                 }); 
/*-- Note : YourObject.class, getAsObject and getAsObjectList are important here --*/              
```

### Downloading a file from server
```java
AndroidNetworking.download(url,dirPath,fileName)
                 .setTag("downloadTest")
                 .setPriority(Priority.MEDIUM)
                 .build()
                 .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                      // do anything with progress  
                    }
                 })
                 .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                      // do anything after completion
                    }
                    @Override
                    public void onError(ANError error) {
                      // handle error    
                    }
                });                 
```
### Uploading a file to server
```java
AndroidNetworking.upload(url)
                 .addMultipartFile("image",file)    
                 .addMultipartParameter("key","value")
                 .setTag("uploadTest")
                 .setPriority(Priority.HIGH)
                 .build()
                 .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                      // do anything with progress 
                    }
                 })
                 .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                      // do anything with response                
                    }
                    @Override
                    public void onError(ANError error) {
                      // handle error 
                    }
                 }); 
```

### Getting Response and completion in an another thread executor 
(Note : Error and Progress will always be returned in main thread of application)
```java
AndroidNetworking.upload(url)
                 .addMultipartFile("image",file)  
                 .addMultipartParameter("key","value")  
                 .setTag("uploadTest")
                 .setPriority(Priority.HIGH)
                 .build()
                 .setExecutor(Executors.newSingleThreadExecutor()) // setting an executor to get response or completion on that executor thread
                 .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                      // do anything with progress 
                    }
                 })
                 .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                      // below code will be executed in the executor provided
                      // do anything with response                
                    }
                    @Override
                    public void onError(ANError error) {
                      // handle error 
                    }
                 }); 
```
### Setting a Percentage Threshold For Not Cancelling the request if it has completed the given threshold
```java
AndroidNetworking.download(url,dirPath,fileName)
                 .setTag("downloadTest")
                 .setPriority(Priority.MEDIUM)
                 .setPercentageThresholdForCancelling(50) // even if at the time of cancelling it will not cancel if 50% 
                 .build()                                 // downloading is done.But can be cancalled with forceCancel.
                 .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                      // do anything with progress  
                    }
                 })
                 .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                      // do anything after completion
                    }
                    @Override
                    public void onError(ANError error) {
                      // handle error    
                    }
                });   
```
### Cancelling a request.
Any request with a given tag can be cancelled. Just do like this.
```java
AndroidNetworking.cancel("tag"); // All the requests with the given tag will be cancelled.
AndroidNetworking.forceCancel("tag");  // All the requests with the given tag will be cancelled , even if any percent threshold is
                                       // set , it will be cancelled forcefully. 
AndroidNetworking.cancelAll(); // All the requests will be cancelled.  
AndroidNetworking.forceCancelAll(); // All the requests will be cancelled , even if any percent threshold is
                               // set , it will be cancelled forcefully.                           
```
### Loading image from network into ImageView
```xml
      <com.androidnetworking.widget.ANImageView
          android:id="@+id/imageView"
          android:layout_width="100dp"
          android:layout_height="100dp"
          android:layout_gravity="center" />
          
      imageView.setDefaultImageResId(R.drawable.default);
      imageView.setErrorImageResId(R.drawable.error);
      imageView.setImageUrl(imageUrl);          
```
### Getting Bitmap from url with some specified parameters
```java
AndroidNetworking.get(imageUrl)
                 .setTag("imageRequestTag")
                 .setPriority(Priority.MEDIUM)
                 .setBitmapMaxHeight(100)
                 .setBitmapMaxWidth(100)
                 .setBitmapConfig(Bitmap.Config.ARGB_8888)
                 .build()
                 .getAsBitmap(new BitmapRequestListener() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                    // do anything with bitmap
                    }
                    @Override
                    public void onError(ANError error) {
                      // handle error
                    }
                });
```
### Error Code Handling
```java
public void onError(ANError error) {
   if (error.getErrorCode() != 0) {
        // received error from server
        // error.getErrorCode() - the error code from server
        // error.getErrorBody() - the error body from server
        // error.getErrorDetail() - just an error detail
        Log.d(TAG, "onError errorCode : " + error.getErrorCode());
        Log.d(TAG, "onError errorBody : " + error.getErrorBody());
        Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
        // get parsed error object (If ApiError is your class)
        ApiError apiError = error.getErrorAsObject(ApiError.class);
   } else {
        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
        Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
   }
}
```
### Remove Bitmap from cache or clear cache
```java
AndroidNetworking.evictBitmap(key); // remove a bitmap with key from LruCache
AndroidNetworking.evictAllBitmap(); // clear LruCache
```
### Prefetch a request (so that it can return from cache when required at instant)
```java
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "30")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .prefetch();
```
### Customizing OkHttpClient for a particular request
```java
OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new GzipRequestInterceptor())
                .build();
                
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                 .addPathParameter("pageNumber", "0")
                 .addQueryParameter("limit", "3")
                 .addHeaders("token", "1234")
                 .setTag("test")
                 .setPriority(Priority.LOW)
                 .setOkHttpClient(okHttpClient) // passing a custom okHttpClient 
                 .build()
                 .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                      // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                    // handle error
                    }
                });
```
### Making a conditional request (Building a request)
```java
ANRequest.GetRequestBuilder getRequestBuilder = new ANRequest.GetRequestBuilder(ApiEndPoint.BASE_URL + ApiEndPoint.CHECK_FOR_HEADER);
               
if(isHeaderRequired){
 getRequestBuilder.addHeaders("token", "1234");
}

if(executorRequired){
 getRequestBuilder.setExecutor(Executors.newSingleThreadExecutor());
}
               
ANRequest anRequest = getRequestBuilder.build();       
                 
anRequest.getAsJSONObject(new JSONObjectRequestListener() {
    @Override
    public void onResponse(JSONObject response) {
      // do anything with response
    }
    @Override
    public void onError(ANError error) {
      // handle error
    }
});
```
### ConnectionClass Listener to get current network quality and bandwidth
```java
// Adding Listener
AndroidNetworking.setConnectionQualityChangeListener(new ConnectionQualityChangeListener() {
            @Override
            public void onChange(ConnectionQuality currentConnectionQuality, int currentBandwidth) {
              // do something on change in connectionQuality
            }
        });
        
// Removing Listener   
AndroidNetworking.removeConnectionQualityChangeListener();

// Getting current ConnectionQuality
ConnectionQuality connectionQuality = AndroidNetworking.getCurrentConnectionQuality();
if(connectionQuality == ConnectionQuality.EXCELLENT) {
  // do something
} else if (connectionQuality == ConnectionQuality.POOR) {
  // do something
} else if (connectionQuality == ConnectionQuality.UNKNOWN) {
  // do something
}
// Getting current bandwidth
int currentBandwidth = AndroidNetworking.getCurrentBandwidth(); // Note : if (currentBandwidth == 0) : means UNKNOWN
```
### Getting Analytics of a request by setting AnalyticsListener on that
```java
AndroidNetworking.download(url,dirPath,fileName)
                 .setTag("downloadTest")
                 .setPriority(Priority.MEDIUM)
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
                 .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                      // do anything with progress  
                    }
                 })
                 .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                      // do anything after completion
                    }
                    @Override
                    public void onError(ANError error) {
                      // handle error    
                    }
                });  
Note : If bytesSent or bytesReceived is -1 , it means it is unknown                
```
### Getting OkHttpResponse in Response
```java
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUserDetail/{userId}")
                .addPathParameter("userId", "1")
                .setTag(this)
                .setPriority(Priority.LOW)
                .setUserAgent("getAnUser")
                .build()
                .getAsOkHttpResponseAndParsed(new TypeToken<User>() {
                }, new OkHttpResponseAndParsedRequestListener<User>() {
                    @Override
                    public void onResponse(Response okHttpResponse, User user) {
                      // do anything with okHttpResponse and user
                    }
                    @Override
                    public void onError(ANError anError) {
                      // handle error
                    }
                });
```
### Making Synchronous Request
```java                
ANRequest request = AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                        .addPathParameter("pageNumber", "0")
                        .addQueryParameter("limit", "3")
                        .build();
ANResponse<List<User>> response = request.executeForObjectList(User.class);
if (response.isSuccess()) {
   List<User> users = responseTwo.getResult();
} else {
   //handle error
}                                        
```
### How caching works ?
* First of all the server must send cache-control in header so that is starts working.
* Response will be cached on the basis of cache-control max-age,max-stale.
* If internet is connected and the age is NOT expired it will return from cache.
* If internet is connected and the age is expired and if server returns 304(NOT MODIFIED) it will return from cache.
* If internet is NOT connected if you are using getResponseOnlyIfCached() - it will return from cache even it date is expired.
* If internet is NOT connected , if you are NOT using getResponseOnlyIfCached() - it will NOT return anything.
* If you are using getResponseOnlyFromNetwork() , it will only return response after validation from server.
* If cache-control is set, it will work according to the max-age,max-stale returned from server.
* If internet is NOT connected only way to get cache Response is by using getResponseOnlyIfCached().

### Enabling Logging
```java
AndroidNetworking.enableLogging(); // simply enable logging
AndroidNetworking.enableLogging(LEVEL.HEADERS); // enabling logging with level
```
### Enabling GZIP From Client to Server
```java
// Enabling GZIP for Request (Not needed if your server doesn't support GZIP Compression), anyway responses 
// from server are automatically unGzipped if required. So enable it only if you need your request to be 
// Gzipped before sending to server(Make sure your server support GZIP Compression).
OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new GzipRequestInterceptor())
                .build();
AndroidNetworking.initialize(getApplicationContext(),okHttpClient);                
```
### IMPORTANT NOTE
* Use IMMEDIATE Priority with caution - use is at appropriate place only when
  1 or 2 (at max 2)IMMEDIATE request is required at instant.Otherwise use HIGH Priority.
* Known Bug : As present if you are using GZIP Interceptor from client to server, Upload progress
  is not working perfectly in Multipart.
  
  If you are using Proguard with Gradle build system (which is usually the case), you don't have to do anything. The appropriate Proguard rules will be automatically applied. If you still need the rules applied in `proguard-rules.pro`, it is as follows:
  ```
  -dontwarn okio.**
  ```

  
### Fast Android Networking Library supports

* Fast Android Networking Library supports all types of HTTP/HTTPS request like GET, POST, DELETE, HEAD, PUT, PATCH
* Fast Android Networking Library supports downloading any type of file
* Fast Android Networking Library supports uploading any type of file (supports multipart upload)
* Fast Android Networking Library supports cancelling a request
* Fast Android Networking Library supports setting priority to any request (LOW, MEDIUM, HIGH, IMMEDIATE)
* Fast Android Networking Library supports [RxJava](https://amitshekhariitbhu.github.io/Fast-Android-Networking/rxjava2_support.html)

As it uses [OkHttp](http://square.github.io/okhttp/) as a networking layer, it supports:

* Fast Android Networking Library supports HTTP/2 support allows all requests to the same host to share a socket
* Fast Android Networking Library uses connection pooling which reduces request latency (if HTTP/2 isn’t available)
* Transparent GZIP shrinks download sizes
* Fast Android Networking Library supports response caching which avoids the network completely for repeat requests

### Difference over other Networking Library
* In Fast Android Networking Library, OkHttpClient can be customized for every request easily — like timeout customization, etc. for each request.
* As Fast Android Networking Library uses [OkHttp](http://square.github.io/okhttp/) and [Okio](https://github.com/square/okio), it is faster.
* Single library for all type of networking.
* Supports RxJava, RxJava2 -> [Check here](https://amitshekhariitbhu.github.io/Fast-Android-Networking/rxjava2_support.html)
* Current bandwidth and connection quality can be obtained to decide logic of code.
* Executor can be passed to any request to get response in another thread.
* Complete analytics of any request can be obtained.
* All types of customization is possible.
* Immediate Request really is immediate now.
* Prefetching of any request can be done so that it gives instant data when required from the cache.
* Proper request canceling.
* Prevents cancellation of a request if it’s completed more than a specific threshold percentage.
* A simple interface to make any type of request.
* Proper Response Caching — which leads to reduced bandwidth usage. 

### TODO
* Integration with other library
* And of course many many features and bug fixes

### CREDITS
* [Square](https://square.github.io/) - As both [OkHttp](http://square.github.io/okhttp/) and [Okio](https://github.com/square/okio)
  used by Fast Android Networking is developed by [Square](https://square.github.io/).
* [Volley](https://android.googlesource.com/platform/frameworks/volley/) - As Fast Android Networking uses ImageLoader that is developed by [Volley](https://android.googlesource.com/platform/frameworks/volley/).  
* [Prashant Gupta](https://github.com/PrashantGupta17) - For RxJava, RxJava2 Support - [RxJava Support](https://github.com/amitshekhariitbhu/Fast-Android-Networking/wiki/Using-Fast-Android-Networking-Library-With-RxJava)

### [Check out Mindorks awesome open source projects here](https://mindorks.com/open-source-projects)

### Contact - Let's become friend
- [Twitter](https://twitter.com/amitiitbhu)
- [Github](https://github.com/amitshekhariitbhu)
- [Medium](https://medium.com/@amitshekhar)
- [Facebook](https://www.facebook.com/amit.shekhar.iitbhu)

### License
```
   Copyright (C) 2016 Amit Shekhar
   Copyright (C) 2011 Android Open Source Project

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

### Contributing to Fast Android Networking
All pull requests are welcome, make sure to follow the [contribution guidelines](CONTRIBUTING.md)
when you submit pull request.

