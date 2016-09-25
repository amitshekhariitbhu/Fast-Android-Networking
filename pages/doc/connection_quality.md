---
title: Connection Quality
tags: [custom]
keywords: "download, http, https, android ,prefetch, connection quality, internet, speed, bandwidth"
published: true
sidebar: doc_sidebar
permalink: connection_quality.html
folder: doc
---


## ConnectionClass Listener to get current network quality and bandwidth
```java
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
if(connectionQuality == ConnectionQuality.EXCELLENT) {
  // do something
} else if (connectionQuality == ConnectionQuality.POOR) {
  // do something
} else if (connectionQuality == ConnectionQuality.UNKNOWN) {
  // do something
}
// Getting current bandwidth

int currentBandwidth = AndroidNetworking.getCurrentBandwidth();
 
// Note : if (currentBandwidth == 0) : means UNKNOWN                          
```


{% include links.html %}
