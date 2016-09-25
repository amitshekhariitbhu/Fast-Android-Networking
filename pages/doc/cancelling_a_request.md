---
title: Cancelling a request
tags: [cancel]
keywords: "cancel, http, https, android , cancel request"
published: true
sidebar: doc_sidebar
permalink: cancelling_a_request.html
folder: doc
---


## Cancelling a request
```java
AndroidNetworking.cancel("tag"); // All the requests with the given tag will be cancelled.
AndroidNetworking.forceCancel("tag");  // All the requests with the given tag will be cancelled , even if any percent threshold is set , it will be cancelled forcefully. 
AndroidNetworking.cancelAll(); // All the requests will be cancelled.  
AndroidNetworking.forceCancelAll(); // All the requests will be cancelled , even if any percent threshold is set , it will be cancelled forcefully.
                                                           
```

{% include links.html %}
