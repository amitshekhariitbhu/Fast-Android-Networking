---
title: Setting Custom ContentType
tags: [custom]
keywords: "contentType, http, https, android , custom request"
published: true
sidebar: doc_sidebar
permalink: setting_content_type.html
folder: doc
---


## Setting Custom ContentType in a request
```java
AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/createAnUser")
                 .addBodyParameter("firstname", "Amit")
                 .addBodyParameter("lastname", "Shekhar")
                 .setContentType("application/json; charset=utf-8") // custom ContentType
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
