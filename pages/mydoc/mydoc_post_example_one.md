---
title: Post Request
tags: [post]
keywords: "POST, http, https, android , networking , post request"
last_updated: "Aug 21, 2016"
summary: "Making a post request : Example One"
published: true
sidebar: mydoc_sidebar
permalink: mydoc_post_example_one.html
folder: mydoc
---


### Making a POST Request
```java
AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/createAnUser")
                 .addBodyParameter("firstname", "Amit")
                 .addBodyParameter("lastname", "Shekhar")
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
```



{% include links.html %}
