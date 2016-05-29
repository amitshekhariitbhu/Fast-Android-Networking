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
compile 'com.amitshekhar.android:android-networking:0.0.3'
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
### Cancelling a request.
Any request with a given tag can be cancelled. Just do like this.
```
AndroidNetworking.cancel("testTag"); // All the requests with the given tag will be cancelled.
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
* Network Speed Change Listener
* Total data consumption in any request
* Network Execution Logic on the basis of network speed change
* Integration with other library
* And of course many many features and bug fixes

### License
```
   Copyright (C) 2016 Amit Shekhar
   Copyright (C) 2011 The Android Open Source Project

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

