---
title: Logging
tags: [custom]
keywords: "logging, http, https, android , logging request"
published: true
sidebar: doc_sidebar
permalink: logging.html
folder: doc
---


Initializing it with some customization , as it uses [OkHttp](http://square.github.io/okhttp/) as networking layer, you can pass custom OkHttpClient while initializing it.

```java
// Adding an Network Interceptor for Debugging purpose :
OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                        .addNetworkInterceptor(new StethoInterceptor())
                        .build();
AndroidNetworking.initialize(getApplicationContext(),okHttpClient);                        
```


Enable or Disable Internal Logging.

```java
AndroidNetworking.enableLogging(); // simply enable logging
AndroidNetworking.enableLogging("tag"); // enabling logging with some tag
AndroidNetworking.disableLogging(); // disable logging
```

{% include links.html %}
