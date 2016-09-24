---
title: Synchronous Request
tags: [get,head,post,download,upload]
keywords: "GET,POST,download,upload, http, https, android , request"
last_updated: "Sept 24, 2016"
summary: "Making Synchronous Request"
published: true
sidebar: doc_sidebar
permalink: synchronous_request.html
folder: doc
---


## Synchronous GET Request
```java
ANRequest request = AndroidNetworking.get(url)
        .addPathParameter("pageNumber", "0")
        .addQueryParameter("limit", "3")
        .build();
        
ANResponse<List<User>> response = request.executeForParsed(new TypeToken<List<User>>() {});

if (response.isSuccess()) {
    List<User> users = response.getResult();
    Log.d(TAG, "userList size : " + users.size());
    for (User user : users) {
        Log.d(TAG, "id : " + user.id);
        Log.d(TAG, "firstname : " + user.firstname);
        Log.d(TAG, "lastname : " + user.lastname);
    }
    Response okHttpResponse = response.getOkHttpResponse();
    Log.d(TAG, "headers : " + okHttpResponse.headers().toString());
} else {
    ANError error = response.getError();
    // Handle Error
}              
```

## Synchronous POST Request
```java
ANRequest request = AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/createAnUser")
                 .addBodyParameter("firstname", "Amit")
                 .addBodyParameter("lastname", "Shekhar")
                 .build();
        
ANResponse<JSONObject> response = request.executeForJSONObject();

if (response.isSuccess()) {
    JSONObject jsonObject = response.getResult();
    Log.d(TAG, "response : " + jsonObject.toString());
    Response okHttpResponse = response.getOkHttpResponse();
    Log.d(TAG, "headers : " + okHttpResponse.headers().toString());
} else {
    ANError error = response.getError();
    // Handle Error
}               
```

## Synchronous Download Request
```java
ANRequest request = AndroidNetworking
                    .download(url, Utils.getRootDirPath(getApplicationContext()), "file1.zip")
                    .build()
                    .setDownloadProgressListener(new DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDownloaded, long totalBytes) {

                        }
                    });
                        
ANResponse<String> response = request.executeForDownload();

if (response.isSuccess()) {
    Response okHttpResponse = response.getOkHttpResponse();
    Log.d(TAG, "headers : " + okHttpResponse.headers().toString());
} else {
    ANError error = response.getError();
    // Handle Error
}          
```

## Synchronous Upload Request
```java
ANRequest request = AndroidNetworking.upload(url)
                 .addMultipartFile("image",file)    
                 .addMultipartParameter("key","value")
                 .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                      // do anything with progress 
                    }
                  })
                 .build();
        
ANResponse<JSONObject> response = request.executeForJSONObject();

if (response.isSuccess()) {
    JSONObject jsonObject = response.getResult();
    Log.d(TAG, "response : " + jsonObject.toString());
    Response okHttpResponse = response.getOkHttpResponse();
    Log.d(TAG, "headers : " + okHttpResponse.headers().toString());
} else {
    ANError error = response.getError();
    // Handle Error
}              
```

{% include links.html %}
