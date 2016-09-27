---
title: Custom Executor
tags: [custom]
keywords: "custom, http, https, android , request, executor, thread, background"
published: true
sidebar: doc_sidebar
permalink: custom_executor.html
folder: doc
---


## Getting Response and completion in an another thread executor
```java
AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setExecutor(Executors.newSingleThreadExecutor()) // setting an executor to get response or completion on that executor thread
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                      // do anything with response
                      if (response.isSuccessful()) {
                        Log.d(TAG, "Headers :" + response.headers());
                      }
                    }
                    @Override
                    public void onError(ANError anError) {
                     // handle error
                    }
                });                                
```

{% include links.html %}
