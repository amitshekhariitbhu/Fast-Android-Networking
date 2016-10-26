---
title: Building a request
tags: [head]
keywords: "http, https, android , request , build , optional "
published: true
sidebar: doc_sidebar
permalink: building_a_request.html
folder: doc
---


## Building a request
```java
ANRequest.GetRequestBuilder getRequestBuilder = new ANRequest.GetRequestBuilder(ApiEndPoint.BASE_URL + ApiEndPoint.CHECK_FOR_HEADER);

if(isHeaderRequired){
 getRequestBuilder.addHeaders("token", "1234");
}

if(something != null){
 getRequestBuilder.addQueryParameter("key", something);
}

if(executorRequired){
 getRequestBuilder.setExecutor(Executors.newSingleThreadExecutor());
}

ANRequest anRequest = getRequestBuilder.build();       

anRequest.getAsJSONObject(new JSONObjectRequestListener() {
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
