---
title: Upload Request
tags: [upload]
keywords: "upload, http, https, android , networking , upload request"
last_updated: "Aug 21, 2016"
summary: "Making a upload request : Example Two"
published: true
sidebar: mydoc_sidebar
permalink: mydoc_upload_example_two.html
folder: mydoc
---


### Uploading a file to server
```java
AndroidNetworking.upload(url)
                 .addMultipartFile("image_one",fileOne)    
                 .addMultipartFile("image_two",fileTwo)    
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


{% include links.html %}
