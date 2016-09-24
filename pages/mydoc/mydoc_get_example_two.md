---
title: Get Request
tags: [get]
keywords: "GET, http, https, android , get request, POJO"
last_updated: "Aug 21, 2016"
summary: "Making a get request : Example Two"
published: true
sidebar: mydoc_sidebar
permalink: mydoc_get_example_two.html
folder: mydoc
---

### Using it with your own JAVA Object - JSON Parser

1.Getting the userList

```java
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsParsed(new TypeToken<List<User>>() {}, new ParsedRequestListener<List<User>>() {
                    @Override
                    public void onResponse(List<User> users) {
                      // do anything with response
                      Log.d(TAG, "userList size : " + users.size());
                      for (User user : users) {
                        Log.d(TAG, "id : " + user.id);
                        Log.d(TAG, "firstname : " + user.firstname);
                        Log.d(TAG, "lastname : " + user.lastname);
                      }
                    }
                    @Override
                    public void onError(ANError anError) {
                     // handle error
                    }
                });
```

2.Getting an user

```java                
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUserDetail/{userId}")
                .addPathParameter("userId", "1")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsParsed(new TypeToken<User>() {}, new ParsedRequestListener<User>() {
                     @Override
                     public void onResponse(User user) {
                        // do anything with response
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

Note : TypeToken and getAsParsed is important here.              



{% include links.html %}
