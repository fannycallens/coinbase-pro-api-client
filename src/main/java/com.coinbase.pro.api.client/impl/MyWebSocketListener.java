package package com.coinbase.pro.api.client.impl;

import com.coinbase.pro.api.client.Callback;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.io.IOException;


public class MyWebSocketListener<T> extends WebSocketListener {

    private Callback<T> callback;

    private static final ObjectMapper mapper = new ObjectMapper();

    private final ObjectReader objectReader;

    private boolean closing = false;

    public WebSocketListener(Callback<T> callback, Class<T> eventClass) {
        this.callback = callback;
        this.objectReader = mapper.readerFor(eventClass);
    }

    public WebSocketListener(ApiCallback<T> callback, TypeReference<T> eventTypeReference) {
        this.callback = callback;
        this.objectReader = mapper.readerFor(eventTypeReference);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            T event = objectReader.readValue(text);
            callback.onResponse(event);
        } catch (IOException e) {
            throw new Exception(e);
        }
    }

    @Override
    public void onClosing(final WebSocket webSocket, final int code, final String reason) {
        closing = true;
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        if (!closing) {
            callback.onFailure(t);
        }
    }
}