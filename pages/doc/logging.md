---
title: Logging
tags: [custom]
keywords: "logging, http, https, android , logging request"
published: true
sidebar: doc_sidebar
permalink: logging.html
folder: doc
---

## Inbuilt Logging
```java
AndroidNetworking.enableLogging(); // simply enable logging
AndroidNetworking.enableLogging(LEVEL.HEADERS); // enabling logging with level
```

## Logging it with Stetho Network Interceptor
```groovy
compile 'com.facebook.stetho:stetho:1.1.1'
```

```java
// Adding a Network Interceptor for Debugging purpose :
OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                        .addNetworkInterceptor(new StethoInterceptor())
                        .build();
AndroidNetworking.initialize(getApplicationContext(),okHttpClient);                        
```

{% include links.html %}
