---
title: Using Fast Android Networking with Jackson Parser
tags: [getting_started]
keywords: "GET, http, https, android , get request, parser, jackson, gradle"
published: true
sidebar: doc_sidebar
permalink: jackson_parser_support.html
folder: doc
--- 

## Using the Fast Android Networking with Jackson Parser

Add this in your build.gradle

```groovy
compile 'com.amitshekhar.android:jackson-android-networking:1.0.0'
```

Then initialize it in onCreate() Method of application class :

```java
AndroidNetworking.initialize(getApplicationContext());
```

Then set the JacksonParserFactory like below

```java
AndroidNetworking.setParserFactory(new JacksonParserFactory());
```


{% include links.html %}
