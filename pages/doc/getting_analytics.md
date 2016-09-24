---
title: Getting Analytics
tags: [custom]
keywords: "download, http, https, android , analytics"
last_updated: "Sept 24, 2016"
summary: "Getting Analytics of a request"
published: true
sidebar: doc_sidebar
permalink: getting_analytics.html
folder: doc
---


## Getting Analytics of a request by setting AnalyticsListener on that request
```java
AndroidNetworking.download(url,dirPath,fileName)
                 .setTag("downloadTest")
                 .setPriority(Priority.MEDIUM)
                 .build()
                 .setAnalyticsListener(new AnalyticsListener() {
                      @Override
                      public void onReceived(long timeTakenInMillis, long bytesSent,
                       long bytesReceived, boolean isFromCache) {
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


{% include links.html %}
