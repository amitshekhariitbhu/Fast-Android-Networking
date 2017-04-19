package com.androidnetworking.authenticators;

import com.androidnetworking.interfaces.FANTokenManager;
import com.androidnetworking.internal.InternalNetworking;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by Omar on 4/17/2017.
 */

public class OAuth2Authenticator implements Authenticator {

    private FANTokenManager tokenManager;

    public OAuth2Authenticator(FANTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (response.request().header("Authorization") != null) {
            return null; // Give up, we've already failed to authenticate.
        }

        invalidateTokens();

        return response.request().newBuilder()
                .header("Authorization", tokenManager.getTokenType() + " " + tokenManager.getAccessToken() )
                .build();
    }

    private void invalidateTokens() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionPool(InternalNetworking.sConnectionPool)
                .build();

        FormBody formBody = new FormBody.Builder()
                .add("client_id", tokenManager.getClientID())
                .add("client_secret", tokenManager.getClientSecret())
                .add("refresh_token", tokenManager.getRefreshToken())
                .add("grant_type", "refresh_token")
                .build();

        Request request = new Request.Builder()
                .post(formBody)
                .url(tokenManager.getAuthorizationURL())
                .build();

        final Gson gson = new Gson();

        try {
            Response response = client.newCall(request).execute();
            
            JsonObject jsonObject = gson.fromJson(response.body().charStream(), JsonObject.class);
            tokenManager.setAccessToken(jsonObject.get("access_token").getAsString());
            tokenManager.setRefreshToken(jsonObject.get("refresh_token").getAsString());
            String tokenType = jsonObject.get("token_type").getAsString();
            String tokenTypeCapitalized = new StringBuilder(tokenType)
                    .replace(0, 1, tokenType.substring(0, 1).toUpperCase()).toString();
            tokenManager.setTokenType(tokenTypeCapitalized);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
