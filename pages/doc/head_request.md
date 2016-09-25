---
title: Head Request
tags: [head]
keywords: "HEAD, http, https, android , head request"
published: true
sidebar: doc_sidebar
permalink: head_request.html
folder: doc
---


## Making a head request
```java
AndroidNetworking.head("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
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
