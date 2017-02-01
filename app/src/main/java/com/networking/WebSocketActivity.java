/*
 *
 *  *    Copyright (C) 2016 Amit Shekhar
 *  *    Copyright (C) 2011 Android Open Source Project
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package com.networking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by amitshekhar on 09/12/16.
 */

public class WebSocketActivity extends AppCompatActivity {

    private static final String TAG = WebSocketActivity.class.getSimpleName();
    private TextView textView;
    private WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_socket);
        textView = (TextView) findViewById(R.id.textView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectWebSocket();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnectWebSocket();
    }

    private void connectWebSocket() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url("ws://echo.websocket.org")
                .build();
        webSocket = client.newWebSocket(request, getWebSocketListener());
    }

    private void disconnectWebSocket() {
        if (webSocket != null) {
            webSocket.cancel();
        }
    }

    private WebSocketListener getWebSocketListener() {
        return new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                webSocket.send("Hello...");
                webSocket.send("...World!");
                webSocket.send(ByteString.decodeHex("deadbeef"));
                webSocket.close(1000, "Goodbye, World!");
            }

            @Override
            public void onMessage(WebSocket webSocket, final String text) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.append("\n");
                        textView.append("MESSAGE: " + text);
                    }
                });
            }

            @Override
            public void onMessage(WebSocket webSocket,final ByteString bytes) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.append("\n");
                        textView.append("MESSAGE: " + bytes.hex());
                    }
                });
            }

            @Override
            public void onClosing(WebSocket webSocket,final int code,final String reason) {
                webSocket.close(1000, null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.append("\n");
                        textView.append("CLOSE: " + code + " " + reason);
                    }
                });
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                t.printStackTrace();
            }
        };
    }
}
