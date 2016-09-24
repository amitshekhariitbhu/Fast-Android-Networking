---
title:  "About Fast Android Networking"
published: true
permalink: about-fast-android-networking.html
summary: "Fast Android Networking is developed by the developers for the developers."
tags: [about, getting_started]
---

Fast Android Networking Library is a powerful library for doing any type of networking in Android applications which is made on top of [OkHttp Networking Layer](http://square.github.io/okhttp/).

## Why use Fast Android Networking ?

* Simple interface to make any type of request.
* All types of customization is possible.
* Recent removal of HttpClient in Android Marshmallow(Android M) made other networking library obsolete.
* No other single library do each and everything like making request, downloading any type of file, uploading file, loading
  image from network in ImageView, etc. There are libraries but they are outdated.
* No other library provided simple interface for doing all types of things in networking like setting priority, cancelling, etc.
* As it uses [Okio](https://github.com/square/okio) , No more GC overhead in android application.
  [Okio](https://github.com/square/okio) is made to handle GC overhead while allocating memory.
  [Okio](https://github.com/square/okio) do some clever things to save CPU and memory.
* As it uses [OkHttp](http://square.github.io/okhttp/) , most important it supports HTTP/2.  
* Proper cancellation of request.
* Proper Response Caching, hence reducing bandwidth usage.
* Prefetching of any request can be done so that it gives instant data when required from cache.
* Immediate Request really is immediate now.
* You can get the current bandwidth and connection quality to write better logical code — download high quality images on excellent connection quality and low on poor connection quality.
* Supports JSON Parsing to Java Objects (also support Jackson Parser).
* Supports RxJava


{% include links.html %}
