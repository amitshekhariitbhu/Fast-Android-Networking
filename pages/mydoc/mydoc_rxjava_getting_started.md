---
title: RxJava Getting Started
tags: [get]
keywords: "RxJava, support , android , networking"
last_updated: "Aug 21, 2016"
summary: "RxJava with Fast Android Networking"
published: true
sidebar: mydoc_sidebar
permalink: mydoc_rxjava_getting_started.html
folder: mydoc
---


## Using Fast Android Networking Library in your application with RxJava

Add this in your build.gradle

```groovy
compile 'com.amitshekhar.android:rx-android-networking:0.1.0'
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
