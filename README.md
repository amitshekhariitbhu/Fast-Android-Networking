# Fast Android Networking Library

[![Build Status](https://travis-ci.org/amitshekhariitbhu/Fast-Android-Networking.svg?branch=master)](https://travis-ci.org/amitshekhariitbhu/Fast-Android-Networking)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android%20Networking-blue.svg?style=flat)](http://android-arsenal.com/details/1/3695)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/amitshekhariitbhu/Fast-Android-Networking/blob/master/LICENSE)

<img src=https://raw.githubusercontent.com/amitshekhariitbhu/Fast-Android-Networking/master/assets/fastandroidnetworking.png width=1000 height=91 />

### About Fast Android Networking Library

Fast Android Networking Library is a powerful library for doing any type of networking in Android applications which is made on top of [OkHttp Networking Layer](http://square.github.io/okhttp/).

Fast Android Networking Library takes care of each and everything. So you don't have to do anything, just make request and listen for the response.

Fast Android Networking Library supports:

* Fast Android Networking Library supports all types of HTTP/HTTPS request like GET, POST, DELETE, HEAD, PUT, PATCH
* Fast Android Networking Library supports downloading any type of file
* Fast Android Networking Library supports uploading any type of file (supports multipart upload)
* Fast Android Networking Library supports cancelling a request
* Fast Android Networking Library supports setting priority to any request (LOW, MEDIUM, HIGH, IMMEDIATE)

As it uses [OkHttp](http://square.github.io/okhttp/) as a networking layer, it supports:

* Fast Android Networking Library supports HTTP/2 support allows all requests to the same host to share a socket
* Fast Android Networking Library uses connection pooling which reduces request latency (if HTTP/2 isn’t available)
* Transparent GZIP shrinks download sizes
* Fast Android Networking Library supports response caching which avoids the network completely for repeat requests

### Why this library :
* Recent removal of HttpClient in Android Marshmallow(Android M) made other networking library obsolete.
* No other single library do each and everything like making request, downloading any type of file, uploading file, loading
  image from network in ImageView, etc. There are libraries but they are outdated.
* No other library provided simple interface for doing all types of things in networking like setting priority, cancelling, etc.
* As it uses [Okio](https://github.com/square/okio) , No more GC overhead in android application.
  [Okio](https://github.com/square/okio) is made to handle GC overhead while allocating memory.
  [Okio](https://github.com/square/okio) do some clever things to save CPU and memory.
* As it uses [OkHttp](http://square.github.io/okhttp/) , most important it supports HTTP/2.  

### Difference over other Networking Library
* In Fast Android Networking Library, OkHttpClient can be customized for every request easily.
* As Fast Android Networking Library uses [OkHttp](http://square.github.io/okhttp/) and [Okio](https://github.com/square/okio), it is faster.
* Single library for all type of networking.
* Current bandwidth and connection quality can be obtained to decide logic of code.
* Executor can be passed to any request to get response in another thread.
* Complete analytics of any request can be obtained.
* All types of customization is possible.
* Immediate Request is really immediate now.
* Prefetching of any request can be done so that it gives instant data when required from cache.
* Proper cancellation of request.
* Do not cancel a request if completed more than a threshold percentage.
* Simple interface to make any type of request.
* Proper Response Caching, hence reducing bandwidth usage.

### Have an issue or need a feature in Fast Android Networking
- Best way to do so is - [Create an issue](https://github.com/amitshekhariitbhu/Fast-Android-Networking/issues/new)

### Loved the Fast Android Networking Library
- Do me a favor by giving a star on this project.
- You can find the star button at the top-right on this page.
- Giving a star makes it more searchable to you and other developers.

## Requirements

Fast Android Networking Library can be included in any Android application. 

Fast Android Networking Library supports Android 2.3 (Gingerbread) and later. 

## Using Fast Android Networking Library in your application

Add this in your build.gradle
```groovy
compile 'com.amitshekhar.android:android-networking:0.0.1'
```
Do not forget to add internet permission in manifest if already not present
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
Then initialize it in onCreate() Method of application class, :
```java
AndroidNetworking.initialize(getApplicationContext());
```
Initializing it with some customization , as it uses [OkHttp](http://square.github.io/okhttp/) as newtorking layer, you can pass custom okHttpClient while initializing it.
```java
# Adding an Network Interceptor for Debugging purpose :
OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                        .addNetworkInterceptor(new StethoInterceptor())
                        .build();
AndroidNetworking.initialize(getApplicationContext(),okHttpClient);                        
```
If you are using proguard, then add this rule in proguard-project.txt
```
-dontwarn okio.**
```
### Making a GET Request
```java
AndroidNetworking.get("http://api.localhost.com/{pageNumber}/test")
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
AndroidNetworking.post("http://api.localhost.com/createAnUser")
                 .addBodyParameter("firstname", "Amit")
                 .addBodyParameter("lastname", "Shekhar")
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
```
You can also post json, file ,etc in POST request like this.
```java
JSONObject jsonObject = new JSONObject();
try {
    jsonObject.put("firstname", "Rohit");
    jsonObject.put("lastname", "Kumar");
    } catch (JSONException e) {
    e.printStackTrace();
    }
        
AndroidNetworking.post("http://api.localhost.com/createAnUser")
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
                
AndroidNetworking.post("http://api.localhost.com/postFile")
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
AndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
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
                
AndroidNetworking.get("http://api.localhost.com/{pageNumber}/test")
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
if(connectionQuality == ConnectionQuality.EXCELLENT){
// do something
}else if (connectionQuality == ConnectionQuality.POOR){
// do something
}else if (connectionQuality == ConnectionQuality.UNKNOWN){
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
AndroidNetworking.enableLogging("tag"); // enabling logging with some tag
AndroidNetworking.disableLogging(); // disable logging
```
### Enabling GZIP From Client to Server
```java
// Enabling GZIP for Request (Not needed if your server doesn't support GZIP Compression), anyway responses 
from server are automatically unGzipped if required. So enable it only if you need your request to be 
Gzipped before sending to server(Make sure your server support GZIP Compression).
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

### TODO
* [RxJava](https://github.com/ReactiveX/RxJava) Support
* Integration with other library
* And of course many many features and bug fixes
* Json Parser

### CREDITS
* [Square](https://square.github.io/) - As both [OkHttp](http://square.github.io/okhttp/) and [Okio](https://github.com/square/okio)
  used by Fast Android Networking is developed by [Square](https://square.github.io/).
* [Volley](https://android.googlesource.com/platform/frameworks/volley/) - As Fast Android Networking uses ImageLoader that is developed by [Volley](https://android.googlesource.com/platform/frameworks/volley/).  

### Contact
- [Twitter](https://twitter.com/amitiitbhu)
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
Just make pull request. You are in!

