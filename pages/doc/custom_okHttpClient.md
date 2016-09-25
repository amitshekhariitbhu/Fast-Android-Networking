---
title: Initializing FAN with Custom OkHttpClient
tags: [custom]
keywords: "download, http, https, android , download request, initialize, custom, network, interceptor"
published: true
sidebar: doc_sidebar
permalink: custom_okHttpClient.html
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


Other example : setting custom timeout

```java
OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                . writeTimeout(120, TimeUnit.SECONDS)
                .build();
                
AndroidNetworking.initialize(getApplicationContext(),okHttpClient);  
```

{% include links.html %}
