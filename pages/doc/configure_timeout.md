---
title: Configure Timeout for Request
tags: [custom]
keywords: "custom, http, https, android , timeout, custom request"
published: true
sidebar: doc_sidebar
permalink: configure_timeout.html
folder: doc
---


## Configuring timeout globally
```java
OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                . writeTimeout(120, TimeUnit.SECONDS)
                .build();

AndroidNetworking.initialize(getApplicationContext(),okHttpClient);                        
```


## Configuring timeout for each request
```java
OkOkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                  .connectTimeout(120, TimeUnit.SECONDS)
                  .readTimeout(120, TimeUnit.SECONDS)
                  . writeTimeout(120, TimeUnit.SECONDS)
                  .build();
  AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                   .addPathParameter("pageNumber", "0")
                   .addQueryParameter("limit", "3")
                   .setTag("test")
                   .setPriority(Priority.LOW)
                   .setOkHttpClient(okHttpClient) // passing a custom okHttpClient 
                   .build()
                   .getAsJSONArray(new JSONArrayRequestListener() {
                      @Override
                      public void onResponse(JSONArray response) {
                        // do anything with response
                      }
                      @Override
                      public void onError(ANError error) {
                      // handle error
                      }
                  });                       
```

{% include links.html %}
