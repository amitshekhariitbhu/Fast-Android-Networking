package com.androidnetworking.interfaces;

/**
 * Created by Omar on 4/19/2017.
 */

public interface FANTokenManager {
    String getAccessToken();
    void setAccessToken(String accessToken);
    String getRefreshToken();
    void setRefreshToken(String refreshToken);
    String getClientID();
    String getClientSecret();
    String getTokenType();
    void setTokenType(String tokenType);
    String getAuthorizationURL();
}
