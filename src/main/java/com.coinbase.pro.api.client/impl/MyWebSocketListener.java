package com.coinbase.pro.api.client.impl;

import com.coinbase.pro.api.client.Callback;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import java.io.IOException;
import java.lang.System;


public class MyWebSocketListener<T> extends WebSocketListener {

    private Callback<T> callback;

    private static final ObjectMapper mapper = new ObjectMapper();

    private final ObjectReader objectReader;

    private boolean closing = false;

    public MyWebSocketListener(Callback<T> callback, Class<T> eventClass) {
        this.callback = callback;
        this.objectReader = mapper.readerFor(eventClass);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        System.out.println(text);
        try {
            T event = objectReader.readValue(text);
            callback.onResponse(event);
        } catch (IOException e) {
            System.err.println("Exception parsing event or processing callback on response");
            e.printStackTrace(System.err);
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