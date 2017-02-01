---
title: Get Request
tags: [get]
keywords: "GET, http, https, android , get request"
published: true
sidebar: doc_sidebar
permalink: get_request.html
folder: doc
---


## Getting JSONArray as Response
```java
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                 .addPathParameter("pageNumber", "0")
                 .addQueryParameter("limit", "3")
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

## Getting Parsed Object as Response
```java
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUserDetail/{userId}")
                .addPathParameter("userId", "1")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(User.class, new ParsedRequestListener<User>() {
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

## Getting Parsed Object List as Response
```java
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsObjectList(User.class, new ParsedRequestListener<List<User>>() {
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
Note : YourObject.class, getAsObject and getAsObjectList are important here. 

{% include links.html %}
