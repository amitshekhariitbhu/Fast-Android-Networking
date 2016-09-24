---
title: Post Request
tags: [post]
keywords: "POST, http, https, android , post request"
last_updated: "Sept 24, 2016"
summary: "Making post request"
published: true
sidebar: doc_sidebar
permalink: post_request.html
folder: doc
---


## Setting post data in body parameter
```java
AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/createAnUser")
                 .addBodyParameter("firstname", "Amit")
                 .addBodyParameter("lastname", "Shekhar")
                 .setPriority(Priority.MEDIUM)
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

## Posting json, file ,etc in POST Request
```java

JSONObject jsonObject = new JSONObject();
try {
    jsonObject.put("firstname", "Rohit");
    jsonObject.put("lastname", "Kumar");
} catch (JSONException e) {
  e.printStackTrace();
}
       
AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/createUser")
                 .addJSONObjectBody(jsonObject) // posting json
                 .setTag("test")
                 .setPriority(Priority.MEDIUM)
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
                
AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/postFile")
                 .addFileBody(file) // posting any type of file
                 .setTag("test")
                 .setPriority(Priority.MEDIUM)
                 .build()
                 .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                      // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                      // handle error
                    }
                });               
```

{% include links.html %}
