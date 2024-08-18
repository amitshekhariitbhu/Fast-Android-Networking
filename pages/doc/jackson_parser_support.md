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

Add this in your `settings.gradle`:
```groovy
maven { url 'https://jitpack.io' }
```

If you are using `settings.gradle.kts`, add the following:
```kotlin
maven { setUrl("https://jitpack.io") }
```

Add this in your `build.gradle`
```groovy
implementation 'com.github.amitshekhariitbhu.Fast-Android-Networking:jackson-android-networking:1.0.4'
```

If you are using `build.gradle.kts`, add the following:
```kotlin
implementation("com.github.amitshekhariitbhu.Fast-Android-Networking:jackson-android-networking:1.0.4")
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
