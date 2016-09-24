---
title: Download Request
tags: [download]
keywords: "download, http, https, android"
last_updated: "Aug 21, 2016"
summary: "Making a download request"
published: true
sidebar: mydoc_sidebar
permalink: mydoc_download_example_one.html
folder: mydoc
---


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


{% include links.html %}
