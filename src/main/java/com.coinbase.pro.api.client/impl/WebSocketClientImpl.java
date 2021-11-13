package com.coinbase.pro.api.client.impl;

import com.coinbase.pro.api.client.Callback;
import com.coinbase.pro.api.client.WebSocketClient;
import com.coinbase.pro.api.client.ClientFactory;
import com.coinbase.pro.api.client.domain.DepthEvent;
import com.coinbase.pro.api.client.impl.MyWebSocketListener;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import java.io.Closeable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WebSocketClientImpl implements WebSocketClient, Closeable {

    private final OkHttpClient client;

    public WebSocketClientImpl(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Closeable onDepthEvent(String symbols, Callback<DepthEvent> callback) {
        final String channel = Arrays.stream(symbols.split(","))
                .map(String::trim)
                .map(s -> String.format("%s@depth", s))
                .collect(Collectors.joining("/"));
        return createNewWebSocket(channel, new MyWebSocketListener<>(callback, DepthEvent.class));
    }

    @Override
    public void close() {
    }

    private Closeable createNewWebSocket(String channel, MyWebSocketListener<?> listener) {
        String streamingUrl = String.format("%s/%s", ClientFactory.getStreamApiBaseUrl(), channel);
        //https://api.exchange.coinbase.com/products/product_id/book?level=1
        Request request = new Request.Builder().url(streamingUrl).build();
        final WebSocket webSocket = client.newWebSocket(request, listener);
        return () -> {
            final int code = 1000;
            listener.onClosing(webSocket, code, null);
            webSocket.close(code, null);
            listener.onClosed(webSocket, code, null);
        };
    }
}
