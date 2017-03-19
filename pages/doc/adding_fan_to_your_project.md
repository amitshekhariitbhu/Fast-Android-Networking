---
title: Adding Fast Android Networking to your Project
tags: [getting_started]
keywords: "GET, http, https, android , get request, adding, include, gradle"
published: true
sidebar: doc_sidebar
permalink: adding_fan_to_your_project.html
folder: doc
---

## Requirements

Fast Android Networking Library can be included in any Android application. 

Fast Android Networking Library supports Android 2.3 (Gingerbread) and later. 

## Using Fast Android Networking Library in your application

Add this in your build.gradle

```groovy
compile 'com.amitshekhar.android:android-networking:1.0.0'
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
