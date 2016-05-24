package com.androidnetworking.error;

import com.androidnetworking.common.AndroidNetworkingData;
import com.androidnetworking.common.Constants;

/**
 * Created by amitshekhar on 22/03/16.
 */
public class AndroidNetworkingError extends Exception {

    private AndroidNetworkingData data;

    private String errorBody;

    private int errorCode = 0;

    private String errorDetail;

    public AndroidNetworkingError() {
    }

    public AndroidNetworkingError(AndroidNetworkingData data) {
        this.data = data;
    }

    public AndroidNetworkingError(String message) {
        super(message);
    }

    public AndroidNetworkingError(String message, AndroidNetworkingData data) {
        super(message);
        this.data = data;
    }

    public AndroidNetworkingError(String message, Throwable throwable) {
        super(message, throwable);
    }

    public AndroidNetworkingError(String message, AndroidNetworkingData data, Throwable throwable) {
        super(message, throwable);
        this.data = data;
    }

    public AndroidNetworkingError(AndroidNetworkingData data, Throwable throwable) {
        super(throwable);
        this.data = data;
    }

    public AndroidNetworkingError(Throwable throwable) {
        super(throwable);
    }

    public AndroidNetworkingData getData() {
        return data;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public String getErrorDetail() {
        return this.errorDetail;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setCancellationMessageInError() {
        this.errorDetail = Constants.REQUEST_CANCELLED_ERROR;
    }

    public String getErrorBody() {
        return errorBody;
    }

    public void setErrorBody(String errorBody) {
        this.errorBody = errorBody;
    }

}
