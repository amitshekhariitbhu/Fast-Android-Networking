package com.androidnetworking.model;

import java.io.Serializable;

/**
 * Created by amitshekhar on 24/05/16.
 */

public class Progress implements Serializable {

    public long currentBytes;
    public long totalBytes;

    public Progress(long currentBytes, long totalBytes) {
        this.currentBytes = currentBytes;
        this.totalBytes = totalBytes;
    }

}