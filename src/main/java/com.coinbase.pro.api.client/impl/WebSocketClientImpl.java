package com.coinbase.pro.api.client.impl;

import com.coinbase.pro.api.client.Callback;
import com.coinbase.pro.api.client.WebSocketClient;
import com.coinbase.pro.api.client.ClientFactory;
import com.coinbase.pro.api.client.domain.DepthEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import java.io.Closeable;


public class WebSocketClientImpl implements WebSocketClient, Closeable {

    private final OkHttpClient client;

    public WebSocketClientImpl(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Closeable onDepthEvent(String ticker, Callback<DepthEvent> callback) {
        String channel = String.format("{\"type\":\"subscribe\",\"product_ids\":[\"%s\"],\"channels\":[\"level2\"]}", ticker);
        //System.out.println(channel);

/*
// Request
{
    "type": "subscribe",
    "channels": ["level2"]
}

 */

        return createNewWebSocket(channel, new MyWebSocketListener<>(callback, DepthEvent.class));
    }

    @Override
    public void close() {
    }

    private Closeable createNewWebSocket(String channel, MyWebSocketListener<?> listener) {
        String streamingUrl = String.format(ClientFactory.getStreamApiBaseUrl(), channel);
        Request request = new Request.Builder().url(streamingUrl).build();
        final WebSocket webSocket = client.newWebSocket(request, listener);
        webSocket.send(channel);
        return () -> {
            final int code = 1000;
            listener.onClosing(webSocket, code, null);
            webSocket.close(code, null);
            listener.onClosed(webSocket, code, null);
        };
    }
}
