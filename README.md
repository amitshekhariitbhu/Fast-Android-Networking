# Android Networking 

[![Build Status](https://travis-ci.org/amitshekhariitbhu/AndroidNetworking.svg?branch=master)](https://travis-ci.org/amitshekhariitbhu/AndroidNetworking)

### This library is under development

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

As of now the library is under development so we have not open it to use through gradle anyway you can add it as a library in android project after downloading it.

After importing it as a library add this in your build.gradle
```
compile project(':android-networking')
```
and add this in your settings.gradle
```
include ':android-networking'
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
### Making a GET Request
```
AndroidNetworking.get("http://api.localhost.com/{pageNumber}/test")
                 .addPathParameter("pageNumber", "0")
                 .addQueryParameter("limit", "3")
                 .addHeaders("token", "1234")
                 .setTag("test")
                 .setPriority(Priority.LOW)
                 .build()
                 .getAsJsonArray(new RequestListener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                    // do anything with response
                    }
                    @Override
                    public void onError(AndroidNetworkingError error) {
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
                 .getAsJsonArray(new RequestListener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                    // do anything with response
                    }
                    @Override
                    public void onError(AndroidNetworkingError error) {
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
                 .getAsJsonArray(new RequestListener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                    // do anything with response
                    }
                    @Override
                    public void onError(AndroidNetworkingError error) {
                    // handle error
                    }
                });
                
AndroidNetworking.post("http://api.localhost.com/postFile")
                 .addFileBody(file) // posting any type of file
                 .setTag("test")
                 .setPriority(Priority.MEDIUM)
                 .build()
                 .getAsJsonObject(new RequestListener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    // do anything with response
                    }
                    @Override
                    public void onError(AndroidNetworkingError error) {
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
                    public void onError(AndroidNetworkingError error) {
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
                 .getAsJsonObject(new RequestListener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    // do anything with response                
                    }
                  
                    @Override
                    public void onError(AndroidNetworkingError error) {
                    // handle error 
                    }
                 }); 
```
### Getting Response and completion in an another executor thread (Note : Error and progress will always be returned in main thread of application)
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
                 .getAsJsonObject(new RequestListener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    // below code will be executed in the executor provided
                    // do anything with response                
                    }
                  
                    @Override
                    public void onError(AndroidNetworkingError error) {
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
      <com.androidnetworking.widget.GreatImageView
          android:id="@+id/greatImageView"
          android:layout_width="100dp"
          android:layout_height="100dp"
          android:layout_gravity="center" />
          
      greatImageView.setDefaultImageResId(R.drawable.default);
      greatImageView.setErrorImageResId(R.drawable.error);
      greatImageView.setImageUrl(imageUrl);          
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
                 .getAsBitmap(new RequestListener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                    // do anything with bitmap
                    }
                    @Override
                    public void onError(AndroidNetworkingError error) {
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

### Contributing to Android Networking
Just make pull request. You are in.

