---
title: Error Code Handling
tags: [error]
keywords: "error, http, https, android , errorCode, error request"
published: true
sidebar: doc_sidebar
permalink: error_code_handling.html
folder: doc
---


## Error Code Handling
```java
 public void onError(ANError error) {
               if (error.getErrorCode() != 0) {
                    // received error from server
                    // error.getErrorCode() - the error code from server
                    // error.getErrorBody() - the error body from server
                    // error.getErrorDetail() - just an error detail
                    Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                    Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                    Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
               } else {
                    // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                    Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
               }
            }                              
```

{% include links.html %}
