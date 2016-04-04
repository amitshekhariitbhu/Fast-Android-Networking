package com.androidnetworking.common;

import com.androidnetworking.error.AndroidNetworkingError;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class AndroidNetworkingResponse<T> {

    private final T mResult;

    private final AndroidNetworkingError mError;

    public interface SuccessListener<T> {
        void onResponse(T response);
    }

    public interface ErrorListener {
        void onError(AndroidNetworkingError error);
    }

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
