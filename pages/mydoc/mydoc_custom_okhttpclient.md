---
title: Custom intialization with configured OkHttpClient
tags: [getting_started]
keywords: "features, capabilities, scalability, multichannel output, dita, hats, comparison, benefits"
last_updated: "July 16, 2016"
summary: "Initalizing with custom OkHttpClient"
published: true
sidebar: mydoc_sidebar
permalink: mydoc_custom_okhttpclient.html
folder: mydoc
---

Initializing it with some customization , as it uses [OkHttp](http://square.github.io/okhttp/) as newtorking layer, you can pass custom OkHttpClient while initializing it.

```java
// Adding an Network Interceptor for Debugging purpose :
OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                        .addNetworkInterceptor(new StethoInterceptor())
                        .build();
AndroidNetworking.initialize(getApplicationContext(),okHttpClient);                        
```

{% include links.html %}
