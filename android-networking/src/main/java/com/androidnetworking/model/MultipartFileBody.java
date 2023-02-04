package com.androidnetworking.model;

import java.io.File;

public class MultipartFileBody {

    public final File file;
    public final String contentType;
    public final String encoding;

    public MultipartFileBody(File file, String contentType, String encoding) {
        this.file = file;
        this.contentType = contentType;
        this.encoding = encoding;
    }

    public MultipartFileBody(File file, String contentType) {
        this.file = file;
        this.contentType = contentType;
        this.encoding = null;
    }

}
