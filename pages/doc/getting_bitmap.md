---
title: Getting bitmap
tags: [download,bitmap]
keywords: "bitmap, http, https, android ,bitmap, image, download request"
last_updated: "Sept 24, 2016"
summary: "Making Image download request"
published: true
sidebar: doc_sidebar
permalink: getting_bitmap.html
folder: doc
---


## Getting Bitmap from URL with some specified parameters
```java
AndroidNetworking.get(imageUrl)
                 .setTag("imageRequestTag")
                 .setPriority(Priority.MEDIUM)
                 .setBitmapMaxHeight(100)
                 .setBitmapMaxWidth(100)
                 .setBitmapConfig(Bitmap.Config.ARGB_8888)
                 .build()
                 .getAsBitmap(new BitmapRequestListener() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                    // do anything with bitmap
                    }
                    @Override
                    public void onError(ANError error) {
                      // handle error
                    }
                });                              
```

{% include links.html %}
