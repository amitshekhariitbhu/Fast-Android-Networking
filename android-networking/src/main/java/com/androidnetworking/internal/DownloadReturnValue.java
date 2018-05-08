package com.androidnetworking.internal;

import okhttp3.Response;

public class DownloadReturnValue {

    private Response mResponse;
    private String mFilePath;

    public DownloadReturnValue(Response response, String filePath) {
        mResponse = response;
        mFilePath = filePath;
    }

    public Response getResponse() {
        return mResponse;
    }

    public String getFilePath() {
        return mFilePath;
    }
}