---
title: Cookie
tags: [custom]
keywords: "cookie, http, https, android , cookie request, cookieJar"
published: true
sidebar: doc_sidebar
permalink: cookie.html
folder: doc
---

## Adding and getting Cookie
```java
CookieJar cookieJar = new CookieJar() {

    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        // here you get the cookies from Response
        cookieStore.put(url.host(), cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url.host());
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }
};

OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .build();

AndroidNetworking.post(url)
        .setOkHttpClient(okHttpClient)
        .build()
        .getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                
            }

            @Override
            public void onError(ANError error) {
                
            }
        });          
```

{% include links.html %}
