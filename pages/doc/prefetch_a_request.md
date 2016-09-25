---
title: Download Request
tags: [custom,download]
keywords: "download, http, https, android ,prefetch, download request"
published: true
sidebar: doc_sidebar
permalink: prefetch_a_request.html
folder: doc
---


## Prefetch a download request
```java
AndroidNetworking.download(url, path, fileName)
        .build()
        .prefetch();                             
```

## Prefetch a GET request
```java
AndroidNetworking.get(url)
        .addPathParameter("pageNumber", "0")
        .addQueryParameter("limit", "3")
        .build()
        .prefetch();                           
```

{% include links.html %}
