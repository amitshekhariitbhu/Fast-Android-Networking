---
title: Upload Request
tags: [upload]
keywords: "upload, http, https, android , upload request"
last_updated: "Sept 24, 2016"
summary: "Making upload request"
published: true
sidebar: doc_sidebar
permalink: upload_request.html
folder: doc
---


### Uploading a file to server
```java
AndroidNetworking.upload(url)
                 .addMultipartFile("image",file)    
                 .addMultipartParameter("key","value")
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

### Uploading more than one file to server in one request
```java
AndroidNetworking.upload(url)
                 .addMultipartFile("image_one",fileOne)    
                 .addMultipartFile("image_two",fileTwo)    
                 .addMultipartParameter("key","value")
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

{% include links.html %}
