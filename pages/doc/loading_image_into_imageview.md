---
title: Loading image into imageview
tags: [download,bitmap]
keywords: "bitmap, http, https, android ,bitmap, image, imageview, download request"
last_updated: "Sept 24, 2016"
summary: "Loading Image into ImageView from network"
published: true
sidebar: doc_sidebar
permalink: loading_image_into_imageview.html
folder: doc
---


## Loading image from network into ImageView
```java
      <com.androidnetworking.widget.ANImageView
          android:id="@+id/imageView"
          android:layout_width="100dp"
          android:layout_height="100dp"
          android:layout_gravity="center" />

      imageView.setDefaultImageResId(R.drawable.default);
      imageView.setErrorImageResId(R.drawable.error);
      imageView.setImageUrl(imageUrl);                        
```

{% include links.html %}
