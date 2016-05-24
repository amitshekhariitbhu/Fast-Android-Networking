package com.androidnetworking.common;

import com.androidnetworking.error.AndroidNetworkingError;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class AndroidNetworkingResponse<T> {

    private final T mResult;

    private final AndroidNetworkingError mError;

    public static <T> AndroidNetworkingResponse<T> success(T result) {
        return new AndroidNetworkingResponse<>(result);
    }

    public static <T> AndroidNetworkingResponse<T> failed(AndroidNetworkingError error) {
        return new AndroidNetworkingResponse<>(error);
    }

    private AndroidNetworkingResponse(T result) {
        this.mResult = result;
        this.mError = null;
    }

    private AndroidNetworkingResponse(AndroidNetworkingError error) {
        this.mResult = null;
        this.mError = error;
        this.mError.setErrorCode(0);
        this.mError.setError(Constants.PARSE_ERROR);
    }

    public T getResult() {
        return mResult;
    }

    public boolean isSuccess() {
        return mError == null;
    }

    public AndroidNetworkingError getError() {
        return mError;
    }

}
