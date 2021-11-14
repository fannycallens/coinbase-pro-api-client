package com.coinbase.pro.api.client;

import com.coinbase.pro.api.client.domain.DepthEvent;
import java.io.Closeable;

/**
 * data streaming facade, supporting streaming of events through a web socket
 */
public interface WebSocketClient extends Closeable {

    /**
     * Open a new web socket to receive DepthEvent on a callback.
     * @param ticker  market (one or coma-separated) symbol(s) to subscribe to
     * @param callback the callback to call on new events
     * @return a Closeable that allows the underlying web socket to be closed.
     */
    Closeable onDepthEvent(String ticker, Callback<DepthEvent> callback);

}
