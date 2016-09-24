---
title: Clear Bitmap Cache
tags: [bitmap]
keywords: "bitmap, http, https, android ,bitmap, image, imageview, cache"
last_updated: "Sept 24, 2016"
summary: "Clear Bitmap Cache"
published: true
sidebar: doc_sidebar
permalink: clear_bitmap_cache.html
folder: doc
---


## Clear Bitmap Cache
```java
AndroidNetworking.evictBitmap(key); // remove a bitmap with key from LruCache
AndroidNetworking.evictAllBitmap(); // clear LruCache                      
```

{% include links.html %}
