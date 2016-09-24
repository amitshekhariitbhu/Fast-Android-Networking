---
title:  "About Fast Android Networking"
published: true
permalink: about-fast-android-networking.html
summary: "Fast Android Networking is developed by the developers for the developers."
tags: [about, getting_started]
---

Fast Android Networking Library is a powerful library for doing any type of networking in Android applications which is made on top of [OkHttp Networking Layer](http://square.github.io/okhttp/).

## Why use Fast Android Networking ?

* Recent removal of HttpClient in Android Marshmallow(Android M) made other networking library obsolete.
* No other single library do each and everything like making request, downloading any type of file, uploading file, loading
  image from network in ImageView, etc. There are libraries but they are outdated.
* No other library provided simple interface for doing all types of things in networking like setting priority, cancelling, etc.
* As it uses [Okio](https://github.com/square/okio) , No more GC overhead in android application.
  [Okio](https://github.com/square/okio) is made to handle GC overhead while allocating memory.
  [Okio](https://github.com/square/okio) do some clever things to save CPU and memory.
* As it uses [OkHttp](http://square.github.io/okhttp/) , most important it supports HTTP/2.  
* What Fast Android Networking Library supports? [Check here](#fast-android-networking-library-supports)
* Difference over other Networking Library [Check here](#difference-over-other-networking-library)
* RxJava Support For Fast Android Networking: [Check here](https://github.com/amitshekhariitbhu/Fast-Android-Networking/wiki/Using-Fast-Android-Networking-Library-With-RxJava)
* Have an issue or need a feature in Fast Android Networking : [Create an issue](https://github.com/amitshekhariitbhu/Fast-Android-Networking/issues/new)


{% include links.html %}
