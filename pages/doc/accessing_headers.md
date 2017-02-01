---
title: Accessing Headers in Response
tags: [head]
keywords: "HEAD, http, https, android , head request"
published: true
sidebar: doc_sidebar
permalink: accessing_headers.html
folder: doc
---


## Accessing Headers from OkHttpResponse
```java
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                 .addPathParameter("pageNumber", "0")
                 .addQueryParameter("limit", "3")
                 .setPriority(Priority.LOW)
                 .build()
                 .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                      // do anything with response
                      if (response.isSuccessful()) {
                        Log.d(TAG, "Headers :" + response.headers());
                        Log.d(TAG, "response : " + response.body().source().readUtf8());
                      }
                    }
                    @Override
                    public void onError(ANError anError) {
                     // handle error
                    }
                 });                                
```

## Accessing Headers from OkHttpResponse with parsed object
```java
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUserDetail/{userId}")
                 .addPathParameter("userId", "1")
                 .build()
                 .getAsOkHttpResponseAndParsed(new TypeToken<User>() {},
                  new OkHttpResponseAndParsedRequestListener<User>() {
                    @Override
                    public void onResponse(Response okHttpResponse, User user) {
                      // do anything with okHttpResponse and user
                        Log.d(TAG, "Headers :" + response.headers());
                        Log.d(TAG, "id : " + user.id);
                        Log.d(TAG, "firstname : " + user.firstname);
                        Log.d(TAG, "lastname : " + user.lastname);                      
                    }
                    @Override
                    public void onError(ANError anError) {
                      // handle error
                    }
                });                              
```

{% include links.html %}
