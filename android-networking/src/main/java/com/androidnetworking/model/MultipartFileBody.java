package com.androidnetworking.model;

import java.io.File;

public class MultipartFileBody {

    public final File file;
    public final String contentType;
    public String fileName;

    public MultipartFileBody(File file, String contentType) {
        this.file = file;
        this.contentType = contentType;
    }

    public MultipartFileBody(File file, String contentType, String fileName) {
        this.file = file;
        this.contentType = contentType;
        this.fileName = fileName;
    }

}
