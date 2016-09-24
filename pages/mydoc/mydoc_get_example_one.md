---
title: Get Request
tags: [get]
keywords: "GET, http, https, android , get request"
last_updated: "Aug 21, 2016"
summary: "Making a get request : Example One"
published: true
sidebar: mydoc_sidebar
permalink: mydoc_get_example_one.html
folder: mydoc
---


```java
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                 .addPathParameter("pageNumber", "0")
                 .addQueryParameter("limit", "3")
                 .addHeaders("token", "1234")
                 .setTag("test")
                 .setPriority(Priority.LOW)
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
