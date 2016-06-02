# Android Networking 

[![Build Status](https://travis-ci.org/amitshekhariitbhu/AndroidNetworking.svg?branch=master)](https://travis-ci.org/amitshekhariitbhu/AndroidNetworking)

<img src=https://raw.githubusercontent.com/amitshekhariitbhu/AndroidNetworking/master/assets/androidnetworking.png width=500 height=300 />

### About Android Networking

Android Networking is a powerful library for doing any type of networking in Android applications which is made on top of [OkHttp Networking Layer](http://square.github.io/okhttp/).

Android Networking takes care of each and everything. So you don't have to do anything, just make request and listen for the response.

Android Networking supports:

* All type of HTTP/HTTPS request like GET,POST,etc
* Downloading any type of file
* Uploading any type of file (supports multipart upload)
* Cancelling a request
* Setting priority to any request (LOW, MEDIUM, HIGH, IMMEDIATE)

As it uses [OkHttp](http://square.github.io/okhttp/) as a networking layer, it supports:

* HTTP/2 support allows all requests to the same host to share a socket
* Connection pooling reduces request latency (if HTTP/2 isn’t available)
* Transparent GZIP shrinks download sizes
* Response caching avoids the network completely for repeat requests

## Requirements

Android Networking can be included in any Android application. 

Android Networking supports Android 2.3 (Gingerbread) and later. 

## Using Android Networking in your application

Add this in your build.gradle
```
compile 'com.amitshekhar.android:android-networking:0.0.1'
```
Do not forget to add internet permission in manifest if already not present
```
<uses-permission android:name="android.permission.INTERNET" />
```
Then initialize it in onCreate() Method of application class, :
```
AndroidNetworking.initialize(getApplicationContext());
```
Initializing it with some customization , as it uses [OkHttp](http://square.github.io/okhttp/) as newtorking layer, you can pass custom okHttpClient while initializing it.
```
# Adding an Network Interceptor for Debugging purpose :
OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                        .addNetworkInterceptor(new StethoInterceptor())
                        .build();
AndroidNetworking.initialize(getApplicationContext(),okHttpClient);                        
```
```
# Enabling GZIP for Request (Not needed if your server doesn't support GZIP Compression), anyway responses from server are automatically unGzipped if required. So enable it only
if you need your request to be Gzipped before sending to server(Make sure your server support GZIP Compression).
OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new GzipRequestInterceptor())
                .build();
AndroidNetworking.initialize(getApplicationContext(),okHttpClient);                
```
If you are using proguard, then add this rule in proguard-project.txt
```
-dontwarn okio.**
```
### Making a GET Request
```
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
```
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
```
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
```
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
```
AndroidNetworking.upload(url)
                 .addMultipartFile("image",file)    
                 .setTag("uploadTest")
                 .setPriority(Priority.IMMEDIATE)
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
```
AndroidNetworking.upload(url)
                 .addMultipartFile("image",file)    
                 .setTag("uploadTest")
                 .setPriority(Priority.IMMEDIATE)
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
```
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
```
AndroidNetworking.cancel("tag"); // All the requests with the given tag will be cancelled.
AndroidNetworking.forceCancel("tag");  // All the requests with the given tag will be cancelled , even if any percent threshold is
                                       // set , it will be cancelled forcefully. 
AndroidNetworking.cancelAll(); // All the requests will be cancelled.  
AndroidNetworking.forceCancelAll(); // All the requests will be cancelled , even if any percent threshold is
                               // set , it will be cancelled forcefully.                           
```
### Loading image from network into ImageView
```
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
```
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
```
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
```
AndroidNetworking.evictBitmap(key); // remove a bitmap with key from LruCache
AndroidNetworking.evictAllBitmap(); // clear LruCache
```
### Prefetch a request (so that it can return from cache when required at instant)
```
AndroidNetworking.get(ApiEndPoint.BASE_URL + ApiEndPoint.GET_JSON_ARRAY)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "30")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .prefetch();
```
### Customizing OkHttpClient for a particular request
```
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
```
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
```
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
```
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
```
AndroidNetworking.enableLogging(); // simply enable logging
AndroidNetworking.enableLogging("tag"); // enabling logging with some tag
AndroidNetworking.disableLogging(); // disable logging
```
### IMPORTANT NOTE
* Use IMMEDIATE Priority with caution - use is at appropriate place only when
  1 or 2 (at max 2)IMMEDIATE request is required at instant.Otherwise use HIGH Priority.
* Known Bug : As on now if you are using GZIP Interceptor from client to server, Upload progress
  is not working perfectly in Multipart(But is working , only upload progress is not working).
  
### Inspiration behind making of this library :
* Recent removal of HttpClient in Android Marshmallow(Android M) made other networking library obsolete.
* No other single library do each and everything like making request, downloading any type of file, uploading file, loading
  image from network in ImageView, etc. There are libraries but they are outdated.
* No other library provided simple interface for doing all types of things in networking like setting priority, cancelling, etc.
* As it uses [Okio](https://github.com/square/okio) , No more GC overhead in android application.
  [Okio](https://github.com/square/okio) is made to handle GC overhead while allocating memory.
  [Okio](https://github.com/square/okio) do some clever things to save CPU and memory.
* As it uses [OkHttp](http://square.github.io/okhttp/) , most important it supports HTTP/2.  

### TODO
* Network Execution Logic on the basis of network speed change
* Integration with other library
* And of course many many features and bug fixes

### License
```
   Copyright (C) 2016 Amit Shekhar

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

### Contributing to Android Networking
Just make pull request. You are in!

