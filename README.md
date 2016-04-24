# Android Networking 

### This library is under development

Android Networking is a powerful library for doing any type of networking in Android applications which is made on top of [OkHttp Networking Layer](http://square.github.io/okhttp/).

Android Networking takes care of each and everythings. So you don't have to do anything, just make request and listen for the response.

Android Networking supports:

* All type of HTTP/HTTPS request like GET,POST,etc
* Downloading any type of file
* Uploading any type of file (supports multipart upload)

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
OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                        .addNetworkInterceptor(new StethoInterceptor())
                        .build();
AndroidNetworking.initialize(okHttpClient);                        
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
You can also post json,file,ect in POST request like this.
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
                 .startDownload(new DownloadProgressListener() {
                   @Override
                   public void onProgress(long bytesDownloaded, long totalBytes,boolean isCompleted){
                     // do anything with progress and completion
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
                 .getAsJsonObject(new UploadProgressListener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    // do anything with response
                    }
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes, boolean isCompleted) {
                    // do anything with progress and completion
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
### Contributing to Android Networking
Just make pull request. You are in.

