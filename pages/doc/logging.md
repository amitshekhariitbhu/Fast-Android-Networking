---
title: Logging
tags: [custom]
keywords: "logging, http, https, android , logging request"
published: true
sidebar: doc_sidebar
permalink: logging.html
folder: doc
---

## Logging it with OkHttp Logging Interceptor
```groovy
compile 'com.squareup.okhttp3:logging-interceptor:3.4.1'
```

```java
// Adding an Interceptor for Debugging purpose :
HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
logging.setLevel(Level.BASIC);
OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                        . addInterceptor(logging)
                        .build();
AndroidNetworking.initialize(getApplicationContext(),okHttpClient);                       
```

## Logging it with Stetho Network Interceptor
```groovy
compile 'com.faceboogroovyk.stetho:stetho:1.1.1'
```

```java
// Adding a Network Interceptor for Debugging purpose :
OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                        .addNetworkInterceptor(new StethoInterceptor())
                        .build();
AndroidNetworking.initialize(getApplicationContext(),okHttpClient);                        
```


### Inbuilt basic Logging
```java
AndroidNetworking.enableLogging(); // simply enable logging
AndroidNetworking.enableLogging("tag"); // enabling logging with some tag
AndroidNetworking.disableLogging(); // disable logging
```



{% include links.html %}
