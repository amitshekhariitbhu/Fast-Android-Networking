---
title: Post Request
tags: [post]
keywords: "POST, http, https, android , networking , post request"
last_updated: "Aug 21, 2016"
summary: "Making a post request : Example Two"
published: true
sidebar: mydoc_sidebar
permalink: mydoc_post_example_two.html
folder: mydoc
---


You can also post json, file ,etc in POST request like this.

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
