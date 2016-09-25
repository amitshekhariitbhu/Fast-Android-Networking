---
title: Download Request
tags: [download]
keywords: "download, http, https, android , download request"
published: true
sidebar: doc_sidebar
permalink: download_request.html
folder: doc
---


## Downloading a file from server
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
