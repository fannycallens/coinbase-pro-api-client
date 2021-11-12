package com.coinbase.pro.api.client;

import java.io.Closeable;
import java.util.List;

/**
 * data streaming facade, supporting streaming of events through a web socket
 */
public interface WebSocketClient extends Closeable {

    /**
     * Open a new web socket to receive {@link DepthEvent depthEvents} on a callback.
     *
     * @param symbols  market (one or coma-separated) symbol(s) to subscribe to
     * @param callback the callback to call on new events
     * @return a {@link Closeable} that allows the underlying web socket to be closed.
     */
    Closeable onDepthEvent(String symbols, Callback<DepthEvent> callback);

}
