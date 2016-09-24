---
title: Getting started with the Fast Android Networking
keywords: sample homepage
tags: [getting_started]
sidebar: mydoc_sidebar
permalink: index.html
summary: These brief instructions will help you get started quickly with the Fast Android Networking.
---

## Requirements

Fast Android Networking Library can be included in any Android application. 

Fast Android Networking Library supports Android 2.3 (Gingerbread) and later. 

## Using Fast Android Networking Library in your application

Add this in your build.gradle

```groovy
compile 'com.amitshekhar.android:android-networking:0.2.0'
```

Do not forget to add internet permission in manifest if already not present

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Then initialize it in onCreate() Method of application class :

```java
AndroidNetworking.initialize(getApplicationContext());
```

{% include links.html %}
