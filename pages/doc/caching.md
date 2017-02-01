---
title: Caching
tags: [custom]
keywords: "custom, http, https, android , caching request , cache"
published: true
sidebar: doc_sidebar
permalink: caching.html
folder: doc
---


## How caching works ?
* First of all the server must send cache-control in header so that is starts working.
* Response will be cached on the basis of cache-control max-age, max-stale.
* If the internet is connected and the age is NOT expired, it will return from cache.
* If the internet is connected and the age is expired and if server returns 304(NOT MODIFIED), it will return from cache.
* If the internet is NOT connected and you are using getResponseOnlyIfCached() - it will return from cache even it the date is expired.
* If the internet is NOT connected, if you are NOT using getResponseOnlyIfCached() - it will NOT return anything.
* If you are using getResponseOnlyFromNetwork(), it will only return response after validating from the server.
* If cache-control is set, it will work according to the max-age and the max-stale returned from server.
* If the internet is NOT connected, only way to get cached response is by using getResponseOnlyIfCached().

## Do not cache response
```java
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                 .addPathParameter("pageNumber", "0")
                 .addQueryParameter("limit", "3")
                 .setPriority(Priority.LOW)
                 .doNotCacheResponse()
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

## Get response only if is cached
```java
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                 .addPathParameter("pageNumber", "0")
                 .addQueryParameter("limit", "3")
                 .setPriority(Priority.LOW)
                 .getResponseOnlyIfCached()
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

## Get response only from network(internet)
```java
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                 .addPathParameter("pageNumber", "0")
                 .addQueryParameter("limit", "3")
                 .setPriority(Priority.LOW)
                 .getResponseOnlyFromNetwork()
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

## Set Max Age Cache Control
```java
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                 .addPathParameter("pageNumber", "0")
                 .addQueryParameter("limit", "3")
                 .setPriority(Priority.LOW)
                 .setMaxAgeCacheControl(0, TimeUnit.SECONDS)
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

## Set Max Stale Cache Control
```java
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                 .addPathParameter("pageNumber", "0")
                 .addQueryParameter("limit", "3")
                 .setPriority(Priority.LOW)
                 .setMaxStaleCacheControl(365, TimeUnit.SECONDS)
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
